package com.kalix.exam.system.dict.dao;

import com.kalix.exam.system.dict.api.dao.IExamDictBeanDao;
import com.kalix.exam.system.dict.entities.ExamDictBean;
import com.kalix.framework.core.impl.dao.GenericDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class ExamDictBeanDaoImpl extends GenericDao<ExamDictBean, Long> implements IExamDictBeanDao {
    @Override
    @PersistenceContext(unitName = "exam-system-dict-unit")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }
}
