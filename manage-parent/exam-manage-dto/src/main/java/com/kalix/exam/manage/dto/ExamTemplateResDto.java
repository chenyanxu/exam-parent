package com.kalix.exam.manage.dto;

import com.kalix.framework.core.api.web.model.BaseDTO;

import java.util.ArrayList;
import java.util.List;

public class ExamTemplateResDto extends BaseDTO {
    private Long examId;      // 考试id
    private String name;      // 考试名
    private Long paperId;     // 考卷Id
    private String paperName; // 考卷名
    private String quesIds;   // 试题ids
    private List<String> templateResUrls = new ArrayList<String>();

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public String getPaperName() {
        return paperName;
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    public String getQuesIds() {
        return quesIds;
    }

    public void setQuesIds(String quesIds) {
        this.quesIds = quesIds;
    }

    public List<String> getTemplateResUrls() {
        return templateResUrls;
    }

    public void setTemplateResUrls(List<String> templateResUrls) {
        this.templateResUrls = templateResUrls;
    }
}
