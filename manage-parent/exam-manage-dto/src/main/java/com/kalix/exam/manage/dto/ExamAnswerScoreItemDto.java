package com.kalix.exam.manage.dto;

import com.kalix.framework.core.api.web.model.BaseDTO;

public class ExamAnswerScoreItemDto extends BaseDTO {
    private Long standerItemId; // 考题评分标准项Id
    private String standerItem; // 考题评分标准项
    private Integer itemScore;  // 每项分数

    private Long examScoreId;  // 得分表Id
    private Integer standerItemScore; // 得分
    private Integer itemDeductScore;  // 扣分

    public Long getStanderItemId() {
        return standerItemId;
    }

    public void setStanderItemId(Long standerItemId) {
        this.standerItemId = standerItemId;
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

    public Long getExamScoreId() {
        return examScoreId;
    }

    public void setExamScoreId(Long examScoreId) {
        this.examScoreId = examScoreId;
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
}
