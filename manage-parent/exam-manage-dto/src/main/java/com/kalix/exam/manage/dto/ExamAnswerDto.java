package com.kalix.exam.manage.dto;

import com.kalix.framework.core.api.web.model.BaseDTO;
import java.util.List;

public class ExamAnswerDto extends BaseDTO {

    private Integer quesTotal; // 试题总数
    private String seqNum;  // 虚拟试题序号
    private Long examId; // 考试Id
    private String subject; // 阅卷科目
    private String subjectVal; // 科目编码
    private Long examAnswerId; // 考生考题答案Id
    private String answer; // 答案文件名
    private Long quesId;  // 试题Id
    private String perScore; // 题总分
    private Long studentId; // 考生Id
    private String stem; // 考题题干
    private String subType; // 考题类型（子类型）
    private Long examScoreId; // 教师评分表Id
    private Integer score;  // 教师评分
    private Integer passScore; // 及格线
    private String teacherType; // 批阅人角色
    private List<ExamAnswerScoreItemDto> examAnswerScoreItems; // 评分项（初审，复审用户使用）

    public Integer getQuesTotal() {
        return quesTotal;
    }

    public void setQuesTotal(Integer quesTotal) {
        this.quesTotal = quesTotal;
    }

    public String getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(String seqNum) {
        this.seqNum = seqNum;
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

    public Long getExamAnswerId() {
        return examAnswerId;
    }

    public void setExamAnswerId(Long examAnswerId) {
        this.examAnswerId = examAnswerId;
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

    public String getPerScore() {
        return perScore;
    }

    public void setPerScore(String perScore) {
        this.perScore = perScore;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public List<ExamAnswerScoreItemDto> getExamAnswerScoreItems() {
        return examAnswerScoreItems;
    }

    public void setExamAnswerScoreItems(List<ExamAnswerScoreItemDto> examAnswerScoreItems) {
        this.examAnswerScoreItems = examAnswerScoreItems;
    }

    public Long getExamScoreId() {
        return examScoreId;
    }

    public void setExamScoreId(Long examScoreId) {
        this.examScoreId = examScoreId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getPassScore() {
        return passScore;
    }

    public void setPassScore(Integer passScore) {
        this.passScore = passScore;
    }

    public String getTeacherType() {
        return teacherType;
    }

    public void setTeacherType(String teacherType) {
        this.teacherType = teacherType;
    }
}
