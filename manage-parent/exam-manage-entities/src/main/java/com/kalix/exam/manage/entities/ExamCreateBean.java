package com.kalix.exam.manage.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kalix.framework.core.api.persistence.PersistentEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "exam_create")
public class ExamCreateBean extends PersistentEntity {
    private String name;  // 考试名
    private String subject;  // 考试科目
    private String subjectVal; // 科目字典值
    private Date examStart; // 考试开始时间
    private Integer duration; // 考试时长
    private Integer examMinTime; // 答卷最少时间
    private String paperName; // 考卷名
    private String paperChoice; // 抽取试卷
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getExamStart() {
        return examStart;
    }

    public void setExamStart(Date examStart) {
        this.examStart = examStart;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getExamMinTime() {
        return examMinTime;
    }

    public void setExamMinTime(Integer examMinTime) {
        this.examMinTime = examMinTime;
    }

    public String getPaperName() {
        return paperName;
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    public String getPaperChoice() {
        return paperChoice;
    }

    public void setPaperChoice(String paperChoice) {
        this.paperChoice = paperChoice;
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
