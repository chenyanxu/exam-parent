package com.kalix.exam.manage.biz;

import com.kalix.exam.manage.api.biz.IExamCreateBeanService;
import com.kalix.exam.manage.api.dao.IExamCreateBeanDao;
import com.kalix.exam.manage.dto.ExamPagerDto;
import com.kalix.exam.manage.entities.ExamCreateBean;
import com.kalix.framework.core.api.persistence.JsonData;
import com.kalix.framework.core.api.persistence.JsonStatus;
import com.kalix.framework.core.impl.biz.ShiroGenericBizServiceImpl;

import java.util.List;

public class ExamCreateBeanServiceImpl extends ShiroGenericBizServiceImpl<IExamCreateBeanDao, ExamCreateBean> implements IExamCreateBeanService {

    @Override
    public JsonData getAllExamPaper() {
        List<ExamPagerDto> examPapers = dao.findByNativeSql("select a.id,a.title,b.label,b.value from enrolment_question_paper a, enrolment_dict b where a.kskm = b.value and b.type='考试科目'", ExamPagerDto.class, null);
        JsonData jsonData = new JsonData();
        if (examPapers == null || examPapers.isEmpty()) {
            jsonData.setTotalCount(0L);
        } else {
            jsonData.setTotalCount(Long.valueOf(examPapers.size()));
        }
        jsonData.setData(examPapers);
        return jsonData;
    }

    @Override
    public void afterDeleteEntity(Long id, JsonStatus status) {
        dao.updateNativeQuery("delete from exam_examinee where examid=" + id + " state='未考'");
        super.afterDeleteEntity(id, status);
    }
}
