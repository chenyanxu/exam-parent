package com.kalix.exam.manage.dto;

import com.kalix.framework.core.api.web.model.BaseDTO;

public class ExamPagerDto extends BaseDTO {

    private String title;
    private String label;
    private String value;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
