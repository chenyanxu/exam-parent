package com.kalix.exam.manage.api.biz;

import com.kalix.exam.manage.dto.*;
import com.kalix.exam.manage.entities.ExamExamineeBean;
import com.kalix.framework.core.api.biz.IBizService;
import com.kalix.framework.core.api.persistence.JsonData;
import com.kalix.framework.core.api.persistence.JsonStatus;
import com.kalix.middleware.excel.api.model.exam.manage.ExamineeRoomInfoDto;

import java.util.List;
import java.util.Map;

public interface IExamExamineeBeanService extends IBizService<ExamExamineeBean> {
    /**
     * 保存分配的考生
     * @param examOrgDto
     * @return
     */
    JsonStatus saveExaminee(ExamOrgDto examOrgDto);

    void deleteByExamId(Long examId);

    /**
     * 获取考生所在单位的列表
     * @param examId
     * @return
     */
    Map<String, Object> getExamOrgTree(Long examId);
    /**
     * 获取所有个人的考试列表
     * @return
     */
    JsonData getAllSelfExaming(String jsonStr);

    /**
     * 更新总成绩
     * @param examId
     * @param userId
     */
    void updateTotalScore(Long examId, Long userId, Integer totalScore);

    /**
     * 获取用户考试信息
     * @return
     */
    List<ExamExamineeDto> findExamineeByUser(String name, String subjectVal);

    /**
     * 获取报考科目
     * @return
     */
    List<ExamSubjectDto> getExamSubjects();

    /**
     * 获取考试素材上传列表
     * @param name
     * @param subjectVal
     * @return
     */
    List<ExamExamineeDto> getExamMaterial(String name, String subjectVal);

    /**
     * 获取考试用户信息
     * @return
     */
    JsonData getExamineeUserInfo(Long userId);

    /**
     * 获取分数段统计信息
     * @return
     */
    JsonData getFractionalStatisticsInfo(String jsonStr);

    /**
     * 获取考生考场信息
     * @param page
     * @param limit
     * @param jsonStr
     * @param sort
     * @return
     */
    JsonData getExamineeRoomsInfo(Integer page, Integer limit, String jsonStr, String sort);

    /**
     * 获取考生考场信息
     * @param page
     * @param limit
     * @param subject
     * @param startDate
     * @param state
     * @return
     */
    List<ExamineeRoomDto> getExamineeRoomsInfo(Integer page, Integer limit, String subject, String startDate, String state);

    /**
     * 修改考生考场信息
     * @param examineeRoomDto
     * @return
     */
    JsonStatus updateExamineeRoomInfo(ExamineeRoomDto examineeRoomDto);

    /**
     * 添加考生考场信息
     * @param examineeRoomDto
     * @return
     */
    JsonStatus saveExamineeRoomInfo(ExamineeRoomDto examineeRoomDto);

    /**
     * 保存导入的考场信息
     * @param examineeRoomDto
     * @return
     */
    JsonStatus saveExamineeRoomInfo(ExamineeRoomInfoDto examineeRoomDto);

    /**
     * 获取考场对照单数据列表
     * @param subjectVal
     * @param startDate
     * @return
     */
    List<ExamineeControlSheetDto> getExamineeControlSheetInfos(String subjectVal, String startDate);
}
