package com.kalix.exam.manage.biz;

import com.kalix.enrolment.question.api.biz.IQuestionCommonBizService;
import com.kalix.exam.manage.api.biz.IExamAnswerBeanService;
import com.kalix.exam.manage.api.biz.IExamCreateBeanService;
import com.kalix.exam.manage.api.biz.IExamQuesBeanService;
import com.kalix.exam.manage.api.dao.IExamAnswerBeanDao;
import com.kalix.exam.manage.dto.*;
import com.kalix.exam.manage.entities.ExamAnswerBean;
import com.kalix.exam.manage.entities.ExamCreateBean;
import com.kalix.framework.core.api.persistence.JsonData;
import com.kalix.framework.core.api.persistence.JsonStatus;
import com.kalix.framework.core.impl.biz.ShiroGenericBizServiceImpl;
import com.kalix.framework.core.util.SerializeUtil;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ExamAnswerBeanServiceImpl extends ShiroGenericBizServiceImpl<IExamAnswerBeanDao, ExamAnswerBean> implements IExamAnswerBeanService {
    private IQuestionCommonBizService questionCommonBizService;
    private IExamCreateBeanService examCreateBeanService;
    private IExamQuesBeanService examQuesBeanService;

    public void setQuestionCommonBizService(IQuestionCommonBizService questionCommonBizService) {
        this.questionCommonBizService = questionCommonBizService;
    }

    public void setExamCreateBeanService(IExamCreateBeanService examCreateBeanService) {
        this.examCreateBeanService = examCreateBeanService;
    }

    public void setExamQuesBeanService(IExamQuesBeanService examQuesBeanService) {
        this.examQuesBeanService = examQuesBeanService;
    }

    @Override
    public Map<String, Object> getExamingPaper(Long paperId, Long examId) {
        Map<String, Object> paperMap = new HashMap<>();
        // 验证试卷及考试是否存在
        if (paperId == null || examId == null) {
            paperMap.put("errcode", "-1");
            paperMap.put("errmsg", "试卷模板或考试不存在，请联系管理员");
            return paperMap;
        }
        try {
            // 获取试卷信息
            paperMap = questionCommonBizService.autoCreateTestPaperMap(paperId, null);
            // 验证试卷是否生成成功
            if (paperMap == null && paperMap.size() == 0) {
                paperMap.put("errcode", "-2");
                paperMap.put("errmsg", "试卷生成失败，请重新进入");
                return paperMap;
            }

            Long userId = shiroService.getCurrentUserId();
            ExamCreateBean examCreateBean = examCreateBeanService.getEntity(examId);
            Integer duration = examCreateBean.getDuration();
            Integer examMinTime = examCreateBean.getExamMinTime();
            // 更新参加考试的状态
            dao.updateNativeQuery("update exam_examinee set starttime=current_timestamp where userid=" + userId + " and examid=" + examId);
            paperMap.put("paperId", paperId);
            paperMap.put("examId", examId);
            paperMap.put("duration", duration);
            paperMap.put("examMinTime", examMinTime);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return paperMap;
    }

    @Override
    public Map<String, Object> getPerCreateExamingPaper(Long paperId, Long examId) {
        Map<String, Object> paperMap = new HashMap<>();
        if (paperId == null || examId == null) {
            paperMap.put("errcode", "-1");
            paperMap.put("errmsg", "试卷模板或考试不存在，请联系管理员");
            return paperMap;
        }
        // 通过examId获取quesIds
        String quesIds = examQuesBeanService.getExamQuesIds(paperId, examId);
        if (quesIds == null) {
            paperMap.put("errcode", "-2");
            paperMap.put("errmsg", "考题不存在，请联系管理员");
            return paperMap;
        }
        try {
            // 获取试卷信息
            paperMap = questionCommonBizService.autoCreateTestPaperMap(paperId, quesIds);
            // 验证试卷是否生成成功
            if (paperMap == null && paperMap.size() == 0) {
                paperMap.put("errcode", "-3");
                paperMap.put("errmsg", "试卷生成失败，请重新进入");
                return paperMap;
            }


            ExamCreateBean examCreateBean = examCreateBeanService.getEntity(examId);
            Integer duration = examCreateBean.getDuration();
            long durationTime = duration*60*1000;
            Integer examMinTime = examCreateBean.getExamMinTime();
            Date examStart = examCreateBean.getExamStart();
            SimpleDateFormat formatData = new SimpleDateFormat("yyyy-MM-dd");
            String examStartStr = formatData.format(examStart);
            String[] dates = examStartStr.split("-");
            String examYear = dates[0];
            String examMonth = dates[1];

            long currentTime = new Date().getTime();
            long startTime = examStart.getTime();
            if (currentTime > startTime) {
                long useTime = currentTime - startTime;
                durationTime = durationTime - useTime;
            }
            String subject = examCreateBean.getSubject();
            String subjectVal = examCreateBean.getSubjectVal();

            // 更新参加考试的状态
            //dao.updateNativeQuery("update exam_examinee set state='已考',starttime=current_timestamp where userid=" + userId + " and examid=" + examId);
            paperMap.put("paperId", paperId);
            paperMap.put("examId", examId);
            paperMap.put("duration", duration);
            paperMap.put("durationTime", durationTime);
            paperMap.put("examYear", examYear);
            paperMap.put("examMonth", examMonth);
            paperMap.put("examMinTime", examMinTime);
            paperMap.put("subject", subject);
            paperMap.put("subjectVal", subjectVal);

        } catch(Exception e) {
            e.printStackTrace();
        }

        return paperMap;
    }

    @Override
    public JsonStatus updateStartExamingState(ExamingDto examingDto) {
        JsonStatus jsonStatus = new JsonStatus();
        if (examingDto == null) {
            return jsonStatus;
        }
        try {
            Long userId = examingDto.getUserId();
            Long examId = examingDto.getExamId();
//            Long userId = shiroService.getCurrentUserId();
            // 设置考试状态表
            dao.updateNativeQuery("update exam_examinee set state='考试中' where userid=" + userId + " and examid=" + examId);
            jsonStatus.setSuccess(true);
            jsonStatus.setMsg("更新成功");
        } catch(Exception e) {
            e.printStackTrace();
            jsonStatus.setFailure(true);
            jsonStatus.setMsg("更新失败");
        }
        return jsonStatus;
    }

    /**
     * 提交试卷
     * @param examingDto
     * @return
     */
    @Override
    public synchronized JsonStatus commitExaming(ExamingDto examingDto) {
        JsonStatus jsonStatus = new JsonStatus();
        try {
            Long examId = examingDto.getExamId();
            Long paperId = examingDto.getPaperId();
            List<ExamQuesDto> quesList = examingDto.getQuesList();
            Long userId = examingDto.getUserId();
            List<ExamAnswerBean> examAnswerBeanList = new ArrayList<>();
            // 选择题类型是2
            List<ExamQuesDto> quesChoiceList = quesList.stream().filter(q->"2".equals(q.getQuesType())).collect(Collectors.toList());
            if (quesChoiceList != null && quesChoiceList.size() > 0) {
                List<String> quesIds = quesChoiceList.stream().map(qc -> String.valueOf(qc.getQuesid())).collect(Collectors.toList());
                String qIds = String.join(",", quesIds);
                List<QuesChoiceDto> choiceList = dao.findByNativeSql("select id,answer from enrolment_question_choice where id in (" + qIds + ")", QuesChoiceDto.class);

                for (ExamQuesDto quesChoiceDto : quesChoiceList) {
                    Boolean answerExist = checkAnswerUserExist(userId, examId, quesChoiceDto.getQuesid());
                    ExamAnswerBean examAnswerBean = null;
                    if (!answerExist) {
                        examAnswerBean = createExamAnswerInfo(examId, paperId, userId, quesChoiceDto);
                        // 计算客观题得分
                        Integer score = getScore(quesChoiceDto, choiceList);
                        examAnswerBean.setScore(score);
                        examAnswerBean.setReadoverState("已批");
                        examAnswerBeanList.add(examAnswerBean);
                    }
                }
            }
            // 主观题处理
            List<ExamQuesDto> quesSubjectList = quesList.stream().filter(q->("5".equals(q.getQuesType()))).collect(Collectors.toList());
            if (quesSubjectList != null && quesSubjectList.size() > 0) {
                for (ExamQuesDto quesSubjectDto : quesSubjectList) {
                    Boolean answerExist = checkAnswerUserExist(userId, examId, quesSubjectDto.getQuesid());
                    ExamAnswerBean examAnswerBean = null;
                    if (!answerExist) {
                        examAnswerBean = createExamAnswerInfo(examId, paperId, userId, quesSubjectDto);
                        examAnswerBean.setReadoverState("未批");
                        examAnswerBeanList.add(examAnswerBean);
                    }
                }
            }
            dao.addBatch(examAnswerBeanList);
            // 设置考试状态表
            dao.updateNativeQuery("update exam_examinee set state='已考',endtime=current_timestamp where userid=" + userId + " and examid=" + examId);
            jsonStatus.setSuccess(true);
            jsonStatus.setMsg("提交成功");
        } catch(Exception e) {
            e.printStackTrace();
            jsonStatus.setFailure(true);
            jsonStatus.setMsg("提交失败");
        }

        return jsonStatus;
    }

    @Override
    public List<ExamAnswerBean> getExamUserAnswer(Long examId, Long paperId, Long userId) {
        String sql = "SELECT * FROM exam_answer WHERE examId = " + examId + " and userId=" + userId;
        if (paperId != null) {
            sql += " and paperId="+paperId;
        }
        return dao.findByNativeSql(sql, ExamAnswerBean.class);
    }

    @Override
    public List<PaperQuesAnswerDto> getPaperQuesAnswerList(Long examId, Long paperId) {
        Long userId = shiroService.getCurrentUserId();
        String sql = "select a.answer,a.answerPicPath,a.questype,a.score,a.title,a.titlenum,a.quesnum,"+
                "b.stem,b.answer as quesanswer,b.answera,b.answerb,b.answerc,b.answerd,b.answere,b.answerf,c.subject,d.totalscore"+
                " from exam_answer a,enrolment_question_choice b,exam_create c,exam_examinee d"+
                " where a.quesid= b.id and a.examid=c.id and a.examid=d.examid and a.userid=d.userid and a.paperid=c.paperid"+
                " and d.state='已考' and a.questype='2' and a.userid="+userId+" and a.paperid="+paperId+" and a.examid="+examId +
                " UNION all " +
                "select a.answer,a.answerPicPath,a.questype,a.score,a.title,a.titlenum,a.quesnum,"+
                "b.stem,'','','','','','','',c.subject,d.totalscore"+
                " from exam_answer a,enrolment_question_subject b,exam_create c,exam_examinee d" +
                " where a.quesid= b.id and a.examid=c.id and a.examid=d.examid and a.userid=d.userid and a.paperid=c.paperid"+
                " and d.state='已考' and a.questype='5' and a.userid="+userId+" and a.paperid="+paperId+" and a.examid="+examId;
        return dao.findByNativeSql(sql, PaperQuesAnswerDto.class);
    }

    @Override
    public List<ExamQuesAttachmentDto> getQuesAnswerMaterial(String name, String subjectVal) {
        Long userId = shiroService.getCurrentUserId();
        String sql = "select a.id,a.userId,a.examId,a.quesid,b.name,b.subject,c.stem,count(d.attachmentname) as attachmentCount " +
                " from exam_answer a " +
                " INNER JOIN exam_create b on a.examId=b.id " +
                " INNER JOIN enrolment_question_subject c on a.quesid=c.id " +
                " left JOIN middleware_attachment d on d.mainid=a.id " +
                " where a.userId=" + userId +
                " and a.readoverstate='未批' ";
        if (name != null && name.trim().length() > 0) {
            sql += " and b.name like'%"+name+"%'";
        }
        if (subjectVal != null && subjectVal.trim().length() > 0) {
            sql += " and b.subjectVal = '"+subjectVal+"'";
        }
        sql += " group by a.id,a.userId,a.examId,a.quesid,b.name,b.subject,c.stem";
        return dao.findByNativeSql(sql, ExamQuesAttachmentDto.class);
    }

    @Override
    public JsonData checkExamingTime(Long examId) {
        LocalDateTime now = LocalDateTime.now();
        String dateTime = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        logger.info("examing,current time:" + dateTime);
        List<String> datas = new ArrayList<>();
        datas.add(dateTime);
        return getResult(datas);
    }

    @Override
    public Boolean checkAnswerUserExist(Long userId, Long examId, Long quesId) {

        String sql = "select a from ExamAnswerBean a " +
                " where a.userId=?1 and a.examId = ?2 and a.quesId=?3";
        List<ExamAnswerBean> examAnswerBeanList = dao.find(sql, userId, examId, quesId);
        if (examAnswerBeanList != null && !examAnswerBeanList.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public JsonData getMaintainResultList(Integer page, Integer limit, String jsonStr, String sort) {
        Map<String, String> jsonMap = SerializeUtil.json2Map(jsonStr);
        String examineeName = jsonMap.get("%examineeName%");
        String examcardNumber = jsonMap.get("%examcardNumber%");
        String examName = jsonMap.get("%examName%");
        if (examName == null) {
            examName = "";
        }
        List<MaintainResultDto> maintainResultList = getMaintainResultList(page, limit, examineeName, examcardNumber, examName);
        Integer count = getMaintainResultCount(examineeName, examcardNumber, examName);
        return getResult(maintainResultList, count);
    }

    private Integer getMaintainResultCount(String examineeName, String examcardNumber, String examName) {
        String sql = "select count(1) from exam_answer a" +
                " INNER JOIN exam_score b on a.id = b.examanswerid" +
                " INNER JOIN sys_user c on a.userid=c.id" +
                " INNER JOIN exam_create d on a.examid=d.id" +
                " where d.name like '%"+examName+"%'";
        if (examineeName != null && !examineeName.isEmpty()) {
            sql += " and c.name like '%"+examineeName+"%'";
        }
        if (examcardNumber != null && !examcardNumber.isEmpty()) {
            sql += " and c.examcardnumber like '%"+examcardNumber+"%'";
        }
        List<Integer> list = dao.findByNativeSql(sql, Integer.class);
        if (list == null) {
            return 0;
        }
        return list.get(0);
    }

    private List<MaintainResultDto> getMaintainResultList(Integer page, Integer limit, String examineeName, String examcardNumber, String examName) {
        int offset = 0;
        if (page != null && limit != null && page > 0 && limit > 0) {
            offset = (page - 1) * limit;
        }
        String sql = "select a.id,a.score,b.id as examScoreId,c.NAME as examineeName,c.examcardnumber," +
                "c.idcards,d.name as examName,d.subject,d.subjectval from exam_answer a" +
                " INNER JOIN exam_score b on a.id = b.examanswerid" +
                " INNER JOIN sys_user c on a.userid=c.id" +
                " INNER JOIN exam_create d on a.examid=d.id" +
                " where d.name like '%"+examName+"%' and a.readoverstate = '已批' ";
        if (examineeName != null && !examineeName.isEmpty()) {
            sql += " and c.name like '%"+examineeName+"%'";
        }
        if (examcardNumber != null && !examcardNumber.isEmpty()) {
            sql += " and c.examcardnumber like '%"+examcardNumber+"%'";
        }
        if (page != null && limit != null) {
            sql += " limit " + limit + " offset " + offset;
        }
        List<MaintainResultDto> maintainResultDtoList = dao.findByNativeSql(sql, MaintainResultDto.class);
        return maintainResultDtoList;
    }

    @Override
    public JsonData getMaintainItemList(Integer page, Integer limit, Long examScoreId, String sort) {
        List<MaintainResultItemDto> maintainResultItemList = getMaintainItemList(page, limit, examScoreId);
        Integer count = getMaintainItemCount(examScoreId);
        return getResult(maintainResultItemList, count);
    }

    private List<MaintainResultItemDto> getMaintainItemList(Integer page, Integer limit, Long examScoreId) {
        int offset = 0;
        if (page != null && limit != null && page > 0 && limit > 0) {
            offset = (page - 1) * limit;
        }
        String sql = "select a.id,a.examscoreid,a.standeritemscore,a.itemdeductscore,b.itemscore,b.standeritem,c.stem" +
                " from exam_score_item a" +
                " LEFT JOIN enrolment_question_scorestandar b on b.id=a.standeritemid" +
                " LEFT JOIN enrolment_question_subject c on c.id=a.quesid" +
                " where a.examscoreid = " + examScoreId;
        if (page != null && limit != null) {
            sql += " limit " + limit + " offset " + offset;
        }
        List<MaintainResultItemDto> maintainResultItemList = dao.findByNativeSql(sql, MaintainResultItemDto.class);
        return maintainResultItemList;
    }

    private Integer getMaintainItemCount(Long examScoreId) {
        String sql = "select count(1) from exam_score_item a" +
                " LEFT JOIN enrolment_question_scorestandar b on b.id=a.standeritemid" +
                " LEFT JOIN enrolment_question_subject c on c.id=a.quesid" +
                " where a.examscoreid = " + examScoreId;
        List<Integer> list = dao.findByNativeSql(sql, Integer.class);
        if (list == null) {
            return 0;
        }
        return list.get(0);
    }

    /**
     * 设置学生考试的提交信息
     * @param examId
     * @param paperId
     * @param userId
     * @param examQuesDto
     * @return
     */
    private ExamAnswerBean createExamAnswerInfo(Long examId, Long paperId, Long userId, ExamQuesDto examQuesDto) {
        ExamAnswerBean examAnswerBean = new ExamAnswerBean();
        examAnswerBean.setExamId(examId);
        examAnswerBean.setPaperId(paperId);
        examAnswerBean.setQuesId(examQuesDto.getQuesid());
        examAnswerBean.setQuesType(examQuesDto.getQuesType());
        examAnswerBean.setSubType(examQuesDto.getSubType());
        examAnswerBean.setQuesNum(examQuesDto.getQuesNum());
        examAnswerBean.setAnswer(examQuesDto.getAnswer());
        examAnswerBean.setAnswerPicPath(examQuesDto.getAnswerPicPath());
        examAnswerBean.setUserId(userId);
        examAnswerBean.setTitleNum(examQuesDto.getTitleNum());
        examAnswerBean.setTitle(examQuesDto.getTitle());
        examAnswerBean.setPerScore(examQuesDto.getPerScore());
        return examAnswerBean;
    }

    /**
     * 计算客观题得分
     * @param quesChoiceDto
     * @param choiceList
     * @return
     */
    private Integer getScore(ExamQuesDto quesChoiceDto, List<QuesChoiceDto> choiceList) {
        if (choiceList == null && choiceList.size() == 0) {
            return null;
        }
        QuesChoiceDto choiceDto = choiceList.stream().filter(e->e.getId().equals(quesChoiceDto.getQuesid())).findFirst().get();
        boolean rightAnswer = choiceDto.getAnswer().trim().equals(quesChoiceDto.getAnswer().trim());

        if (rightAnswer) {
            return quesChoiceDto.getPerScore();
        }
        return 0;
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

    private JsonData getResult(List<?> list, int count) {
        JsonData jsonData = new JsonData();
        if (list == null) {
            jsonData.setTotalCount(0L);
        } else {
            jsonData.setTotalCount(Long.valueOf(count));
        }
        jsonData.setData(list);
        return jsonData;
    }
}
