package com.kalix.exam.manage.export;

import com.kalix.exam.manage.api.biz.IExamExamineeBeanService;
import com.kalix.exam.manage.api.export.IExamineeControlSheetService;
import com.kalix.exam.manage.dto.ExamineeControlSheetDto;
import com.kalix.framework.core.util.SerializeUtil;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;

import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;


import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ExamineeControlSheetServiceImpl implements IExamineeControlSheetService {
    private SXSSFWorkbook wb = null;
    private IExamExamineeBeanService examExamineeBeanService;
    private String title = "吉林省高等教育自学考试考场对照单";
    private Integer columnSize = 7;

    public void setExamExamineeBeanService(IExamExamineeBeanService examExamineeBeanService) {
        this.examExamineeBeanService = examExamineeBeanService;
    }

    @Override
    public void doExport(String jsonStr, HttpServletResponse response) {
        Map<String, String> jsonMap = SerializeUtil.json2Map(jsonStr);
        String subjectVal = jsonMap.get("subjectVal");
        String startDate = jsonMap.get("dateBegin");
        // 获取考试对照单基本信息
        List<ExamineeControlSheetDto> examineeControlSheetInfos =
                examExamineeBeanService.getExamineeControlSheetInfos(subjectVal, startDate);

        wb = new SXSSFWorkbook(500);

        if (examineeControlSheetInfos != null && !examineeControlSheetInfos.isEmpty()) {
            // 照片文件路径设置
            setExamineePhotoPath(examineeControlSheetInfos, startDate);
            ExamineeControlSheetDto examineeControlSheetDto = examineeControlSheetInfos.get(0);
            String subject = "考试科目：" + examineeControlSheetDto.getSubject() + "（实践）";
            String examTime = "考试时间：" + getExamTimeScope(examineeControlSheetDto);

            Map<String, List<ExamineeControlSheetDto>> examineeControlSheetDtoList =
                            examineeControlSheetInfos.stream()
                            .collect(Collectors.groupingBy(ExamineeControlSheetDto::getExamRoom));

            if (examineeControlSheetDtoList != null && !examineeControlSheetDtoList.isEmpty()) {
                for (Map.Entry<String, List<ExamineeControlSheetDto>> examineeControlSheetDtoMap : examineeControlSheetDtoList.entrySet()) {
                    String examRoom = examineeControlSheetDtoMap.getKey();
                    List<ExamineeControlSheetDto> examineeControlSheetList = examineeControlSheetDtoMap.getValue();
                    String finalTitle = title + "("+examRoom+")";
                    if (examineeControlSheetList != null && !examineeControlSheetList.isEmpty()) {
                        // 按座号排序
                        examineeControlSheetList = examineeControlSheetList.stream().sorted((e1, e2)->e1.getExamRoomNo().compareTo(e2.getExamRoomNo()))
                                .collect(Collectors.toList());
                        // 数据导出
                        SXSSFSheet sheet = wb.createSheet(examRoom);

                        Integer startRow = 0;
                        Integer startColumn = 1;
                        sheet.setColumnWidth(0, 8*256);
                        for (int i=0; i<columnSize; i++) {
                            sheet.setColumnWidth(startColumn + i, 10*256);
                        }

                        // 创建标题行
                        createTitle(sheet, startRow, startColumn, finalTitle);
                        // 创建考试科目行
                        createSubTitle(sheet, startRow + 1, startColumn, subject);
                        // 创建考试时间行
                        createSubTitle(sheet, startRow + 2, startColumn, examTime);
                        // 创建照片数据
                        createExamineeData(sheet, startRow + 4, startColumn, examineeControlSheetList);
                    }
                }
            }

        }

        try {
            writeFile(response, "考场对照信息.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createExamineeData(SXSSFSheet sheet, Integer startRow, Integer startColumn, List<ExamineeControlSheetDto> examineeControlSheetList) {
        Integer photoStartRow = startRow;
        SXSSFRow photoRow = null;
        SXSSFRow infoRow = null;
        SXSSFRow signRow = null;
        SXSSFRow blankRow = null;
        for (int i=0; i<examineeControlSheetList.size(); i++) {
            ExamineeControlSheetDto examineeControlSheetDto = examineeControlSheetList.get(i);
            if (i%columnSize == 0 && i > 0) {
                photoStartRow += 4;
            }
            if (i%columnSize == 0) {
                photoRow = sheet.createRow(photoStartRow);
                infoRow = sheet.createRow(photoStartRow+1);
                signRow = sheet.createRow(photoStartRow+2);
                blankRow = sheet.createRow(photoStartRow+3);
            }
            // 照片
            createPhotoInfo(sheet, photoRow, photoStartRow, startColumn + (i%columnSize), examineeControlSheetDto.getPhotoPath());
            // 考生信息
            createExamineeInfo(infoRow, startColumn + (i%columnSize), examineeControlSheetDto);
            // 签字框
            createSignInfo(signRow, startColumn + (i%columnSize));
            // 空白行
            createBlankRow(blankRow);
        }
    }

    private void createSignInfo(SXSSFRow signRow, Integer startColumn) {
        if (signRow == null) {
            return;
        }
        Cell signCell = signRow.createCell(startColumn);
        signCell.setCellStyle(getSignStyle());
        signCell.setCellValue("签字:");
    }

    private void createBlankRow(SXSSFRow blankRow) {
        if (blankRow == null) {
            return;
        }
        blankRow.setHeightInPoints(7);
    }

    private void createExamineeInfo(SXSSFRow infoRow, Integer startColumn,ExamineeControlSheetDto examineeControlSheetDto) {
        if (infoRow == null) {
            return;
        }
        String name = examineeControlSheetDto.getName();
        Integer roomNo = examineeControlSheetDto.getExamRoomNo();
        String examCardNumber = examineeControlSheetDto.getExamCardNumber();
        String roomNoStr = roomNo < 10 ? "0" + String.valueOf(roomNo) : String.valueOf(roomNo);
        String infoContent = roomNoStr + " " + name + "\r" + examCardNumber;
        infoRow.setHeightInPoints(29);
        Cell infoCell = infoRow.createCell(startColumn);
        infoCell.setCellStyle(getInfoStyle());
        infoCell.setCellValue(new XSSFRichTextString(infoContent));
    }

    private void createPhotoInfo(SXSSFSheet sheet, SXSSFRow photoRow, Integer startRow, Integer startColumn, String photoPath) {
        try {
            if (photoRow == null) {
                return;
            }
            photoRow.setHeightInPoints(83);
            Cell photoCell = photoRow.createCell(startColumn);
            photoCell.setCellStyle(getPhotoStyle());
            if (photoPath != null && photoPath.trim().length() > 0) {
                BufferedImage bufferImage = ImageIO.read(new File(photoPath));
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferImage, "GIF", byteArrayOutputStream);
                // InputStream imputStream = new FileInputStream(new File(photoPath));
                // byte[] pictureData = readInputStream(imputStream);
                int pictureIdx = wb.addPicture(byteArrayOutputStream.toByteArray(), wb.PICTURE_TYPE_PNG);
                XSSFDrawing drawing = (XSSFDrawing)sheet.createDrawingPatriarch();
                XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, startColumn, startRow, startColumn + 1, startRow + 1);
                anchor.setAnchorType(3);
                drawing.createPicture(anchor, pictureIdx);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[4096];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        while((len=inStream.read(buffer)) != -1){
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        inStream.close();
        //把outStream里的数据写入内存
        return outStream.toByteArray();
    }

    private void createTitle(SXSSFSheet sheet, Integer startRow, Integer startColumn, String title) {
        SXSSFRow titleRow = sheet.createRow(startRow);
        titleRow.setHeightInPoints(20);
        Cell titleCell = titleRow.createCell(startColumn);
        titleCell.setCellStyle(getTitleStyle());
        titleCell.setCellValue(new XSSFRichTextString(title));
        for (int i=startColumn; i < columnSize; i++) {
            Cell titleTempCell = titleRow.createCell(startColumn + i);
            titleTempCell.setCellStyle(getTitleStyle());
            titleTempCell.setCellValue("");
        }
        sheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(),
                titleRow.getRowNum(), titleCell.getColumnIndex(), columnSize));
    }

    private void createSubTitle(SXSSFSheet sheet, Integer startRow, Integer startColumn, String content) {
        SXSSFRow contentRow = sheet.createRow(startRow);
        Cell titleCell = contentRow.createCell(startColumn);
        titleCell.setCellStyle(getContentStyle());
        titleCell.setCellValue(new XSSFRichTextString(content));
        for (int i=startColumn; i < columnSize; i++) {
            Cell titleTempCell = contentRow.createCell(startColumn + i);
            titleTempCell.setCellStyle(getContentStyle());
            titleTempCell.setCellValue("");
        }
        sheet.addMergedRegion(new CellRangeAddress(contentRow.getRowNum(),
                contentRow.getRowNum(), titleCell.getColumnIndex(), columnSize));
    }

    private void setExamineePhotoPath(List<ExamineeControlSheetDto> examineeControlSheetInfos, String startDate) {
        String[] dateArr = startDate.split("-");
        String month = dateArr[1];
        String photoDir = month.length()==1 ? dateArr[0] + "0" + month : dateArr[0] + month;
        String photoPath = "D:/examPhone/" + photoDir;
        File photoDirectory = new File(photoPath);
        if (photoDirectory.isDirectory()) {
            String[] fileNames = photoDirectory.list();
            List<String> fileNameList = Arrays.asList(fileNames);
            if (fileNameList != null && !fileNameList.isEmpty()) {
                examineeControlSheetInfos.stream().map(e->{
                    String idCards = e.getIdCards().toUpperCase();
                    Optional<String> fileNameOptional = fileNameList.stream().filter(f->f.indexOf(idCards.toUpperCase()) != -1).findFirst();
                    if (fileNameOptional.isPresent()) {
                        String fileName = fileNameOptional.get();
                        if (fileName != null && fileName.length() > 0) {
                            e.setPhotoPath(photoPath + "/" + fileName);
                        }
                    }
                    return e;
                }).collect(Collectors.toList());
            }
        }
    }

    private String getExamTimeScope(ExamineeControlSheetDto examineeControlSheetDto) {
        Date examStart = examineeControlSheetDto.getExamStart();
        Integer duration = examineeControlSheetDto.getDuration();
        long examEndTime = examStart.getTime() + duration*60*1000;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String startStr = sdf.format(examStart.getTime());
        String endStr = sdf.format(examEndTime);
        String[] startArr = startStr.split(" ");
        String[] endArr = endStr.split(" ");
        String[] dateArr = startArr[0].split("-");
        StringBuffer sbf = new StringBuffer();
        sbf.append(dateArr[0]).append("年").append(dateArr[1]).append("月").append(dateArr[2]).append("日")
                .append(" ").append(startArr[1]).append("-").append(endArr[1]);
        return sbf.toString();
    }

    /**
     * 输出到客户端
     *
     * @param fileName 输出文件名
     */
    public void writeFile(HttpServletResponse response, String fileName) throws IOException {
        response.reset();
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with,content-type");
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        write(response.getOutputStream());
    }

    private CellStyle getTitleStyle() {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        Font titleFont = makeFont((short) 14, Font.BOLDWEIGHT_BOLD);
        style.setFont(titleFont);
        return style;
    }

    private CellStyle getContentStyle() {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        Font contentFont = makeFont((short) 11, Font.BOLDWEIGHT_NORMAL);
        style.setFont(contentFont);
        return style;
    }

    private CellStyle getSignStyle() {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        makeBorder(style);
        Font contentFont = makeFont((short) 8, Font.BOLDWEIGHT_NORMAL);
        style.setFont(contentFont);
        return style;
    }

    private CellStyle getInfoStyle() {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setWrapText(true);
        makeBorder(style);
        Font infoFont = makeFont((short) 8, Font.BOLDWEIGHT_NORMAL);
        style.setFont(infoFont);
        return style;
    }

    private CellStyle getPhotoStyle() {
        CellStyle style = wb.createCellStyle();
        makeBorder(style);
        return style;
    }

    private Font makeFont(short size, short weight) {
        Font font = wb.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints(size);
        font.setBoldweight(weight);
        return font;
    }

    private void makeBorder(CellStyle style) {
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
    }

    /**
     * 输出数据流
     *
     * @param os 输出数据流
     */
    public void write(OutputStream os) throws IOException {
        wb.write(os);
        dispose();
    }

    /**
     * 清理临时文件
     */
    public void dispose() {
        wb.dispose();
    }
}
