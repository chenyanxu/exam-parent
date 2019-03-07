package com.kalix.exam.manage.dto;

import com.kalix.framework.core.api.web.model.BaseDTO;

public class ExamQuesInfoDto extends BaseDTO {
    /**
     * select a.examid as examId,a.quesids as quesIds,a.paperid as paperId,
     * b.subject,b.subjectval as subjectVal from exam_ques a, exam_create b
     * where a.examid=b.id and b.examstart >= current_timestamp;
     */
    private Long examId;
    private String quesIds;
    private Long paperId;
    private String subject;
    private String subjectVal;

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public String getQuesIds() {
        return quesIds;
    }

    public void setQuesIds(String quesIds) {
        this.quesIds = quesIds;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubjectVal() {
        return subjectVal;
    }

    public void setSubjectVal(String subjectVal) {
        this.subjectVal = subjectVal;
    }
}
