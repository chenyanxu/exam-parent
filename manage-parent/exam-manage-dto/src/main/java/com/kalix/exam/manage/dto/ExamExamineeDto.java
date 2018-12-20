package com.kalix.exam.manage.dto;

import com.kalix.framework.core.api.web.model.BaseDTO;

public class ExamExamineeDto extends BaseDTO {
    private String name; // 考试名
    private String subject; // 考试科目
    private String subjectVal; // 考试科目编码
    private Long paperId; // 考卷Id
    private Long userId; // 考生Id
    private Long examId; // 考试Id
    private Integer totalScore; // 考试成绩

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }
}
