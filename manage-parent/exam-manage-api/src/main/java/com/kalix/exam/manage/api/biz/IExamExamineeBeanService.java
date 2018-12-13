package com.kalix.exam.manage.api.biz;

import com.kalix.exam.manage.dto.ExamOrgDto;
import com.kalix.exam.manage.entities.ExamExamineeBean;
import com.kalix.framework.core.api.biz.IBizService;
import com.kalix.framework.core.api.persistence.JsonStatus;

public interface IExamExamineeBeanService extends IBizService<ExamExamineeBean> {
    JsonStatus saveExaminee(ExamOrgDto examOrgDto);

    void deleteByExamId(Long examId);
}
