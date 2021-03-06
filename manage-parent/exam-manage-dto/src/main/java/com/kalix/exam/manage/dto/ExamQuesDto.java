package com.kalix.exam.manage.dto;

import com.kalix.framework.core.api.web.model.BaseDTO;

public class ExamQuesDto extends BaseDTO {
    // 考题标题
    private String title;
    // 考题标题序号
    private Integer titleNum;
    // 每题分数
    private Integer perScore;
    // 试题Id
    private Long quesid;
    // 试题序号
    private Integer quesNum;
    // 试题类型
    private String quesType;
    // 试题子类型
    private String subType;
    // 考题答案
    private String answer;
    // 考题答案图片路径
    private String answerPicPath;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTitleNum() {
        return titleNum;
    }

    public void setTitleNum(Integer titleNum) {
        this.titleNum = titleNum;
    }

    public Integer getPerScore() {
        return perScore;
    }

    public void setPerScore(Integer perScore) {
        this.perScore = perScore;
    }

    public Long getQuesid() {
        return quesid;
    }

    public void setQuesid(Long quesid) {
        this.quesid = quesid;
    }

    public Integer getQuesNum() {
        return quesNum;
    }

    public void setQuesNum(Integer quesNum) {
        this.quesNum = quesNum;
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
}
