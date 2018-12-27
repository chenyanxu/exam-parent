package com.kalix.exam.manage.api.biz;

import com.kalix.framework.core.api.IService;
import com.kalix.framework.core.api.persistence.JsonData;

public interface IExamMaterialService  extends IService {

    /**
     * 统计素材上传数的考试列表
     * @return
     */
    JsonData getExamMaterialCounts(String jsonStr);

}
