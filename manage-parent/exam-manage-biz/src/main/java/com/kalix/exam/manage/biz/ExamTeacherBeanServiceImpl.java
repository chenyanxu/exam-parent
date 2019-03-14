package com.kalix.exam.manage.biz;

import com.kalix.admin.core.api.biz.IOrganizationBeanService;
import com.kalix.admin.core.api.biz.IUserBeanService;
import com.kalix.admin.core.dto.model.OrganizationDTO;
import com.kalix.admin.core.entities.UserBean;
import com.kalix.exam.manage.api.biz.IExamCreateBeanService;
import com.kalix.exam.manage.api.biz.IExamTeacherBeanService;
import com.kalix.exam.manage.api.dao.IExamTeacherBeanDao;
import com.kalix.exam.manage.dto.ExamOrgDto;
import com.kalix.exam.manage.dto.ExamTeacherDto;
import com.kalix.exam.manage.entities.ExamCreateBean;
import com.kalix.exam.manage.entities.ExamTeacherBean;
import com.kalix.framework.core.api.persistence.JsonData;
import com.kalix.framework.core.api.persistence.JsonStatus;
import com.kalix.framework.core.impl.biz.ShiroGenericBizServiceImpl;
import com.kalix.framework.core.util.SerializeUtil;

import java.util.*;

public class ExamTeacherBeanServiceImpl extends ShiroGenericBizServiceImpl<IExamTeacherBeanDao,ExamTeacherBean> implements IExamTeacherBeanService {

    private IUserBeanService userBeanService;
    private IExamCreateBeanService examCreateBeanService;
    private IOrganizationBeanService organizationBeanService;

    public void setUserBeanService(IUserBeanService userBeanService) {
        this.userBeanService = userBeanService;
    }

    public void setExamCreateBeanService(IExamCreateBeanService examCreateBeanService) {
        this.examCreateBeanService = examCreateBeanService;
    }

    public void setOrganizationBeanService(IOrganizationBeanService organizationBeanService) {
        this.organizationBeanService = organizationBeanService;
    }

    @Override
    public JsonStatus saveExamTeacher(ExamOrgDto examOrgDto) {
        JsonStatus jsonStatus = new JsonStatus();
        Long examId = examOrgDto.getExamId();
        // 验证examId及orgIds
        String orgIds = examOrgDto.getOrgIds();
        if (examId == null || orgIds == null || orgIds.isEmpty()) {
            jsonStatus.setMsg("考试信息为空或者参加考试单位信息为空");
            jsonStatus.setFailure(true);
            return jsonStatus;
        }
        try {
            // 批量插入教师信息
            List<ExamTeacherBean> examTeachers = new ArrayList<>();
            List<Long> userIds = new ArrayList<>();
            if (orgIds.indexOf(",") != -1) {
                String[] orgIdArr = orgIds.split(",");
                if (orgIdArr.length > 0) {
                    for (String orgId : orgIdArr) {
                        if (orgId != null) {
                            setTeacherUserId(examId, orgId, userIds, examTeachers);
                        }
                    }
                }
            } else {
                setTeacherUserId(examId, orgIds, userIds, examTeachers);
            }
            if (examTeachers.size() > 0) {
                // 删除examId相关信息
                deleteByExamId(examId);
                // 批量添加分配的考生
                dao.addBatch(examTeachers);
                // 修改考试分配状态
                updateDistributeStat(examId);

                jsonStatus.setMsg("教师分配成功");
                jsonStatus.setSuccess(true);
            } else {
                jsonStatus.setMsg("请先在考生所在单位添加教师");
                jsonStatus.setFailure(true);
            }
        } catch(Exception e) {
            jsonStatus.setMsg("教师分配失败");
            jsonStatus.setFailure(true);
        }
        return jsonStatus;
    }

    @Override
    public void deleteByExamId(Long examId) {
        dao.updateNativeQuery("delete from exam_teacher where examid=" + examId);
    }

    private void setTeacherUserId(Long examId, String orgId, List<Long> userIds, List<ExamTeacherBean> examTeachers) {
        List<UserBean> users = userBeanService.findUserByOrgId(Long.parseLong(orgId.trim()));
        Long currentUserId = shiroService.getCurrentUserId();
        if (users != null) {
            ExamTeacherBean examTeacherBean = null;
            for (UserBean user : users) {
                if (!userIds.contains(user.getId())) {
                    userIds.add(user.getId());
                    examTeacherBean = new ExamTeacherBean();
                    examTeacherBean.setExamId(examId);
                    examTeacherBean.setUserId(user.getId());
                    examTeacherBean.setOrgId(Long.parseLong(orgId));
                    examTeacherBean.setCreateById(currentUserId);
                    examTeacherBean.setUpdateById(currentUserId);
                    examTeacherBean.setCreationDate(new Date());
                    examTeachers.add(examTeacherBean);
                }
            }
        }
    }

    private void updateDistributeStat(Long examId) {
        ExamCreateBean examCreateBean = examCreateBeanService.getEntity(examId);
        examCreateBean.setTeacherDistribute("已分配");
        examCreateBeanService.updateEntity(examCreateBean);
    }

    private List<Long> getOrgIdsByExamId(Long examId) {
        return dao.findByNativeSql("select orgid from exam_teacher where examid=?", Long.class, examId);
    }

    @Override
    public Map<String, Object> getExamOrgTree(Long examId) {
        OrganizationDTO organizationDTO = organizationBeanService.getAllOrg(null);
        List<Long> orgIds = getOrgIdsByExamId(examId);
        Map<String, Object> map = new HashMap<>();
        map.put("treeData", organizationDTO);
        map.put("orgIds", orgIds);
        return map;
    }

    @Override
    public JsonData getAllExamTeachers(Integer page, Integer limit, String jsonStr, String sort) {
        Map<String, String> jsonMap = SerializeUtil.json2Map(jsonStr);
        String name = jsonMap.get("%name%");
        List<ExamTeacherDto> examTeacherDtoList = getAllExamTeachers(page, limit, name);
        Integer count = getAllExamTeachersCount(page, limit, name);
        return getResult(examTeacherDtoList, count);
    }

    @Override
    public List<ExamTeacherBean> getTeacherByUserId(Long userId) {
        String sql = "select examId,teacherType,scoreWeight from exam_teacher where userId=" + userId;
        return dao.findByNativeSql(sql, ExamTeacherBean.class);
    }

    @Override
    public List<ExamTeacherDto> getTeacherDtoByUserId(Long userId) {
        String sql = "select a.id,c.name,a.userid,a.examid,b.name as examName,b.subject,b.subjectval,a.orgid," +
                " a.teachertype,d.label as teacherTypeName,a.scoreweight" +
                " from exam_teacher a " +
                " left JOIN exam_create b on a.examid = b.id" +
                " left JOIN sys_user c on c.id = a.userid" +
                " left JOIN exam_dict d on a.teachertype = d.value and d.type='阅卷教师'" +
                " where a.userid=" + userId;
        List<ExamTeacherDto> ExamTeacherDtoList = dao.findByNativeSql(sql, ExamTeacherDto.class);
        return ExamTeacherDtoList;
    }

    private List<ExamTeacherDto> getAllExamTeachers(Integer page, Integer limit, String name) {
        int offset = 0;
        if (page != null && limit != null && page > 0 && limit > 0) {
            offset = (page - 1) * limit;
        }
        String sql = "select a.id,c.name,a.userid,a.examid,b.name as examName,b.subject,b.subjectval,a.orgid," +
                " a.teachertype,d.label as teacherTypeName,a.scoreweight" +
                " from exam_teacher a " +
                " left JOIN exam_create b on a.examid = b.id" +
                " left JOIN sys_user c on c.id = a.userid" +
                " left JOIN exam_dict d on a.teachertype = d.value and d.type='阅卷教师'";
        if (name != null && !name.isEmpty()) {
            sql += " where c.name like'%"+name+"%'";
        }
        if (page != null && limit != null) {
            sql += " limit " + limit + " offset " + offset;
        }
        List<ExamTeacherDto> ExamTeacherDtoList = dao.findByNativeSql(sql, ExamTeacherDto.class);
        return ExamTeacherDtoList;
    }

    private Integer getAllExamTeachersCount(Integer page, Integer limit, String name) {
        String sql = "select count(1) " +
                " from exam_teacher a " +
                " left JOIN exam_create b on a.examid = b.id" +
                " left JOIN sys_user c on c.id = a.userid" +
                " left JOIN exam_dict d on a.teachertype = d.value and d.type='阅卷教师'";
        if (name != null && !name.isEmpty()) {
            sql += " where c.name like'%"+name+"%'";
        }
        List<Integer> list = dao.findByNativeSql(sql, Integer.class);
        if (list == null) {
            return 0;
        }
        return list.get(0);
    }

    private JsonData getResult(List<?> list, int count) {
        JsonData jsonData = new JsonData();
        if (list == null) {
            jsonData.setTotalCount(0L);
        } else {
            jsonData.setTotalCount(Long.valueOf(count));
        }
        jsonData.setData(list);
        return jsonData;
    }
}
