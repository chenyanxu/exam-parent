package com.kalix.exam.manage.entities;

import com.kalix.framework.core.api.persistence.PersistentEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Date;

@Entity
@Table(name = "exam_answer",uniqueConstraints = @UniqueConstraint(columnNames = {"examId","quesId","userId"}))
public class ExamAnswerBean extends PersistentEntity {
    private Long examId; // 考试Id
    private Long paperId;  // 试卷Id
    private Long quesId;  // 考题Id
    private String quesType;  // 考题类型
    private String subType;  // 考题子类型
    private String answer;  // 考生答案
    private String answerPicPath; // 考生答案图片
    private Long userId;  // 考生Id
    private Integer score;  // 得分
    private String readoverState; // 批阅状态
    private Long readoverBy;  // 初阅人
    private Long readoverSecondBy; // 复阅人
    private Long readoverThirdBy; // 终阅人（组长）
    private Date readoverOn;  // 批阅时间
    private Integer titleNum;  // 标题号
    private Integer quesNum;  // 题序号
    private String title;  // 标题
    private Integer perScore; // 每题分数

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public Long getQuesId() {
        return quesId;
    }

    public void setQuesId(Long quesId) {
        this.quesId = quesId;
    }

    public String getQuesType() {
        return quesType;
    }

    public void setQuesType(String quesType) {
        this.quesType = quesType;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswerPicPath() {
        return answerPicPath;
    }

    public void setAnswerPicPath(String answerPicPath) {
        this.answerPicPath = answerPicPath;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getReadoverState() {
        return readoverState;
    }

    public void setReadoverState(String readoverState) {
        this.readoverState = readoverState;
    }

    public Long getReadoverBy() {
        return readoverBy;
    }

    public void setReadoverBy(Long readoverBy) {
        this.readoverBy = readoverBy;
    }

    public Long getReadoverSecondBy() {
        return readoverSecondBy;
    }

    public void setReadoverSecondBy(Long readoverSecondBy) {
        this.readoverSecondBy = readoverSecondBy;
    }

    public Long getReadoverThirdBy() {
        return readoverThirdBy;
    }

    public void setReadoverThirdBy(Long readoverThirdBy) {
        this.readoverThirdBy = readoverThirdBy;
    }

    public Date getReadoverOn() {
        return readoverOn;
    }

    public void setReadoverOn(Date readoverOn) {
        this.readoverOn = readoverOn;
    }

    public Integer getTitleNum() {
        return titleNum;
    }

    public void setTitleNum(Integer titleNum) {
        this.titleNum = titleNum;
    }

    public Integer getQuesNum() {
        return quesNum;
    }

    public void setQuesNum(Integer quesNum) {
        this.quesNum = quesNum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPerScore() {
        return perScore;
    }

    public void setPerScore(Integer perScore) {
        this.perScore = perScore;
    }
}
