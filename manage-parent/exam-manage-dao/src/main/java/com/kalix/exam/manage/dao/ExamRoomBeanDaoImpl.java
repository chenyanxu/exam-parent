package com.kalix.exam.manage.dao;

import com.kalix.exam.manage.api.dao.IExamRoomBeanDao;
import com.kalix.exam.manage.entities.ExamRoomBean;
import com.kalix.framework.core.impl.dao.GenericDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class ExamRoomBeanDaoImpl extends GenericDao<ExamRoomBean, Long> implements IExamRoomBeanDao {
    @Override
    @PersistenceContext(unitName = "exam-manage-unit")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }
}
