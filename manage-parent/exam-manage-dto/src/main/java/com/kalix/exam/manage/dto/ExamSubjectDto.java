package com.kalix.exam.manage.dto;

import com.kalix.framework.core.api.web.model.QueryDTO;

public class ExamSubjectDto extends QueryDTO {
    private String id;
    private String text;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
