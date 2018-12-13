package com.kalix.exam.manage.dao;

import com.kalix.exam.manage.api.dao.IExamExamineeBeanDao;
import com.kalix.exam.manage.entities.ExamExamineeBean;
import com.kalix.framework.core.impl.dao.GenericDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class ExamExamineeBeanDaoImpl extends GenericDao<ExamExamineeBean, Long> implements IExamExamineeBeanDao {
    @Override
    @PersistenceContext(unitName = "exam-manage-unit")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }
}
