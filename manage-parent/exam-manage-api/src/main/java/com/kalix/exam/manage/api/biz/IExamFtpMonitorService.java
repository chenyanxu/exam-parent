package com.kalix.exam.manage.api.biz;

import com.kalix.framework.core.api.IService;
import com.kalix.framework.core.api.persistence.JsonData;

public interface IExamFtpMonitorService extends IService {
    JsonData getExamFileCommitInfos(Integer page, Integer limit, String jsonStr, String sort);
    JsonData getExamIdentityPhotoInfos(Integer page, Integer limit, String jsonStr, String sort);
}
