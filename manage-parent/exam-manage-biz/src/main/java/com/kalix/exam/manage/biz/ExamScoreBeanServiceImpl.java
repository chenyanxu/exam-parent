package com.kalix.exam.manage.biz;

import com.kalix.exam.manage.api.biz.IExamScoreBeanService;
import com.kalix.exam.manage.api.biz.IExamScoreItemBeanService;
import com.kalix.exam.manage.api.biz.IExamTeacherBeanService;
import com.kalix.exam.manage.api.dao.IExamScoreBeanDao;
import com.kalix.exam.manage.dto.ExamAnswerDto;
import com.kalix.exam.manage.dto.ExamAnswerScoreItemDto;
import com.kalix.exam.manage.dto.ExamTeacherDto;
import com.kalix.exam.manage.entities.ExamScoreBean;
import com.kalix.framework.core.api.persistence.JsonData;
import com.kalix.framework.core.impl.biz.ShiroGenericBizServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


public class ExamScoreBeanServiceImpl extends ShiroGenericBizServiceImpl<IExamScoreBeanDao, ExamScoreBean> implements IExamScoreBeanService {

    private IExamTeacherBeanService examTeacherBeanService;

    private IExamScoreItemBeanService examScoreItemBeanService;

    public void setExamTeacherBeanService(IExamTeacherBeanService examTeacherBeanService) {
        this.examTeacherBeanService = examTeacherBeanService;
    }

    public void setExamScoreItemBeanService(IExamScoreItemBeanService examScoreItemBeanService) {
        this.examScoreItemBeanService = examScoreItemBeanService;
    }

    //    private IScoreStanderBeanService scoreStanderBeanService;
//
//    public void setScoreStanderBeanService(IScoreStanderBeanService scoreStanderBeanService) {
//        this.scoreStanderBeanService = scoreStanderBeanService;
//    }

    @Override
    public JsonData getAllExamSubjectsByTeacherId(Long userId) {
        // 教师角色查询
        List<ExamTeacherDto> examTeacherList = examTeacherBeanService.getTeacherDtoByUserId(userId);
        return getResult(examTeacherList);
    }

    @Override
    public JsonData getExamAnswerForScore(Long userId, String subjectVal, String teacherType) {
        // 试题总数获取
        Integer totalNum = getExamAnswerCount(userId, subjectVal);
        List<ExamAnswerDto> examAnswerList = null;
        // 试题查询
        if ("1".equals(teacherType)) {
            // 初审用户
            examAnswerList = getAllExamAnswerList(userId, subjectVal, totalNum, "未批");
        } else if ("2".equals(teacherType)) {
            // 复审用户
            examAnswerList = getAllExamAnswerList(userId, subjectVal, totalNum, "已初审");
        } else if ("3".equals(teacherType)) {
            // 组长
            examAnswerList = getAllExamAnswerSuperList(userId, subjectVal, totalNum, "已复审");
        }

        return getResult(examAnswerList);
    }

    private List<ExamAnswerDto> getAllExamAnswerSuperList(Long userId, String subjectCode, Integer totalNum, String state){
        String sql = "select a.examid as examId,a.teachertype as teacherType,b.subject,b.subjectval as subjectVal,b.passScore," +
                " d.id as examAnswerId,d.answer,d.quesid as quesId,d.perscore,d.userid as studentId," +
                " e.stem,e.subtype,f.id as examScoreId,f.score " +
                " from exam_teacher a,exam_create b,exam_answer d," +
                " enrolment_question_subject e,exam_score f " +
                " where a.examid = b.id and d.examid = a.examid " +
                " and e.id = d.quesid and f.examid=a.examid and f.userid=d.userid " +
                " and d.readOverState='" + state + "'" +
                " and a.userid = " + userId + " and f.teacherid != " + userId;

        if (subjectCode != null && !subjectCode.trim().isEmpty()) {
            sql += " and b.subjectval = " + subjectCode;
        }

        List<ExamAnswerDto> examAnswerTempList = dao.findByNativeSql(sql, ExamAnswerDto.class);
        if (examAnswerTempList == null || examAnswerTempList.isEmpty()) {
            return null;
        }
        // 获取及格的
        Map<String, List<ExamAnswerDto>> examAnswerPassListMap = null;
        List<ExamAnswerDto> examAnswerPassList = examAnswerTempList.stream().filter((e)->e.getScore() >= e.getPassScore()).collect(Collectors.toList());
        if (examAnswerPassList != null && !examAnswerPassList.isEmpty()) {
            examAnswerPassListMap = examAnswerPassList.stream().collect(Collectors.groupingBy(ExamAnswerDto::getTeacherType));
        }

        // 获取不及格的
        Map<String, List<ExamAnswerDto>> examAnswerNotPassListMap = null;
        List<ExamAnswerDto> examAnswerNotPassList = examAnswerTempList.stream().filter((e)->e.getScore() < e.getPassScore()).collect(Collectors.toList());
        if (examAnswerNotPassList != null && !examAnswerNotPassList.isEmpty()) {
            examAnswerNotPassListMap = examAnswerNotPassList.stream().collect(Collectors.groupingBy(ExamAnswerDto::getTeacherType));
        }

        List<ExamAnswerDto> examAnswerDtoList = new ArrayList<>();
        if (examAnswerPassListMap != null && examAnswerNotPassListMap != null) {
            for (Map.Entry<String, List<ExamAnswerDto>> passEntry : examAnswerPassListMap.entrySet()) {
                for (Map.Entry<String, List<ExamAnswerDto>> notPassEntry : examAnswerNotPassListMap.entrySet()) {
                    String passKey = passEntry.getKey();
                    String notPassKey = notPassEntry.getKey();
                    if (!passKey.equals(notPassKey)) {
                        List<ExamAnswerDto> passList = passEntry.getValue();
                        List<ExamAnswerDto> notPassList = notPassEntry.getValue();
                        if (passList != null && !passList.isEmpty()
                                && notPassList != null && !notPassList.isEmpty()) {
                            for (ExamAnswerDto examAnswerDto : passList) {
                                Long examAnswerId = examAnswerDto.getExamAnswerId();
                                Optional<ExamAnswerDto> examAnswerOptional = notPassList.stream().filter((np)->np.getExamAnswerId()==examAnswerId).findFirst();
                                if (examAnswerOptional.isPresent()) {
                                    ExamAnswerDto answerDto = examAnswerOptional.get();
                                    Long scoreId = answerDto.getExamScoreId();
                                    List<ExamAnswerScoreItemDto> examAnswerScoreItems = examScoreItemBeanService.getExamAnswerScoreSuperItemList(scoreId);
                                    answerDto.setExamAnswerScoreItems(examAnswerScoreItems);
                                    examAnswerDtoList.add(answerDto);
                                }
                            }
                        }
                    }
                }
            }
        }

        return examAnswerDtoList;
    }

    /**
     * 试题总数获取
     * @param userId
     * @param subjectCode
     * @return
     */
    private Integer getExamAnswerCount(Long userId, String subjectCode) {
        Integer totalNum = null;
        String examAnswerCountStr = cacheManager.get("exam_answer_" + subjectCode + "_count");
        if (examAnswerCountStr == null || examAnswerCountStr.isEmpty()) {
            totalNum = getExamAnswerCount(userId);
            cacheManager.save("exam_answer_" + subjectCode + "_count", totalNum, 86400);
        } else {
            totalNum = Integer.valueOf(examAnswerCountStr);
        }
        return totalNum;
    }

    /**
     * 序号值对应的考生答案表Id获取
     * @param subjectCode
     * @param seqNum
     * @return
     */
    private Long getExamAnswerId(String subjectCode, String seqNum) {
        String examAnswerIdStr = cacheManager.get("exam_answer_" + subjectCode + "_" + seqNum);
        Long examAnswerId = null;
        if (examAnswerIdStr != null && !examAnswerIdStr.isEmpty()) {
            examAnswerId = Long.valueOf(examAnswerIdStr);
        }
        return examAnswerId;
    }

    /**
     * 获取已答考题列表信息
     * @param userId
     * @return
     */
    private List<ExamAnswerDto> getAllExamAnswerList(Long userId, String subjectCode, Integer totalNum, String state) {
        String sql = "select a.examid as examId,b.subject,b.subjectval as subjectVal," +
                " d.id as examAnswerId,d.answer,d.quesid as quesId,d.perscore,d.userid as studentId," +
                " e.stem,e.subtype" +
                " from exam_teacher a,exam_create b,exam_answer d," +
                " enrolment_question_subject e" +
                " where a.examid = b.id and d.examid = a.examid " +
                " and e.id = d.quesid" +
                " and d.readOverState='" + state + "'" +
                " and a.userid = " + userId;


        if (subjectCode != null && !subjectCode.trim().isEmpty()) {
            sql += " and b.subjectval = " + subjectCode;
        }

        List<ExamAnswerDto> examAnswerTempList = dao.findByNativeSql(sql, ExamAnswerDto.class);
        if (examAnswerTempList == null || examAnswerTempList.isEmpty()) {
            return null;
        }

//        if (examAnswerId == null) {
//            for (int i = 0; i < examAnswerTempList.size(); i++) {
//                ExamAnswerDto examAnswerDto = examAnswerTempList.get(i);
//                Long answerId = examAnswerDto.getExamAnswerId();
//                String subjectVal = examAnswerDto.getSubjectVal();
//                String seqNum = getSeqNum(i);
//                examAnswerDto.setSeqNum(seqNum);
//                cacheManager.save("exam_answer_" + subjectVal + "_" + seqNum, answerId, 86400);
//            }
//        }
        List<ExamAnswerDto> examAnswerList = new ArrayList<>();
        ExamAnswerDto answerDto = examAnswerTempList.get(0);
        Long quesId = answerDto.getQuesId();
        List<ExamAnswerScoreItemDto> examAnswerScoreItemList = getExamAnswerScoreItemList(quesId);
        answerDto.setExamAnswerScoreItems(examAnswerScoreItemList);
        answerDto.setQuesTotal(totalNum);
        examAnswerList.add(answerDto);
        return examAnswerList;
    }

    private List<ExamAnswerScoreItemDto> getExamAnswerScoreItemList(Long quesId) {
        String sql = "select id as standerItemId,standerItem,itemScore from enrolment_question_scorestandar where quesid=" + quesId;
        return dao.findByNativeSql(sql, ExamAnswerScoreItemDto.class);
    }

    /**
     * 获取题序号
     * @param index
     * @return
     */
    private String getSeqNum(int index) {
        int num = index + 1;
        if (String.valueOf(num).length() == 1) {
            return "00" + num;
        }
        if (String.valueOf(num).length() == 2) {
            return "0" + num;
        }
        if (String.valueOf(num).length() == 3) {
            return String.valueOf(num);
        }
        return "000";
    }

    /**
     * 获取已答过的考题数
     * @param userId
     * @return
     */
    private Integer getExamAnswerCount(Long userId) {
        String sql = "select count(1) from exam_teacher a,exam_create b,sys_user c," +
                "exam_answer d,enrolment_question_subject e" +
                " where a.examid = b.id and a.userid = c.id" +
                " and a.userid = " + userId;
        List<Integer> count = dao.findByNativeSql(sql, Integer.class);
        if (count == null || count.isEmpty()) {
            return 0;
        }
        return count.get(0);
    }

    private JsonData getResult(List<?> list) {
        JsonData jsonData = new JsonData();
        if (list == null) {
            jsonData.setTotalCount(0L);
        } else {
            jsonData.setTotalCount(Long.valueOf(list.size()));
        }
        jsonData.setData(list);
        return jsonData;
    }
}
