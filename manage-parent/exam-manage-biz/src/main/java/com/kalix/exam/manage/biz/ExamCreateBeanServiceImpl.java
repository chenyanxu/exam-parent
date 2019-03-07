package com.kalix.exam.manage.biz;

import com.kalix.enrolment.question.api.biz.IQuestionCommonBizService;
import com.kalix.enrolment.question.api.biz.IQuestionService;
import com.kalix.enrolment.question.api.biz.ISubjectBeanService;
import com.kalix.enrolment.question.entities.SubjectBean;
import com.kalix.exam.manage.api.biz.IExamCreateBeanService;
import com.kalix.exam.manage.api.biz.IExamQuesBeanService;
import com.kalix.exam.manage.api.dao.IExamCreateBeanDao;
import com.kalix.exam.manage.dto.ExamPagerDto;
import com.kalix.exam.manage.dto.ExamQuesInfoDto;
import com.kalix.exam.manage.dto.ExamTemplateResDto;
import com.kalix.exam.manage.entities.ExamCreateBean;
import com.kalix.exam.manage.entities.ExamQuesBean;
import com.kalix.framework.core.api.persistence.JsonData;
import com.kalix.framework.core.api.persistence.JsonStatus;
import com.kalix.framework.core.impl.biz.ShiroGenericBizServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExamCreateBeanServiceImpl extends ShiroGenericBizServiceImpl<IExamCreateBeanDao, ExamCreateBean> implements IExamCreateBeanService {

    private IQuestionCommonBizService questionCommonBizService;

    private IExamQuesBeanService examQuesBeanService;

    private IQuestionService subjectBeanService;

    public void setQuestionCommonBizService(IQuestionCommonBizService questionCommonBizService) {
        this.questionCommonBizService = questionCommonBizService;
    }

    public void setExamQuesBeanService(IExamQuesBeanService examQuesBeanService) {
        this.examQuesBeanService = examQuesBeanService;
    }

    public void setSubjectBeanService(IQuestionService subjectBeanService) {
        this.subjectBeanService = subjectBeanService;
    }

    @Override
    public JsonData getAllExamPaper() {
        List<ExamPagerDto> examPapers = dao.findByNativeSql("select a.id,a.title,b.label,b.value from enrolment_question_paper a, enrolment_dict b where a.kskm = b.value and b.type='考试科目'", ExamPagerDto.class, null);
        JsonData jsonData = new JsonData();
        if (examPapers == null || examPapers.isEmpty()) {
            jsonData.setTotalCount(0L);
        } else {
            jsonData.setTotalCount(Long.valueOf(examPapers.size()));
        }
        jsonData.setData(examPapers);
        return jsonData;
    }

    @Override
    public JsonStatus preCreateExamPaper(Long id) {
        ExamCreateBean examCreateBean = dao.get(id);
        Long paperId = examCreateBean.getPaperId();
//        Boolean isPreCreate = examCreateBean.getPaperPreCreate();
        JsonStatus jsonStatus = new JsonStatus();
//        if (isPreCreate) {
//            jsonStatus.setFailure(true);
//            jsonStatus.setMsg("试卷已创建过");
//        }
        try {
            Map<String ,Object> paperMap = questionCommonBizService.autoCreateTestPaperMap(paperId, null);
            List<Map<String, Object>> quesList = (List<Map<String, Object>>)paperMap.get("quesList");
            if (quesList != null && quesList.size() > 0) {
                Map<String, List<Map<String, Object>>> quesPaperMap = quesList.stream().collect(Collectors.groupingBy((m)->{
                    Map<String, Object> map = (Map<String, Object>)m;
                    String quesType = (String)map.get("quesType");
                    String subType = (String)map.get("subType");
                    return quesType + "###" + subType;
                }));
                List<ExamQuesBean> examQuesList = new ArrayList<>();
                for (Map.Entry<String, List<Map<String, Object>>> entry : quesPaperMap.entrySet()) {
                    String key = entry.getKey();
                    String[] types = key.split("###");
                    String quesType = types[0];
                    String subType = types[1];
                    List<Map<String, Object>> quesByTypeList = entry.getValue();
                    String quesIds = "";
                    for (Map<String, Object> quesMap : quesByTypeList) {
                        quesIds += (String)quesMap.get("quesIds") + ",";
                    }
                    quesIds = quesIds.substring(0, quesIds.length()-1);
                    ExamQuesBean examQuesBean = examQuesBeanService.getExamQuesInfo(id, quesIds, quesType, subType);
                    if (examQuesBean == null) {
                        examQuesBean = new ExamQuesBean();
                        examQuesBean.setExamId(id);
                        examQuesBean.setQuesIds(quesIds);
                        examQuesBean.setQuestype(quesType);
                        examQuesBean.setSubType(subType);
                        examQuesBean.setPaperId(paperId);
                        examQuesList.add(examQuesBean);
                    }
                }
                if(!examQuesList.isEmpty()) {
                    examQuesBeanService.addBatch(examQuesList);
                    String quesIds = examQuesList.get(0).getQuesIds();
                    String searchQuesId = "";
                    if (quesIds.indexOf(",") != -1) {
                        String[] quesArr = quesIds.split(",");
                        searchQuesId = quesArr[0];
                    } else {
                        searchQuesId = quesIds;
                    }
                    // 临时添加，用试题分析字段区分A,B卷
                    SubjectBean subjectBean = (SubjectBean) subjectBeanService.getEntity(Long.valueOf(searchQuesId));
                    String analysis = subjectBean.getAnalysis();
                    examCreateBean.setPaperChoice(analysis);
                    dao.save(examCreateBean);
                }
            }
            jsonStatus.setSuccess(true);
            jsonStatus.setMsg("创建成功！");
        } catch(Exception e) {
            e.printStackTrace();
            jsonStatus.setFailure(true);
            jsonStatus.setMsg("创建失败！");
        }
        return jsonStatus;
    }

    @Override
    public void afterDeleteEntity(Long id, JsonStatus status) {
        dao.updateNativeQuery("delete from exam_examinee where examid=" + id + " state='未考'");
        super.afterDeleteEntity(id, status);
    }

    @Override
    public JsonData getAllTemplateRes() {
        JsonData jsonData = new JsonData();
        String sql = "SELECT ob.id as examid, ob.name, ob.paperid, ob.papername, t.quesids FROM exam_create ob, exam_ques t " +
                " where ob.id = t.examid and ob.paperid = t.paperid and " +
                " ob.creationdate >= (CURRENT_DATE) and ob.creationdate < (CURRENT_DATE + interval '1 Days')";
        List<ExamTemplateResDto> list = this.dao.findByNativeSql(sql, ExamTemplateResDto.class);
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                ExamTemplateResDto examTemplateResDto = list.get(i);
                String quesIds = examTemplateResDto.getQuesIds();
                String[] mainIds = quesIds.split(",");
                for (int j=0; j< mainIds.length; j++) {
                    String mainId = mainIds[j];
                    String pathSql = "select a.attachmentpath from middleware_attachment a, enrolment_question_subject s " +
                            " where a.mainid = s.id and s.id = '" + mainId + "'";
                    List<String> pathList = this.dao.findByNativeSql(pathSql, String.class);
                    if (pathList != null && pathList.size() > 0) {
                        for (String pathUrl: pathList) {
                            examTemplateResDto.getTemplateResUrls().add(pathUrl);
                        }
                    }
                }
            }
        }
        jsonData.setData(list);
        jsonData.setTotalCount((long) list.size());
        return jsonData;
    }

    @Override
    public JsonData getAllExamQuesIds() {
        String sql = "select a.examid as examId,a.quesids as quesIds,a.paperid as paperId," +
                " b.subject,b.subjectval as subjectVal from exam_ques a, exam_create b " +
                " where a.examid=b.id and b.examstart >= current_timestamp;";
        List<ExamQuesInfoDto> list = dao.findByNativeSql(sql, ExamQuesInfoDto.class);
        return getResult(list);
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
