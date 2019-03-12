package com.kalix.exam.manage.entities;

import com.kalix.framework.core.api.persistence.PersistentEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "exam_teacher")
public class ExamTeacherBean extends PersistentEntity {
    private Long userId; // 教师Id
    private Long examId; // 考试Id
    private Long orgId; // 教师所在机构Id
    private Integer teacherType; // 0:阅卷教师 1:复审教师
    private Float scoreWeight; // 给分权重

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Integer getTeacherType() {
        return teacherType;
    }

    public void setTeacherType(Integer teacherType) {
        this.teacherType = teacherType;
    }

    public Float getScoreWeight() {
        return scoreWeight;
    }

    public void setScoreWeight(Float scoreWeight) {
        this.scoreWeight = scoreWeight;
    }
}
