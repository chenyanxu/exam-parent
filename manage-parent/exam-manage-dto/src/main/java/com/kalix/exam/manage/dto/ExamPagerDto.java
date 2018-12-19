package com.kalix.exam.manage.dto;

import com.kalix.framework.core.api.web.model.BaseDTO;

public class ExamPagerDto extends BaseDTO {

//    private Long id;
    private String title;
    private String label;
    private String value;

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }

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
