package com.kalix.exam.manage.api.biz;

import com.kalix.exam.manage.dto.ExamExamineeDto;
import com.kalix.exam.manage.dto.ExamOrgDto;
import com.kalix.exam.manage.dto.ExamSubjectDto;
import com.kalix.exam.manage.entities.ExamExamineeBean;
import com.kalix.framework.core.api.biz.IBizService;
import com.kalix.framework.core.api.persistence.JsonData;
import com.kalix.framework.core.api.persistence.JsonStatus;

import java.util.List;
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
    Map<String, Object> getExamOrgTree(Long examId);
    /**
     * 获取所有个人的考试列表
     * @return
     */
    JsonData getAllSelfExaming(String jsonStr);

    /**
     * 更新总成绩
     * @param examId
     * @param userId
     */
    void updateTotalScore(Long examId, Long userId, Integer totalScore);

    /**
     * 获取用户考试信息
     * @return
     */
    List<ExamExamineeDto> findExamineeByUser(String name, String subjectVal);

    /**
     * 获取报考科目
     * @return
     */
    List<ExamSubjectDto> getExamSubjects();

}
