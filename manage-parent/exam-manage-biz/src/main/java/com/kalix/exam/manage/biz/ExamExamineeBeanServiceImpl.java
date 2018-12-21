package com.kalix.exam.manage.biz;

import com.kalix.admin.core.api.biz.IOrganizationBeanService;
import com.kalix.admin.core.api.biz.IUserBeanService;
import com.kalix.admin.core.dto.model.OrganizationDTO;
import com.kalix.admin.core.entities.UserBean;
import com.kalix.exam.manage.api.biz.IExamCreateBeanService;
import com.kalix.exam.manage.api.biz.IExamExamineeBeanService;
import com.kalix.exam.manage.api.dao.IExamExamineeBeanDao;
import com.kalix.exam.manage.dto.ExamExamineeDto;
import com.kalix.exam.manage.dto.ExamOrgDto;
import com.kalix.exam.manage.dto.ExamSubjectDto;
import com.kalix.exam.manage.entities.ExamCreateBean;
import com.kalix.exam.manage.entities.ExamExamineeBean;
import com.kalix.framework.core.api.persistence.JsonData;
import com.kalix.framework.core.api.persistence.JsonStatus;
import com.kalix.framework.core.impl.biz.ShiroGenericBizServiceImpl;
import com.kalix.framework.core.util.SerializeUtil;

import javax.persistence.Transient;
import java.util.*;

public class ExamExamineeBeanServiceImpl extends ShiroGenericBizServiceImpl<IExamExamineeBeanDao,ExamExamineeBean> implements IExamExamineeBeanService {

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
    @Transient
    public JsonStatus saveExaminee(ExamOrgDto examOrgDto) {
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
            // 批量插入考生信息
            List<ExamExamineeBean> examExamineans = new ArrayList<>();
            List<Long> userIds = new ArrayList<>();

            if (orgIds.indexOf(",") != -1) {
                String[] orgIdArr = orgIds.split(",");
                if (orgIdArr.length > 0) {
                    for (String orgId : orgIdArr) {
                        if (orgId != null) {
                            setExamineeUserId(examId, orgId, userIds, examExamineans);
                        }
                    }
                }
            } else {
                setExamineeUserId(examId, orgIds, userIds, examExamineans);
            }
            if (examExamineans.size() > 0) {
                // 删除examId相关信息
                deleteByExamId(examId);
                // 批量添加分配的考生
                dao.addBatch(examExamineans);
                // 修改考试分配状态
                updateDistributeStat(examId);
                // 发通知消息

                jsonStatus.setMsg("考生分配成功");
                jsonStatus.setSuccess(true);
            } else {
                jsonStatus.setMsg("请先在考生所在单位添加考生");
                jsonStatus.setFailure(true);
            }
        } catch(Exception e) {
            jsonStatus.setMsg("考生分配失败");
            jsonStatus.setFailure(true);
        }
        return jsonStatus;
    }

    private void setExamineeUserId(Long examId, String orgId, List<Long> userIds, List<ExamExamineeBean> examExamineans) {
        List<UserBean> users = userBeanService.findUserByOrgId(Long.parseLong(orgId.trim()));
        Long currentUserId = shiroService.getCurrentUserId();
        if (users != null) {
            ExamExamineeBean examExamineeBean = null;
            for (UserBean user : users) {
                if (!userIds.contains(user.getId())) {
                    userIds.add(user.getId());
                    examExamineeBean = new ExamExamineeBean();
                    examExamineeBean.setExamId(examId);
                    // 0:未考 1:已考
                    examExamineeBean.setState("未考");
                    examExamineeBean.setUserId(user.getId());
                    examExamineeBean.setOrgId(Long.parseLong(orgId));
                    examExamineeBean.setCreateById(currentUserId);
                    examExamineeBean.setUpdateById(currentUserId);
                    examExamineeBean.setCreationDate(new Date());
                    examExamineans.add(examExamineeBean);
                }
            }
        }
    }

    @Override
    public void deleteByExamId(Long examId) {
        dao.updateNativeQuery("delete from exam_examinee where examid=" + examId);
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
    public JsonData getAllSelfExaming(String jsonStr) {
        String name = "";
        if (jsonStr != null && !jsonStr.isEmpty()) {
            Map<String, Object> jsonMap = SerializeUtil.jsonToMap(jsonStr);
            name = (String)jsonMap.get("%name%");
        }
        Long currentUserId = shiroService.getCurrentUserId();
        String sql = "select a.id,a.name,a.subject,a.duration,a.paperId " +
                " from exam_create a,exam_examinee b " +
                " where b.examid=a.id  and  b.userid=?" +
                " and b.state='未考'";
        if (name != null && !name.isEmpty()) {
            sql += " and a.name like'%"+name+"%'";
        }
        List<ExamCreateBean> examings = dao.findByNativeSql(sql, ExamCreateBean.class, currentUserId);
        JsonData jsonData = new JsonData();
        if (examings == null) {
            jsonData.setTotalCount(0L);
        } else {
            jsonData.setTotalCount(Long.valueOf(examings.size()));
        }
        jsonData.setData(examings);
        return jsonData;
    }

    @Override
    public void updateTotalScore(Long examId, Long userId, Integer totalScore) {
        String sql = "update exam_examinee set totalScore=" + totalScore
                + " where examId=" + examId + " and userId=" + userId + " and state='已考'";
        dao.updateNativeQuery(sql);
    }

    @Override
    public List<ExamExamineeDto> findExamineeByUser(String name, String subjectVal) {
        Long userId = shiroService.getCurrentUserId();
        String sql = "select a.userId,a.examId,a.totalScore,b.name,b.subject,b.subjectVal,b.paperId " +
                "from exam_examinee a,exam_create b where a.examId=b.id and a.userId=" + userId +
                " and a.state='已考'";
        if (name != null && name.trim().length() > 0) {
            sql += " and b.name like'%"+name+"%'";
        }
        if (subjectVal != null && subjectVal.trim().length() > 0) {
            sql += " and b.subjectVal = '"+subjectVal+"'";
        }
        return dao.findByNativeSql(sql, ExamExamineeDto.class);
    }

    @Override
    public List<ExamSubjectDto> getExamSubjects() {
        Long userId = shiroService.getCurrentUserId();
        String sql = "select distinct b.subject as text,b.subjectVal as id" +
                " from exam_examinee a,exam_create b where a.examId=b.id and a.userId=" + userId;
        return dao.findByNativeSql(sql, ExamSubjectDto.class);
    }

    private void updateDistributeStat(Long examId) {
        ExamCreateBean examCreateBean = examCreateBeanService.getEntity(examId);
        examCreateBean.setDistributeStat("已分配");
        examCreateBeanService.updateEntity(examCreateBean);
    }

    private List<Long> getOrgIdsByExamId(Long examId) {
        return dao.findByNativeSql("select orgid from exam_examinee where examid=?", Long.class, examId);
    }
}
