package com.kalix.exam.manage.dao;

import com.kalix.exam.manage.api.dao.IExamAnswerBeanDao;
import com.kalix.exam.manage.entities.ExamAnswerBean;
import com.kalix.framework.core.impl.dao.GenericDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class ExamAnswerBeanDaoImpl extends GenericDao<ExamAnswerBean, Long> implements IExamAnswerBeanDao {
    @Override
    @PersistenceContext(unitName = "exam-manage-unit")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }
}
