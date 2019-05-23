package com.kalix.exam.manage.dto;

import com.kalix.framework.core.api.web.model.BaseDTO;

/**
 * 考生考场维护Dto
 * select a.id,a.examid,a.userid,c.name,c.examcardnumber,c.idcards,a.state,a.examroom,a.examroomno from exam_examinee a
 * INNER JOIN exam_create b ON a.examid = b.id
 * INNER JOIN sys_user c ON a.userid = c.id
 * where a.state = '未考'
 * and b.subjectval = '123' and b.examstart >= '2019-04-01' and b.examstart <= '2019-06-01'
 */
public class ExamineeRoomDto extends BaseDTO {
    private Long examId; // 考试Id
    private Long userId; // 考生Id
    private String name; // 考生
    private String examCardNumber; // 准考证号
    private String idCards; // 身份证号
    private String state; // 参考状态
    private String examRoom; // 考场
    private Integer examRoomNo; // 考场坐号

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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
}
