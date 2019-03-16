package com.kalix.exam.manage.export;

import com.kalix.exam.manage.api.biz.IExamCreateBeanService;
import com.kalix.exam.manage.api.export.IExamExportService;
import com.kalix.exam.manage.dto.ExamResultsDto;
import com.kalix.framework.core.util.SerializeUtil;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class ExamExportServiceImpl implements IExamExportService {

    private SXSSFWorkbook wb = null;
    private final String title = "2019年上半年吉林省高等教育自学考试\n实践性环节考核成绩单";
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
        ExamResultsDto examResultsDto = examResultsDtoList.get(0);
        String firstTeacher = examResultsDto.getFirstTeacher();
        String secondTeacher = examResultsDto.getSecondTeacher();
        String groupLeader = examResultsDto.getGroupLeader();
        wb = new SXSSFWorkbook(500);
        SXSSFSheet sheet = wb.createSheet(subjectVal);
        // 创建title
        createTitle(sheet);
        // 创建header
        createHeader(sheet);
        // 创建数据
        int size = 0; //createData(sheet, examResultsDtoList);
        // 创建结尾
        createfooter(sheet, size, firstTeacher, secondTeacher, groupLeader);

        try {
            writeFile(response, "汇总数据.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private int createData(SXSSFSheet sheet, List<ExamResultsDto> examResultsDtoList) {
        for (int i=0; i<examResultsDtoList.size(); i++) {
            ExamResultsDto examResultsDto = examResultsDtoList.get(i);
            SXSSFRow dataRow = sheet.createRow(i + 2);
            // 序号
            createCellData(dataRow, 0, String.valueOf((i+1)));
            // 准考证号
            createCellData(dataRow, 1, examResultsDto.getExamCardNumber());
            // 姓名
            createCellData(dataRow, 2, examResultsDto.getName());
            // 身份证号
            createCellData(dataRow, 3, examResultsDto.getIdCards());
            // 科目
            createCellData(dataRow, 4, examResultsDto.getSubject());
            // 总成绩
            createCellData(dataRow, 5, String.valueOf(examResultsDto.getTotalScore()));
        }
        return examResultsDtoList.size();
    }

    private void createCellData(SXSSFRow dataRow, int column, String value) {
        Cell cell = dataRow.createCell(column);
        cell.setCellStyle(getHeaderStyle());
        cell.setCellValue(value);
    }

    private void createfooter(SXSSFSheet sheet, int dataSize, String firstTeacher,
                     String secondTeacher, String groupLeader) {
        SXSSFRow footerRow = sheet.createRow(dataSize + 2); //从0算起
        Cell firstCell = footerRow.createCell(0);
        firstCell.setCellStyle(getHeaderStyle());
        firstCell.setCellValue("");

        Cell firstTeacherCell = footerRow.createCell(1);
        firstTeacherCell.setCellStyle(getHeaderStyle());
        firstTeacherCell.setCellValue("初阅人：" + firstTeacher);
        sheet.addMergedRegion(new CellRangeAddress(footerRow.getRowNum(),
                footerRow.getRowNum(), 1, 2));

        Cell secondTeacherCell = footerRow.createCell(3);
        secondTeacherCell.setCellStyle(getHeaderStyle());
        secondTeacherCell.setCellValue("复阅人：" + secondTeacher);

        Cell groupLeaderCell = footerRow.createCell(4);
        groupLeaderCell.setCellStyle(getHeaderStyle());
        groupLeaderCell.setCellValue("组长：" + firstTeacher);
        sheet.addMergedRegion(new CellRangeAddress(footerRow.getRowNum(),
                footerRow.getRowNum(), 4, 5));
    }

    private String[] getHeaders() {
        return new String[] {"序号","准考证号","姓名","身份证号","科目","总成绩"};
    }

    private void createHeader(SXSSFSheet sheet) {
        String[] headers = getHeaders();
        SXSSFRow headerRow = sheet.createRow(1);
        for (int i = 0; i < headers.length; i++) {
            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellStyle(getHeaderStyle());
            headerCell.setCellValue(headers[i]);
        }
    }

    private void createTitle(SXSSFSheet sheet) {
        SXSSFRow titleRow = sheet.createRow(0);
        titleRow.setHeightInPoints(54);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellStyle(getTitleStyle());
        titleCell.setCellValue(title);
        sheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(),
                titleRow.getRowNum(), titleRow.getRowNum(), getHeaders().length - 1));
    }

    private CellStyle getHeaderStyle() {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        makeBorder(style);
        Font headerFont = makeFont((short) 11);
        style.setFont(headerFont);
        return style;
    }

    private CellStyle getTitleStyle() {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        makeBorder(style);
        Font titleFont = makeFont((short) 16);
        style.setFont(titleFont);
        return style;
    }

    private Font makeFont(short size) {
        Font font = wb.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints(size);
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
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
