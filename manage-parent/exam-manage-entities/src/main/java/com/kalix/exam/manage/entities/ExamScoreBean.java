package com.kalix.exam.manage.entities;

import com.kalix.framework.core.api.persistence.PersistentEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "exam_score")
public class ExamScoreBean extends PersistentEntity {
    private Long examId; // 考试Id
    private Long userId; // 考生Id
    private Long examAnswerId; // 考试答案表Id
    private Long teacherId; // 阅卷人Id
    private String teacherType; // 阅卷人分类
    private Integer score; // 打分

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
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

    public String getTeacherType() {
        return teacherType;
    }

    public void setTeacherType(String teacherType) {
        this.teacherType = teacherType;
    }
}
