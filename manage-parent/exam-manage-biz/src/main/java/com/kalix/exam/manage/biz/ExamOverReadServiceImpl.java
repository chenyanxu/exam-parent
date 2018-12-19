package com.kalix.exam.manage.biz;

import com.kalix.exam.manage.api.biz.IExamOverReadService;
import com.kalix.exam.manage.api.dao.IExamAnswerBeanDao;
import com.kalix.exam.manage.dto.ExamOverReadDto;
import com.kalix.exam.manage.dto.ExamSubjectDto;
import com.kalix.framework.core.api.persistence.JsonData;

import java.util.List;

public class ExamOverReadServiceImpl implements IExamOverReadService {
    private IExamAnswerBeanDao dao;

    public void setDao(IExamAnswerBeanDao dao) {
        this.dao = dao;
    }

    @Override
    public JsonData getAllSubjects() {
        JsonData jsonData = new JsonData();
        List<ExamSubjectDto> subjectList = dao.findByNativeSql("select label as text,value as id from enrolment_dict where type='考试科目'", ExamSubjectDto.class);
        if (subjectList == null && subjectList.isEmpty()) {
            jsonData.setTotalCount(0L);
        } else {
            jsonData.setTotalCount(Long.valueOf(subjectList.size()));
        }
        jsonData.setData(subjectList);
        return jsonData;
    }

    @Override
    public JsonData getAllExamQuesBySubject(String subjectCode, String name) {
        String sql = "select a.answer,a.examId,a.paperId,a.quesId,a.quesType,a.perScore,a.userId," +
                "b.name,b.subjectVal,c.stem,c.scoreStandard " +
                "from exam_answer a,exam_create b,enrolment_question_subject c " +
                "where a.examId = b.id and a.quesId=c.id and a.readOverState='未批' and a.quesType='5'" +
                " and b.subjectVal='"+subjectCode+"'";
        if (name != null && name.trim().length() > 0) {
            sql += " and b.name like'%"+name+"%'";
        }
        List<ExamOverReadDto> examOverReadList = dao.findByNativeSql(sql, ExamOverReadDto.class);
        JsonData jsonData = new JsonData();
        if (examOverReadList == null || examOverReadList.isEmpty()) {
            jsonData.setTotalCount(0L);
        } else {
            jsonData.setTotalCount(Long.valueOf(examOverReadList.size()));
        }
        jsonData.setData(examOverReadList);
        return jsonData;
    }
}
