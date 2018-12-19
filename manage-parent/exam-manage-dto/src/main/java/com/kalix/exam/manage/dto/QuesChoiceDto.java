package com.kalix.exam.manage.dto;

import com.kalix.framework.core.api.web.model.QueryDTO;

public class QuesChoiceDto extends QueryDTO {
    private Long id;
    private String answer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
