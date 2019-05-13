package com.kalix.exam.manage.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kalix.framework.core.api.web.model.BaseDTO;
import java.util.Date;

/**
 * -- 阅卷人 阅卷数统计
 * select count(a.userid), b.name, c.subject,a.examid from
 * exam_score a
 * LEFT JOIN sys_user b on a.teacherid = b.id
 * LEFT JOIN exam_create c on a.examid = c.id
 * where
 * c.name like '%基础%' and c.examstart BETWEEN '2019-04-26' and '2019-04-27'
 * GROUP BY b.name,c.subject,a.examid;
 */
public class MarkingNumCountDto extends BaseDTO {
    private Integer count; // 阅卷数
    private String name;   // 阅卷人
    private String subject; // 科目
    private String examName;   // 考试名称
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date examStart; // 考试时间

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public Date getExamStart() {
        return examStart;
    }

    public void setExamStart(Date examStart) {
        this.examStart = examStart;
    }
}
