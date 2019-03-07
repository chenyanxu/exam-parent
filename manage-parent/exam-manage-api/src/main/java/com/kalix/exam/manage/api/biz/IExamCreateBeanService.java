package com.kalix.exam.manage.api.biz;

import com.kalix.exam.manage.entities.ExamCreateBean;
import com.kalix.framework.core.api.biz.IBizService;
import com.kalix.framework.core.api.persistence.JsonData;
import com.kalix.framework.core.api.persistence.JsonStatus;

public interface IExamCreateBeanService extends IBizService<ExamCreateBean> {
    /**
     * 获取所有考卷模板
     * @return
     */
    JsonData getAllExamPaper();


    /**
     * 预先创建试卷
     * @param id
     * @return
     */
    JsonStatus preCreateExamPaper(Long id);

    /**
     * 获取考试试题资源（附件）
     */
    JsonData getAllTemplateRes();

    /**
     * 获取所有未考的考题Ids（用于考题材料下载，大于当前时间的考试的考题）
     * @return
     */
    JsonData getAllExamQuesIds();
}
