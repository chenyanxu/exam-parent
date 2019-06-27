package com.kalix.exam.manage.export;

import com.kalix.exam.manage.api.biz.IExamExamineeBeanService;
import com.kalix.exam.manage.api.export.IExamineeNumberCardInfoService;
import com.kalix.exam.manage.dto.ExamineeControlSheetDto;
import com.kalix.framework.core.util.SerializeUtil;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Date;

public class ExamineeNumberCardInfoServiceImpl implements IExamineeNumberCardInfoService {
    private SXSSFWorkbook wb = null;
    private IExamExamineeBeanService examExamineeBeanService;

    public void setExamExamineeBeanService(IExamExamineeBeanService examExamineeBeanService) {
        this.examExamineeBeanService = examExamineeBeanService;
    }

    @Override
    public void doExport(String jsonStr, HttpServletResponse response) {
        Map<String, String> jsonMap = SerializeUtil.json2Map(jsonStr);
        String subjectVal = jsonMap.get("subjectVal");
        String startDate = jsonMap.get("dateBegin");

        List<ExamineeControlSheetDto> examineeControlSheetInfos = null;
        if (subjectVal != null && startDate != null) {
            // 获取考试对照单基本信息
            examineeControlSheetInfos = examExamineeBeanService.getExamineeControlSheetInfos(subjectVal, startDate);
        }

        wb = new SXSSFWorkbook(500);
        String subject = "";
        String examTime = "";
        if (examineeControlSheetInfos != null && !examineeControlSheetInfos.isEmpty()) {
            ExamineeControlSheetDto examineeControlSheetDto = examineeControlSheetInfos.get(0);
            subject = examineeControlSheetDto.getSubject();
            examTime = getExamTimeScope(examineeControlSheetDto);
        }
        String sheetName = subject.length()>0 ? subject : "sheet1";
        SXSSFSheet sheet = wb.createSheet(sheetName);
        Integer startRow = 0;
        Integer startColumn = 0;
        sheet.setColumnWidth(startColumn, 13*256);
        sheet.setColumnWidth(startColumn + 1, 20*256);
        sheet.setColumnWidth(startColumn + 2, 30*256);
        sheet.setColumnWidth(startColumn + 3, 16*256);
        sheet.setColumnWidth(startColumn + 4, 11*256);
        sheet.setColumnWidth(startColumn + 5, 11*256);
        sheet.setColumnWidth(startColumn + 6, 9*256);

        // 创建header
        createHeader(sheet, startRow, startColumn);
        if (examineeControlSheetInfos != null && !examineeControlSheetInfos.isEmpty()) {
            createData(sheet, startRow + 1, startColumn, examTime, examineeControlSheetInfos);
        }

        try {
            writeFile(response, "考生信息.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createData(SXSSFSheet sheet, Integer startRow, Integer startColumn, String examTime, List<ExamineeControlSheetDto> examineeControlSheetInfos) {
        for (int i = 0; i < examineeControlSheetInfos.size(); i++) {
            ExamineeControlSheetDto examineeControlSheetDto = examineeControlSheetInfos.get(i);
            Integer dataRowNum = i + startRow;
            SXSSFRow dataRow = sheet.createRow(dataRowNum);
            dataRow.setHeightInPoints(27);
            // 姓名
            createCellData(dataRow, startColumn, examineeControlSheetDto.getName());
            // 准考证号
            createCellData(dataRow, startColumn + 1, examineeControlSheetDto.getExamCardNumber());
            // 身份证号
            createCellData(dataRow, startColumn + 2, examineeControlSheetDto.getIdCards());
            // 考试时间
            createCellData(dataRow, startColumn + 3, examTime);
            // 场次
            createCellData(dataRow, startColumn + 4, "");
            // 考场
            createCellData(dataRow, startColumn + 5, examineeControlSheetDto.getExamRoom());
            // 座号
            createCellData(dataRow, startColumn + 6, String.valueOf(examineeControlSheetDto.getExamRoomNo()));
        }
    }

    private void createCellData(SXSSFRow dataRow, int column, String value) {
        Cell cell = dataRow.createCell(column);
        cell.setCellStyle(getDataStyle());
        cell.setCellValue(new XSSFRichTextString(value));
    }

    private CellStyle getDataStyle() {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        makeBorder(style);
        Font dataFont = makeFont((short) 12, Font.BOLDWEIGHT_NORMAL);
        style.setFont(dataFont);
        return style;
    }

    private void createHeader(SXSSFSheet sheet, Integer startRow, Integer startColumn) {
        String[] headers = getHeaders();
        SXSSFRow headerRow = sheet.createRow(startRow);
        headerRow.setHeightInPoints(24);
        for (int i = 0; i < headers.length; i++) {
            Integer headerColumnIndex = i + startColumn;
            Cell headerCell = headerRow.createCell(headerColumnIndex);
            headerCell.setCellStyle(getHeadersStyle());
            headerCell.setCellValue(headers[i]);
        }
    }

    private CellStyle getHeadersStyle() {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        makeBorder(style);
        Font headerFont = makeFont((short) 12, Font.BOLDWEIGHT_BOLD);
        style.setFont(headerFont);
        return style;
    }

    private String[] getHeaders() {
        return new String[] {"姓名", "准考证号", "身份证号", "考试时间", "场次", "考场", "座号"};
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
                .append("\r").append(startArr[1]).append("-").append(endArr[1]);
        return sbf.toString();
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
