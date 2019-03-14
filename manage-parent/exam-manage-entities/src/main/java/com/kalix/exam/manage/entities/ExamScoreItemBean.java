package com.kalix.exam.manage.entities;

import com.kalix.framework.core.api.persistence.PersistentEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "exam_score_item")
public class ExamScoreItemBean extends PersistentEntity {
    private Long examScoreId;
    private Long quesId;
    private Long standerItemId;
    private Integer standerItemScore;
    private Integer itemDeductScore;

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
