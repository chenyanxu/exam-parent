package com.kalix.exam.manage.biz;

import com.kalix.exam.manage.api.biz.IExamExamineeBeanService;
import com.kalix.exam.manage.api.biz.IExamScoreQueryService;
import com.kalix.exam.manage.dto.ExamExamineeDto;
import com.kalix.exam.manage.dto.ExamSubjectDto;
import com.kalix.framework.core.api.persistence.JsonData;
import com.kalix.framework.core.util.SerializeUtil;

import java.util.List;
import java.util.Map;

public class ExamScoreQueryServiceImpl implements IExamScoreQueryService {
    private IExamExamineeBeanService examExamineeBeanService;

    public void setExamExamineeBeanService(IExamExamineeBeanService examExamineeBeanService) {
        this.examExamineeBeanService = examExamineeBeanService;
    }

    @Override
    public JsonData getExamScore(String jsonStr) {
        Map<String, String> jsonMap = SerializeUtil.json2Map(jsonStr);
        String subjectCode = jsonMap.get("subjectCode");
        String name = jsonMap.get("%name%");
        List<ExamExamineeDto> examExamineeList = examExamineeBeanService.findExamineeByUser(name, subjectCode);
        return getResult(examExamineeList);
    }

    @Override
    public JsonData getExamSubjects() {
        List<ExamSubjectDto> examSubjectList = examExamineeBeanService.getExamSubjects();
        return getResult(examSubjectList);
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
