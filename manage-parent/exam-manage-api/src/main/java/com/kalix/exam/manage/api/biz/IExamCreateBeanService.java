package com.kalix.exam.manage.api.biz;

import com.kalix.exam.manage.entities.ExamCreateBean;
import com.kalix.framework.core.api.biz.IBizService;
import com.kalix.framework.core.api.persistence.JsonData;

public interface IExamCreateBeanService extends IBizService<ExamCreateBean> {
    JsonData getAllExamPaper();
}
