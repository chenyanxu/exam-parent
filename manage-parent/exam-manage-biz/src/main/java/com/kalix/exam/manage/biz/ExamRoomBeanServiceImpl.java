package com.kalix.exam.manage.biz;

import com.kalix.exam.manage.api.biz.IExamRoomBeanService;
import com.kalix.exam.manage.api.dao.IExamRoomBeanDao;
import com.kalix.exam.manage.entities.ExamRoomBean;
import com.kalix.framework.core.impl.biz.ShiroGenericBizServiceImpl;

public class ExamRoomBeanServiceImpl extends ShiroGenericBizServiceImpl<IExamRoomBeanDao, ExamRoomBean> implements IExamRoomBeanService {
}
