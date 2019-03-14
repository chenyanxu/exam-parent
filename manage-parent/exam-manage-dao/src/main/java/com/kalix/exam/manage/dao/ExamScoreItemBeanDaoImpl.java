package com.kalix.exam.manage.dao;

import com.kalix.exam.manage.api.dao.IExamScoreItemBeanDao;
import com.kalix.exam.manage.entities.ExamScoreItemBean;
import com.kalix.framework.core.impl.dao.GenericDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class ExamScoreItemBeanDaoImpl extends GenericDao<ExamScoreItemBean, Long> implements IExamScoreItemBeanDao {
    @Override
    @PersistenceContext(unitName = "exam-manage-unit")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }
}