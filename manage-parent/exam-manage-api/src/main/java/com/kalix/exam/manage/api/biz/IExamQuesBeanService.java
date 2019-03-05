package com.kalix.exam.manage.api.biz;

import com.kalix.exam.manage.entities.ExamQuesBean;
import com.kalix.framework.core.api.biz.IBizService;
import com.kalix.framework.core.api.persistence.JsonData;

import java.util.List;

public interface IExamQuesBeanService extends IBizService<ExamQuesBean> {
    ExamQuesBean getExamQuesInfo(Long examId, String quesIds, String quesType, String subType);
    void addBatch(List<ExamQuesBean> examQuesBeans);

    JsonData getAllTemplateRes();

}
