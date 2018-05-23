package ru.bpc.billing.service.report.revenue.sv;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import ru.bpc.billing.domain.Carrier;
import ru.bpc.billing.domain.FileType;
import ru.bpc.billing.domain.ProcessingFile;
import ru.bpc.billing.domain.billing.BillingFile;
import ru.bpc.billing.domain.bo.OperationType;
import ru.bpc.billing.domain.bo.sv.SvOperationType;
import ru.bpc.billing.domain.report.ReportRecord;
import ru.bpc.billing.service.ApplicationService;
import ru.bpc.billing.service.CurrencyService;
import ru.bpc.billing.service.ISystem;
import ru.bpc.billing.service.report.*;
import ru.bpc.billing.service.report.revenue.Formula;
import ru.bpc.billing.util.BillingFileUtils;
import ru.bpc.billing.util.CountryUtils;
import ru.bpc.billing.util.poi.CellStyleDescriptor;
import ru.bpc.billing.util.poi.PoiSSUtil;

import javax.annotation.Resource;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: Krainov
 * Date: 15.09.2014
 * Time: 15:24
 */
public class SvExcelRevenueReportBuilder implements ReportBuilder {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    //мапа для хранения номеров строк на которых отображали тотал данные на странице реджектов
    private Map<RejectReportGroup, Integer> totalRejectByCountryAndMpsNumRows;
    //мапа для хранения номеров строк на который отображали реджекты по валютам на странице реджектов
    private Map<String, Integer> totalRejectByCurrencyNumRows;
    private Map<RejectReportGroup, String> ratesMps;
    //реджекты посчитанные на листе Реджектов по валюте и стране
    private Map<RejectReportCurrencyGroup, Integer> rejectCurrencyGroup;

    private static final String TEMPLATE_PATH = "revenueReportTemplate.xlsx";
    private static final String REJECT_TOTAL_TITLE = "Reject Итого";
    private static final String SUBTOTALS_FOR_COUNTRY_TITLE = "Итого по %s";
    private static final String TOTAL_TITLE = "Итого";
    private static final int START_ROW = 2;
    private static final int COLUMN_COUNT_MAIN = 18;
    private static final int COLUMN_COUNT_REJECT = 19;
    private static final int MAIN_SHEET_INDEX = 0;
    private static final int REJECT_SHEET_INDEX = 1;
    private static final String FILE_NAME = "report_sales_proceeds_for_";
    private static final int RUB_MINOR_UNIT = 2;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HHmm");

    protected static CellStyleDescriptor countryTotalStyleDescriptor = new CellStyleDescriptor().withBorders(new CellStyleDescriptor.Borders().right()).wrapText();
    protected static CellStyleDescriptor countryStyleDescriptor = new CellStyleDescriptor().withBorders(new CellStyleDescriptor.Borders().right()).wrapText();
    protected static CellStyleDescriptor rubStyle = new CellStyleDescriptor(formatString(RUB_MINOR_UNIT)).withBorders(new CellStyleDescriptor.Borders().left().right().size(CellStyle.BORDER_THIN));
    protected static CellStyleDescriptor rubStyle2 = new CellStyleDescriptor(formatString(RUB_MINOR_UNIT)).withBorders(new CellStyleDescriptor.Borders().right().size(CellStyle.BORDER_MEDIUM));
    protected static CellStyleDescriptor causeReject = new CellStyleDescriptor().withBorders(new CellStyleDescriptor.Borders().right().size(CellStyle.BORDER_MEDIUM)).wrapText();
    protected static CellStyleDescriptor rejectStyle = new CellStyleDescriptor(formatString(2)).withBorders(new CellStyleDescriptor.Borders().left().right().size(CellStyle.BORDER_THIN));
    protected static CellStyleDescriptor rejectStyle2 = new CellStyleDescriptor(formatString(2)).withBorders(new CellStyleDescriptor.Borders().right().size(CellStyle.BORDER_MEDIUM));
    protected static CellStyleDescriptor rateStyle = new CellStyleDescriptor(formatString(2)).withBorders(new CellStyleDescriptor.Borders().left().right().size(CellStyle.BORDER_THIN));
    private HashMap<CurrencyStyleDescriptor, CellStyleDescriptor> styleDescriptorHashMap = new HashMap<CurrencyStyleDescriptor, CellStyleDescriptor>();


//    private CurrencyService currencyService;
    @Resource
    private ApplicationService applicationService;
    @Resource
    protected MessageSource messageSource;

    public static final String subtotalForCountryTitle(String country) {
        return String.format(SUBTOTALS_FOR_COUNTRY_TITLE, CountryUtils.getCountryName(country, "ru"));
    }

    public static String formatString(int minorUnit) {
        if (minorUnit == 0) {
            return "0";
        }
        return "0." + StringUtils.repeat("0", minorUnit);
    }

    protected CellStyleDescriptor getCellStyleDescriptorByCurrency(CurrencyStyleDescriptor styleDescriptor) {
        CellStyleDescriptor descriptor = styleDescriptorHashMap.get(styleDescriptor);
        if ( null != descriptor ) return descriptor;
        int minorOperation = 0;
        if ( null != styleDescriptor.currencyNumericCode ) {
            Currency currency = CurrencyService.findByNumericCode(styleDescriptor.currencyNumericCode);
            if ( null != currency ) minorOperation = currency.getDefaultFractionDigits();
            else logger.warn("Unable to find currency by numericCode: {} and set minorOperation as default value = {}",styleDescriptor.currencyNumericCode,minorOperation);
        }

        if ( styleDescriptor.isRight ) {
            descriptor = new CellStyleDescriptor(formatString(minorOperation)).withBorders(new CellStyleDescriptor.Borders().right());
        }
        else {
            descriptor = new CellStyleDescriptor(formatString(minorOperation)).withBorders(new CellStyleDescriptor.Borders().left().right().size(CellStyle.BORDER_THIN));
        }
        styleDescriptorHashMap.put(styleDescriptor,descriptor);
        return descriptor;
    }

    protected void fillRow(ReportRow revenueRow, Row row, Multimap<CellStyleDescriptor,Cell> styles, int submissionMinorUnit) {
        styles.put(countryStyleDescriptor,PoiSSUtil.createCellAndSetValue(row, 3, LoadAndGroupTickets.countryMps(revenueRow.countryCode, revenueRow.mps)));

        CellStyleDescriptor currencyOperationStyle = getCellStyleDescriptorByCurrency(new CurrencyStyleDescriptor(revenueRow.currencyOperation,false));
        CellStyleDescriptor currencyOperationStyle2 = getCellStyleDescriptorByCurrency(new CurrencyStyleDescriptor(revenueRow.currencyOperation, true));
        CellStyleDescriptor currencyMpsStyle = getCellStyleDescriptorByCurrency(new CurrencyStyleDescriptor(revenueRow.currencyMps,false));
        CellStyleDescriptor currencyMpsStyle2 = getCellStyleDescriptorByCurrency(new CurrencyStyleDescriptor(revenueRow.currencyMps, true));

        PoiSSUtil.createCellAndSetValue(row, 4, revenueRow.qty);               //кол-во
        String alphabeticCode = null;
        if ( null != revenueRow.currencyOperation ) {
            Currency currency = CurrencyService.findByNumericCode(revenueRow.currencyOperation);
            if ( null != currency ) {
                alphabeticCode = currency.getCurrencyCode();
            }
            else logger.warn("Unable to find currency [client] by numericCode: {}",revenueRow.currencyOperation);
        }
        PoiSSUtil.createCellAndSetValue(row, 5, alphabeticCode); //валюта

        if ( null != revenueRow.grossOperation )
            styles.put(currencyOperationStyle, PoiSSUtil.createCellAndSetValue(row, 6, revenueRow.grossOperation.doubleValue()));
        if ( null != revenueRow.feeOperation )
            styles.put(currencyOperationStyle, PoiSSUtil.createCellAndSetValue(row, 7, revenueRow.feeOperation.doubleValue()));
        if ( null != revenueRow.netOperation )
            styles.put(currencyOperationStyle2, PoiSSUtil.createCellAndSetValue(row, 8, revenueRow.netOperation.doubleValue()));

        String alphabeticCodeMps = null;
        if ( null != revenueRow.currencyMps ){
            Currency currencyMps = CurrencyService.findByNumericCode(revenueRow.currencyMps);
            if ( null != currencyMps ) {
                alphabeticCodeMps = currencyMps.getCurrencyCode();
            }
            else logger.warn("Unable to find currency [mps] by numericCode: {}",revenueRow.currencyMps);
        }
        PoiSSUtil.createCellAndSetValue(row, 9, alphabeticCodeMps);//currencyMps

        if ( null != revenueRow.rateMps )
            styles.put(rateStyle, PoiSSUtil.createCellAndSetValue(row, 10, revenueRow.rateMps));
        if ( null != revenueRow.grossMps )
            styles.put(currencyMpsStyle, PoiSSUtil.createCellAndSetValue(row, 11, revenueRow.grossMps.doubleValue()));
        if ( null != revenueRow.feeMps )
            styles.put(currencyMpsStyle, PoiSSUtil.createCellAndSetValue(row, 12, revenueRow.feeMps.doubleValue()));
        if ( null != revenueRow.netMps )
            styles.put(currencyMpsStyle2, PoiSSUtil.createCellAndSetValue(row, 13, revenueRow.netMps.doubleValue()));

        if ( null != revenueRow.rateBank )
            styles.put(rateStyle, PoiSSUtil.createCellAndSetValue(row, 14, revenueRow.rateBank));
        if ( null != revenueRow.grossBank )
            styles.put(rubStyle, PoiSSUtil.createCellAndSetValue(row, 15, revenueRow.grossBank.doubleValue()));
        if ( null != revenueRow.feeBank )
            styles.put(rubStyle, PoiSSUtil.createCellAndSetValue(row, 16, revenueRow.feeBank.doubleValue()));
        if ( null != revenueRow.netBank )
            styles.put(rubStyle2, PoiSSUtil.createCellAndSetValue(row, 17, revenueRow.netBank.doubleValue()));
        if ( null != revenueRow.errorMessage )
            styles.put(causeReject, PoiSSUtil.createCellAndSetValue(row, 19, revenueRow.errorMessage));
    }

    protected class CurrencyStyleDescriptor {
        private final String currencyNumericCode;
        private final Boolean isRight;

        protected CurrencyStyleDescriptor(String currencyNumericCode, Boolean right) {
            this.currencyNumericCode = currencyNumericCode;
            isRight = right;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CurrencyStyleDescriptor that = (CurrencyStyleDescriptor) o;

            if (currencyNumericCode != null ? !currencyNumericCode.equals(that.currencyNumericCode) : that.currencyNumericCode != null)
                return false;
            if (isRight != null ? !isRight.equals(that.isRight) : that.isRight != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = currencyNumericCode != null ? currencyNumericCode.hashCode() : 0;
            result = 31 * result + (isRight != null ? isRight.hashCode() : 0);
            return result;
        }
    }

    private void setStyles(Workbook workbook, Multimap<CellStyleDescriptor, Cell> styles,  int rowNum, int columnNum, int sheetIndex) {
        for (CellStyleDescriptor cellStyleDescriptor: styles.keySet()) {
            CellStyle cellStyle = cellStyleDescriptor.createCellStyle(workbook);
            cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
            for (Cell cell: styles.get(cellStyleDescriptor)) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    private void setBorders(Workbook workbook, int rowNum, int columnNum, int sheetIndex) {
        Sheet sheet = workbook.getSheetAt(sheetIndex);

        CellStyle rightSideMediumStyle = workbook.createCellStyle();
        rightSideMediumStyle.setBorderRight(CellStyle.BORDER_MEDIUM);
        rightSideMediumStyle.setBorderBottom(CellStyle.BORDER_THIN);
        rightSideMediumStyle.setAlignment(CellStyle.ALIGN_CENTER);

        CellStyle rightSideThinStyle = workbook.createCellStyle();
        rightSideThinStyle.setBorderRight(CellStyle.BORDER_THIN);
        rightSideThinStyle.setBorderBottom(CellStyle.BORDER_THIN);
        rightSideThinStyle.setAlignment(CellStyle.ALIGN_CENTER);

        CellStyle rightSideThinLeftAlignStyle = workbook.createCellStyle();
        rightSideThinLeftAlignStyle.setBorderRight(CellStyle.BORDER_THIN);
        rightSideThinLeftAlignStyle.setBorderBottom(CellStyle.BORDER_THIN);
        rightSideThinLeftAlignStyle.setAlignment(CellStyle.ALIGN_LEFT);

        CellStyle percentStyle = workbook.createCellStyle();
        percentStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
        percentStyle.setBorderRight(CellStyle.BORDER_MEDIUM);
        percentStyle.setBorderBottom(CellStyle.BORDER_THIN);
        percentStyle.setAlignment(CellStyle.ALIGN_CENTER);


        for (int i = START_ROW; i < rowNum; i++) {
            Row r = sheet.getRow(i);
            for (int j = 0; j <= columnNum; j++) {
                Cell cell = r.getCell(j, Row.CREATE_NULL_AS_BLANK);
                if ( 0 < j && j <= 5 || j == 8 || j == 13 || j == 17 || j == 19) {
                    cell.setCellStyle(rightSideMediumStyle);
                } else if (j == 18) {
                    cell.setCellStyle(percentStyle);
                } else {
                    cell.setCellStyle(rightSideThinStyle);
                }
            }
        }
    }

    static double roundDown4(double d) {
        return (long) (d * 1e4) / 1e4;
    }

    private void addRow(Workbook workbook, int indexSheet, int rowNum, String invoiceNumber, String ticketNumber,
                        ReportRow calculatedRow, Multimap<CellStyleDescriptor, Cell> styles, int feeCell) {
        Sheet sheet = workbook.getSheetAt(indexSheet);
        Row row = PoiSSUtil.insertRow(sheet, rowNum);
        PoiSSUtil.createCellAndSetValue(row, 1, invoiceNumber);
        PoiSSUtil.createCellAndSetValue(row, 2, ticketNumber);
        fillRow(calculatedRow, row, styles, 2);

        PoiSSUtil.createCellAndSetValue(row, 18, roundDown4(calculatedRow.feeRate));//так как получаем значение комиссии в коде, то округляем его
        //PoiSSUtil.createCellAndSetFormula(row, 18, Formula.feeRateFormula(rowNum, feeCell));
    }

    protected void addTotalRejectRowByParams(Workbook workbook, int indexSheet, int rowNum,
                                           boolean sumFromToStartToFinish,
                                           String currency,
                                           String invoiceNumber,
                                           String ticketNumber,
                                           String country, String mps,
                                           Collection<Integer> rowNums,
                                           Multimap<CellStyleDescriptor, Cell> styles,
                                           int feeCell,String currencyMps, String rateBank) {
        Sheet sheet = workbook.getSheetAt(indexSheet);
        Row row = PoiSSUtil.insertRow(sheet, rowNum);
        String alphabeticCode = null;
        if ( null != currency ) {
            Currency currencyByAlphabeticCode = CurrencyService.findByNumericCode(currency);
            if ( null != currencyByAlphabeticCode ) {
                alphabeticCode = currencyByAlphabeticCode.getCurrencyCode();
            }
            else logger.warn("Unable to find currency [client] by numericCode: {}",currency);
        }
        PoiSSUtil.createCellAndSetValue(row, 0, alphabeticCode);
        PoiSSUtil.createCellAndSetValue(row, 1, invoiceNumber);
        PoiSSUtil.createCellAndSetValue(row, 2, ticketNumber);
        styles.put(countryStyleDescriptor,PoiSSUtil.createCellAndSetValue(row, 3, LoadAndGroupTickets.countryMps(country, mps)));
        PoiSSUtil.createCellAndSetValue(row, 4, rowNums.size());

        String alphabeticCodeMps = null;
        if ( null != currencyMps ) {
            Currency currencyMpsByAlphabeticCode = CurrencyService.findByNumericCode(currencyMps);
            if ( null != currencyMpsByAlphabeticCode ) {
                alphabeticCodeMps = currencyMpsByAlphabeticCode.getCurrencyCode();
            }
            else logger.warn("Unable to find currency [mps] by numericCode: {}",currencyMps);

        }
        styles.put(rejectStyle,PoiSSUtil.createCellAndSetFormula(row,6, Formula.subtotalsMinusRejects("G",sumFromToStartToFinish,rowNums)));
        styles.put(rejectStyle,PoiSSUtil.createCellAndSetFormula(row,7, Formula.subtotalsMinusRejects("H",sumFromToStartToFinish,rowNums)));
        styles.put(rejectStyle2,PoiSSUtil.createCellAndSetFormula(row,8, Formula.subtotalsMinusRejects("I",sumFromToStartToFinish,rowNums)));

        PoiSSUtil.createCellAndSetValue(row, 9, alphabeticCodeMps);
        styles.put(rejectStyle, PoiSSUtil.createCellAndSetFormula(row, 11, Formula.subtotalsMinusRejects("L", sumFromToStartToFinish, rowNums)));
        styles.put(rejectStyle, PoiSSUtil.createCellAndSetFormula(row, 12, Formula.subtotalsMinusRejects("M", sumFromToStartToFinish, rowNums)));
        styles.put(rejectStyle2, PoiSSUtil.createCellAndSetFormula(row, 13, Formula.subtotalsMinusRejects("N", sumFromToStartToFinish, rowNums)));

        styles.put(rateStyle, PoiSSUtil.createCellAndSetValue(row, 14, rateBank));
        styles.put(rubStyle, PoiSSUtil.createCellAndSetFormula(row, 15, Formula.subtotalsMinusRejects("P", sumFromToStartToFinish, rowNums)));
        styles.put(rubStyle, PoiSSUtil.createCellAndSetFormula(row, 16, Formula.subtotalsMinusRejects("Q", sumFromToStartToFinish, rowNums)));
        styles.put(rubStyle2, PoiSSUtil.createCellAndSetFormula(row, 17, Formula.subtotalsMinusRejects("R", sumFromToStartToFinish, rowNums)));

        PoiSSUtil.createCellAndSetFormula(row, 18, Formula.feeRateFormula(rowNum, feeCell));
    }

    protected void addTotalRejectRowByCountryAndMps(Workbook workbook, int indexSheet, int rowNum, String invoiceNumber,
                                                  String country, String mps, int rowNumFromRejectList,Multimap<CellStyleDescriptor, Cell> styles, String rateMps) {
        Sheet sheet = workbook.getSheetAt(indexSheet);
        Row row = PoiSSUtil.insertRow(sheet, rowNum);
        PoiSSUtil.createCellAndSetValue(row, 1, invoiceNumber);
        styles.put(countryStyleDescriptor,PoiSSUtil.createCellAndSetValue(row, 3, LoadAndGroupTickets.countryMps(country, mps)));
        PoiSSUtil.createCellAndSetFormula(row, 4, Formula.fromRejectList("E", rowNumFromRejectList));

        styles.put(rejectStyle, PoiSSUtil.createCellAndSetFormula(row,6,Formula.fromRejectList("G",rowNumFromRejectList)));
        styles.put(rejectStyle, PoiSSUtil.createCellAndSetFormula(row,7,Formula.fromRejectList("H",rowNumFromRejectList)));
        styles.put(rejectStyle2, PoiSSUtil.createCellAndSetFormula(row,8,Formula.fromRejectList("I",rowNumFromRejectList)));

        PoiSSUtil.createCellAndSetFormula(row, 9, Formula.fromRejectList("J", rowNumFromRejectList));

        styles.put(rateStyle, PoiSSUtil.createCellAndSetValue(row, 10, rateMps));
        styles.put(rejectStyle, PoiSSUtil.createCellAndSetFormula(row, 11, Formula.fromRejectList("L", rowNumFromRejectList)));
        styles.put(rejectStyle, PoiSSUtil.createCellAndSetFormula(row, 12, Formula.fromRejectList("M", rowNumFromRejectList)));
        styles.put(rejectStyle2, PoiSSUtil.createCellAndSetFormula(row, 13, Formula.fromRejectList("N", rowNumFromRejectList)));
        styles.put(rateStyle, PoiSSUtil.createCellAndSetFormula(row, 14, Formula.fromRejectList("O", rowNumFromRejectList)));
        styles.put(rubStyle, PoiSSUtil.createCellAndSetFormula(row, 15, Formula.fromRejectList("P", rowNumFromRejectList)));
        styles.put(rubStyle, PoiSSUtil.createCellAndSetFormula(row, 16, Formula.fromRejectList("Q", rowNumFromRejectList)));
        styles.put(rubStyle2, PoiSSUtil.createCellAndSetFormula(row, 17, Formula.fromRejectList("R", rowNumFromRejectList)));
        PoiSSUtil.createCellAndSetFormula(row, 18, Formula.fromRejectList("S",rowNumFromRejectList));
    }

    private void addTotalRejectRowByCurrency(Workbook workbook, int indexSheet, int rowNum, String currency,
                                             String invoiceNumber, int rowNumFromRejectList, Multimap<CellStyleDescriptor, Cell> styles
    ) {
        Sheet sheet = workbook.getSheetAt(indexSheet);
        Row row = PoiSSUtil.insertRow(sheet, rowNum);

        String alphabeticCode = null;
        if ( null != currency ) {
            Currency currencyByAlphabeticCode = CurrencyService.findByNumericCode(currency);
            if ( null != currencyByAlphabeticCode ) {
                alphabeticCode = currencyByAlphabeticCode.getCurrencyCode();
            }
            else logger.warn("Unable to find currency by numericCode: {}",currency);
        }
        PoiSSUtil.createCellAndSetValue(row, 0, alphabeticCode);
        PoiSSUtil.createCellAndSetValue(row, 1, invoiceNumber);
        PoiSSUtil.createCellAndSetValue(row, 9, alphabeticCode);

        styles.put(getCellStyleDescriptorByCurrency(new CurrencyStyleDescriptor(currency, false)), PoiSSUtil.createCellAndSetFormula(row, 11, Formula.fromRejectList("L", rowNumFromRejectList)));
        styles.put(getCellStyleDescriptorByCurrency(new CurrencyStyleDescriptor(currency, false)), PoiSSUtil.createCellAndSetFormula(row, 12, Formula.fromRejectList("M", rowNumFromRejectList)));
        styles.put(getCellStyleDescriptorByCurrency(new CurrencyStyleDescriptor(currency, true)), PoiSSUtil.createCellAndSetFormula(row, 13, Formula.fromRejectList("N", rowNumFromRejectList)));

        PoiSSUtil.createCellAndSetFormula(row, 18, Formula.fromRejectList("S", rowNumFromRejectList));
    }

    private void addTotalRowByCountryAndCurrency(Workbook workbook, int indexSheet, int rowNum, String country,
                                                 Multimap<CellStyleDescriptor, Cell> styles,
                                                 String currencyOperation, int startRow, int endRow, Integer rejectRow) {
        Sheet sheet = workbook.getSheetAt(indexSheet);
        Row row = PoiSSUtil.insertRow(sheet, rowNum);
        styles.put(countryTotalStyleDescriptor, PoiSSUtil.createCellAndSetValue(row, 0, subtotalForCountryTitle(country)));

        String alphabeticCode = null;
        if ( null != currencyOperation ) {
            Currency currency = CurrencyService.findByNumericCode(currencyOperation);
            if ( null != currency ) {
                alphabeticCode = currency.getCurrencyCode();
            }
            else logger.warn("Unable to find currency [client] by numericCode: {}",currencyOperation);
        }

        CellStyleDescriptor currencyOperationStyle = getCellStyleDescriptorByCurrency(new CurrencyStyleDescriptor(currencyOperation,false));
        CellStyleDescriptor currencyOperationStyle2 = getCellStyleDescriptorByCurrency(new CurrencyStyleDescriptor(currencyOperation, true));
        PoiSSUtil.createCellAndSetFormula(row,4, Formula.sumByCurrencyOperationCount(startRow,endRow,alphabeticCode));
        PoiSSUtil.createCellAndSetValue(row, 5, alphabeticCode); //валюта

        styles.put(currencyOperationStyle, PoiSSUtil.createCellAndSetFormula(row,6,Formula.sumByCurrencyOperationGross(startRow,endRow,alphabeticCode)
                        + (null != rejectRow ? "-" + Formula.fromRejectList("G",rejectRow) : "")
        ));
        styles.put(currencyOperationStyle, PoiSSUtil.createCellAndSetFormula(row, 7, Formula.sumByCurrencyOperationFee(startRow, endRow, alphabeticCode)
                        + (null != rejectRow ? "-" + Formula.fromRejectList("H",rejectRow) : "")
        ));
        styles.put(currencyOperationStyle2, PoiSSUtil.createCellAndSetFormula(row, 8, Formula.sumByCurrencyOperationNet(startRow, endRow, alphabeticCode)
                        + (null != rejectRow ? "-" + Formula.fromRejectList("I",rejectRow) : "")
        ));
    }

    private void addTotalRowByCountry(Workbook workbook, int indexSheet, int rowNum, String country,
                                      Collection<Integer> rowNums, Collection<Integer> rejectRowNums,
                                      Multimap<CellStyleDescriptor, Cell> styles
    ) {
        Sheet sheet = workbook.getSheetAt(indexSheet);
        Row row = PoiSSUtil.insertRow(sheet, rowNum);
        styles.put(countryTotalStyleDescriptor, PoiSSUtil.createCellAndSetValue(row, 0, subtotalForCountryTitle(country)));

        Integer[] rejects = new Integer[]{};
        if (null != rejectRowNums) rejects = rejectRowNums.toArray(new Integer[]{});

        styles.put(rubStyle, PoiSSUtil.createCellAndSetFormula(row, 15, Formula.subtotalsMinusRejects("P", true, rowNums, rejects)));
        styles.put(rubStyle, PoiSSUtil.createCellAndSetFormula(row, 16, Formula.subtotalsMinusRejects("Q", true, rowNums, rejects)));
        styles.put(rubStyle2, PoiSSUtil.createCellAndSetFormula(row, 17, Formula.subtotalsMinusRejects("R", true, rowNums, rejects)));

        row.createCell(18);
    }

    public void addRejectRowByCurrency(Workbook workbook, int indexSheet, int rowNum, String country, Multimap<CellStyleDescriptor, Cell> styles, String currencyOperation, int startRow, int endRow) {
        Sheet sheet = workbook.getSheetAt(indexSheet);
        Row row = PoiSSUtil.insertRow(sheet, rowNum);
        String alphabeticCode = null;
        if ( null != currencyOperation ) {
            Currency currency = CurrencyService.findByNumericCode(currencyOperation);
            if ( null != currency ) {
                alphabeticCode = currency.getCurrencyCode();
            }
            else logger.warn("Unable to find currency [client] by numericCode: {}",currencyOperation);
        }

        CellStyleDescriptor currencyOperationStyle = getCellStyleDescriptorByCurrency(new CurrencyStyleDescriptor(currencyOperation,false));
        CellStyleDescriptor currencyOperationStyle2 = getCellStyleDescriptorByCurrency(new CurrencyStyleDescriptor(currencyOperation, true));

        PoiSSUtil.createCellAndSetValue(row, 1, REJECT_TOTAL_TITLE);
        styles.put(countryStyleDescriptor,PoiSSUtil.createCellAndSetValue(row, 3, CountryUtils.getCountryName(country,"ru")));
        PoiSSUtil.createCellAndSetFormula(row, 4, Formula.sumByCurrencyOperationCount(startRow, endRow, alphabeticCode));
        PoiSSUtil.createCellAndSetValue(row, 5, alphabeticCode); //валюта

        styles.put(currencyOperationStyle, PoiSSUtil.createCellAndSetFormula(row,6,Formula.sumByCurrencyOperationGross(startRow,endRow,alphabeticCode)));
        styles.put(currencyOperationStyle, PoiSSUtil.createCellAndSetFormula(row, 7, Formula.sumByCurrencyOperationFee(startRow, endRow, alphabeticCode)));
        styles.put(currencyOperationStyle2, PoiSSUtil.createCellAndSetFormula(row, 8, Formula.sumByCurrencyOperationNet(startRow, endRow, alphabeticCode)));
    }

    private void addTotalRow(Workbook workbook, int indexSheet, int rowNum, Collection<Integer> rowNums, Multimap<CellStyleDescriptor, Cell> styles, Integer feeCell, boolean doCount) {
        Sheet sheet = workbook.getSheetAt(indexSheet);
        Row row = PoiSSUtil.insertRow(sheet, rowNum);
        PoiSSUtil.createCellAndSetValue(row, 0, TOTAL_TITLE);
        if (doCount)
            PoiSSUtil.createCellAndSetFormula(row, 4, Formula.subtotalsMinusRejects("E", false, rowNums));

        styles.put(rubStyle, PoiSSUtil.createCellAndSetFormula(row, 15, Formula.subtotalsMinusRejects("P", false, rowNums)));
        styles.put(rubStyle, PoiSSUtil.createCellAndSetFormula(row, 16, Formula.subtotalsMinusRejects("Q", false, rowNums)));
        styles.put(rubStyle2, PoiSSUtil.createCellAndSetFormula(row, 17, Formula.subtotalsMinusRejects("R", false, rowNums)));

        if (null != feeCell) {
            PoiSSUtil.createCellAndSetFormula(row, 18, Formula.feeRateFormula(rowNum, feeCell));
        }
    }

    private void buildMainSheet(Workbook workbook, AtomicBoolean stopped, LoadAndGroupTickets loadAndGroupTickets) throws InterruptedException {
        int rowNum = START_ROW;
        Multimap<CellStyleDescriptor, Cell> styles = ArrayListMultimap.create();

        List<Integer> subtotalCountries = new ArrayList<Integer>();
        for (String country : loadAndGroupTickets.countries) {
            int startRow = rowNum+1;
            Set<String> currenciesOperation = new HashSet<String>();
            List<Integer> allRowsWithoutRejects = new ArrayList<Integer>();
            List<Integer> rejectTotalRows = new ArrayList<Integer>();
            //отображаем successOnReject
            Multimap<ReportGroup, ReportRecord> successOnRejectGroup = loadAndGroupTickets.groupedSuccessOnRejectTickets.get(country);
            if (null != successOnRejectGroup) {
                for (ReportGroup group : successOnRejectGroup.keySet()) {
                    Collection<ReportRecord> records = successOnRejectGroup.get(group);
                    int i = 1;
                    for (ReportRecord record : records) {
                        if (i % 100 == 0 && stopped.get()) {
                            throw new InterruptedException("Interrupted");
                        }

                        ReportRow revenueRow = loadAndGroupTickets.calculateRow(Arrays.asList(record), 2);
                        addRow(workbook, MAIN_SHEET_INDEX, rowNum++, revenueRow.invoiceNumber, revenueRow.documentNumber, revenueRow, styles, 7);
                        allRowsWithoutRejects.add(rowNum);
                        i++;

                        currenciesOperation.add(revenueRow.currencyOperation);
                    }
                }
            }
            //отображаем успешные сгруппированные
            Multimap<ReportGroup, ReportRecord> successGroup = loadAndGroupTickets.groupedSuccessTickets.get(country);
            if (null != successGroup) {
                int i = 1;
                for (ReportGroup group : successGroup.keySet()) {
                    if (i % 100 == 0 && stopped.get()) {
                        throw new InterruptedException("Interrupted");
                    }
                    Collection<ReportRecord> records = successGroup.get(group);
                    ReportRow revenueRow = loadAndGroupTickets.calculateRow(records, 2);
                    addRow(workbook, MAIN_SHEET_INDEX, rowNum++, revenueRow.invoiceNumber, null, revenueRow, styles, 7);
                    allRowsWithoutRejects.add(rowNum);
                    i++;

                    currenciesOperation.add(revenueRow.currencyOperation);
                }
            }
            int endRow = rowNum;
            //отображаем список тотал реджектов по стране и мпс, которые берём с реджект листа, так как они уже там подсчитанны
            for (OperationType operationType : SvOperationType.values()) {
                RejectReportGroup rejectRevenueGroup = new RejectReportGroup(country, operationType.getType());
                Integer rowNumRejectTotalByCountryAndMps = totalRejectByCountryAndMpsNumRows.get(rejectRevenueGroup);
                if ( null == rowNumRejectTotalByCountryAndMps ) continue;
                String rateMps = ratesMps.get(rejectRevenueGroup);
                addTotalRejectRowByCountryAndMps(workbook, MAIN_SHEET_INDEX, rowNum++, REJECT_TOTAL_TITLE, country, operationType.getType(),
                        rowNumRejectTotalByCountryAndMps,styles, rateMps);
                rejectTotalRows.add(rowNum);
            }
            //отображаем итог по стране по конкретной валюте за вычитом реджектов по валюте с листа реджектов
            for (String currencyOperation : currenciesOperation) {
                Integer rowRejectRowNum = rejectCurrencyGroup.get(new RejectReportCurrencyGroup(country,currencyOperation));
                addTotalRowByCountryAndCurrency(workbook, MAIN_SHEET_INDEX, rowNum++, country, styles, currencyOperation, startRow,endRow, rowRejectRowNum);
            }

            //отображаем итог по стране
            addTotalRowByCountry(workbook, MAIN_SHEET_INDEX, rowNum++, country, allRowsWithoutRejects, rejectTotalRows, styles);
            subtotalCountries.add(rowNum);

        }
        //отображаем список тотал реджектов по валюте, которые берём с реджект листа, как как они уже там подсчитаны
        for (Map.Entry<String, Integer> entry : totalRejectByCurrencyNumRows.entrySet()) {
            String currency = entry.getKey();
            Integer rowNumRejectByCurrencyInRejectList = entry.getValue();
            addTotalRejectRowByCurrency(workbook, MAIN_SHEET_INDEX, rowNum++, currency, REJECT_TOTAL_TITLE, rowNumRejectByCurrencyInRejectList, styles);
        }

        if (0 < loadAndGroupTickets.countries.size()) {
            //отображаем total
            addTotalRow(workbook,MAIN_SHEET_INDEX,rowNum++,subtotalCountries,styles,null,false);
            setBorders(workbook,rowNum,COLUMN_COUNT_MAIN,MAIN_SHEET_INDEX);
            setStyles(workbook, styles, rowNum,COLUMN_COUNT_MAIN,MAIN_SHEET_INDEX);
        }
    }

    private void buildRejectSheet(Workbook workbook, AtomicBoolean stopped, LoadAndGroupTickets loadAndGroupTickets) throws InterruptedException {
        int rowNum = START_ROW;
        Multimap<CellStyleDescriptor, Cell> styles = ArrayListMultimap.create();

        if (null != loadAndGroupTickets.groupedRejectTickets) {
            List<Integer> totalRejectRowNums = new ArrayList<Integer>();
            Multimap<String, Integer> totalRejectByCurrency = ArrayListMultimap.create();
            for (String country : loadAndGroupTickets.groupedRejectTickets.keySet()) {
                Set<String> currenciesOperation = new HashSet<String>();
                int startRow = rowNum+1;
                Multimap<RejectReportGroup, Integer> totalRejectByCountryAndMps = ArrayListMultimap.create();
                Multimap<RejectReportGroup, ReportRecord> recordMultimap = loadAndGroupTickets.groupedRejectTickets.get(country);
                for (RejectReportGroup group : recordMultimap.keySet()) {
                    Collection<ReportRecord> revenueRecords = recordMultimap.get(group);
                    //отображаем все реджекты по стране и мпс
                    int i = 1;
                    for (ReportRecord revenueRecord : revenueRecords) {
                        if (i % 100 == 0 && stopped.get()) {
                            throw new InterruptedException("Interrupted");
                        }
                        ReportRow revenueRow = loadAndGroupTickets.calculateRow(Arrays.asList(revenueRecord), 2);
                        addRow(workbook, REJECT_SHEET_INDEX, rowNum++, revenueRow.invoiceNumber, revenueRow.documentNumber, revenueRow, styles, 7);
                        totalRejectByCountryAndMps.put(group, rowNum);
                        if (null != revenueRecord.getCurrencyMPS())
                            totalRejectByCurrency.put(revenueRecord.getCurrencyMPS(), rowNum);
                        i++;

                        if ( null != revenueRow.currencyOperation ) currenciesOperation.add(revenueRow.currencyOperation);
                    }
                }
                int endRow = rowNum;
                //total rejects by country and currency
                for (String currencyOperation : currenciesOperation) {
                    addRejectRowByCurrency(workbook,REJECT_SHEET_INDEX,rowNum++,country,styles,currencyOperation,startRow,endRow);
                    rejectCurrencyGroup.put(new RejectReportCurrencyGroup(country,currencyOperation),rowNum);
                }

                //total rejects by country and mps
                for (RejectReportGroup group : totalRejectByCountryAndMps.keySet()) {
                    addTotalRejectRowByParams(workbook, REJECT_SHEET_INDEX, rowNum++, true,
                            null, REJECT_TOTAL_TITLE, null, group.getCountryCode(), group.getMpsName(), totalRejectByCountryAndMps.get(group), styles,12, group.getCurrencyMps(), group.getRateBank());
                    totalRejectRowNums.add(rowNum);
                    totalRejectByCountryAndMpsNumRows.put(group, rowNum);
                    ratesMps.put(group,group.getRateMps());
                }
            }
            //total reject by currency
            for (String currency : totalRejectByCurrency.keySet()) {
                addTotalRejectRowByParams(workbook, REJECT_SHEET_INDEX, rowNum++, true, currency, REJECT_TOTAL_TITLE,
                        null, null, null, totalRejectByCurrency.get(currency), styles,12, null, null);
                totalRejectByCurrencyNumRows.put(currency,rowNum);
            }

            if (0 < loadAndGroupTickets.groupedRejectTickets.size()) {
                //total reject
                addTotalRow(workbook, REJECT_SHEET_INDEX, rowNum++,totalRejectRowNums,styles,16,true);
                setBorders(workbook,rowNum,COLUMN_COUNT_REJECT,REJECT_SHEET_INDEX);
                setStyles(workbook, styles, rowNum,COLUMN_COUNT_REJECT,REJECT_SHEET_INDEX);
            }
        }
    }


    @Override
    public FileType getFileType() {
        return FileType.REVENUE_REPORT_EXCEL;
    }

    @Override
    public File build(LoadAndGroupTickets loadAndGroupTickets, AtomicBoolean stopped) throws ReportBuildException {
        logger.debug("started building sheets");
        totalRejectByCountryAndMpsNumRows = new HashMap<>();
        totalRejectByCurrencyNumRows = new HashMap<>();
        ratesMps = new HashMap<>();
        rejectCurrencyGroup = new HashMap<>();

        Workbook workbook = PoiSSUtil.createWorkBookFromTemplate(TEMPLATE_PATH);

        //!!!not order, because main sheet has formulas which links to reject list
        try {
            buildRejectSheet(workbook, stopped, loadAndGroupTickets);
            buildMainSheet(workbook, stopped, loadAndGroupTickets);
        } catch (InterruptedException e) {
            throw new ReportBuildException("Error build reject or main sheet for " + getFileType() + " type file",e);
        }
        //generate filename depends on count of billing files
        StringBuilder fileFormats = new StringBuilder();
        for (BillingFile billingFile : loadAndGroupTickets.getBillingFiles()) {
            fileFormats.append(billingFile.getFormat().name()).append("_");
        }
        boolean isConsolidate = 1 < loadAndGroupTickets.getBillingFiles().size();

        Carrier carrier = BillingFileUtils.getCarrier(loadAndGroupTickets.getBillingFiles());
        String iataCode = carrier == null ? "" : carrier.getIataCode();
        String fileName = FILE_NAME + fileFormats.toString() + dateFormat.format(loadAndGroupTickets.getCreatedDate()) +
                (isConsolidate ? "_consolidate_" : "_") + iataCode + ".xlsx";

        //save file in home report dir
        logger.debug("File name: {}",fileName);
        File file = new File(applicationService.getHomeDir(getFileType()) + fileName);
        logger.debug("File: {}",file);
        PoiSSUtil.saveWorkbook(workbook, file);

        return file;
    }

    @Override
    public boolean linkFileToRecord(ProcessingFile processingFile, ReportRecord reportRecord) {
        return true;
    }

    @Override
    public String getSystemName() {
        return messageSource.getMessage(ISystem.SYSTEM_PREFIX + "report.excelRevenueSv",new Object[]{},
                "Excel revenue report for Smart Vista", LocaleContextHolder.getLocale());
    }
}
