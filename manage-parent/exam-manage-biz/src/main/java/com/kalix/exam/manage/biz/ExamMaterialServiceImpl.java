package com.kalix.exam.manage.biz;

import com.kalix.exam.manage.api.biz.IExamAnswerBeanService;
import com.kalix.exam.manage.api.biz.IExamExamineeBeanService;
import com.kalix.exam.manage.api.biz.IExamMaterialService;
import com.kalix.exam.manage.dto.ExamExamineeDto;
import com.kalix.exam.manage.dto.ExamQuesAttachmentDto;
import com.kalix.framework.core.api.persistence.JsonData;
import com.kalix.framework.core.util.SerializeUtil;

import java.util.List;
import java.util.Map;

public class ExamMaterialServiceImpl implements IExamMaterialService {
    private IExamAnswerBeanService examAnswerBeanService;

    public void setExamAnswerBeanService(IExamAnswerBeanService examAnswerBeanService) {
        this.examAnswerBeanService = examAnswerBeanService;
    }
//    private IExamExamineeBeanService examExamineeBeanService;
//
//    public void setExamExamineeBeanService(IExamExamineeBeanService examExamineeBeanService) {
//        this.examExamineeBeanService = examExamineeBeanService;
//    }

    @Override
    public JsonData getExamMaterialCounts(String jsonStr) {
        Map<String, String> jsonMap = SerializeUtil.json2Map(jsonStr);
        String subjectCode = jsonMap.get("id");
        String name = jsonMap.get("%name%");
//        List<ExamExamineeDto> examExamineeList = examExamineeBeanService.getExamMaterial(name, subjectCode);
        List<ExamQuesAttachmentDto> examQuesAttachmentList = examAnswerBeanService.getQuesAnswerMaterial(name, subjectCode);
        return getResult(examQuesAttachmentList);
    }

    private JsonData getResult(List<?> list) {
        JsonData jsonData = new JsonData();
        if (list == null) {
            jsonData.setTotalCount(0L);
        } else {
            jsonData.setTotalCount(Long.valueOf(list.size()));
        }
        jsonData.setData(list);
        return jsonData;
    }
}
