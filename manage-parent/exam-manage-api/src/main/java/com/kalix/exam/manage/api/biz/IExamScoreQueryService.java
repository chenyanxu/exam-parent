package com.kalix.exam.manage.api.biz;

import com.kalix.framework.core.api.IService;
import com.kalix.framework.core.api.persistence.JsonData;

public interface IExamScoreQueryService extends IService {

    /**
     * 获取成绩查询列表
     * @param jsonStr
     * @return
     */
    JsonData getExamScore(String jsonStr);

    /**
     * 获取报考科目
     * @return
     */
    JsonData getExamSubjects();

}
