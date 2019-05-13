package com.kalix.exam.manage.api.biz;

import com.kalix.exam.manage.dto.ExamOverReadDto;
import com.kalix.framework.core.api.IService;
import com.kalix.framework.core.api.persistence.JsonData;
import com.kalix.framework.core.api.persistence.JsonStatus;

public interface IExamOverReadService extends IService {
    /**
     * 获取考试科目列表
     * @return
     */
    JsonData getAllSubjects();
    /**
     * 按科目查询批卷列表
     * @param jsonStr
     * @return
     */
    JsonData getAllExamQuesBySubject(String jsonStr);

    /**
     * 考题打分
     * @param examOverReadDto
     * @return
     */
    JsonStatus overReadScore(ExamOverReadDto examOverReadDto);

    /**
     * 统计阅卷总数
     * @param jsonStr
     * @return
     */
    JsonData overReadPaperStatistic(String jsonStr);

    /**
     * 按科目查评分历史
     * @param jsonStr
     * @return
     */
    public JsonData getOverReadQuesBySubject(String jsonStr);

    /**
     * 获取阅卷数信息
     * @param jsonStr
     * @return
     */
    public JsonData getMarkingNumberInfo(Integer page, Integer limit, String jsonStr, String sort);

}
