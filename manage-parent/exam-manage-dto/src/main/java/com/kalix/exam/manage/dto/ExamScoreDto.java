package com.kalix.exam.manage.dto;

import com.kalix.framework.core.api.web.model.BaseDTO;

import java.util.*;

public class ExamScoreDto extends BaseDTO {
    private Long examId; // 考试Id
    private Long studentId; // 考生Id
    private Long teacherId; // 阅卷人Id
    private String teacherType; // 阅卷人分类
    private Integer score; // 打分
    private Long examAnswerId; // 考试答案表Id
    private Integer passScore; // 及格线
    private List<ExamScoreItemDto> examScoreItems; // 打分项列表

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherType() {
        return teacherType;
    }

    public void setTeacherType(String teacherType) {
        this.teacherType = teacherType;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Long getExamAnswerId() {
        return examAnswerId;
    }

    public void setExamAnswerId(Long examAnswerId) {
        this.examAnswerId = examAnswerId;
    }

    public Integer getPassScore() {
        return passScore;
    }

    public void setPassScore(Integer passScore) {
        this.passScore = passScore;
    }

    public List<ExamScoreItemDto> getExamScoreItems() {
        return examScoreItems;
    }

    public void setExamScoreItems(List<ExamScoreItemDto> examScoreItems) {
        this.examScoreItems = examScoreItems;
    }
}
