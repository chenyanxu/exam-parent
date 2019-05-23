package com.kalix.middleware.excel.api.model.exam.manage;

import com.kalix.framework.core.api.web.model.BaseDTO;
import com.kalix.middleware.excel.api.annotation.ExcelField;

public class ExamineeRoomInfoDto extends BaseDTO {
    private String subjectVal; // 考试科目
    private String examStart; // 考试时间
    private String name; // 考生
    private String examCardNumber; // 准考证号
    private String idCards; // 身份证号
    private String examRoom; // 考场
    private String examRoomNo; // 考场坐号

    @ExcelField(title = "考试科目", type = 0, align = 1, dictType = "考试科目", sort = 10)
    public String getSubjectVal() {
        return subjectVal;
    }

    public void setSubjectVal(String subjectVal) {
        this.subjectVal = subjectVal;
    }

    @ExcelField(title = "考试时间", type = 0, align = 1, sort = 20)
    public String getExamStart() {
        return examStart;
    }

    public void setExamStart(String examStart) {
        this.examStart = examStart;
    }

    @ExcelField(title = "考生", type = 0, align = 1, sort = 30)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ExcelField(title = "准考证号", type = 0, align = 1, sort = 40)
    public String getExamCardNumber() {
        return examCardNumber;
    }

    public void setExamCardNumber(String examCardNumber) {
        this.examCardNumber = examCardNumber;
    }

    @ExcelField(title = "身份证号", type = 0, align = 1, sort = 50)
    public String getIdCards() {
        return idCards;
    }

    public void setIdCards(String idCards) {
        this.idCards = idCards;
    }

    @ExcelField(title = "考场", type = 0, align = 1, sort = 60)
    public String getExamRoom() {
        return examRoom;
    }

    public void setExamRoom(String examRoom) {
        this.examRoom = examRoom;
    }

    @ExcelField(title = "考场坐号", type = 0, align = 1, sort = 70)
    public String getExamRoomNo() {
        return examRoomNo;
    }

    public void setExamRoomNo(String examRoomNo) {
        this.examRoomNo = examRoomNo;
    }
}
