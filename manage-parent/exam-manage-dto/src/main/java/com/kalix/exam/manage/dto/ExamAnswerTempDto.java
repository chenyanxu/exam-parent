package com.kalix.exam.manage.dto;

import com.kalix.framework.core.api.web.model.BaseDTO;

import java.util.List;

public class ExamAnswerTempDto extends BaseDTO {
    /**
     --总数
     select count(1) from exam_teacher a,exam_create b,sys_user c,exam_answer d,enrolment_question_subject e
     where a.examid = b.id and a.userid = c.id
     and a.userid = 1004475
     **/
    /**
    select a.examid,
    b.subject,b.subjectval,
    c.name,
    d.answer,d.quesid,d.perscore,d.userid as studentId,
    e.stem,e.subtype,
    f.standeritem,f.itemscore,f.id as standerItemId
    from exam_teacher a,exam_create b,sys_user c,exam_answer d,enrolment_question_subject e,enrolment_question_scorestandar f
    where a.examid = b.id and a.userid = c.id and d.examid = a.examid and e.id = d.quesid and e.id=f.quesid and f.subtype=e.subtype
    and a.userid = 1004475
     **/
    private Integer quesTotal; // 试题总数
    private Long examId; // 考试Id
    private String subject; // 阅卷科目
    private String subjectVal; // 科目编码
    private String name; // 阅卷人
    private String answer; // 答案文件名
    private Long quesId;  // 试题Id
    private String perscore; // 题总分
    private Long studentId; // 考生Id
    private String stem; // 考题题干
    private String subtype; // 考题类型（子类型）
    private Long standerItemId; // 考题评分标准项Id
    private String standerItem; // 考题评分标准项
    private Integer itemScore;  // 每项分数


    public Integer getQuesTotal() {
        return quesTotal;
    }

    public void setQuesTotal(Integer quesTotal) {
        this.quesTotal = quesTotal;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Long getQuesId() {
        return quesId;
    }

    public void setQuesId(Long quesId) {
        this.quesId = quesId;
    }

    public String getPerscore() {
        return perscore;
    }

    public void setPerscore(String perscore) {
        this.perscore = perscore;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public Long getStanderItemId() {
        return standerItemId;
    }

    public void setStanderItemId(Long standerItemId) {
        this.standerItemId = standerItemId;
    }

    public String getStanderItem() {
        return standerItem;
    }

    public void setStanderItem(String standerItem) {
        this.standerItem = standerItem;
    }

    public Integer getItemScore() {
        return itemScore;
    }

    public void setItemScore(Integer itemScore) {
        this.itemScore = itemScore;
    }
}
