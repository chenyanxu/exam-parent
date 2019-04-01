package com.kalix.exam.manage.export;

import com.kalix.exam.manage.api.biz.IExamCreateBeanService;
import com.kalix.exam.manage.api.export.IExamExportService;
import com.kalix.exam.manage.dto.ExamResultsDto;
import com.kalix.exam.manage.entities.ExamCreateBean;
import com.kalix.framework.core.util.SerializeUtil;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ExamExportServiceImpl implements IExamExportService {

    private SXSSFWorkbook wb = null;
    private String title = "吉林省高等教育自学考试\r实践性环节考核成绩单";
    private IExamCreateBeanService examCreateBeanService;

    public void setExamCreateBeanService(IExamCreateBeanService examCreateBeanService) {
        this.examCreateBeanService = examCreateBeanService;
    }

    @Override
    public void doExport(String jsonStr, HttpServletResponse response) {
        Map<String, String> jsonMap = SerializeUtil.json2Map(jsonStr);
        String subjectVal = jsonMap.get("subjectVal");

        List<ExamResultsDto> examResultsDtoList = examCreateBeanService.getExamResultDtoList(subjectVal);
//        if (examResultsDtoList == null || examResultsDtoList.isEmpty()) {
//            return;
//        }

        wb = new SXSSFWorkbook(500);
        SXSSFSheet sheet = wb.createSheet(subjectVal);
        Integer startRow = 1;
        Integer startColumn = 1;
        sheet.setColumnWidth(0, 3*256);
        sheet.setColumnWidth(startColumn, 5*256);
        sheet.setColumnWidth(startColumn + 1, 15*256);
        sheet.setColumnWidth(startColumn + 2, 12*256);
        sheet.setColumnWidth(startColumn + 3, 20*256);
        sheet.setColumnWidth(startColumn + 4, 15*256);
        sheet.setColumnWidth(startColumn + 5, 12*256);
        // 创建title
        Long examId = null;
        if (examResultsDtoList != null && !examResultsDtoList.isEmpty()) {
            ExamResultsDto examResultsDto = examResultsDtoList.get(0);
            examId = examResultsDto.getExamId();
        }
        createTitle(sheet, startRow, startColumn, examId);

        // 创建header
        createHeader(sheet, startRow, startColumn);
        // 创建数据
        int size = 0;
        if (examResultsDtoList != null && !examResultsDtoList.isEmpty()) {
            size = createData(sheet, startRow, startColumn, examResultsDtoList);
        }
        // 创建结尾
        String firstTeacher = "";
        String secondTeacher = "";
        String groupLeader = "";
        // 修改为手工签字
        if (examResultsDtoList != null && !examResultsDtoList.isEmpty()) {
//            ExamResultsDto examResultsDto = examResultsDtoList.get(0);
//            firstTeacher = examResultsDto.getFirstTeacher();
//            secondTeacher = examResultsDto.getSecondTeacher();
//            groupLeader = examResultsDto.getGroupLeader();
        }
        createfooter(sheet, size, startRow, startColumn, firstTeacher, secondTeacher, groupLeader);

        try {
            writeFile(response, "汇总数据.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private int createData(SXSSFSheet sheet, Integer startRow, Integer startColumn ,List<ExamResultsDto> examResultsDtoList) {
        for (int i=0; i<examResultsDtoList.size(); i++) {
            ExamResultsDto examResultsDto = examResultsDtoList.get(i);
            Integer dataRowNum = i + startRow + 2;
            SXSSFRow dataRow = sheet.createRow(dataRowNum);
            dataRow.setHeightInPoints(20);
            // 序号
            createCellData(dataRow, startColumn, String.valueOf((i+1)));
            // 准考证号
            createCellData(dataRow, startColumn + 1, examResultsDto.getExamCardNumber());
            // 姓名
            createCellData(dataRow, startColumn + 2, examResultsDto.getName());
            // 身份证号
            createCellData(dataRow, startColumn + 3, examResultsDto.getIdCards());
            // 科目
            createCellData(dataRow, startColumn + 4, examResultsDto.getSubject());
            // 总成绩
            createCellData(dataRow, startColumn + 5, String.valueOf(examResultsDto.getTotalScore()));
        }
        return examResultsDtoList.size();
    }

    private void createCellData(SXSSFRow dataRow, int column, String value) {
        Cell cell = dataRow.createCell(column);
        cell.setCellStyle(getDataStyle());
        cell.setCellValue(value);
    }

    private void createfooter(SXSSFSheet sheet, int dataSize, int startRow, int startColumn, String firstTeacher,
                     String secondTeacher, String groupLeader) {
        int footerRowNum = startRow + dataSize + 2;
        SXSSFRow footerRow = sheet.createRow(footerRowNum); //从0算起
        footerRow.setHeightInPoints(24);
        Cell firstCell = footerRow.createCell(startColumn);
        firstCell.setCellStyle(getFooterStyle());
        firstCell.setCellValue("");

        Cell firstTeacherCell = footerRow.createCell(startColumn + 1);
        firstTeacherCell.setCellStyle(getFooterStyle());
        firstTeacherCell.setCellValue("初阅人：" + firstTeacher);
        Cell firstTeacherTempCell = footerRow.createCell(startColumn + 2);
        firstTeacherTempCell.setCellStyle(getFooterStyle());
        firstTeacherTempCell.setCellValue("");
        sheet.addMergedRegion(new CellRangeAddress(footerRow.getRowNum(),
                footerRow.getRowNum(), startColumn + 1, startColumn + 2));

        Cell secondTeacherCell = footerRow.createCell(startColumn + 3);
        secondTeacherCell.setCellStyle(getFooterStyle());
        secondTeacherCell.setCellValue("复阅人：" + secondTeacher);

        Cell groupLeaderCell = footerRow.createCell(startColumn + 4);
        groupLeaderCell.setCellStyle(getFooterStyle());
        groupLeaderCell.setCellValue("组长：" + groupLeader);
        Cell groupLeaderTempCell = footerRow.createCell(startColumn + 5);
        groupLeaderTempCell.setCellStyle(getFooterStyle());
        groupLeaderTempCell.setCellValue("");
        sheet.addMergedRegion(new CellRangeAddress(footerRow.getRowNum(),
                footerRow.getRowNum(), startColumn + 4, startColumn + 5));

        // 日期行
        SXSSFRow footerDateRow = sheet.createRow(footerRowNum+1);
        footerDateRow.setHeightInPoints(24);
        Cell dateCell = footerDateRow.createCell(startColumn + 4);
        dateCell.setCellStyle(getFooterDateStyle());
        dateCell.setCellValue("日期：");

    }

    private String[] getHeaders() {
        return new String[] {"序号","准考证号","姓名","身份证号","科目","总成绩"};
    }

    private void createHeader(SXSSFSheet sheet, Integer startRow, Integer startColumn) {
        Integer headerRowNum = startRow + 1;
        String[] headers = getHeaders();
        SXSSFRow headerRow = sheet.createRow(headerRowNum);
        headerRow.setHeightInPoints(20);
        for (int i = 0; i < headers.length; i++) {
            Integer headerColumnIndex = i + startColumn;
            Cell headerCell = headerRow.createCell(headerColumnIndex);
            headerCell.setCellStyle(getHeaderStyle());
            headerCell.setCellValue(headers[i]);
        }
    }

    private void createTitle(SXSSFSheet sheet, Integer startRow, Integer startColumn, Long examId) {
        String title = getExportTitle(examId);
        SXSSFRow titleRow = sheet.createRow(startRow);
        titleRow.setHeightInPoints(54);
        Cell titleCell = titleRow.createCell(startColumn);
        titleCell.setCellStyle(getTitleStyle());
        titleCell.setCellValue(new XSSFRichTextString(title));
        int colNum = getHeaders().length;
        for (int i=startColumn; i < colNum; i++) {
            Cell titleTempCell = titleRow.createCell(startColumn + i);
            titleTempCell.setCellStyle(getTitleStyle());
            titleTempCell.setCellValue("");
        }
        sheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(),
                titleRow.getRowNum(), titleCell.getColumnIndex(), getHeaders().length));
    }

    private String getExportTitle(Long examId) {
        ExamCreateBean examCreateBean = null;
        if (examId != null) {
            examCreateBean = examCreateBeanService.getEntity(examId);
        }
        if (examCreateBean == null) {
            return title;
        }
        Date startDate = examCreateBean.getExamStart();
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        String term = "";
        if (month < 6) {
            term = "上半年";
        } else {
            term = "下半年";
        }
        return year + "年" + term + title;
    }

    private CellStyle getDataStyle() {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        makeBorder(style);
        Font dataFont = makeFont((short) 11, Font.BOLDWEIGHT_NORMAL);
        style.setFont(dataFont);
        return style;
    }

    private CellStyle getHeaderStyle() {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        makeBorder(style);
        Font headerFont = makeFont((short) 11, Font.BOLDWEIGHT_BOLD);
        style.setFont(headerFont);
        return style;
    }

    private CellStyle getFooterStyle() {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        makeBorder(style);
        Font headerFont = makeFont((short) 11, Font.BOLDWEIGHT_BOLD);
        style.setFont(headerFont);
        return style;
    }

    private CellStyle getFooterDateStyle() {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_RIGHT);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        Font headerFont = makeFont((short) 11, Font.BOLDWEIGHT_BOLD);
        style.setFont(headerFont);
        return style;
    }

    private CellStyle getTitleStyle() {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setWrapText(true);
        makeBorder(style);
        Font titleFont = makeFont((short) 16, Font.BOLDWEIGHT_BOLD);
        style.setFont(titleFont);
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
