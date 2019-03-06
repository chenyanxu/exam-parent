package com.kalix.exam.manage.dto;

import com.kalix.framework.core.api.web.model.BaseDTO;

import java.util.Date;

public class ExamExamineeUserDto extends BaseDTO {
    /***
     * select a.id as examId,a.name as examName,a.subject,a.examStart,a.duration,a.paperId,c.name as userName,c.idCards
     * from exam_create a,exam_examinee b,sys_user c where b.examid=a.id and b.userid=c.id
     * and  b.userid=1003153
     * and b.state='未考'
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
}
