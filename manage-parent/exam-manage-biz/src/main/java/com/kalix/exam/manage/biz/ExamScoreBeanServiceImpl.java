package com.kalix.exam.manage.biz;

import com.kalix.exam.manage.api.biz.*;
import com.kalix.exam.manage.api.dao.IExamScoreBeanDao;
import com.kalix.exam.manage.dto.*;
import com.kalix.exam.manage.entities.ExamAnswerBean;
import com.kalix.exam.manage.entities.ExamScoreBean;
import com.kalix.exam.manage.entities.ExamScoreItemBean;
import com.kalix.framework.core.api.persistence.JsonData;
import com.kalix.framework.core.api.persistence.JsonStatus;
import com.kalix.framework.core.impl.biz.ShiroGenericBizServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ExamScoreBeanServiceImpl extends ShiroGenericBizServiceImpl<IExamScoreBeanDao, ExamScoreBean> implements IExamScoreBeanService {

    private final String firstCheckState = "已初审";
    private final String secondCheckState = "已复审";
    private final String notOverReadState = "未批";
    private final String overReadState = "已批";
    private final String overReading = "批阅中";

    private IExamTeacherBeanService examTeacherBeanService;

    private IExamScoreItemBeanService examScoreItemBeanService;

    private IExamAnswerBeanService examAnswerBeanService;

    private IExamExamineeBeanService examExamineeBeanService;

    public void setExamTeacherBeanService(IExamTeacherBeanService examTeacherBeanService) {
        this.examTeacherBeanService = examTeacherBeanService;
    }

    public void setExamScoreItemBeanService(IExamScoreItemBeanService examScoreItemBeanService) {
        this.examScoreItemBeanService = examScoreItemBeanService;
    }

    public void setExamAnswerBeanService(IExamAnswerBeanService examAnswerBeanService) {
        this.examAnswerBeanService = examAnswerBeanService;
    }

    public void setExamExamineeBeanService(IExamExamineeBeanService examExamineeBeanService) {
        this.examExamineeBeanService = examExamineeBeanService;
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
            examAnswerList = getAllExamAnswerList(userId, subjectVal, totalNum, notOverReadState);
        } else if ("2".equals(teacherType)) {
            // 复审用户
            examAnswerList = getAllExamAnswerList(userId, subjectVal, totalNum, firstCheckState);
        } else if ("3".equals(teacherType)) {
            // 组长
            examAnswerList = getAllExamAnswerSuperList(userId, subjectVal, totalNum, secondCheckState);
        }

        return getResult(examAnswerList);
    }

    @Override
    public JsonStatus updateMaintainScore(MaintainResultItemDto maintainResultItemDto) {
        try {
            if (maintainResultItemDto == null) {
                return JsonStatus.failureResult("修改失败");
            }
            // 修改单项分数
            Long examScoreItemId = maintainResultItemDto.getId();
            Integer standerItemScore = maintainResultItemDto.getStanderItemScore();
            ExamScoreItemBean examScoreItemBean = examScoreItemBeanService.getEntity(examScoreItemId);
            examScoreItemBean.setStanderItemScore(standerItemScore);
            examScoreItemBeanService.saveEntity(examScoreItemBean);

            // 修改考题分数
            Long examScoreId = maintainResultItemDto.getExamScoreId();
            List<ExamAnswerScoreItemDto> examAnswerScoreItems = examScoreItemBeanService.getExamAnswerScoreSuperItemList(examScoreId);
            if (examAnswerScoreItems != null && !examAnswerScoreItems.isEmpty()) {
                Integer examScore = 0;
                for (ExamAnswerScoreItemDto examAnswerScoreItemDto : examAnswerScoreItems) {
                    examScore += examAnswerScoreItemDto.getStanderItemScore();
                }
                ExamScoreBean examScoreBean = dao.get(examScoreId);
                examScoreBean.setScore(examScore);
                dao.save(examScoreBean);
                // 修改答案得分表
                Long examAnswerId = examScoreBean.getExamAnswerId();
                ExamAnswerBean examAnswerBean = examAnswerBeanService.getEntity(examAnswerId);
                examAnswerBean.setScore(examScore);
                examAnswerBeanService.saveEntity(examAnswerBean);
                // 修改考生表总分数
                Long examId = examAnswerBean.getExamId();
                Long paperId = examAnswerBean.getPaperId();
                Long userId = examAnswerBean.getUserId();
                List<ExamAnswerBean> examAnswerBeanList = examAnswerBeanService.getExamUserAnswer(examId, paperId, userId);
                Integer totalScore = 0;
                for (ExamAnswerBean answerBean : examAnswerBeanList) {
                    totalScore += answerBean.getScore();
                }
                examExamineeBeanService.updateTotalScore(examId, userId, totalScore);
            }
            return JsonStatus.successResult("修改成功");
        } catch(Exception e) {
            e.printStackTrace();
            return JsonStatus.failureResult("修改失败");
        }
    }

    private List<ExamAnswerDto> getAllExamAnswerSuperList(Long userId, String subjectCode, Integer totalNum, String state){
        String sql = "select a.examid as examId,a.teachertype as teacherType,b.subject,b.subjectval as subjectVal,b.passScore," +
                " d.id as examAnswerId,d.answer,d.quesid as quesId,d.perscore,d.userid as studentId," +
                " e.stem,e.subtype,f.id as examScoreId,f.score " +
                " from exam_teacher a,exam_create b,exam_answer d," +
                " enrolment_question_subject e,exam_score f " +
                " where a.examid = b.id and d.examid = a.examid " +
                " and e.id = d.quesid and f.examid=a.examid and f.userid=d.userid and f.teacherid= a.userid" +
                " and d.readOverState='" + state + "'" +
                " and a.teachertype != '3' ";

        if (subjectCode != null && !subjectCode.trim().isEmpty()) {
            sql += " and b.subjectval = '" + subjectCode + "'";
        }

        String currentSql = sql + " and d.readoverby=" + userId;
        List<ExamAnswerDto> examAnswerTempList = dao.findByNativeSql(currentSql, ExamAnswerDto.class);
        if (examAnswerTempList == null || examAnswerTempList.isEmpty()) {
            examAnswerTempList = dao.findByNativeSql(sql, ExamAnswerDto.class);
            if (examAnswerTempList == null || examAnswerTempList.isEmpty()) {
                return null;
            }
        }

        /**
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
                                Optional<ExamAnswerDto> examAnswerOptional = notPassList.stream().filter((np)->np.getExamAnswerId().equals(examAnswerId)).findFirst();
                                if (examAnswerOptional.isPresent()) {
                                    ExamAnswerDto answerDto = examAnswerOptional.get();
                                    // Long scoreId = answerDto.getExamScoreId();
                                    // List<ExamAnswerScoreItemDto> examAnswerScoreItems = examScoreItemBeanService.getExamAnswerScoreSuperItemList(scoreId);
                                    // answerDto.setExamAnswerScoreItems(examAnswerScoreItems);
                                    examAnswerDtoList.add(answerDto);
                                }
                            }
                        }
                    }
                }
            }
        }

        // 按考题答案Id在做分组，返回第一个满足条件的对象
        Map<Long, List<ExamAnswerDto>> resultMap = null;
        if (examAnswerTempList != null && !examAnswerTempList.isEmpty()) {
            resultMap = examAnswerTempList.stream().collect(Collectors.groupingBy(ExamAnswerDto::getExamAnswerId));
        }
         **/
        Map<Long, List<ExamAnswerDto>> resultMap = examAnswerTempList.stream().collect(Collectors.groupingBy(ExamAnswerDto::getExamAnswerId));
        List<ExamAnswerDto> resultDtoList = null;
        if (resultMap != null && !resultMap.isEmpty()) {
            for (Map.Entry<Long, List<ExamAnswerDto>> resultEntry : resultMap.entrySet()) {
                List<ExamAnswerDto> resultList = resultEntry.getValue();
                if (resultList != null && !resultList.isEmpty()) {
                    resultDtoList = resultList.stream().map((e)->{
                        Long examScoreId = e.getExamScoreId();
                        List<ExamAnswerScoreItemDto> examAnswerScoreItems = examScoreItemBeanService.getExamAnswerScoreSuperItemList(examScoreId);
                        e.setExamAnswerScoreItems(examAnswerScoreItems);
                        return e;
                    }).collect(Collectors.toList());
                }
                break;
            }
        }

        if (resultDtoList != null && !resultDtoList.isEmpty()) {
            for (ExamAnswerDto examAnswerDto : resultDtoList) {
                Long examAnswerId = examAnswerDto.getExamAnswerId();
                updateTempReadOverPerson(examAnswerId, userId);
            }
        }

        return resultDtoList;
    }

    /**
     * 试题总数获取
     * @param userId
     * @param subjectCode
     * @return
     */
    private Integer getExamAnswerCount(Long userId, String subjectCode) {
        Integer totalNum = null;
        totalNum = getExamAnswerCount(userId);
        /**
        String examAnswerCountStr = cacheManager.get("exam_answer_" + subjectCode + "_count");
        if (examAnswerCountStr == null || examAnswerCountStr.isEmpty()) {
            totalNum = getExamAnswerCount(userId);
            cacheManager.save("exam_answer_" + subjectCode + "_count", totalNum, 86400);
        } else {
            totalNum = Integer.valueOf(examAnswerCountStr);
        }
         **/
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
        String sql = "select a.examid as examId,a.teachertype as teacherType,b.subject,b.subjectval as subjectVal,b.passScore," +
                " d.id as examAnswerId,d.answer,d.quesid as quesId,d.perscore,d.userid as studentId," +
                " e.stem,e.subtype" +
                " from exam_teacher a,exam_create b,exam_answer d," +
                " enrolment_question_subject e" +
                " where a.examid = b.id and d.examid = a.examid " +
                " and e.id = d.quesid" +
                " and d.readOverState='" + state + "'" +
                " and a.userid = " + userId;


        if (subjectCode != null && !subjectCode.trim().isEmpty()) {
            sql += " and b.subjectval = '" + subjectCode + "'";
        }

        String currentSql = sql + " and d.readoverby=" + userId;

        List<ExamAnswerDto> examAnswerTempList = dao.findByNativeSql(currentSql, ExamAnswerDto.class);
        if (examAnswerTempList == null || examAnswerTempList.isEmpty()) {
            examAnswerTempList = dao.findByNativeSql(sql, ExamAnswerDto.class);
            if (examAnswerTempList == null || examAnswerTempList.isEmpty()) {
                return null;
            }
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

        // 修改临时批阅人状态
        Long examAnswerId = answerDto.getExamAnswerId();
        updateTempReadOverPerson(examAnswerId, userId);
        return examAnswerList;
    }

    /**
     * 更新临时批阅人
     * @param examAnswerId
     * @param userId
     */
    private void updateTempReadOverPerson(Long examAnswerId, Long userId) {
        ExamAnswerBean examAnswerBean = examAnswerBeanService.getEntity(examAnswerId);
        examAnswerBean.setReadoverBy(userId);
        examAnswerBeanService.saveEntity(examAnswerBean);
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
                " where a.examid = b.id and a.userid = c.id and d.quesid=e.id" +
                " and a.userid = " + userId;
        List<Integer> count = dao.findByNativeSql(sql, Integer.class);
        if (count == null || count.isEmpty()) {
            return 0;
        }
        return count.get(0);
    }

    @Override
    public JsonStatus examAnswerForScore(ExamScoreDto examScoreDto) {
        JsonStatus jsonStatus = new JsonStatus();
        try {
            Boolean isCommited = checkCommitScore(examScoreDto.getExamId(), examScoreDto.getStudentId(), examScoreDto.getExamAnswerId());
            if (isCommited) {
                jsonStatus.setMsg("批阅成绩已提交!");
                jsonStatus.setFailure(true);
                return jsonStatus;
            }
            ExamScoreBean examScoreBean = saveExamScoreBean(examScoreDto);
            Long examScoreId = examScoreBean.getId();
            List<ExamScoreItemDto> examScoreItems = examScoreDto.getExamScoreItems();
            if (examScoreItems != null && !examScoreItems.isEmpty()) {
                List<ExamScoreItemBean> examScoreItemBeanList = new ArrayList<>();
                for (ExamScoreItemDto examScoreItemDto : examScoreItems) {
                    ExamScoreItemBean examScoreItemBean = makeExamScoreItemBean(examScoreItemDto, examScoreId);
                    examScoreItemBeanList.add(examScoreItemBean);
                }
                examScoreItemBeanService.saveForBatch(examScoreItemBeanList);
                String teacherType = examScoreDto.getTeacherType();
                Long examAnswerId = examScoreDto.getExamAnswerId();
                Integer passScore = examScoreDto.getPassScore();
                Long teacherId = examScoreDto.getTeacherId();
                Long examId = examScoreDto.getExamId();
                Long studentId = examScoreDto.getStudentId();
                if ("1".equals(teacherType)) {
                    // 初审教师更新ExamAnswer表状态
                    updateExamAnswerBeanState(examAnswerId, firstCheckState);
                } else if ("2".equals(teacherType)) {
                    // 获取初审教师的打分对比自己的打分
                    Integer score = examScoreDto.getScore();
                    // 获取初审教师的打分
                    Integer firstTrialScore = getFirstTrialScore(examAnswerId, teacherId);
                    if ((score < passScore && firstTrialScore < passScore)
                            || (score >= passScore && firstTrialScore >= passScore)) {
                        Double totalScoreDouble = (firstTrialScore*0.4) + (score*0.6);
                        String totalScoreStr = String.valueOf(Math.ceil(totalScoreDouble));
                        if (totalScoreStr.endsWith(".0")) {
                            totalScoreStr = totalScoreStr.substring(0,totalScoreStr.length()-2);
                        }
                        Integer giveScore = Integer.parseInt(totalScoreStr);
                        // 教师给题定分
                        updateExamAnswerBeanScoreState(examAnswerId, giveScore, overReadState);
                        // 考生表检测更新分数
                        updateExamineeScore(examId, studentId);
                    } else {
                        // 复审教师更新ExamAnswer表状态
                        updateExamAnswerBeanState(examAnswerId, secondCheckState);
                    }
                } else if ("3".equals(teacherType)) {
                    Integer score = examScoreDto.getScore();
                    // 教师给题定分
                    updateExamAnswerBeanScoreState(examAnswerId, score, overReadState);
                    // 考生表检测更新分数
                    updateExamineeScore(examId, studentId);
                }
                // 去掉临时批阅状态
                updateTempReadOverPerson(examAnswerId, null);
            }
            jsonStatus.setSuccess(true);
            jsonStatus.setMsg("提交成功");
        } catch(Exception e) {
            e.printStackTrace();
            jsonStatus.setFailure(true);
            jsonStatus.setMsg("提交失败");
        }
        return jsonStatus;
    }

    private Integer getFirstTrialScore(Long examAnswerId, Long teacherId) {
        String sql = "select a from ExamScoreBean a " +
                " where a.examAnswerId=?1 and a.teacherId <> ?2 and a.teacherType=?3";
        List<ExamScoreBean> examScoreBeanList = dao.find(sql, examAnswerId, teacherId, "1");

        if (examScoreBeanList == null || examScoreBeanList.isEmpty()) {
            return 0;
        }
        return examScoreBeanList.get(0).getScore();
    }

    private Boolean checkCommitScore(Long examId, Long userId, Long examAnswerId) {
        String sql = "select a from ExamScoreBean a " +
                " where a.examId=?1 and a.userId <> ?2 and a.examAnswerId=?3";
        List<ExamScoreBean> examScoreBeanList = dao.find(sql, examId, userId, examAnswerId);
        if (examScoreBeanList != null && !examScoreBeanList.isEmpty()) {
            return true;
        }
        return false;
    }

    private void updateExamAnswerBeanState(Long examAnswerId, String state) {
        ExamAnswerBean examAnswerBean = examAnswerBeanService.getEntity(examAnswerId);
        examAnswerBean.setReadoverState(state);
        examAnswerBeanService.saveEntity(examAnswerBean);
    }

    private void updateExamAnswerBeanScoreState(Long examAnswerId, Integer score, String state) {
        ExamAnswerBean examAnswerBean = examAnswerBeanService.getEntity(examAnswerId);
        examAnswerBean.setReadoverState(state);
        examAnswerBean.setScore(score);
        examAnswerBeanService.saveEntity(examAnswerBean);
    }

    private void updateExamineeScore(Long examId, Long studentId) {
        List<ExamAnswerBean> examAnswerList = examAnswerBeanService.getExamUserAnswer(examId, null, studentId);
        boolean isAllOverRead = examAnswerList.stream().allMatch(e->"已批".equals(e.getReadoverState()));
        // 如果全是已批
        if (isAllOverRead) {
            // 计算总成绩
            Integer totalScore = examAnswerList.stream().map(ExamAnswerBean::getScore).filter(s->s!=null).reduce(Integer::sum).get();
            // 更新学生总成绩
            examExamineeBeanService.updateTotalScore(examId, studentId, totalScore);
        }
    }

    /**
     * 保存ExamScore主表
     * @param examScoreDto
     * @return
     */
    private ExamScoreBean saveExamScoreBean(ExamScoreDto examScoreDto) {
        ExamScoreBean examScoreBean = new ExamScoreBean();
        examScoreBean.setExamId(examScoreDto.getExamId());
        examScoreBean.setScore(examScoreDto.getScore());
        examScoreBean.setTeacherId(examScoreDto.getTeacherId());
        examScoreBean.setUserId(examScoreDto.getStudentId());
        examScoreBean.setExamAnswerId(examScoreDto.getExamAnswerId());
        examScoreBean.setTeacherType(examScoreDto.getTeacherType());
        return dao.save(examScoreBean);
    }

    /**
     * 保存考试项
     * @param examScoreItemDto
     * @return
     */
    private ExamScoreItemBean makeExamScoreItemBean(ExamScoreItemDto examScoreItemDto, Long examScoreId) {
        ExamScoreItemBean examScoreItemBean = new ExamScoreItemBean();
        examScoreItemBean.setExamScoreId(examScoreId);
        examScoreItemBean.setItemDeductScore(examScoreItemDto.getItemDeductScore());
        examScoreItemBean.setQuesId(examScoreItemDto.getQuesId());
        examScoreItemBean.setStanderItemId(examScoreItemDto.getStanderItemId());
        examScoreItemBean.setStanderItemScore(examScoreItemDto.getStanderItemScore());
        return examScoreItemBean;
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
