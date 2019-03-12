package com.kalix.middleware.excel.api.model.exam.manage;

import com.kalix.framework.core.api.web.model.BaseDTO;
import com.kalix.middleware.excel.api.annotation.ExcelField;

/**
 * Created by zangyanming on 2018/9/13.
 */

public class ExamRoomDto extends BaseDTO {
    private String subjectVal;     // 考试科目，字典[考试科目]
    private String room;           // 考场编号，字典[考场编号]
    private String seatNo;         // 座位号
    private String examCardNumber; // 准考证号
    private String idCards;        // 身份证号
    private String name;           // 姓名
    private String attend;         // 成绩备注，字典[成绩备注]
    private String remarks;        // 备注

    @ExcelField(title = "考试科目", type = 0, align = 1, dictType = "考试科目", sort = 10)
    public String getSubjectVal() {
        return subjectVal;
    }

    public void setSubjectVal(String subjectVal) {
        this.subjectVal = subjectVal;
    }

    @ExcelField(title = "考场编号", type = 0, align = 1, dictType = "考场编号", sort = 20)
    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    @ExcelField(title = "座位号", type = 0, align = 1, sort = 30)
    public String getSeatNo() {
        return seatNo;
    }

    public void setSeatNo(String seatNo) {
        this.seatNo = seatNo;
    }

    @ExcelField(title = "姓名", type = 0, align = 1, sort = 40)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ExcelField(title = "准考证号", type = 0, align = 1, sort = 50)
    public String getExamCardNumber() {
        return examCardNumber;
    }

    public void setExamCardNumber(String examCardNumber) {
        this.examCardNumber = examCardNumber;
    }

    @ExcelField(title = "身份证号", type = 0, align = 1, sort = 60)
    public String getIdCards() {
        return idCards;
    }

    public void setIdCards(String idCards) {
        this.idCards = idCards;
    }

    @ExcelField(title = "成绩备注", type = 0, align = 1, sort = 70)
    public String getAttend() {
        return attend;
    }

    public void setAttend(String attend) {
        this.attend = attend;
    }

    @ExcelField(title = "备注", type = 0, align = 1, sort = 80)
    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
