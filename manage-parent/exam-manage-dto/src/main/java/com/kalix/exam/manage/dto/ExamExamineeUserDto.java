package com.kalix.exam.manage.dto;

import com.kalix.framework.core.api.web.model.BaseDTO;

import java.util.Date;

public class ExamExamineeUserDto extends BaseDTO {
    /***
     * select a.id as examId,a.name as examName,a.subject,a.examStart,a.duration,a.paperId,c.name as userName,c.idCards
     * from exam_create a,exam_examinee b,sys_user c where b.examid=a.id and b.userid=c.id
     * and  b.userid=1003153
     * and b.state in ('未考','考试中')
     */
    private Long examId;
    private String examName;
    private String subject;
    private Date examStart;
    private Integer duration;
    private Long paperId;
    private String userName;
    private String idCards;
    private String examTimeStr;
    private String examDateStr;
    private Long examEndTime;
    private String quesIds;

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

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

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIdCards() {
        return idCards;
    }

    public void setIdCards(String idCards) {
        this.idCards = idCards;
    }

    public String getExamTimeStr() {
        return examTimeStr;
    }

    public void setExamTimeStr(String examTimeStr) {
        this.examTimeStr = examTimeStr;
    }

    public String getExamDateStr() {
        return examDateStr;
    }

    public void setExamDateStr(String examDateStr) {
        this.examDateStr = examDateStr;
    }

    public Long getExamEndTime() {
        return examEndTime;
    }

    public void setExamEndTime(Long examEndTime) {
        this.examEndTime = examEndTime;
    }

    public String getQuesIds() {
        return quesIds;
    }

    public void setQuesIds(String quesIds) {
        this.quesIds = quesIds;
    }
}
