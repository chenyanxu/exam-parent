package com.kalix.exam.manage.api.biz;

import com.kalix.framework.core.api.IService;
import com.kalix.framework.core.api.persistence.JsonData;

public interface IExamOverReadService extends IService {
    /**
     * 获取考试科目列表
     * @return
     */
    JsonData getAllSubjects();

    /**
     * 按科目查询批卷列表
     * @param subjectCode
     * @return
     */
    JsonData getAllExamQuesBySubject(String subjectCode, String name);
}
