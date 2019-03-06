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
    public String getExamQuesIds(Long paperId, Long examId) {
        String sql = "select * from exam_ques where examId=" + examId + " and paperId=" + paperId;
        List<ExamQuesBean> examQuesBeanList = dao.findByNativeSql(sql, ExamQuesBean.class);
        if (examQuesBeanList != null && !examQuesBeanList.isEmpty()){
            return examQuesBeanList.get(0).getQuesIds();
        }
        return null;
    }

    @Override
    public void addBatch(List<ExamQuesBean> examQuesBeans) {
        dao.addBatch(examQuesBeans);
    }

    @Override
    public JsonData getAllTemplateRes() {
        JsonData jsonData = new JsonData();
        String sql = "SELECT * FROM " + this.dao.getTableName() + " ob " +
                " where ob.creationdate >= (CURRENT_DATE) and ob.creationdate < (CURRENT_DATE + interval '1 Days');";
        List<ExamCreateBean> list = this.dao.findByNativeSql(sql, ExamCreateBean.class);
        // 通过试卷拿到试题，通过试题拿到附件,返回结果
        jsonData.setData(list);
        jsonData.setTotalCount((long) list.size());
        return jsonData;
    }

}
