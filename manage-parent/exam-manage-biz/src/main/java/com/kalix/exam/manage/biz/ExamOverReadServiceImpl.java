package com.kalix.exam.manage.biz;

import com.github.abel533.echarts.Grid;
import com.github.abel533.echarts.axis.AxisLabel;
import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.Bar;
import com.kalix.exam.manage.api.biz.IExamAnswerBeanService;
import com.kalix.exam.manage.api.biz.IExamExamineeBeanService;
import com.kalix.exam.manage.api.biz.IExamOverReadService;
import com.kalix.exam.manage.api.dao.IExamAnswerBeanDao;
import com.kalix.exam.manage.dto.ExamOverReadDto;
import com.kalix.exam.manage.dto.ExamSubjectDto;
import com.kalix.exam.manage.dto.OverReadStatisticDto;
import com.kalix.exam.manage.entities.ExamAnswerBean;
import com.kalix.framework.core.api.persistence.JsonData;
import com.kalix.framework.core.api.persistence.JsonStatus;
import com.kalix.framework.core.api.security.IShiroService;
import com.kalix.framework.core.util.SerializeUtil;

import java.beans.Transient;
import java.util.*;
import java.util.stream.Collectors;

public class ExamOverReadServiceImpl implements IExamOverReadService {
    private IExamAnswerBeanDao dao;
    private IExamAnswerBeanService examAnswerBeanService;
    private IExamExamineeBeanService examExamineeBeanService;
    private IShiroService shiroService;

    public void setDao(IExamAnswerBeanDao dao) {
        this.dao = dao;
    }

    public void setExamAnswerBeanService(IExamAnswerBeanService examAnswerBeanService) {
        this.examAnswerBeanService = examAnswerBeanService;
    }

    public void setExamExamineeBeanService(IExamExamineeBeanService examExamineeBeanService) {
        this.examExamineeBeanService = examExamineeBeanService;
    }

    public void setShiroService(IShiroService shiroService) {
        this.shiroService = shiroService;
    }

    @Override
    public JsonData getAllSubjects() {
        List<ExamSubjectDto> subjectList = dao.findByNativeSql("select label as text,value as id from enrolment_dict where type='考试科目'", ExamSubjectDto.class);
        return getResult(subjectList);
    }

    @Override
    public JsonData getAllExamQuesBySubject(String jsonStr) {
        Map<String, String> jsonMap = SerializeUtil.json2Map(jsonStr);
        String subjectCode = jsonMap.get("subjectCode");
        if (subjectCode == null || "".equals(subjectCode.trim())) {
            subjectCode = jsonMap.get("%subjectCode%");
        }
        String name = jsonMap.get("%name%");
        String sql = "select a.id,a.answer,a.answerPicPath,a.examId,a.paperId,a.quesId,a.quesType,a.perScore,a.score,a.userId,d.name as userName," +
                "b.name,b.subjectVal,c.stem,c.scoreStandard " +
                "from exam_answer a,exam_create b,enrolment_question_subject c,sys_user d " +
                "where a.examId = b.id and a.quesId=c.id and a.userId=d.id " +
                "and a.readOverState='未批' and a.quesType='5'" +
                " and b.subjectVal='"+subjectCode+"'";
        if (name != null && name.trim().length() > 0) {
            sql += " and b.name like'%"+name+"%'";
        }
        List<ExamOverReadDto> examOverReadList = null;
        try {
            examOverReadList = dao.findByNativeSql(sql, ExamOverReadDto.class);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return getResult(examOverReadList);
    }

    @Override
    @Transient
    public JsonStatus overReadScore(ExamOverReadDto examOverReadDto) {
        JsonStatus jsonStatus = new JsonStatus();
        try {
            Long userId = shiroService.getCurrentUserId();
            Long examAnswerId = examOverReadDto.getId();
            // 更新批阅试题
            ExamAnswerBean examAnswerBean = examAnswerBeanService.getEntity(examAnswerId);
            examAnswerBean.setScore(examOverReadDto.getScore());
            examAnswerBean.setReadoverState("已批");
            examAnswerBean.setReadoverBy(userId);
            examAnswerBean.setReadoverOn(new Date());
            examAnswerBeanService.updateEntity(examAnswerBean);

            // 查询试题列表所有批阅状态
            Long examId = examOverReadDto.getExamId();
            Long paperId = examOverReadDto.getPaperId();
            Long studentId = examOverReadDto.getUserId();
            List<ExamAnswerBean> examAnswerList = examAnswerBeanService.getExamUserAnswer(examId, paperId, studentId);
            boolean isAllOverRead = examAnswerList.stream().allMatch(e->"已批".equals(e.getReadoverState()));
            // 如果全是已批
            if (isAllOverRead) {
                // 计算总成绩
                Integer totalScore = examAnswerList.stream().map(ExamAnswerBean::getScore).filter(s->s!=null).reduce(Integer::sum).get();
                // 更新学生总成绩
                examExamineeBeanService.updateTotalScore(examId, studentId, totalScore);
            }

            jsonStatus.setMsg("提交成功");
            jsonStatus.setSuccess(true);
        } catch(Exception e) {
            e.printStackTrace();
            jsonStatus.setMsg("提交失败");
            jsonStatus.setFailure(true);
        }
        return jsonStatus;
    }

    @Override
    public JsonData overReadPaperStatistic(String jsonStr) {
        Map<String, String> jsonMap = SerializeUtil.json2Map(jsonStr);
        String startDate = jsonMap.get("dateBegin");
        String endDate = jsonMap.get("dateEnd");
        Long userId = shiroService.getCurrentUserId();
        // 统计阅卷数
        String sql = "select count(a.examId),b.subject from exam_answer a,exam_create b " +
                " where a.examId = b.id and a.readOverState='已批' and a.quesType='5' and a.readoverby='" + userId + "'";
        if (startDate != null && startDate.trim().length() > 0) {
            sql += " and a.readoveron >= to_date('"+startDate+"','YYYY-MM-DD')";
        }
        if (endDate != null && endDate.trim().length() > 0) {
            sql += " and a.readoveron <= to_date('"+endDate+"','YYYY-MM-DD')";
        }
        sql += " GROUP BY b.subject";

        // 统计阅题数
//        sql = "select count(a.examId),b.subject,a.userId,a.quesid from exam_answer a,exam_create b " +
//                "where a.examId = b.id and a.readOverState='已批' and a.quesType='5' and a.readoverby='-1' " +
//                " and a.readoveron between to_date('2015-07-01','YYYY-MM-DD') and to_date('2015-08-15','YYYY-MM-DD')" +
//                " GROUP BY b.subject,a.userId,a.quesid";

        List<OverReadStatisticDto> overReadStatisticList = dao.findByNativeSql(sql, OverReadStatisticDto.class);
        // 考试科目列表 x轴
        List<ExamSubjectDto> subjectList = dao.findByNativeSql("select label as text,value as id from enrolment_dict where type='考试科目'", ExamSubjectDto.class);
        List<String> subjectCollection = subjectList.stream().map(s->s.getText()).collect(Collectors.toList());
        String[] subArr = new String[subjectCollection.size()];
        String[] subjects = subjectCollection.toArray(subArr);

        int[] datas = new int[subjects.length];
        for (int i=0; i<subjects.length; i++) {
            String subject = subjects[i];
            if (overReadStatisticList != null && overReadStatisticList.size() > 0) {
                Optional<OverReadStatisticDto> optional = overReadStatisticList.stream().filter(o->o.getSubject().equals(subject)).findFirst();
                if (optional.isPresent()) {
                    datas[i] = optional.get().getCount();
                } else {
                    datas[i] = 0;
                }
            } else {
                datas[i] = 0;
            }
        }
        String chartData = barChart(subjects, datas, "阅卷科目统计", "数据来自在线考试系统");
        Map<String, String> barMap = new HashMap<>();
        JsonData jsonData = new JsonData();
        List<Map<String, String>> dataList = new ArrayList<>();
        barMap.put("option", chartData);
        dataList.add(barMap);
        jsonData.setData(dataList);
        return jsonData;
    }

    /**
     * 柱状图Options
     *
     * @param types
     * @param datas
     * @param chartTitle
     * @return
     */
    private String barChart(String[] types, int[] datas, String chartTitle, String subTitle) {
        GsonOption option = new GsonOption();
        option.title(chartTitle); // 标题
        if (subTitle != null && subTitle.trim().length() > 0) {
            option.getTitle().setSubtext(subTitle);
        }
        option.tooltip().show(true).formatter("{a} <br/>{b} : {c}");//显示工具提示,设置提示格式
        option.legend(chartTitle);// 图例
        Grid grid = new Grid();
        grid.bottom(100);
        option.setGrid(grid);
        Bar bar = new Bar(chartTitle);// 图类别(柱状图)
        CategoryAxis category = new CategoryAxis();// 轴分类
        category.data(types);// 轴数据类别
        // 循环数据
        for (int i = 0; i < datas.length; i++) {
            int data = datas[i];
            Map<String, Object> map = new HashMap<>();
            map.put("value", data);
            bar.data(map);
        }
        option.xAxis(category);// x轴
        AxisLabel axisLabel = new AxisLabel();
        axisLabel.setRotate(30);
        option.getxAxis().get(0).setAxisLabel(axisLabel);
        option.yAxis(new com.github.abel533.echarts.axis.ValueAxis());// y轴
        option.series(bar);
        return option.toString();
    }

    @Override
    public JsonData getOverReadQuesBySubject(String jsonStr) {
        Map<String, String> jsonMap = SerializeUtil.json2Map(jsonStr);
        String subject = jsonMap.get("subject");
        String startDate = jsonMap.get("dateBegin");
        String endDate = jsonMap.get("dateEnd");

        String sql = "select a.answer,a.answerPicPath,a.perScore,a.score,a.userId,d.name as userName," +
                "b.name,b.subject,c.stem,c.scoreStandard " +
                " from exam_answer a,exam_create b,enrolment_question_subject c,sys_user d " +
                " where a.examId = b.id and a.quesId=c.id and a.userId=d.id " +
                " and a.readOverState='已批' and a.quesType='5'" +
                " and b.subject='"+subject+"'";
        if (startDate != null && startDate.trim().length() > 0) {
            sql += " and a.readoveron >= to_date('"+startDate+"','YYYY-MM-DD')";
        }
        if (endDate != null && endDate.trim().length() > 0) {
            sql += " and a.readoveron <= to_date('"+endDate+"','YYYY-MM-DD')";
        }

        List<ExamOverReadDto> examOverReadList = dao.findByNativeSql(sql, ExamOverReadDto.class);
        return getResult(examOverReadList);
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
