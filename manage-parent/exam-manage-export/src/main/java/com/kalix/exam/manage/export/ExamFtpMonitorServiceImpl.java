package com.kalix.exam.manage.export;

import com.kalix.exam.manage.api.biz.IExamExamineeBeanService;
import com.kalix.exam.manage.api.biz.IExamFtpMonitorService;
import com.kalix.exam.manage.dto.ExamineeRoomDto;
import com.kalix.exam.manage.export.utils.FtpUtils;
import com.kalix.framework.core.api.persistence.JsonData;
import com.kalix.framework.core.util.SerializeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExamFtpMonitorServiceImpl implements IExamFtpMonitorService {
    private IExamExamineeBeanService examExamineeBeanService;

    public void setExamExamineeBeanService(IExamExamineeBeanService examExamineeBeanService) {
        this.examExamineeBeanService = examExamineeBeanService;
    }

    @Override
    public JsonData getExamFileCommitInfos(Integer page, Integer limit, String jsonStr, String sort) {
        Map<String, String> jsonMap = SerializeUtil.json2Map(jsonStr);
        String subject = jsonMap.get("subjectVal");
        String startDate = jsonMap.get("dateBegin");
        String commitState = jsonMap.get("commitState");

        List<String> fileNames = FtpUtils.getFtpFileNames();
        List<ExamineeRoomDto> examineeRoomDtoList = examExamineeBeanService.getExamineeRoomsInfo(null, null, subject, startDate, null);

        Integer count = getExamFileCounts(commitState, fileNames, examineeRoomDtoList);

        List<ExamineeRoomDto> examineeRoomDtoPageList = getExamFileInfos(page, limit, commitState, fileNames, examineeRoomDtoList);
        return getResult(examineeRoomDtoPageList, count);
    }

    @Override
    public JsonData getExamIdentityPhotoInfos(Integer page, Integer limit, String jsonStr, String sort) {
        Map<String, String> jsonMap = SerializeUtil.json2Map(jsonStr);
        String subject = jsonMap.get("subjectVal");
        String startDate = jsonMap.get("dateBegin");

        List<String> fileNames = FtpUtils.getFtpIdentityPhotos();
        List<ExamineeRoomDto> examineeRoomDtoList = examExamineeBeanService.getExamineeRoomsInfo(null, null, subject, startDate, null);

        Integer count = getExamIdentityPhotoCounts(fileNames, examineeRoomDtoList);

        List<ExamineeRoomDto> examineeRoomDtoPageList = getExamIdentityPhotoInfos(page, limit, fileNames, examineeRoomDtoList);
        return getResult(examineeRoomDtoPageList, count);
    }

    private List<ExamineeRoomDto> getExamIdentityPhotoInfos(Integer page, Integer limit, List<String> fileNames, List<ExamineeRoomDto> examineeRoomDtoList) {
        if (fileNames == null || fileNames.isEmpty()) {
            return null;
        }

        if (examineeRoomDtoList != null && !examineeRoomDtoList.isEmpty()) {
            examineeRoomDtoList = filterExamineePhotoList(examineeRoomDtoList, fileNames);
            return getPagedExamineeRoomDtoList(examineeRoomDtoList, page, limit);
        } else {
            return null;
        }
    }

    private Integer getExamIdentityPhotoCounts(List<String> fileNames, List<ExamineeRoomDto> examineeRoomDtoList) {
        if (examineeRoomDtoList != null && !examineeRoomDtoList.isEmpty()) {
            examineeRoomDtoList = filterExamineePhotoList(examineeRoomDtoList, fileNames);
            return examineeRoomDtoList.size();
        } else {
            return 0;
        }
    }

    private List<ExamineeRoomDto> filterExamineePhotoList(List<ExamineeRoomDto> examineeRoomDtoList, List<String> fileNames) {
        ExamineeRoomDto examineeRoomDto = examineeRoomDtoList.get(0);
        Long examId = examineeRoomDto.getExamId();
        final List<String> ftpFiles = getFtpPhotoNames(examId, fileNames);
        examineeRoomDtoList = examineeRoomDtoList.stream().map(e -> {
            String fileName = e.getExamId() + "-" + e.getIdCards();
            Optional<String> ftpFileNameOpt = ftpFiles.stream().filter(f -> (f.toUpperCase().indexOf(fileName.toUpperCase()) != -1)).findFirst();
            if (ftpFileNameOpt.isPresent()) {
                e.setFileName(ftpFileNameOpt.get());
            }
            return e;
        }).collect(Collectors.toList());

        return examineeRoomDtoList;
    }

    private List<String> getFtpPhotoNames(Long examId, List<String> fileNames) {
        if (fileNames == null || fileNames.isEmpty()) {
            return null;
        }
        final String examIdStr = String.valueOf(examId);
        return fileNames.stream().filter(e-> {
            if (e.indexOf("-") != -1) {
                String[] fileArr = e.split("-");
                if (fileArr.length == 2) {
                    if (fileArr[0].endsWith(examIdStr)) {
                        return true;
                    }
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    private List<String> getFtpFileNames(Long examId, List<String> fileNames) {
        if (fileNames == null || fileNames.isEmpty()) {
            return null;
        }
        final String examIdStr = String.valueOf(examId);
        return fileNames.stream().filter(e-> {
            if (e.indexOf("-") != -1) {
                String[] fileArr = e.split("-");
                if (fileArr.length == 2) {
                    String[] nameArr = fileArr[1].split("\\.");
                    if (examIdStr.equals(nameArr[0])) {
                        return true;
                    }
                }
            }
            return false;
        }).collect(Collectors.toList());
    }
    private List<ExamineeRoomDto> getExamFileInfos(Integer page, Integer limit, String commitState, List<String> fileNames, List<ExamineeRoomDto> examineeRoomDtoList) {
        if ("2".equals(commitState) && (fileNames == null || fileNames.isEmpty())) {
            return null;
        }

        if (examineeRoomDtoList != null && !examineeRoomDtoList.isEmpty()) {
            examineeRoomDtoList = filterExamineeRoomDtoList(examineeRoomDtoList, commitState, fileNames);
            return getPagedExamineeRoomDtoList(examineeRoomDtoList, page, limit);
        } else {
            return null;
        }
    }

    private List<ExamineeRoomDto> getPagedExamineeRoomDtoList(List<ExamineeRoomDto> examineeRoomDtoList, Integer page, Integer limit) {
        int totalRowNum = examineeRoomDtoList.size();
        int totalPageNum = (totalRowNum - 1) / limit + 1;

        int fromIdx = (page - 1) * limit;
        int toIdx = page * limit;

        if (page == totalPageNum && totalPageNum * limit > totalRowNum) {
            toIdx = totalRowNum;
        }

        return examineeRoomDtoList.subList(fromIdx, toIdx);
    }

    private Integer getExamFileCounts(String commitState, List<String> fileNames, List<ExamineeRoomDto> examineeRoomDtoList) {
        if ("2".equals(commitState) && (fileNames == null || fileNames.isEmpty())) {
            return 0;
        }
        if (examineeRoomDtoList != null && !examineeRoomDtoList.isEmpty()) {
            examineeRoomDtoList = filterExamineeRoomDtoList(examineeRoomDtoList, commitState, fileNames);
            return examineeRoomDtoList.size();
        } else {
            return 0;
        }
    }

    private List<ExamineeRoomDto> filterExamineeRoomDtoList(List<ExamineeRoomDto> examineeRoomDtoList, String commitState, List<String> fileNames) {
        ExamineeRoomDto examineeRoomDto = examineeRoomDtoList.get(0);
        Long examId = examineeRoomDto.getExamId();
        final List<String> ftpFiles = getFtpFileNames(examId, fileNames);
        examineeRoomDtoList = examineeRoomDtoList.stream().map(e -> {
            String fileName = e.getIdCards() + "-" + e.getExamId();
            Optional<String> ftpFileNameOpt = ftpFiles.stream().filter(f -> (f.toUpperCase().indexOf(fileName.toUpperCase()) != -1)).findFirst();
            if (ftpFileNameOpt.isPresent()) {
                e.setFileName(ftpFileNameOpt.get());
            }
            return e;
        }).collect(Collectors.toList());
        if ("1".equals(commitState)) {
            examineeRoomDtoList = examineeRoomDtoList.stream().filter(e -> (e.getFileName() == null || "".equals(e.getFileName().trim())))
                    .collect(Collectors.toList());
        } else if ("2".equals(commitState)) {
            examineeRoomDtoList = examineeRoomDtoList.stream().filter(e -> (e.getFileName() != null && !"".equals(e.getFileName().trim())))
                    .collect(Collectors.toList());
        }
        return examineeRoomDtoList;
    }

    private JsonData getResult(List<?> list, int count) {
        JsonData jsonData = new JsonData();
        if (list == null) {
            list = new ArrayList();
            jsonData.setTotalCount(0L);
        } else {
            jsonData.setTotalCount(Long.valueOf(count));
        }
        jsonData.setData(list);
        return jsonData;
    }
}
