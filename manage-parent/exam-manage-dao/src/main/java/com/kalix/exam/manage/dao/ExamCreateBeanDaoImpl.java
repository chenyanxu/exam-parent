package com.kalix.exam.manage.dao;

import com.kalix.exam.manage.api.dao.IExamCreateBeanDao;
import com.kalix.exam.manage.entities.ExamCreateBean;
import com.kalix.framework.core.impl.dao.GenericDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class ExamCreateBeanDaoImpl extends GenericDao<ExamCreateBean, Long> implements IExamCreateBeanDao {
    @Override
    @PersistenceContext(unitName = "exam-manage-unit")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }
}
