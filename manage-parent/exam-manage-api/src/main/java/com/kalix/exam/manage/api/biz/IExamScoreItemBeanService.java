package com.kalix.exam.manage.api.biz;

import com.kalix.exam.manage.dto.ExamAnswerScoreItemDto;
import com.kalix.exam.manage.entities.ExamScoreItemBean;
import com.kalix.framework.core.api.biz.IBizService;

import java.util.List;

public interface IExamScoreItemBeanService extends IBizService<ExamScoreItemBean> {
    /**
     * 通过得分表Id获取得分项详细信息
     * @param scoreId
     * @return
     */
    List<ExamAnswerScoreItemDto> getExamAnswerScoreSuperItemList(Long scoreId);
}
