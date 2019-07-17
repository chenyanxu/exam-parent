package com.kalix.exam.manage.biz;

import com.github.abel533.echarts.Legend;
import com.github.abel533.echarts.Tooltip;
import com.github.abel533.echarts.axis.Axis;
import com.github.abel533.echarts.axis.AxisLabel;
import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.Trigger;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.Bar;
import com.github.abel533.echarts.series.Line;
import com.github.abel533.echarts.series.Series;
import com.kalix.admin.core.api.biz.IOrganizationBeanService;
import com.kalix.admin.core.api.biz.IUserBeanService;
import com.kalix.admin.core.dto.model.OrganizationDTO;
import com.kalix.admin.core.entities.UserBean;
import com.kalix.exam.manage.api.biz.IExamCreateBeanService;
import com.kalix.exam.manage.api.biz.IExamExamineeBeanService;
import com.kalix.exam.manage.api.biz.IExamQuesBeanService;
import com.kalix.exam.manage.api.dao.IExamExamineeBeanDao;
import com.kalix.exam.manage.biz.utils.ExamBaseConfigUtil;
import com.kalix.exam.manage.dto.*;
import com.kalix.exam.manage.entities.ExamCreateBean;
import com.kalix.exam.manage.entities.ExamExamineeBean;
import com.kalix.framework.core.api.persistence.JsonData;
import com.kalix.framework.core.api.persistence.JsonStatus;
import com.kalix.framework.core.impl.biz.ShiroGenericBizServiceImpl;
import com.kalix.framework.core.util.SerializeUtil;
import com.kalix.middleware.excel.api.model.exam.manage.ExamineeRoomInfoDto;

import javax.persistence.Transient;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ExamExamineeBeanServiceImpl extends ShiroGenericBizServiceImpl<IExamExamineeBeanDao,ExamExamineeBean> implements IExamExamineeBeanService {

    private IUserBeanService userBeanService;
    private IExamCreateBeanService examCreateBeanService;
    private IOrganizationBeanService organizationBeanService;
    private IExamQuesBeanService examQuesBeanService;
    private final Integer PER_ROOM_COUNT = 26;


    public void setUserBeanService(IUserBeanService userBeanService) {
        this.userBeanService = userBeanService;
    }

    public void setExamCreateBeanService(IExamCreateBeanService examCreateBeanService) {
        this.examCreateBeanService = examCreateBeanService;
    }

    public void setOrganizationBeanService(IOrganizationBeanService organizationBeanService) {
        this.organizationBeanService = organizationBeanService;
    }

    public void setExamQuesBeanService(IExamQuesBeanService examQuesBeanService) {
        this.examQuesBeanService = examQuesBeanService;
    }

    @Override
    @Transient
    public JsonStatus saveExaminee(ExamOrgDto examOrgDto) {
        JsonStatus jsonStatus = new JsonStatus();
        Long examId = examOrgDto.getExamId();
        // 验证examId及orgIds
        String orgIds = examOrgDto.getOrgIds();
        if (examId == null || orgIds == null || orgIds.isEmpty()) {
            jsonStatus.setMsg("考试信息为空或者参加考试单位信息为空");
            jsonStatus.setFailure(true);
            return jsonStatus;
        }

        try {
            // 批量插入考生信息
            List<ExamExamineeBean> examExamineans = new ArrayList<>();
            List<Long> userIds = new ArrayList<>();

            if (orgIds.indexOf(",") != -1) {
                String[] orgIdArr = orgIds.split(",");
                if (orgIdArr.length > 0) {
                    for (String orgId : orgIdArr) {
                        if (orgId != null) {
                            setExamineeUserId(examId, orgId, userIds, examExamineans);
                        }
                    }
                }
            } else {
                setExamineeUserId(examId, orgIds, userIds, examExamineans);
            }
            if (examExamineans.size() > 0) {
                // 按用户Id先排序
                // examExamineans.sort((e1,e2) -> e1.getUserId().intValue() - e2.getUserId().intValue());
                // 设置考场和座号（默认26人一个考场）
                /**
                int totalSize = examExamineans.size();
                int pageSize = PER_ROOM_COUNT;
                int cNum = 0;
                for (int i = 0; i < totalSize; i++) {
                    if (i%pageSize == 0) {
                        cNum++;
                    }
                    examExamineans.get(i).setExamRoomNo((i%pageSize+1));
                    examExamineans.get(i).setExamRoom("第" + cNum + "考场");
                    // System.out.println("UserId:" + examExamineans.get(i).getUserId());
                }
                 **/
                for (ExamExamineeBean test : examExamineans) {
                    System.out.println("UserId:" + test.getUserId() + " RoomNum:" + test.getExamRoom() + "RoomNo:" + test.getExamRoomNo());
                }
                System.out.println("---------------------------------------");
                setExamineeRoomAndNo(examExamineans);
                for (ExamExamineeBean test : examExamineans) {
                    System.out.println("UserId:" + test.getUserId() + " RoomNum:" + test.getExamRoom() + "RoomNo:" + test.getExamRoomNo());
                }
                // 初始化考生密码
                userBeanService.updateUserPasswordByCardId(userIds);
                // 删除examId相关信息
                deleteByExamId(examId);
                // 批量添加分配的考生
                dao.addBatch(examExamineans);
                // 修改考试分配状态
                updateDistributeStat(examId);
                // 发通知消息

                jsonStatus.setMsg("考生分配成功");
                jsonStatus.setSuccess(true);
            } else {
                jsonStatus.setMsg("请先在考生所在单位添加考生");
                jsonStatus.setFailure(true);
            }
        } catch(Exception e) {
            e.printStackTrace();
            jsonStatus.setMsg("考生分配失败");
            jsonStatus.setFailure(true);
        }
        return jsonStatus;
    }

    private void setExamineeRoomAndNo(List<ExamExamineeBean> examExamineans) {
        if (examExamineans == null || examExamineans.isEmpty()) {
            return;
        }
        LinkedHashMap<String, String> roomPersonConfigs = ExamBaseConfigUtil.getAllRoomPersonConfigs();
        int totalNum = examExamineans.size();
        int startNum = 0;
        List<ExamExamineeBean> tempList = null;
        for (Map.Entry<String, String> entry : roomPersonConfigs.entrySet()) {
            String roomNumStr = entry.getKey();
            String persons = entry.getValue();
            if (roomNumStr == null || "".equals(roomNumStr) || persons == null || "".equals(persons)) {
                return;
            }
            int roomNum = Integer.parseInt(roomNumStr);
            int personNum = Integer.parseInt(persons);

            if (totalNum <= personNum) {
                tempList = examExamineans.subList(startNum, startNum + totalNum);
                for (int i = 0; i < totalNum; i++) {
                    tempList.get(i).setExamRoomNo(i+1);
                    tempList.get(i).setExamRoom("第" + roomNum + "考场");
                }
                break;
            } else {
                tempList = examExamineans.subList(startNum, startNum + personNum);
                for (int i = 0; i < personNum; i++) {
                    tempList.get(i).setExamRoomNo(i+1);
                    tempList.get(i).setExamRoom("第" + roomNum + "考场");
                }
                startNum += personNum;
                totalNum -= personNum;
            }
        }
    }

    private void setExamineeUserId(Long examId, String orgId, List<Long> userIds, List<ExamExamineeBean> examExamineans) {
        List<UserBean> users = userBeanService.findUserByOrgId(Long.parseLong(orgId.trim()));
        Long currentUserId = shiroService.getCurrentUserId();
        if (users != null) {
            ExamExamineeBean examExamineeBean = null;
            for (UserBean user : users) {
                if (!userIds.contains(user.getId())) {
                    userIds.add(user.getId());
                    examExamineeBean = new ExamExamineeBean();
                    examExamineeBean.setExamId(examId);
                    // 0:未考 1:已考
                    examExamineeBean.setState("未考");
                    examExamineeBean.setUserId(user.getId());
                    examExamineeBean.setOrgId(Long.parseLong(orgId));
                    examExamineeBean.setCreateById(currentUserId);
                    examExamineeBean.setUpdateById(currentUserId);
                    examExamineeBean.setCreationDate(new Date());
                    examExamineans.add(examExamineeBean);
                }
            }
        }
    }

    @Override
    public void deleteByExamId(Long examId) {
        dao.updateNativeQuery("delete from exam_examinee where examid=" + examId);
    }

    @Override
    public Map<String, Object> getExamOrgTree(Long examId) {
        OrganizationDTO organizationDTO = organizationBeanService.getAllOrg(null);
        List<Long> orgIds = getOrgIdsByExamId(examId);
        Map<String, Object> map = new HashMap<>();
        map.put("treeData", organizationDTO);
        map.put("orgIds", orgIds);
        return map;
    }

    @Override
    public JsonData getAllSelfExaming(String jsonStr) {
        String name = "";
        if (jsonStr != null && !jsonStr.isEmpty()) {
            Map<String, Object> jsonMap = SerializeUtil.jsonToMap(jsonStr);
            name = (String)jsonMap.get("%name%");
        }
        Long currentUserId = shiroService.getCurrentUserId();
        String sql = "select a.id,a.name,a.subject,a.duration,a.paperId " +
                " from exam_create a,exam_examinee b " +
                " where b.examid=a.id  and  b.userid=?" +
                " and b.state in ('未考','考试中')";
        if (name != null && !name.isEmpty()) {
            sql += " and a.name like'%"+name+"%'";
        }
        List<ExamCreateBean> examings = dao.findByNativeSql(sql, ExamCreateBean.class, currentUserId);
        JsonData jsonData = new JsonData();
        if (examings == null) {
            jsonData.setTotalCount(0L);
        } else {
            jsonData.setTotalCount(Long.valueOf(examings.size()));
        }
        jsonData.setData(examings);
        return jsonData;
    }

    @Override
    public void updateTotalScore(Long examId, Long userId, Integer totalScore) {
        String sql = "update exam_examinee set totalScore=" + totalScore
                + " where examId=" + examId + " and userId=" + userId + " and state='已考'";
        dao.updateNativeQuery(sql);
    }

    @Override
    public List<ExamExamineeDto> findExamineeByUser(String name, String subjectVal) {
        Long userId = shiroService.getCurrentUserId();
        String sql = "select a.userId,a.examId,a.totalScore,a.startTime,b.name,b.subject,b.subjectVal,b.paperId " +
                "from exam_examinee a,exam_create b where a.examId=b.id and a.userId=" + userId +
                " and a.state='已考'";
        if (name != null && name.trim().length() > 0) {
            sql += " and b.name like'%"+name+"%'";
        }
        if (subjectVal != null && subjectVal.trim().length() > 0) {
            sql += " and b.subjectVal = '"+subjectVal+"'";
        }
        return dao.findByNativeSql(sql, ExamExamineeDto.class);
    }

    @Override
    public List<ExamSubjectDto> getExamSubjects() {
        Long userId = shiroService.getCurrentUserId();
        String sql = "select distinct b.subject as text,b.subjectVal as id" +
                " from exam_examinee a,exam_create b where a.examId=b.id and a.userId=" + userId;
        return dao.findByNativeSql(sql, ExamSubjectDto.class);
    }

    @Override
    public List<ExamExamineeDto> getExamMaterial(String name, String subjectVal) {
        Long userId = shiroService.getCurrentUserId();
        String sql = "select a.id,a.userId,a.examId,a.starttime,b.name,b.subject,b.paperId,count(c.attachmentname) as attachmentCount " +
                " from exam_examinee a INNER JOIN exam_create b on a.examId=b.id " +
                " left JOIN middleware_attachment c on c.mainid=a.id " +
                " where a.state='已考' and a.userId="+userId;
        if (name != null && name.trim().length() > 0) {
            sql += " and b.name like'%"+name+"%'";
        }
        if (subjectVal != null && subjectVal.trim().length() > 0) {
            sql += " and b.subjectVal = '"+subjectVal+"'";
        }
        sql += " GROUP BY a.id,a.userId,a.examId,a.starttime,b.name,b.subject,b.paperId";
        return dao.findByNativeSql(sql, ExamExamineeDto.class);
    }

    @Override
    public JsonData getExamineeUserInfo(Long userId) {
        //Long userId = shiroService.getCurrentUserId();
        String sql = "select a.id as examId,a.name as examName,a.subject,a.examStart,a.duration,a.paperId,c.name as userName,c.idCards" +
                " from exam_create a,exam_examinee b,sys_user c where b.examid=a.id and b.userid=c.id" +
                " and  b.userid=" + userId +
                " and b.state in ('未考','考试中')";
        List<ExamExamineeUserDto> examineeUserDtos = dao.findByNativeSql(sql, ExamExamineeUserDto.class);
        ExamExamineeUserDto examExamineeUserDto = null;
        List<ExamExamineeUserDto> examineeUserNeedList = null;
        if (examineeUserDtos != null && !examineeUserDtos.isEmpty()) {
            List<ExamExamineeUserDto> examineeUserDtoList = examineeUserDtos.stream().map((dto)->{
                Date startDate = dto.getExamStart();
                Integer duration = dto.getDuration();
                long endTime = startDate.getTime() + (duration*60*1000);
                String examTimeStr = getExamTimeStr(duration, startDate);
                String examDateStr = getExamDateStr(startDate);

                dto.setExamEndTime(endTime);
                dto.setExamTimeStr(examTimeStr);
                dto.setExamDateStr(examDateStr);
                return dto;
            }).collect(Collectors.toList());
            if (examineeUserDtoList.size() == 1) {
                examExamineeUserDto = examineeUserDtos.get(0);
            } else {
                examineeUserNeedList  = examineeUserDtoList.stream().filter((dto)->System.currentTimeMillis()<=dto.getExamEndTime())
                        .sorted((dto1,dto2)-> dto1.getExamEndTime().compareTo(dto2.getExamEndTime()))
                        .collect(Collectors.toList());
                examExamineeUserDto = examineeUserNeedList.get(0);
            }
        }
        List<ExamExamineeUserDto> examExamineeUserList = new ArrayList<>();
        if (examExamineeUserDto != null) {
            Long paperId = examExamineeUserDto.getPaperId();
            Long examId = examExamineeUserDto.getExamId();
            String quesIds = examQuesBeanService.getExamQuesIds(paperId, examId);
            examExamineeUserDto.setQuesIds(quesIds);
            examExamineeUserList.add(examExamineeUserDto);
        }
        return getResult(examExamineeUserList);
    }

    @Override
    public JsonData getFractionalStatisticsInfo(String jsonStr) {
        // 考试科目
        Map<String, String> jsonMap = SerializeUtil.json2Map(jsonStr);
        String subjectCode = jsonMap.get("subjectVal");

        // 考试时间
        String startDate = jsonMap.get("dateBegin");
        String endDate = jsonMap.get("dateEnd");
        // 通过考试科目及考试时间查询exam_create获取所有的examId
        List<ExamExamineeDto> examExamineeDtoList = examCreateBeanService.getExamIdsBySubjectCodeAndDate(subjectCode, startDate, endDate);
        List<Long> examIds = null;
        String subject = "";
        if (examExamineeDtoList != null && !examExamineeDtoList.isEmpty()) {
            examIds = examExamineeDtoList.stream().map(e->e.getExamId()).collect(Collectors.toList());
            subject = examExamineeDtoList.get(0).getSubject();
        }
        // 通过in(examId)及已考状态 统计分数段人数
        List<Integer> countDatas = getCountDatas(examIds);
        // 计算总人数及人数比例
        Integer totalExaminee = countDatas.stream().mapToInt(e->e).sum();
        List<Double> scaleDatas = null;
        if (totalExaminee == 0) {
            scaleDatas = Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0);
        } else {
            scaleDatas = countDatas.stream().map(e-> Double.valueOf(e*100/totalExaminee))
                    .collect(Collectors.toList());
        }
        // 通过examId及已考状态 sum查询出总分数
        Integer sumTotalScore = getSumTotalScore(examIds);
        // 总分数/总人数 = 平均分
        Double avgScore = null;
        if (totalExaminee > 0) {
            avgScore = Double.valueOf(sumTotalScore/totalExaminee);
        } else {
            avgScore = 0.0;
        }


        String title = subject + "考试分数段统计";
        String subTitle = "";

        // 按分数段统计的人数
        // List<Integer> countDatas = Arrays.asList(30, 50, 30, 90, 10);
        // 按分数段统计的人数比例
        // List<Double> scaleDatas = Arrays.asList(18.1, 20.8, 6.6, 91.4, 19.2);

        String option = getFractionalStatisticsChart(title, subTitle, countDatas, scaleDatas);
        Map<String, Object> map = new HashMap<>();
        map.put("option", option);
        map.put("totalExaminee", totalExaminee);
        map.put("countDatas", countDatas);
        map.put("scaleDatas", scaleDatas);
        map.put("avgScore", avgScore);

        List<Map<String, Object>> fractionalStatistics = new ArrayList<>();
        fractionalStatistics.add(map);
        return JsonData.toJsonData(fractionalStatistics);
    }

    @Override
    public JsonData getExamineeRoomsInfo(Integer page, Integer limit, String jsonStr, String sort) {
        Map<String, String> jsonMap = SerializeUtil.json2Map(jsonStr);
        String subject = jsonMap.get("subjectVal");
        String startDate = jsonMap.get("dateBegin");
        // String endDate = jsonMap.get("dateEnd");
        String state = jsonMap.get("examState");
        if (subject == null || startDate == null) {
                // || endDate == null) {
            return getResult(null, 0);
        }

        Integer count = getExamineeRoomsCount(subject, startDate, state);
        List<ExamineeRoomDto> examineeRoomDtoList = getExamineeRoomsInfo(page, limit, subject, startDate, state);

        return getResult(examineeRoomDtoList, count);
    }

    @Override
    public JsonStatus updateExamineeRoomInfo(ExamineeRoomDto examineeRoomDto) {
        try {
            Long id = examineeRoomDto.getId();
            String examRoom = examineeRoomDto.getExamRoom();
            Integer examRoomNo = examineeRoomDto.getExamRoomNo();
            ExamExamineeBean examExamineeBean = dao.get(id);
            examExamineeBean.setExamRoom(examRoom);
            examExamineeBean.setExamRoomNo(examRoomNo);
            dao.save(examExamineeBean);
            return JsonStatus.successResult("修改成功");
        } catch(Exception e) {
            e.printStackTrace();
            return JsonStatus.failureResult("修改失败");
        }
    }

    @Override
    @Transactional
    public JsonStatus saveExamineeRoomInfo(ExamineeRoomDto examineeRoomDto) {
        String subjectVal = examineeRoomDto.getSubjectCode();
        String startDate = examineeRoomDto.getDateBegin();
        String examineeName = examineeRoomDto.getName();
        String idCards = examineeRoomDto.getIdCards();
        String examCardNumber = examineeRoomDto.getExamCardNumber();
        String examRoom = examineeRoomDto.getExamRoom();
        Integer examRoomNo = examineeRoomDto.getExamRoomNo();

        // 获取考试信息
        List<ExamCreateBean> examList = examCreateBeanService.getExamBySubjectCodeAndStartDate(subjectVal, startDate);
        if(examList == null || examList.isEmpty()) {
            return JsonStatus.failureResult("请先创建一个考试");
        }
        ExamCreateBean examCreateBean = examList.get(0);

        // 获取考生信息
        List<UserBean> userBeans = userBeanService.getUsersByNameIdCardAndExamNum(examineeName, idCards, examCardNumber);
        if (userBeans == null || userBeans.isEmpty()) {
            return JsonStatus.failureResult("请先创建考生的用户信息");
        }
        Long userId = userBeans.get(0).getId();

        // 获取考生机构信息
        List<Long> orgIdList = userBeanService.getOrgIdsByUserId(userId);
        if (orgIdList == null || orgIdList.isEmpty()) {
            return JsonStatus.failureResult("请先在该考试机构下添加用户");
        }
        // 通过考试Id获取已有的机构Id
        Long examId = examCreateBean.getId();
        List<ExamExamineeBean> examExamineeList = getExamineeListByExamId(examId);
        if (examExamineeList == null || examExamineeList.isEmpty()) {
            return JsonStatus.failureResult("请在创建考试用例中分配考生");
        }
        List<Long> examOrgIds = examExamineeList.stream().map(e->e.getOrgId()).distinct().collect(Collectors.toList());
        Long examNeedOrgId = null;
        for (Long orgId : orgIdList) {
            for (Long examOrgId : examOrgIds) {
                if (orgId != null && examOrgId != null && orgId.equals(examOrgId)) {
                    examNeedOrgId = examOrgId;
                    break;
                }
            }
            if (examNeedOrgId != null) {
                break;
            }
        }
        if (examNeedOrgId == null) {
            return JsonStatus.failureResult("请先在该考试机构下添加用户");
        }
        // 验证考场及座号信息
        List<ExamExamineeBean> examExamineeFilters = examExamineeList.stream().filter(e->
                e.getExamRoom().equals(examRoom)&&e.getExamRoomNo().equals(examRoomNo)
        ).collect(Collectors.toList());
        if (examExamineeFilters != null && !examExamineeFilters.isEmpty()) {
            return JsonStatus.failureResult("考场及座号重复，请重新添加");
        }

        try {
            // 初始化考试密码
            List<Long> userIds = Arrays.asList(userId);
            userBeanService.updateUserPasswordByCardId(userIds);
            // 保存考生信息
            ExamExamineeBean examExamineeBean = new ExamExamineeBean();
            examExamineeBean.setExamId(examId);
            examExamineeBean.setOrgId(examNeedOrgId);
            examExamineeBean.setUserId(userId);
            examExamineeBean.setState("未考");
            examExamineeBean.setExamRoom(examRoom);
            examExamineeBean.setExamRoomNo(examRoomNo);
            dao.save(examExamineeBean);
            return JsonStatus.successResult("保存成功");
        } catch(Exception e) {
            e.printStackTrace();
            return JsonStatus.failureResult("保存失败");
        }
    }

    private List<ExamExamineeBean> getExamineeListByExamId(Long examId) {
        String sql = "select * from exam_examinee where examid=" + examId;
        return dao.findByNativeSql(sql, ExamExamineeBean.class);
    }

    @Override
    @Transactional
    public JsonStatus saveExamineeRoomInfo(ExamineeRoomInfoDto examineeRoomDto) {
        if (examineeRoomDto == null) {
            return JsonStatus.failureResult("无保存数据");
        }
        try {
            ExamExamineeBean examExamineeBean = getExamExamineeByRoomInfoDto(examineeRoomDto);
            if (examExamineeBean == null || examExamineeBean.getId() <= 0) {
                return JsonStatus.failureResult("保存数据不正确");
            }
            examExamineeBean.setExamRoom(examineeRoomDto.getExamRoom());
            examExamineeBean.setExamRoomNo(Integer.parseInt(examineeRoomDto.getExamRoomNo().trim()));
            dao.save(examExamineeBean);
            return JsonStatus.successResult("保存成功");
        } catch(Exception e) {
            e.printStackTrace();
            return JsonStatus.failureResult("保存失败");
        }
    }

    @Override
    public List<ExamineeControlSheetDto> getExamineeControlSheetInfos(String subjectVal, String startDate) {
        String sql = "SELECT b.subject,b.examStart,b.duration,C.NAME,C.examcardnumber,C.idcards,A.examroom,A.examroomno FROM exam_examinee A " +
                " INNER JOIN exam_create b ON A.examid = b.ID INNER JOIN sys_user C ON A.userid = C.ID " +
                " WHERE b.subjectval = '"+subjectVal+"' AND b.examstart = to_timestamp('"+startDate+"','YYYY-MM-DD hh24:mi:ss')";
        return dao.findByNativeSql(sql, ExamineeControlSheetDto.class);
    }

    private ExamExamineeBean getExamExamineeByRoomInfoDto(ExamineeRoomInfoDto examineeRoomDto) {
        String examCardNumber = examineeRoomDto.getExamCardNumber();
        String idCards = examineeRoomDto.getIdCards();
        String name = examineeRoomDto.getName();
        String examStart = examineeRoomDto.getExamStart();
        String subjectVal = examineeRoomDto.getSubjectVal();
        String sql = "select a.* from exam_examinee a INNER JOIN exam_create b ON a.examid=b.id INNER JOIN sys_user c ON a.userid = c.id " +
                "where b.examstart = to_timestamp('"+examStart+"','YYYY-MM-DD hh24:mi:ss') " +
                "and b.subjectval = '"+subjectVal+"' and c.name='"+name+"' and c.idcards='"+idCards+"' and c.examcardnumber='"+examCardNumber+"'";
        List<ExamExamineeBean> examExamineeBeans = dao.findByNativeSql(sql, ExamExamineeBean.class);
        if (examExamineeBeans == null || examExamineeBeans.isEmpty()) {
            return null;
        }
        return examExamineeBeans.get(0);
    }

    public List<ExamineeRoomDto> getExamineeRoomsInfo(Integer page, Integer limit, String subject, String startDate, String state) {
        int offset = 0;
        if (page != null && limit != null && page > 0 && limit > 0) {
            offset = (page - 1) * limit;
        }
        String sql = "select a.id,a.examid,a.userid,c.name,c.examcardnumber,c.idcards,a.state,a.examroom,a.examroomno from exam_examinee a " +
                "INNER JOIN exam_create b ON a.examid = b.id INNER JOIN sys_user c ON a.userid = c.id " +
                "where b.subjectval = '" + subject + "'";
        if (startDate != null && startDate.trim().length() > 0) {
            sql += " and b.examstart = to_timestamp('"+startDate+"','YYYY-MM-DD hh24:mi:ss')";
        }
//        if (endDate != null && endDate.trim().length() > 0) {
//            sql += " and b.examstart <= to_date('"+endDate+"','YYYY-MM-DD')";
//        }
        if (state != null) {
            sql += " and a.state = '"+state+"'";
        }
        sql += " order by c.id";
        if (page != null && limit != null) {
            sql += " limit " + limit + " offset " + offset;
        }
        List<ExamineeRoomDto> examineeRoomDtoList =  dao.findByNativeSql(sql, ExamineeRoomDto.class);
        return examineeRoomDtoList;
    }

    private Integer getExamineeRoomsCount(String subject, String startDate, String state) {
        String sql = "select count(1) from exam_examinee a " +
                "INNER JOIN exam_create b ON a.examid = b.id INNER JOIN sys_user c ON a.userid = c.id " +
                "where b.subjectval = '" + subject + "'";
        if (startDate != null && startDate.trim().length() > 0) {
            sql += " and b.examstart = to_timestamp('"+startDate+"','YYYY-MM-DD hh24:mi:ss')";
        }
//        if (endDate != null && endDate.trim().length() > 0) {
//            sql += " and b.examstart <= to_date('"+endDate+"','YYYY-MM-DD')";
//        }
        if (state != null) {
            sql += " and a.state = '"+state+"'";
        }
        List<Integer> examineeRoomsCount = dao.findByNativeSql(sql, Integer.class);
        if (examineeRoomsCount == null) {
            return 0;
        }
        return examineeRoomsCount.get(0);
    }

    private Integer getSumTotalScore(List<Long> examIds) {
        if (examIds == null || examIds.isEmpty()) {
            return 0;
        }
        List<String> examIdList = examIds.stream().map(e->String.valueOf(e)).collect(Collectors.toList());
        String examIdStrs = String.join(",", examIdList);
        String sql = "select sum(a.totalscore) from exam_examinee a " +
                "where a.examid in (" + examIdStrs + ") and a.state='已考' ";
        List<Integer> sumList = dao.findByNativeSql(sql, Integer.class);
        if (sumList == null || sumList.isEmpty()) {
            return 0;
        }
        Integer sumTotalScore = sumList.get(0);
        if (sumTotalScore == null) {
            sumTotalScore = 0;
        }
        return sumTotalScore;
    }

    private List<Integer> getCountDatas(List<Long> examIds) {
        if (examIds == null || examIds.isEmpty()) {
            return Arrays.asList(0, 0, 0, 0, 0);
        }
        List<String> examIdList = examIds.stream().map(e->String.valueOf(e)).collect(Collectors.toList());
        String examIdStrs = String.join(",", examIdList);
        String baseSql = "select count(1) from exam_examinee a " +
                "where a.examid in (" + examIdStrs + ") and a.state='已考' ";

        Integer c1 = getCountData(baseSql, 90, 100);
        Integer c2 = getCountData(baseSql, 80, 89);
        Integer c3 = getCountData(baseSql, 70, 79);
        Integer c4 = getCountData(baseSql, 60, 69);
        Integer c5 = getCountData(baseSql, 0, 59);

        return Arrays.asList(c1, c2, c3, c4, c5);
    }

    private Integer getCountData(String baseSql, Integer minScore, Integer maxScore) {
        baseSql += " and a.totalscore >= " + minScore + " and a.totalscore <= " + maxScore;
//        System.out.println("baseSql======================");
//        System.out.println(baseSql);
        List<Integer> countList = dao.findByNativeSql(baseSql, Integer.class);
        if (countList == null || countList.isEmpty()) {
            return 0;
        }
        return countList.get(0);
    }

    private String getFractionalStatisticsChart(String title, String subTitle, List<Integer> countDatas, List<Double> scaleDatas) {
        List<String> legendDatas = Arrays.asList("人数", "比例");
        List<String> categoryAxisDatas = Arrays.asList("90-100 优", "80-89 良", "70-79 中等", "60-69 及格", "小于60 不及格");
        List<String> yAxisFormatters = Arrays.asList("{value} 人", "{value} %");

        GsonOption option = new GsonOption();
        option.title(title); // 标题
        if (subTitle != null && subTitle.trim().length() > 0) {
            option.getTitle().setSubtext(subTitle);
        }
        Tooltip toolTip = new Tooltip();
        toolTip.setTrigger(Trigger.axis);
        option.setTooltip(toolTip);
        option.setCalculable(true);

        Legend legend = new Legend();
        legend.setData(legendDatas);
        option.setLegend(legend);

        CategoryAxis category = new CategoryAxis();
        category.setData(categoryAxisDatas);
        option.xAxis(category);

        List<Axis> valueAxisList = new ArrayList<>();
        for (int i = 0; i < legendDatas.size(); i++) {
            ValueAxis valueAxis = new ValueAxis();
            valueAxis.setName(legendDatas.get(i));
            AxisLabel axisLabel = new AxisLabel();
            axisLabel.setFormatter(yAxisFormatters.get(i));
            valueAxis.setAxisLabel(axisLabel);
            if (i == 0) {
                valueAxis.setSplitNumber(1);
            }
            valueAxisList.add(valueAxis);
        }
        option.yAxis(valueAxisList);

        List<Series> seriesList = new ArrayList<>();
        Bar bar = new Bar();
        bar.setName(legendDatas.get(0));
        bar.setData(countDatas);
        seriesList.add(bar);

        Line line = new Line();
        line.setName(legendDatas.get(1));
        line.setyAxisIndex(1);
        line.setData(scaleDatas);
        seriesList.add(line);
        option.setSeries(seriesList);

        return option.toString();
    }

    private String getExamDateStr(Date examStart) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        return format.format(examStart);
    }

    private String getExamTimeStr(Integer duration, Date examStart) {
        long endTime = examStart.getTime() + duration*60*1000;
        Date endDate = new Date(endTime);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String startDateTime = format.format(examStart);
        String startTimeStr = startDateTime.substring(startDateTime.length()-5);
        String endDateTime = format.format(endDate);
        String endTimeStr = endDateTime.substring(endDateTime.length()-5);
        return startTimeStr + "-" + endTimeStr;
    }

    private void updateDistributeStat(Long examId) {
        ExamCreateBean examCreateBean = examCreateBeanService.getEntity(examId);
        examCreateBean.setDistributeStat("已分配");
        examCreateBeanService.updateEntity(examCreateBean);
    }

    private List<Long> getOrgIdsByExamId(Long examId) {
        return dao.findByNativeSql("select orgid from exam_examinee where examid=?", Long.class, examId);
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

    private JsonData getResult(List<?> list, int count) {
        JsonData jsonData = new JsonData();
        if (list == null) {
            jsonData.setTotalCount(0L);
        } else {
            jsonData.setTotalCount(Long.valueOf(count));
        }
        jsonData.setData(list);
        return jsonData;
    }
}
