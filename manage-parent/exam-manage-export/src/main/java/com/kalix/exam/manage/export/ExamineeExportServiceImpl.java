package com.kalix.exam.manage.export;

import com.kalix.exam.manage.api.biz.IExamExamineeBeanService;
import com.kalix.exam.manage.api.export.IExamineeExportService;
import com.kalix.exam.manage.dto.ExamineeRoomDto;
import com.kalix.framework.core.util.SerializeUtil;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class ExamineeExportServiceImpl implements IExamineeExportService {
    private SXSSFWorkbook wb = null;
    private IExamExamineeBeanService examExamineeBeanService;

    public void setExamExamineeBeanService(IExamExamineeBeanService examExamineeBeanService) {
        this.examExamineeBeanService = examExamineeBeanService;
    }

    @Override
    public void doExport(String jsonStr, HttpServletResponse response) {
        Map<String, String> jsonMap = SerializeUtil.json2Map(jsonStr);
        String subject = jsonMap.get("subjectVal");
        String startDate = jsonMap.get("dateBegin");
        String state = jsonMap.get("examState");
        List<ExamineeRoomDto> examineeRoomDtoList = null;

        if (subject != null && startDate != null) {
            examineeRoomDtoList = examExamineeBeanService.getExamineeRoomsInfo(null, null, subject, startDate, state);
        }

        wb = new SXSSFWorkbook(500);
        SXSSFSheet sheet = wb.createSheet(subject);
        Integer startRow = 0;
        Integer startColumn = 0;
        sheet.setColumnWidth(startColumn, 16*256);
        sheet.setColumnWidth(startColumn + 1, 13*256);
        sheet.setColumnWidth(startColumn + 2, 10*256);
        sheet.setColumnWidth(startColumn + 3, 14*256);
        sheet.setColumnWidth(startColumn + 4, 20*256);
        sheet.setColumnWidth(startColumn + 5, 10*256);
        // 创建header
        createHeader(sheet, startRow, startColumn);
        // 创建数据
        if (examineeRoomDtoList != null && !examineeRoomDtoList.isEmpty()) {
            createData(sheet, startRow, startColumn, examineeRoomDtoList);
        }

        try {
            writeFile(response, "考生信息.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void createData(SXSSFSheet sheet, Integer startRow, Integer startColumn ,List<ExamineeRoomDto> examineeRoomDtoList) {
        for (int i=0; i<examineeRoomDtoList.size(); i++) {
            ExamineeRoomDto examineeRoomDto = examineeRoomDtoList.get(i);
            Integer dataRowNum = i + startRow + 1;
            SXSSFRow dataRow = sheet.createRow(dataRowNum);
            dataRow.setHeightInPoints(20);
            // 考场
            createCellData(dataRow, startColumn, examineeRoomDto.getExamRoom());
            // 考场座号
            String examRoomNo = "";
            if (examineeRoomDto.getExamRoomNo() != null) {
                examRoomNo = String.valueOf(examineeRoomDto.getExamRoomNo());
            }
            createCellData(dataRow, startColumn + 1, examRoomNo);
            // 考生
            createCellData(dataRow, startColumn + 2, examineeRoomDto.getName());
            // 准考证号
            createCellData(dataRow, startColumn + 3, examineeRoomDto.getExamCardNumber());
            // 身份证号
            createCellData(dataRow, startColumn + 4, examineeRoomDto.getIdCards());
            // 参考状态
            createCellData(dataRow, startColumn + 5, examineeRoomDto.getState());
        }
    }

    private void createCellData(SXSSFRow dataRow, int column, String value) {
        Cell cell = dataRow.createCell(column);
        cell.setCellStyle(getDataStyle());
        cell.setCellValue(value);
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

    private String[] getHeaders() {
        return new String[] {"考场","考场座号","考生","准考证号","身份证号","参考状态"};
    }

    private void createHeader(SXSSFSheet sheet, Integer startRow, Integer startColumn) {
        String[] headers = getHeaders();
        SXSSFRow headerRow = sheet.createRow(startRow);
        headerRow.setHeightInPoints(20);
        for (int i = 0; i < headers.length; i++) {
            Integer headerColumnIndex = i + startColumn;
            Cell headerCell = headerRow.createCell(headerColumnIndex);
            headerCell.setCellStyle(getHeaderStyle());
            headerCell.setCellValue(headers[i]);
        }
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
}
