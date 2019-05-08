package com.kalix.exam.manage.dto;

import com.kalix.framework.core.api.web.model.BaseDTO;

/**
 * 考试成绩维护Dto
 * select
 * a.id,
 * a.score,
 * b.id as examScoreId,
 * c.NAME as examineeName,
 * c.examcardnumber,
 * c.idcards,
 * d.name as examName,
 * d.subject,
 * d.subjectval
 * from exam_answer a
 * INNER JOIN exam_score b on a.id = b.examanswerid
 * INNER JOIN sys_user c on a.userid=c.id
 * INNER JOIN exam_create d on a.examid=d.id
 * where d.name like '%%';
 */
public class MaintainResultDto extends BaseDTO {
    private Integer score; // 考试总成绩
    private Long examScoreId; // 打分表Id
    private String examineeName; // 考生名
    private String examCardNumber; // 准考证号
    private String idCards; // 身份证号
    private String examName; // 考试名
    private String subject; // 考试科目
    private String subjectVal; // 科目编码

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Long getExamScoreId() {
        return examScoreId;
    }

    public void setExamScoreId(Long examScoreId) {
        this.examScoreId = examScoreId;
    }

    public String getExamineeName() {
        return examineeName;
    }

    public void setExamineeName(String examineeName) {
        this.examineeName = examineeName;
    }

    public String getExamCardNumber() {
        return examCardNumber;
    }

    public void setExamCardNumber(String examCardNumber) {
        this.examCardNumber = examCardNumber;
    }

    public String getIdCards() {
        return idCards;
    }

    public void setIdCards(String idCards) {
        this.idCards = idCards;
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
}
