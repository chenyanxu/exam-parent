package com.kalix.exam.manage.dao;

import com.kalix.exam.manage.api.dao.IExamTeacherBeanDao;
import com.kalix.exam.manage.entities.ExamTeacherBean;
import com.kalix.framework.core.impl.dao.GenericDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class ExamTeacherBeanDaoImpl extends GenericDao<ExamTeacherBean, Long> implements IExamTeacherBeanDao {
    @Override
    @PersistenceContext(unitName = "exam-manage-unit")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }
}
