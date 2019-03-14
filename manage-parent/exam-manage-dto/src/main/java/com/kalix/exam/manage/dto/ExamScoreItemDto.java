package com.kalix.exam.manage.dto;

import com.kalix.framework.core.api.web.model.BaseDTO;

public class ExamScoreItemDto extends BaseDTO {
    private Long examScoreId; // exam_score表Id
    private Long quesId;  // 试题Id
    private Long standerItemId;  // 试题标准项Id
    private Integer standerItemScore;  // 得分
    private Integer itemDeductScore;   // 扣分

    public Long getExamScoreId() {
        return examScoreId;
    }

    public void setExamScoreId(Long examScoreId) {
        this.examScoreId = examScoreId;
    }

    public Long getQuesId() {
        return quesId;
    }

    public void setQuesId(Long quesId) {
        this.quesId = quesId;
    }

    public Long getStanderItemId() {
        return standerItemId;
    }

    public void setStanderItemId(Long standerItemId) {
        this.standerItemId = standerItemId;
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
