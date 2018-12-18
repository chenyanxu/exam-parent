package com.kalix.exam.manage.dto;

import com.kalix.framework.core.api.web.model.BaseDTO;

import java.util.List;

public class ExamingDto extends BaseDTO {
    // 考题Id
    private Long examId;
    // 考卷模板Id
    private Long paperId;
    // 考题列表
    private List<ExamQuesDto> quesList;

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

    public List<ExamQuesDto> getQuesList() {
        return quesList;
    }

    public void setQuesList(List<ExamQuesDto> quesList) {
        this.quesList = quesList;
    }
}
