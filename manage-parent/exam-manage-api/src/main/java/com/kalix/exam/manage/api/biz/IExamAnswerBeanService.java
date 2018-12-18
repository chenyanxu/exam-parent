package com.kalix.exam.manage.api.biz;

import com.kalix.exam.manage.entities.ExamAnswerBean;
import com.kalix.framework.core.api.biz.IBizService;

import java.util.Map;

public interface IExamAnswerBeanService extends IBizService<ExamAnswerBean> {
    /**
     * 生成考卷
     * @param paperId
     * @param examId
     * @return
     */
    Map<String, Object> getExamingPaper(Long paperId, Long examId);
}
