package com.kalix.exam.manage.biz;

import com.kalix.exam.manage.api.biz.IExamAnswerBeanService;
import com.kalix.exam.manage.api.biz.IExamExamineeBeanService;
import com.kalix.exam.manage.api.biz.IExamScoreQueryService;
import com.kalix.exam.manage.dto.*;
import com.kalix.framework.core.api.persistence.JsonData;
import com.kalix.framework.core.util.SerializeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExamScoreQueryServiceImpl implements IExamScoreQueryService {
    private IExamExamineeBeanService examExamineeBeanService;
    private IExamAnswerBeanService examAnswerBeanService;

    public void setExamExamineeBeanService(IExamExamineeBeanService examExamineeBeanService) {
        this.examExamineeBeanService = examExamineeBeanService;
    }

    public void setExamAnswerBeanService(IExamAnswerBeanService examAnswerBeanService) {
        this.examAnswerBeanService = examAnswerBeanService;
    }

    @Override
    public JsonData getExamScore(String jsonStr) {
        Map<String, String> jsonMap = SerializeUtil.json2Map(jsonStr);
        String subjectCode = jsonMap.get("id");
        String name = jsonMap.get("%name%");
        List<ExamExamineeDto> examExamineeList = examExamineeBeanService.findExamineeByUser(name, subjectCode);
        return getResult(examExamineeList);
    }

    @Override
    public JsonData getExamSubjects() {
        List<ExamSubjectDto> examSubjectList = examExamineeBeanService.getExamSubjects();
        return getResult(examSubjectList);
    }

    @Override
    public JsonData getExamPaper(Long examId, Long paperId) {
        List<PaperQuesAnswerDto> paperQuesAnswerList = examAnswerBeanService.getPaperQuesAnswerList(examId, paperId);
        if (paperQuesAnswerList == null || paperQuesAnswerList.size() == 0) {
            JsonData jsonData = new JsonData();
            jsonData.setTotalCount(0L);
            return jsonData;
        }
        PaperViewDto paperViewDto = new PaperViewDto();
        PaperQuesAnswerDto paperQuesAnswerDto = paperQuesAnswerList.get(0);
        paperViewDto.setSubject(paperQuesAnswerDto.getSubject());
        paperViewDto.setTotalScore(paperQuesAnswerDto.getTotalScore());
        Map<String, List<PaperQuesAnswerDto>> paperQuesMap = paperQuesAnswerList.stream().collect(Collectors.groupingBy(PaperQuesAnswerDto::getQuesType));
        List<PaperQuesDto> PaperQuesDtoList = new ArrayList<>();
        if (paperQuesMap != null && paperQuesMap.size() > 0) {
            for (Map.Entry<String, List<PaperQuesAnswerDto>> entry : paperQuesMap.entrySet()) {
                String quesType = entry.getKey();
                List<PaperQuesAnswerDto> paperQues = entry.getValue();
                PaperQuesDto paperQuesDto = new PaperQuesDto();
                paperQuesDto.setQuesType(quesType);
                if (paperQues != null && paperQues.size() > 0) {
                    PaperQuesAnswerDto quesAnswerDto = paperQues.get(0);
                    paperQuesDto.setTitle(quesAnswerDto.getTitle());
                    paperQuesDto.setTitleNum(quesAnswerDto.getTitleNum());
                    List<PaperQuesInfoDto> paperQuesInfos = paperQues.stream().map(p->{
                        PaperQuesInfoDto paperQuesInfoDto = new PaperQuesInfoDto();
                        paperQuesInfoDto.setQuesNum(p.getQuesNum());
                        paperQuesInfoDto.setStem(p.getStem());
                        paperQuesInfoDto.setAnswer(p.getAnswer());
                        paperQuesInfoDto.setQuesAnswer(p.getQuesAnswer());
                        paperQuesInfoDto.setAnswerA(p.getAnswerA());
                        paperQuesInfoDto.setAnswerB(p.getAnswerB());
                        paperQuesInfoDto.setAnswerC(p.getAnswerC());
                        paperQuesInfoDto.setAnswerD(p.getAnswerD());
                        paperQuesInfoDto.setAnswerE(p.getAnswerE());
                        paperQuesInfoDto.setAnswerF(p.getAnswerF());
                        paperQuesInfoDto.setScore(p.getScore());
                        return paperQuesInfoDto;
                    }).collect(Collectors.toList());
                    paperQuesInfos.sort((p1, p2)->p1.getQuesNum().compareTo(p2.getQuesNum()));
                    paperQuesDto.setPaperQuesInfos(paperQuesInfos);
                }
                PaperQuesDtoList.add(paperQuesDto);
            }
        }
        PaperQuesDtoList.sort((p1, p2)->p1.getTitleNum().compareTo(p2.getTitleNum()));
        paperViewDto.setPaperQuesList(PaperQuesDtoList);
        List<PaperViewDto> paperViewDtoList = new ArrayList<>();
        paperViewDtoList.add(paperViewDto);
        return getResult(paperViewDtoList);
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
