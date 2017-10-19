package com.rzsd.wechat.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

public class ExcelReaderUtil {

    @SuppressWarnings("resource")
    public static XSSFSheet getSheet(String excelPath, String sheetName) throws IOException {
        if (StringUtils.isEmpty(excelPath)) {
            throw new IllegalArgumentException("Excel路径未设置。");
        }
        if (StringUtils.isEmpty(sheetName)) {
            throw new IllegalArgumentException("Excel的Sheet名路径未设置。");
        }
        XSSFWorkbook xssfWorkbook = null;
        try (InputStream is = new FileInputStream(excelPath)) {
            xssfWorkbook = new XSSFWorkbook(is);
        } catch (IOException e) {
            xssfWorkbook = null;
            throw e;
        }

        return xssfWorkbook.getSheet(sheetName);
    }

    public static XSSFWorkbook getBook(String excelPath) throws IOException {
        if (StringUtils.isEmpty(excelPath)) {
            throw new IllegalArgumentException("Excel路径未设置。");
        }
        XSSFWorkbook xssfWorkbook = null;
        try (InputStream is = new FileInputStream(excelPath)) {
            xssfWorkbook = new XSSFWorkbook(is);
        } catch (IOException e) {
            xssfWorkbook = null;
            throw e;
        }

        return xssfWorkbook;
    }

    public static List<String> getRowValues(XSSFRow row) {
        List<String> res = new ArrayList<String>();
        if (row == null) {
            return res;
        }
        for (int i = 0; i < row.getLastCellNum(); i++) {
            if (row.getCell(i) != null && !StringUtils.isEmpty(getCellValue(row, i))) {
                res.add(String.valueOf(getCellValue(row, i)));
            }
        }
        return res;
    }

    public static String getCellValue(XSSFSheet sheet, int rowNum, int cellNum) {
        XSSFRow row = sheet.getRow(rowNum);
        return getCellValue(row, cellNum);
    }

    public static String getCellValue(XSSFRow row, int cellNum) {
        if (row == null) {
            return null;
        }
        XSSFCell cell = row.getCell(cellNum);
        if (cell == null) {
            return null;
        }

        String cellVal;
        switch (cell.getCellType()) {
        case XSSFCell.CELL_TYPE_STRING:
            cellVal = cell.getStringCellValue();
            break;
        case XSSFCell.CELL_TYPE_NUMERIC:
            // 日付判定
            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                cellVal = formatter.format(cell.getDateCellValue());
            } else {
                NumberFormat nf = NumberFormat.getInstance();
                nf.setGroupingUsed(false);
                cellVal = nf.format(cell.getNumericCellValue());
            }
            break;
        case XSSFCell.CELL_TYPE_BOOLEAN:
            cellVal = String.valueOf(cell.getBooleanCellValue());
            break;
        case XSSFCell.CELL_TYPE_BLANK:
            cellVal = "";
            break;
        default:
            cellVal = "";
            break;
        }
        return cellVal;
    }
}
