package com.kalix.exam.manage.api.biz;

import com.kalix.exam.manage.dto.ExamOrgDto;
import com.kalix.exam.manage.dto.ExamTeacherDto;
import com.kalix.exam.manage.entities.ExamTeacherBean;
import com.kalix.framework.core.api.biz.IBizService;
import com.kalix.framework.core.api.persistence.JsonData;
import com.kalix.framework.core.api.persistence.JsonStatus;

import java.util.List;
import java.util.Map;

public interface IExamTeacherBeanService extends IBizService<ExamTeacherBean> {
    /**
     * 保存分配的考生
     * @param examOrgDto
     * @return
     */
    JsonStatus saveExamTeacher(ExamOrgDto examOrgDto);

    /**
     * 通过ExamId删除数据
     * @param examId
     */
    void deleteByExamId(Long examId);

    /**
     * 获取教师所在单位的列表
     * @param examId
     * @return
     */
    Map<String, Object> getExamOrgTree(Long examId);

    /**
     * 获取所有阅卷教师
     * @param jsonStr
     * @return
     */
    JsonData getAllExamTeachers(Integer page, Integer limit, String jsonStr, String sort);

    /**
     * 通过用户Id获取阅卷教师
     * @param userId
     * @return
     */
    List<ExamTeacherBean> getTeacherByUserId(Long userId);

    /**
     * 通过用户Id获取阅卷教师及考试信息
     * @param userId
     * @return
     */
    List<ExamTeacherDto> getTeacherDtoByUserId(Long userId);
}
