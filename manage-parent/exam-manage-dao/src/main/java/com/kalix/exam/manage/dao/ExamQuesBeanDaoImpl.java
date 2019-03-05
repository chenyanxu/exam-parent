package com.kalix.exam.manage.dao;

import com.kalix.exam.manage.api.dao.IExamQuesBeanDao;
import com.kalix.exam.manage.entities.ExamQuesBean;
import com.kalix.framework.core.impl.dao.GenericDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class ExamQuesBeanDaoImpl extends GenericDao<ExamQuesBean, Long> implements IExamQuesBeanDao {
    @Override
    @PersistenceContext(unitName = "exam-manage-unit")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }
}
