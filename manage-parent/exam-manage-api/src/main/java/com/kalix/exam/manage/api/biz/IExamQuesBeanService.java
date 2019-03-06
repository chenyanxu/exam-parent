package com.kalix.exam.manage.api.biz;

import com.kalix.exam.manage.entities.ExamQuesBean;
import com.kalix.framework.core.api.biz.IBizService;
import com.kalix.framework.core.api.persistence.JsonData;

import java.util.List;

public interface IExamQuesBeanService extends IBizService<ExamQuesBean> {
    /**
     * 获取对应信息
     * @param examId
     * @param quesIds
     * @param quesType
     * @param subType
     * @return
     */
    ExamQuesBean getExamQuesInfo(Long examId, String quesIds, String quesType, String subType);

    /**
     * 按照试卷模板Id，考试Id获取试题的ids串
     * @param paperId
     * @param examId
     * @return
     */
    String getExamQuesIds(Long paperId, Long examId);

    /**
     * 批量添加对应表信息
     * @param examQuesBeans
     */
    void addBatch(List<ExamQuesBean> examQuesBeans);

    JsonData getAllTemplateRes();

}
