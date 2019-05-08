package com.kalix.exam.manage.dto;

import com.kalix.framework.core.api.web.model.BaseDTO;

/**
 * 考试成绩打分项维护Dto
 * select
 * a.id,
 * a.standeritemscore,
 * a.itemdeductscore,
 * b.itemscore,
 * b.standeritem,
 * c.stem
 * from exam_score_item a
 * LEFT JOIN enrolment_question_scorestandar b on b.id=a.standeritemid
 * LEFT JOIN enrolment_question_subject c on c.id=a.quesid
 * where a.examscoreid = 1;
 */
public class MaintainResultItemDto extends BaseDTO {
    private String stem; // 题干
    private String standerItem; // 评分项
    private Integer itemScore; // 总分
    private Integer standerItemScore; // 打分
    private Integer itemDeductScore; // 扣分
    private Long examScoreId; // 打分表Id

    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
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

    public Integer getStanderItemScore() {
        return standerItemScore;
    }

    public void setStanderItemScore(Integer standerItemScore) {
        this.standerItemScore = standerItemScore;
    }

    public Integer getItemDeductScore() {
        return itemDeductScore;
    }

    public void setItemDeductScore(Integer itemDeductScore) {
        this.itemDeductScore = itemDeductScore;
    }

    public Long getExamScoreId() {
        return examScoreId;
    }

    public void setExamScoreId(Long examScoreId) {
        this.examScoreId = examScoreId;
    }
}
