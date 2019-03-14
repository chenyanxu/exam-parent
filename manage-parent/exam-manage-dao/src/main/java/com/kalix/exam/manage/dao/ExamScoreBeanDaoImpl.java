package com.kalix.exam.manage.dao;

import com.kalix.exam.manage.api.dao.IExamScoreBeanDao;
import com.kalix.exam.manage.entities.ExamScoreBean;
import com.kalix.framework.core.impl.dao.GenericDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class ExamScoreBeanDaoImpl extends GenericDao<ExamScoreBean, Long> implements IExamScoreBeanDao {
    @Override
    @PersistenceContext(unitName = "exam-manage-unit")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }
}