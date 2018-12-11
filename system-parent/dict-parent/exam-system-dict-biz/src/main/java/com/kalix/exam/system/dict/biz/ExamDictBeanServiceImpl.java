package com.kalix.exam.system.dict.biz;

import com.kalix.exam.system.dict.api.biz.IExamDictBeanService;
import com.kalix.exam.system.dict.api.dao.IExamDictBeanDao;
import com.kalix.exam.system.dict.entities.ExamDictBean;
import com.kalix.framework.core.api.persistence.JsonStatus;
import com.kalix.framework.core.impl.system.BaseDictServiceImpl;

import java.util.ArrayList;
import java.util.List;

public class ExamDictBeanServiceImpl extends BaseDictServiceImpl<IExamDictBeanDao, ExamDictBean> implements IExamDictBeanService {

    /**
    @Override
    public JsonStatus saveEntity(ExamDictBean entity) {
        Integer maxValue = dao.getFieldMaxValue("value", "type='" + entity.getType() + "'");

        maxValue = maxValue + 1;

        entity.setValue(maxValue);

        return super.saveEntity(entity);
    }
**/
    @Override
    public ExamDictBean getDictBeanByTypeAndValue(String type, String value) {
        ExamDictBean examDictBean = new ExamDictBean();
        String tbName = dao.getTableName();
        String sql = "select * from %s where type='%s' and value='%s'";
        if (tbName != null) {
            sql = String.format(sql, tbName, type, value);
            List list = dao.findByNativeSql(sql, ExamDictBean.class);
            if (list.size() == 1) {
                examDictBean = (ExamDictBean) list.get(0);
            }
        }
        return examDictBean;
    }

    @Override
    public List<ExamDictBean> getDictBeanByType(String type) {
        List<ExamDictBean> list = new ArrayList<>();
        String tbName = dao.getTableName();
        String sql = "select * from %s where type='%s'";
        if (tbName != null) {
            sql = String.format(sql, tbName, type);
            list = dao.findByNativeSql(sql, ExamDictBean.class);
        }
        return list;
    }
}
