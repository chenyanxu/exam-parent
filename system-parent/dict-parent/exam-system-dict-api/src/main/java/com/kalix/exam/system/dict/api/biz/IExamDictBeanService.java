package com.kalix.exam.system.dict.api.biz;

import com.kalix.exam.system.dict.entities.ExamDictBean;
import com.kalix.framework.core.api.system.IDictBeanService;

import java.util.List;

/**
 * @类描述：字典dao接口类
 * @创建人：yz
 * @创建时间：2018-12-11 10:05
 * @修改人：
 * @修改时间：
 * @修改备注：
 */
public interface IExamDictBeanService extends IDictBeanService<ExamDictBean> {
    ExamDictBean getDictBeanByTypeAndValue(String type, String value);
    List<ExamDictBean> getDictBeanByType(String type);
}
