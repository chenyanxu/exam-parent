package com.kalix.exam.manage.biz;

import com.kalix.enrolment.question.api.biz.IQuestionCommonBizService;
import com.kalix.exam.manage.api.biz.IExamAnswerBeanService;
import com.kalix.exam.manage.api.biz.IExamCreateBeanService;
import com.kalix.exam.manage.api.dao.IExamAnswerBeanDao;
import com.kalix.exam.manage.dto.*;
import com.kalix.exam.manage.entities.ExamAnswerBean;
import com.kalix.exam.manage.entities.ExamCreateBean;
import com.kalix.framework.core.api.persistence.JsonStatus;
import com.kalix.framework.core.impl.biz.ShiroGenericBizServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExamAnswerBeanServiceImpl extends ShiroGenericBizServiceImpl<IExamAnswerBeanDao, ExamAnswerBean> implements IExamAnswerBeanService {
    private IQuestionCommonBizService questionCommonBizService;
    private IExamCreateBeanService examCreateBeanService;

    public void setQuestionCommonBizService(IQuestionCommonBizService questionCommonBizService) {
        this.questionCommonBizService = questionCommonBizService;
    }

    public void setExamCreateBeanService(IExamCreateBeanService examCreateBeanService) {
        this.examCreateBeanService = examCreateBeanService;
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
            paperMap = questionCommonBizService.autoCreateTestPaperMap(paperId, examId);
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
            dao.updateNativeQuery("update exam_examinee set state='已考',starttime=current_timestamp where userid=" + userId + " and examid=" + examId);
            paperMap.put("paperId", paperId);
            paperMap.put("examId", examId);
            paperMap.put("duration", duration);
            paperMap.put("examMinTime", examMinTime);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return paperMap;
    }

    /**
     * 提交试卷
     * @param examingDto
     * @return
     */
    @Override
    public JsonStatus commitExaming(ExamingDto examingDto) {
        JsonStatus jsonStatus = new JsonStatus();
        try {
            Long examId = examingDto.getExamId();
            Long paperId = examingDto.getPaperId();
            List<ExamQuesDto> quesList = examingDto.getQuesList();
            Long userId = shiroService.getCurrentUserId();
            List<ExamAnswerBean> examAnswerBeanList = new ArrayList<>();
            // 选择题类型是2
            List<ExamQuesDto> quesChoiceList = quesList.stream().filter(q->"2".equals(q.getQuesType())).collect(Collectors.toList());
            if (quesChoiceList != null && quesChoiceList.size() > 0) {
                List<String> quesIds = quesChoiceList.stream().map(qc -> String.valueOf(qc.getQuesid())).collect(Collectors.toList());
                String qIds = String.join(",", quesIds);
                List<QuesChoiceDto> choiceList = dao.findByNativeSql("select id,answer from enrolment_question_choice where id in (" + qIds + ")", QuesChoiceDto.class);

                for (ExamQuesDto quesChoiceDto : quesChoiceList) {
                    ExamAnswerBean examAnswerBean = createExamAnswerInfo(examId, paperId, userId, quesChoiceDto);
                    // 计算客观题得分
                    Integer score = getScore(quesChoiceDto, choiceList);
                    examAnswerBean.setScore(score);
                    examAnswerBean.setReadoverState("已批");
                    examAnswerBeanList.add(examAnswerBean);
                }
            }
            // 主观题处理
            List<ExamQuesDto> quesSubjectList = quesList.stream().filter(q->("5".equals(q.getQuesType()))).collect(Collectors.toList());
            if (quesSubjectList != null && quesSubjectList.size() > 0) {
                for (ExamQuesDto quesSubjectDto : quesSubjectList) {
                    ExamAnswerBean examAnswerBean = createExamAnswerInfo(examId, paperId, userId, quesSubjectDto);
                    examAnswerBean.setReadoverState("未批");
                    examAnswerBeanList.add(examAnswerBean);
                }
            }
            dao.addBatch(examAnswerBeanList);
            // 设置考试状态表
            dao.updateNativeQuery("update exam_examinee set endtime=current_timestamp where userid=" + userId + " and examid=" + examId);
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
        String sql = "SELECT * FROM exam_answer " +
                "WHERE examId = "+examId+" and paperId="+paperId+" and userId=" + userId;
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
}
