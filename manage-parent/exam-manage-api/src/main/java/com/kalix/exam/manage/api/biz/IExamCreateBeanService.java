package com.kalix.exam.manage.api.biz;

import com.kalix.exam.manage.entities.ExamCreateBean;
import com.kalix.framework.core.api.biz.IBizService;
import com.kalix.framework.core.api.persistence.JsonData;

public interface IExamCreateBeanService extends IBizService<ExamCreateBean> {
    /**
     * 获取所有考卷模板
     * @return
     */
    JsonData getAllExamPaper();
}
