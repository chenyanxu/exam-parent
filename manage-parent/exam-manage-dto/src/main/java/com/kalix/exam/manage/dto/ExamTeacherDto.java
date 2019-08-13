package com.kalix.exam.manage.dto;

import com.kalix.framework.core.api.web.model.BaseDTO;

import java.util.Date;

public class ExamTeacherDto extends BaseDTO {
    private String name; // 教师姓名
    private Long userId; // 教师Id
    private Long examId; // 考试Id
    private String examName; // 考试名称
    private String subject; // 考试科目
    private String subjectVal; // 考试科目编号
    private Long orgId; // 教师所在机构Id
    private String teacherType; // 0:阅卷教师 1:复审教师
    private String teacherTypeName; // 教师类型描述
    private Float scoreWeight; // 给分权重
    private Date examStart; // 考试开始时间

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public String getSubjectVal() {
        return subjectVal;
    }

    public void setSubjectVal(String subjectVal) {
        this.subjectVal = subjectVal;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getTeacherType() {
        return teacherType;
    }

    public void setTeacherType(String teacherType) {
        this.teacherType = teacherType;
    }

    public String getTeacherTypeName() {
        return teacherTypeName;
    }

    public void setTeacherTypeName(String teacherTypeName) {
        this.teacherTypeName = teacherTypeName;
    }

    public Float getScoreWeight() {
        return scoreWeight;
    }

    public void setScoreWeight(Float scoreWeight) {
        this.scoreWeight = scoreWeight;
    }

    public Date getExamStart() {
        return examStart;
    }

    public void setExamStart(Date examStart) {
        this.examStart = examStart;
    }
}
