package ru.bpc.billing.util.poi;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * User: Krainov
 * Date: 13.11.13
 * Time: 17:06
 */
public class PoiSSUtil {

    public static Workbook createWorkBookFromTemplate(String templatePath) {
        return createWorkBookFromTemplate(new ClassPathResource(templatePath));
    }

    public static Workbook createWorkBookFromTemplate(Resource resource) {
        try {
            InputStream xlsTemplate = new FileInputStream(resource.getFile().getAbsoluteFile());
            return new XSSFWorkbook(xlsTemplate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Cell createCellAndSetValue(Row row, int cellNumber, String value) {
        Cell cell = row.createCell(cellNumber);
        cell.setCellValue(value);
        return cell;
    }

    public static Cell createCellAndSetValue(Row row, int cellNumber, String value, CellStyle cellStyle) {
        Cell cell = row.createCell(cellNumber);
        cell.setCellValue(value);
        if ( null != cellStyle ) cell.setCellStyle(cellStyle);
        return cell;
    }

    public static Cell createCellAndSetValue(Row row, int cellNumber, double value) {
        Cell cell = row.createCell(cellNumber, Cell.CELL_TYPE_NUMERIC);
        cell.setCellValue(value);
        return cell;
    }

    public static Cell createCellAndSetValue(Row row, int cellNumber, double value, CellStyle cellStyle) {
        Cell cell = row.createCell(cellNumber, Cell.CELL_TYPE_NUMERIC);
        cell.setCellValue(value);
        if ( null != cellStyle ) cell.setCellStyle(cellStyle);
        return cell;
    }

    public static Cell createCellAndSetFormula(Row row, int cellNumber, String formula) {
        Cell cell = row.createCell(cellNumber, Cell.CELL_TYPE_NUMERIC);
        if (StringUtils.isBlank(formula) ) return cell;
        cell.setCellFormula(formula);
        return cell;
    }

    public static Cell createCellAndSetFormula(Row row, int cellNumber, String formula, CellStyle cellStyle) {
        Cell cell = row.createCell(cellNumber, Cell.CELL_TYPE_NUMERIC);
        if (StringUtils.isBlank(formula) ) return cell;
        cell.setCellFormula(formula);
        if ( null != cellStyle ) cell.setCellStyle(cellStyle);
        return cell;
    }

    public static CellStyle createStyleWithBorders(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(CellStyle.BORDER_MEDIUM);
        style.setBorderLeft(CellStyle.BORDER_MEDIUM);
        style.setBorderRight(CellStyle.BORDER_MEDIUM);
        style.setBorderTop(CellStyle.BORDER_MEDIUM);
        return style;
    }

    public static CellStyle createStyleWithBorders(Workbook workbook, CellStyleDescriptor.Borders borders) {
        CellStyle style = workbook.createCellStyle();
        if ( borders.isBottom()) style.setBorderBottom(borders.getSize());
        if ( borders.isLeft() ) style.setBorderLeft(borders.getSize());
        if ( borders.isRight() ) style.setBorderRight(borders.getSize());
        if ( borders.isTop() ) style.setBorderTop(borders.getSize());
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBorderTop(CellStyle.BORDER_THIN);
        return style;
    }

    public static String saveWorkbook(Workbook workBook, File output) {
        try {
            if (!output.exists()) {
                output.getParentFile().mkdirs();
            }
            FileOutputStream outFile = new FileOutputStream(output);
            workBook.write(outFile);
            outFile.close();
            if (workBook instanceof SXSSFWorkbook) {
                ((SXSSFWorkbook)workBook).dispose();
            }
            return output.getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Row insertRow(Sheet sheet, int rowNum) {
        sheet.shiftRows(rowNum, sheet.getLastRowNum(), 1);
        return sheet.createRow(rowNum);
    }

    public static Row getLastRow(Sheet sheet) {
        return sheet.getRow(sheet.getLastRowNum());
    }

    public static Row getRow(Sheet sheet, int rowNum) {
        return sheet.getRow(rowNum);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
