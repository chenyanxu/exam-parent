package com.kalix.exam.manage.biz;

import com.kalix.enrolment.question.api.biz.IQuestionCommonBizService;
import com.kalix.enrolment.question.entities.ChoiceBean;
import com.kalix.exam.manage.api.biz.IExamAnswerBeanService;
import com.kalix.exam.manage.api.dao.IExamAnswerBeanDao;
import com.kalix.exam.manage.dto.ExamQuesDto;
import com.kalix.exam.manage.dto.ExamingDto;
import com.kalix.exam.manage.entities.ExamAnswerBean;
import com.kalix.framework.core.api.persistence.JsonStatus;
import com.kalix.framework.core.impl.biz.ShiroGenericBizServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExamAnswerBeanServiceImpl extends ShiroGenericBizServiceImpl<IExamAnswerBeanDao, ExamAnswerBean> implements IExamAnswerBeanService {
    private IQuestionCommonBizService questionCommonBizService;

    public void setQuestionCommonBizService(IQuestionCommonBizService questionCommonBizService) {
        this.questionCommonBizService = questionCommonBizService;
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
            /**
            List<Map<String, Object>> quesList = (List<Map<String, Object>>)paperInfo.get("quesList");
            // 初始化学生考试数据
            for (Map<String, Object> map : quesList) {
                String title = (String)map.get("title");
                Integer titleNum = (Integer)map.get("titleNum");
                List<Map<String, Object>> question = (List<Map<String, Object>>)map.get("question");
                for (int i = 0; i < question.size(); i++) {
                    Integer quesNum = i+1;
                    Map<String, Object> quesMap = question.get(i);
                    Map<String, Object> paperBeanMap = (Map<String, Object>)quesMap.get("paperBean");
                    Long quesId = (Long)paperBeanMap.get("quesid");
                    String quesType = (String)paperBeanMap.get("quesType");
                    String subType = (String)paperBeanMap.get("subType");
                    ExamAnswerBean examAnswerBean = new ExamAnswerBean();
                    examAnswerBean.setExamId(examId);

                }
            }
             **/
            // 更新参加考试的状态
            dao.updateNativeQuery("update exam_examinee set state='已考',starttime=current_timestamp where userid=" + userId + " and examid=" + examId);
            paperMap.put("paperId", paperId);
            paperMap.put("examId", examId);
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
            // 选择题类型是2
            List<ExamQuesDto> quesChoiceList = quesList.stream().filter(q->"2".equals(q.getQuesType())).collect(Collectors.toList());
            List<String> quesIds = quesChoiceList.stream().map(qc->String.valueOf(qc.getQuesid())).collect(Collectors.toList());
            String qIds = String.join(",", quesIds);
            List<ChoiceBean> choiceList =  dao.findByNativeSql("select * from enrolment_question_choice where id in ("+qIds+")", ChoiceBean.class);

            List<ExamAnswerBean> examAnswerBeanList = new ArrayList<>();
            for (ExamQuesDto quesChoiceDto : quesChoiceList) {
                ExamAnswerBean examAnswerBean = createExamAnswerInfo(examId, paperId, userId, quesChoiceDto);
                // 计算客观题得分
                Integer score = getScore(quesChoiceDto, choiceList);
                examAnswerBean.setScore(score);
                examAnswerBeanList.add(examAnswerBean);
            }

            // 主观题处理
            List<ExamQuesDto> quesSubjectList = quesList.stream().filter(q->(!"2".equals(q.getQuesType()))).collect(Collectors.toList());
            for (ExamQuesDto quesSubjectDto : quesSubjectList) {
                ExamAnswerBean examAnswerBean = createExamAnswerInfo(examId, paperId, userId, quesSubjectDto);
                examAnswerBeanList.add(examAnswerBean);
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
        examAnswerBean.setUserId(userId);
        examAnswerBean.setReadoverState("未批");
        examAnswerBean.setTitleNum(examQuesDto.getTitleNum());
        examAnswerBean.setTitle(examQuesDto.getTitle());
        return examAnswerBean;
    }

    /**
     * 计算客观题得分
     * @param quesChoiceDto
     * @param choiceList
     * @return
     */
    private Integer getScore(ExamQuesDto quesChoiceDto, List<ChoiceBean> choiceList) {
        if (choiceList == null || choiceList.isEmpty()) {
            return 0;
        }
        boolean rightAnswer = choiceList.stream().anyMatch(e->(e.getId()==quesChoiceDto.getQuesid()&&e.getAnswer().equals(quesChoiceDto.getAnswer())));
        if (rightAnswer) {
            return quesChoiceDto.getPerScore();
        }
        return 0;
    }
}
