package com.kalix.exam.manage.api.biz;

import com.kalix.exam.manage.dto.ExamOverReadDto;
import com.kalix.framework.core.api.IService;
import com.kalix.framework.core.api.persistence.JsonData;
import com.kalix.framework.core.api.persistence.JsonStatus;

public interface IExamOverReadService extends IService {
    /**
     * 获取考试科目列表
     * @return
     */
    JsonData getAllSubjects();

    /**
     * 按科目查询批卷列表
     * @param jsonStr
     * @return
     */
    JsonData getAllExamQuesBySubject(String jsonStr);

    /**
     * 考题打分
     * @param examOverReadDto
     * @return
     */
    JsonStatus overReadScore(ExamOverReadDto examOverReadDto);
}
