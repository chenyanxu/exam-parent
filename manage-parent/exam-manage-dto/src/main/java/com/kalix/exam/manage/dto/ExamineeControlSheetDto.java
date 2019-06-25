package com.kalix.exam.manage.dto;

import com.kalix.framework.core.api.web.model.BaseDTO;
import java.util.Date;

/**
 * SELECT b.subject,b.examStart,b.duration,C.NAME,C.examcardnumber,C.idcards,A.examroom,A.examroomno FROM exam_examinee A
 * INNER JOIN exam_create b ON A.examid = b.ID INNER JOIN sys_user C ON A.userid = C.ID
 * WHERE b.subjectval = '10042' AND b.examstart = to_timestamp('2019-06-20 13:00:00','YYYY-MM-DD hh24:mi:ss')
 * 考场对照单模型
 */
public class ExamineeControlSheetDto extends BaseDTO {
    private String subject; // 考试科目
    private Date examStart; // 考试时间
    private Integer duration; // 考试时长
    private String name; // 考生姓名
    private String examCardNumber; // 准考证号
    private String idCards; // 身份证号
    private String examRoom; // 考场
    private Integer examRoomNo; // 座号
    private String photoPath; // 照片位置
    private String examTimeStr; // 考试时间串

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Date getExamStart() {
        return examStart;
    }

    public void setExamStart(Date examStart) {
        this.examStart = examStart;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getExamRoom() {
        return examRoom;
    }

    public void setExamRoom(String examRoom) {
        this.examRoom = examRoom;
    }

    public Integer getExamRoomNo() {
        return examRoomNo;
    }

    public void setExamRoomNo(Integer examRoomNo) {
        this.examRoomNo = examRoomNo;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getExamTimeStr() {
        return examTimeStr;
    }

    public void setExamTimeStr(String examTimeStr) {
        this.examTimeStr = examTimeStr;
    }
}
