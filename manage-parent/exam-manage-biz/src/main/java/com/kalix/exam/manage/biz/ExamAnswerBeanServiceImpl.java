package com.kalix.exam.manage.biz;

import com.kalix.enrolment.question.api.biz.IQuestionCommonBizService;
import com.kalix.exam.manage.api.biz.IExamAnswerBeanService;
import com.kalix.exam.manage.api.dao.IExamAnswerBeanDao;
import com.kalix.exam.manage.entities.ExamAnswerBean;
import com.kalix.framework.core.impl.biz.ShiroGenericBizServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            dao.updateNativeQuery("update exam_examinee set state='已考',starttime=current_timestamp where userid=" + userId + " and examid=" + examId);
            paperMap.put("paperId", paperId);
            paperMap.put("examId", examId);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return paperMap;
    }
}
