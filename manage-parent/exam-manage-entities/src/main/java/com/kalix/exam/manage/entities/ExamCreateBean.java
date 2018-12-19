package com.kalix.exam.manage.entities;

import com.kalix.framework.core.api.persistence.PersistentEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "exam_create")
public class ExamCreateBean extends PersistentEntity {
    private String name;  // 考试名
    private String subject;  // 考试科目
    private String subjectVal; // 科目字典值
    private Integer duration; // 考试时长
    private String paperName; // 考卷名
    private Long paperId;  // 考卷Id
    private String distributeStat; // 考生分配状态

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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getPaperName() {
        return paperName;
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public String getDistributeStat() {
        return distributeStat;
    }

    public void setDistributeStat(String distributeStat) {
        this.distributeStat = distributeStat;
    }
}
