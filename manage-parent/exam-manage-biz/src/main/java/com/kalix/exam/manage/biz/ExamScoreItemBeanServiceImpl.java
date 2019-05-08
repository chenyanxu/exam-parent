package com.kalix.exam.manage.biz;

import com.kalix.exam.manage.api.biz.IExamScoreItemBeanService;
import com.kalix.exam.manage.api.dao.IExamScoreItemBeanDao;
import com.kalix.exam.manage.dto.ExamAnswerScoreItemDto;
import com.kalix.exam.manage.entities.ExamScoreItemBean;
import com.kalix.framework.core.impl.biz.ShiroGenericBizServiceImpl;

import java.util.List;

public class ExamScoreItemBeanServiceImpl extends ShiroGenericBizServiceImpl<IExamScoreItemBeanDao, ExamScoreItemBean> implements IExamScoreItemBeanService {
    @Override
    public List<ExamAnswerScoreItemDto> getExamAnswerScoreSuperItemList(Long scoreId) {
        String sql = "select a.examScoreId,a.itemdeductscore,a.standeritemscore, a.standerItemId,b.itemscore,b.standeritem from exam_score_item a,enrolment_question_scorestandar b " +
                " where a.quesid=b.quesid and a.standerItemId=b.id and a.examScoreId=" + scoreId;
        return dao.findByNativeSql(sql, ExamAnswerScoreItemDto.class);
    }

    @Override
    public void saveForBatch(List<ExamScoreItemBean> examScoreItemBeanList) {
        dao.addBatch(examScoreItemBeanList);
    }

}
