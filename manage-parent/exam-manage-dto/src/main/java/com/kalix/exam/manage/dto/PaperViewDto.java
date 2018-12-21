package com.kalix.exam.manage.dto;

import com.kalix.framework.core.api.web.model.BaseDTO;

import java.util.List;

public class PaperViewDto extends BaseDTO {
    private String subject; // 考试科目
    private Integer totalScore; // 考试成绩
    private List<PaperQuesDto> paperQuesList; // 考试大题

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public List<PaperQuesDto> getPaperQuesList() {
        return paperQuesList;
    }

    public void setPaperQuesList(List<PaperQuesDto> paperQuesList) {
        this.paperQuesList = paperQuesList;
    }
}
