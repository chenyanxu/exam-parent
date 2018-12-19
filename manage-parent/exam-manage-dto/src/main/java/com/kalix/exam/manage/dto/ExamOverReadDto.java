package com.kalix.exam.manage.dto;

import com.kalix.framework.core.api.web.model.BaseDTO;

public class ExamOverReadDto extends BaseDTO {
    private String answer;
    private Long examId;
    private Long paperId;
    private Long quesId;
    private String quesType;
    private String perScore;
    private Long userId;
    private String name;
    private String subjectVal;
    private String stem;
    private String scoreStandard;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public Long getQuesId() {
        return quesId;
    }

    public void setQuesId(Long quesId) {
        this.quesId = quesId;
    }

    public String getQuesType() {
        return quesType;
    }

    public void setQuesType(String quesType) {
        this.quesType = quesType;
    }

    public String getPerScore() {
        return perScore;
    }

    public void setPerScore(String perScore) {
        this.perScore = perScore;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubjectVal() {
        return subjectVal;
    }

    public void setSubjectVal(String subjectVal) {
        this.subjectVal = subjectVal;
    }

    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public String getScoreStandard() {
        return scoreStandard;
    }

    public void setScoreStandard(String scoreStandard) {
        this.scoreStandard = scoreStandard;
    }
}
