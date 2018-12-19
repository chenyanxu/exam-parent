package com.kalix.exam.manage.biz;

import com.kalix.exam.manage.api.biz.IExamAnswerBeanService;
import com.kalix.exam.manage.api.biz.IExamOverReadService;
import com.kalix.exam.manage.api.dao.IExamAnswerBeanDao;
import com.kalix.exam.manage.dto.ExamOverReadDto;
import com.kalix.exam.manage.dto.ExamSubjectDto;
import com.kalix.exam.manage.entities.ExamAnswerBean;
import com.kalix.framework.core.api.persistence.JsonData;
import com.kalix.framework.core.api.persistence.JsonStatus;
import com.kalix.framework.core.api.security.IShiroService;
import com.kalix.framework.core.util.SerializeUtil;

import java.beans.Transient;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ExamOverReadServiceImpl implements IExamOverReadService {
    private IExamAnswerBeanDao dao;
    private IExamAnswerBeanService examAnswerBeanService;
    private IShiroService shiroService;

    public void setDao(IExamAnswerBeanDao dao) {
        this.dao = dao;
    }

    public void setExamAnswerBeanService(IExamAnswerBeanService examAnswerBeanService) {
        this.examAnswerBeanService = examAnswerBeanService;
    }

    public void setShiroService(IShiroService shiroService) {
        this.shiroService = shiroService;
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
    public JsonData getAllExamQuesBySubject(String jsonStr) {
        Map<String, String> jsonMap = SerializeUtil.json2Map(jsonStr);
        String subjectCode = jsonMap.get("subjectCode");
        String name = jsonMap.get("%name%");
        String sql = "select a.id,a.answer,a.examId,a.paperId,a.quesId,a.quesType,a.perScore,a.score,a.userId," +
                "b.name,b.subjectVal,c.stem,c.scoreStandard " +
                "from exam_answer a,exam_create b,enrolment_question_subject c " +
                "where a.examId = b.id and a.quesId=c.id " +
                "and a.readOverState='未批' and a.quesType='5'" +
                " and b.subjectVal='"+subjectCode+"'";
        if (name != null && name.trim().length() > 0) {
            sql += " and b.name like'%"+name+"%'";
        }
        List<ExamOverReadDto> examOverReadList = null;
        try {
            examOverReadList = dao.findByNativeSql(sql, ExamOverReadDto.class);
        } catch(Exception e) {
            e.printStackTrace();
        }

        JsonData jsonData = new JsonData();
        if (examOverReadList == null || examOverReadList.isEmpty()) {
            jsonData.setTotalCount(0L);
        } else {
            jsonData.setTotalCount(Long.valueOf(examOverReadList.size()));
        }
        jsonData.setData(examOverReadList);
        return jsonData;
    }

    @Override
    @Transient
    public JsonStatus overReadScore(ExamOverReadDto examOverReadDto) {
        JsonStatus jsonStatus = new JsonStatus();
        try {
            Long userId = shiroService.getCurrentUserId();
            Long examAnswerId = examOverReadDto.getId();
            ExamAnswerBean examAnswerBean = examAnswerBeanService.getEntity(examAnswerId);
            examAnswerBean.setScore(examOverReadDto.getScore());
            examAnswerBean.setReadoverState("已批");
            examAnswerBean.setReadoverBy(userId);
            examAnswerBean.setReadoverOn(new Date());
            examAnswerBeanService.updateEntity(examAnswerBean);
            jsonStatus.setMsg("提交成功");
            jsonStatus.setSuccess(true);
        } catch(Exception e) {
            e.printStackTrace();
            jsonStatus.setMsg("提交失败");
            jsonStatus.setFailure(true);
        }
        return jsonStatus;
    }
}
