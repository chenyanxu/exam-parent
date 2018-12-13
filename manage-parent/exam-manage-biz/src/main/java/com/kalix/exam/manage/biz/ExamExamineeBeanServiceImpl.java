package com.kalix.exam.manage.biz;

import com.kalix.admin.core.api.biz.IUserBeanService;
import com.kalix.admin.core.entities.UserBean;
import com.kalix.exam.manage.api.biz.IExamExamineeBeanService;
import com.kalix.exam.manage.api.dao.IExamExamineeBeanDao;
import com.kalix.exam.manage.dto.ExamOrgDto;
import com.kalix.exam.manage.entities.ExamExamineeBean;
import com.kalix.framework.core.api.persistence.JsonStatus;
import com.kalix.framework.core.impl.biz.GenericBizServiceImpl;

import javax.persistence.Transient;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ExamExamineeBeanServiceImpl extends GenericBizServiceImpl<IExamExamineeBeanDao,ExamExamineeBean> implements IExamExamineeBeanService {

    private IUserBeanService userBeanService;

    public void setUserBeanService(IUserBeanService userBeanService) {
        this.userBeanService = userBeanService;
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
            // 删除examId相关信息
            deleteByExamId(examId);

            // 批量插入考生信息
            if (orgIds.indexOf(",") != -1) {
                String[] orgIdArr = orgIds.split(",");

                if (orgIdArr.length > 0) {
                    for (String orgId : orgIdArr) {
                        if (orgId != null) {
                            List<UserBean> users = userBeanService.findUserByOrgId(Long.parseLong(orgId.trim()));
                            List<Long> userIds = users.stream().map((u)->u.getId()).collect(Collectors.toList());

                        }
                    }
                }
            }
            jsonStatus.setMsg("考生分配成功");
            jsonStatus.setSuccess(true);
        } catch(Exception e) {
            jsonStatus.setMsg("考生分配失败");
            jsonStatus.setSuccess(true);
        }
        return jsonStatus;
    }

    @Override
    public void deleteByExamId(Long examId) {
        dao.updateNativeQuery("delete from exam_examinee where examid=" + examId);
    }
}
