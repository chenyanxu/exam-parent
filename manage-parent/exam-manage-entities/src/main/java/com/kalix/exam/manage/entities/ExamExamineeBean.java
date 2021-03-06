package com.kalix.exam.manage.entities;

import com.kalix.framework.core.api.persistence.PersistentEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "exam_examinee")
public class ExamExamineeBean extends PersistentEntity {
    private Long userId; // 考生Id
    private String state; // 学生考试状态
    private Date startTime; // 考试开始时间
    private Date endTime;  // 考试结束时间
    private Long examId; // 考试Id
    private Integer totalScore; // 考试成绩
    private Long orgId; // 考生所在机构Id
    private String examRoom; // 考场
    private Integer examRoomNo; // 考场坐号

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getExamRoom() {
        return examRoom;
    }

    public void setExamRoom(String examRoom) {
        this.examRoom = examRoom;
    }

    public Integer getExamRoomNo() {
        return examRoomNo;
    }

    public void setExamRoomNo(Integer examRoomNo) {
        this.examRoomNo = examRoomNo;
    }
}
