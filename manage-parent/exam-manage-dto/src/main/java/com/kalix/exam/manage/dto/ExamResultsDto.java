package com.kalix.exam.manage.dto;

import com.kalix.framework.core.api.web.model.BaseDTO;

public class ExamResultsDto extends BaseDTO {
    /**
    select c.name,c.idcards,c.examcardnumber,a.totalscore,b.subject,a.examid,b.subjectval
    from exam_examinee a, exam_create b, sys_user c
    where c.id= a.userid and a.examid=b.id and a.userid=c.id
    and b.subjectval= '10041' and a.state='已考';

    select a.userid,a.teachertype,b.name from exam_teacher a,sys_user b where a.examid = 1;
    **/
    private String examCardNumber; // 准考证号
    private String name; // 姓名
    private String idCards; // 身份证号
    private String subject; // 科目
    private Integer totalScore; // 总成绩
    private Long examId; // 考试Id
    private String firstTeacher; // 初阅人
    private String secondTeacher; // 复阅人
    private String groupLeader; // 组长

    public String getExamCardNumber() {
        return examCardNumber;
    }

    public void setExamCardNumber(String examCardNumber) {
        this.examCardNumber = examCardNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdCards() {
        return idCards;
    }

    public void setIdCards(String idCards) {
        this.idCards = idCards;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public String getFirstTeacher() {
        return firstTeacher;
    }

    public void setFirstTeacher(String firstTeacher) {
        this.firstTeacher = firstTeacher;
    }

    public String getSecondTeacher() {
        return secondTeacher;
    }

    public void setSecondTeacher(String secondTeacher) {
        this.secondTeacher = secondTeacher;
    }

    public String getGroupLeader() {
        return groupLeader;
    }

    public void setGroupLeader(String groupLeader) {
        this.groupLeader = groupLeader;
    }
}
