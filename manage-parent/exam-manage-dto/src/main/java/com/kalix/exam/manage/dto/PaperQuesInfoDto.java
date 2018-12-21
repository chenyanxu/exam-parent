package com.kalix.exam.manage.dto;

import com.kalix.framework.core.api.web.model.BaseDTO;

public class PaperQuesInfoDto extends BaseDTO {
    private Integer quesNum; // 题号
    private String stem;  // 题干
    private String answerA;
    private String answerB;
    private String answerC;
    private String answerD;
    private String answerE;
    private String answerF;
    private String quesAnswer; // 正确答案
    private String answer; // 考生答案
    private Integer score; // 得分

    public Integer getQuesNum() {
        return quesNum;
    }

    public void setQuesNum(Integer quesNum) {
        this.quesNum = quesNum;
    }

    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public String getAnswerA() {
        return answerA;
    }

    public void setAnswerA(String answerA) {
        this.answerA = answerA;
    }

    public String getAnswerB() {
        return answerB;
    }

    public void setAnswerB(String answerB) {
        this.answerB = answerB;
    }

    public String getAnswerC() {
        return answerC;
    }

    public void setAnswerC(String answerC) {
        this.answerC = answerC;
    }

    public String getAnswerD() {
        return answerD;
    }

    public String getAnswerE() {
        return answerE;
    }

    public void setAnswerE(String answerE) {
        this.answerE = answerE;
    }

    public String getAnswerF() {
        return answerF;
    }

    public void setAnswerF(String answerF) {
        this.answerF = answerF;
    }

    public void setAnswerD(String answerD) {
        this.answerD = answerD;
    }

    public String getQuesAnswer() {
        return quesAnswer;
    }

    public void setQuesAnswer(String quesAnswer) {
        this.quesAnswer = quesAnswer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
