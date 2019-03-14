package com.kalix.exam.manage.api.biz;

import com.kalix.exam.manage.entities.ExamScoreBean;
import com.kalix.framework.core.api.biz.IBizService;
import com.kalix.framework.core.api.persistence.JsonData;

public interface IExamScoreBeanService extends IBizService<ExamScoreBean> {

    /**
     * 通过阅卷人的Id查询科目
     * @param userId
     * @return
     */
    JsonData getAllExamSubjectsByTeacherId(Long userId);

    /**
     * 查询批阅试题信息
     * @param userId
     * @return
     */
    JsonData getExamAnswerForScore(Long userId, String subjectVal, String teacherType);
}
