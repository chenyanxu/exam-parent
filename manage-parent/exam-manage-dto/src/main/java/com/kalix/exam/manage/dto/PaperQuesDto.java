package com.kalix.exam.manage.dto;

import com.kalix.framework.core.api.web.model.BaseDTO;

import java.util.List;

public class PaperQuesDto extends BaseDTO {
    private String title;
    private Integer titleNum;
    private String quesType;
    private List<PaperQuesInfoDto> paperQuesInfos;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTitleNum() {
        return titleNum;
    }

    public void setTitleNum(Integer titleNum) {
        this.titleNum = titleNum;
    }

    public String getQuesType() {
        return quesType;
    }

    public void setQuesType(String quesType) {
        this.quesType = quesType;
    }

    public List<PaperQuesInfoDto> getPaperQuesInfos() {
        return paperQuesInfos;
    }

    public void setPaperQuesInfos(List<PaperQuesInfoDto> paperQuesInfos) {
        this.paperQuesInfos = paperQuesInfos;
    }
}
