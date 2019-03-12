package com.kalix.exam.manage.biz;

import com.kalix.exam.manage.api.biz.IExamQuesBeanService;
import com.kalix.exam.manage.api.dao.IExamQuesBeanDao;
import com.kalix.exam.manage.entities.ExamCreateBean;
import com.kalix.exam.manage.entities.ExamQuesBean;
import com.kalix.framework.core.api.persistence.JsonData;
import com.kalix.framework.core.impl.biz.ShiroGenericBizServiceImpl;
import java.util.List;

public class ExamQuesBeanServiceImpl extends ShiroGenericBizServiceImpl<IExamQuesBeanDao, ExamQuesBean> implements IExamQuesBeanService {

    @Override
    public ExamQuesBean getExamQuesInfo(Long examId, String quesIds, String quesType, String subType) {
        String sql = "select * from exam_ques " +
                " where examId=" + examId + " and quesIds='"+quesIds+"'" +
                " and quesType='"+quesType+"' and subType='"+subType+"'";
        List<ExamQuesBean> examQuesBeanList = dao.findByNativeSql(sql, ExamQuesBean.class);
        if (examQuesBeanList != null && !examQuesBeanList.isEmpty()){
            return examQuesBeanList.get(0);
        }
        return null;
    }

    @Override
    public void deleteExamQuesInfo(String quesIds, String subType) {
        String sql = "delete from exam_ques where quesIds='"+quesIds+"'" +
                " and subType='"+subType+"'";
        dao.updateNativeQuery(sql);
    }

    @Override
    public String getExamQuesIds(Long paperId, Long examId) {
        String sql = "select * from exam_ques where examId=" + examId + " and paperId=" + paperId;
        List<ExamQuesBean> examQuesBeanList = dao.findByNativeSql(sql, ExamQuesBean.class);
        if (examQuesBeanList != null && !examQuesBeanList.isEmpty()){
            return examQuesBeanList.get(0).getQuesIds();
        }
        return null;
    }

    @Override
    public List<ExamQuesBean> getExamQuesInfo(Long paperId) {
        String sql = "select * from exam_ques where paperId=" + paperId;
        List<ExamQuesBean> examQuesBeanList = dao.findByNativeSql(sql, ExamQuesBean.class);
        return examQuesBeanList;
    }

    @Override
    public void addBatch(List<ExamQuesBean> examQuesBeans) {
        dao.addBatch(examQuesBeans);
    }
}
