package com.kalix.exam.manage.api.biz;

import com.kalix.exam.manage.dto.ExamingDto;
import com.kalix.exam.manage.dto.PaperQuesAnswerDto;
import com.kalix.exam.manage.entities.ExamAnswerBean;
import com.kalix.framework.core.api.biz.IBizService;
import com.kalix.framework.core.api.persistence.JsonStatus;

import java.util.List;
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

    /**
     * 获取考生一次考试的所有答题列表
     * @param examId
     * @param paperId
     * @param userId
     * @return
     */
    List<ExamAnswerBean> getExamUserAnswer(Long examId, Long paperId, Long userId);

    /**
     * 获取考卷题列表及考试成绩
     * @return
     */
    List<PaperQuesAnswerDto> getPaperQuesAnswerList(Long examId, Long paperId);
}
