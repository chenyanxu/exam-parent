package com.kalix.exam.manage.biz;

import com.kalix.exam.manage.api.biz.IExamAnswerBeanService;
import com.kalix.exam.manage.api.dao.IExamAnswerBeanDao;
import com.kalix.exam.manage.entities.ExamAnswerBean;
import com.kalix.framework.core.impl.biz.ShiroGenericBizServiceImpl;

public class ExamAnswerBeanServiceImpl extends ShiroGenericBizServiceImpl<IExamAnswerBeanDao, ExamAnswerBean> implements IExamAnswerBeanService {
}
