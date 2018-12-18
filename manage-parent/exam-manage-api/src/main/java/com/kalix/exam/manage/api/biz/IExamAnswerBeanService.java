package com.kalix.exam.manage.api.biz;

import com.kalix.exam.manage.dto.ExamingDto;
import com.kalix.exam.manage.entities.ExamAnswerBean;
import com.kalix.framework.core.api.biz.IBizService;
import com.kalix.framework.core.api.persistence.JsonStatus;

import java.util.Map;

public interface IExamAnswerBeanService extends IBizService<ExamAnswerBean> {
    /**
     * 生成考卷
     * @param paperId
     * @param examId
     * @return
     */
    Map<String, Object> getExamingPaper(Long paperId, Long examId);

    /**
     * 考试提交
     * @param examingDto
     * @return
     */
    JsonStatus commitExaming(ExamingDto examingDto);
}
