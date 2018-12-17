package com.kalix.exam.manage.api.biz;

import com.kalix.exam.manage.dto.ExamOrgDto;
import com.kalix.exam.manage.entities.ExamExamineeBean;
import com.kalix.framework.core.api.biz.IBizService;
import com.kalix.framework.core.api.persistence.JsonData;
import com.kalix.framework.core.api.persistence.JsonStatus;

import java.util.Map;

public interface IExamExamineeBeanService extends IBizService<ExamExamineeBean> {
    /**
     * 保存分配的考生
     * @param examOrgDto
     * @return
     */
    JsonStatus saveExaminee(ExamOrgDto examOrgDto);

    void deleteByExamId(Long examId);

    /**
     * 获取考生所在单位的列表
     * @param examId
     * @return
     */
    java.util.Map<String, Object> getExamOrgTree(Long examId);
    /**
     * 获取所有个人的考试列表
     * @return
     */
    JsonData getAllSelfExaming(String jsonStr);

    /**
     * 生成考卷
     * @param paperId
     * @param examId
     * @return
     */
    Map<String, Object> getExamingPaper(Long paperId, Long examId);
}
