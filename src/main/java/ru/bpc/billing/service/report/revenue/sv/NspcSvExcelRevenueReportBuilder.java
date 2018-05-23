package ru.bpc.billing.service.report.revenue.sv;

import com.google.common.collect.Multimap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import ru.bpc.billing.domain.FileType;
import ru.bpc.billing.service.CurrencyService;
import ru.bpc.billing.service.ISystem;
import ru.bpc.billing.service.report.LoadAndGroupTickets;
import ru.bpc.billing.service.report.ReportRow;
import ru.bpc.billing.service.report.ReportType;
import ru.bpc.billing.service.report.revenue.Formula;
import ru.bpc.billing.util.poi.CellStyleDescriptor;
import ru.bpc.billing.util.poi.PoiSSUtil;

import java.util.Collection;
import java.util.Currency;

/**
 * User: Krainov
 * Date: 28.04.2015
 * Time: 13:30
 */
public class NspcSvExcelRevenueReportBuilder extends SvExcelRevenueReportBuilder {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    protected void fillRow(ReportRow revenueRow, Row row, Multimap<CellStyleDescriptor,Cell> styles, int submissionMinorUnit) {
        styles.put(countryStyleDescriptor, PoiSSUtil.createCellAndSetValue(row, 3, LoadAndGroupTickets.countryMps(revenueRow.countryCode, revenueRow.mps)));

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

        /*
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
        */

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

        /*
        PoiSSUtil.createCellAndSetFormula(row, 9, Formula.fromRejectList("J", rowNumFromRejectList));

        styles.put(rateStyle, PoiSSUtil.createCellAndSetValue(row, 10, rateMps));
        styles.put(rejectStyle, PoiSSUtil.createCellAndSetFormula(row, 11, Formula.fromRejectList("L", rowNumFromRejectList)));
        styles.put(rejectStyle, PoiSSUtil.createCellAndSetFormula(row, 12, Formula.fromRejectList("M", rowNumFromRejectList)));
        styles.put(rejectStyle2, PoiSSUtil.createCellAndSetFormula(row, 13, Formula.fromRejectList("N", rowNumFromRejectList)));
        */

        styles.put(rateStyle, PoiSSUtil.createCellAndSetFormula(row, 14, Formula.fromRejectList("O", rowNumFromRejectList)));
        styles.put(rubStyle, PoiSSUtil.createCellAndSetFormula(row, 15, Formula.fromRejectList("P", rowNumFromRejectList)));
        styles.put(rubStyle, PoiSSUtil.createCellAndSetFormula(row, 16, Formula.fromRejectList("Q", rowNumFromRejectList)));
        styles.put(rubStyle2, PoiSSUtil.createCellAndSetFormula(row, 17, Formula.fromRejectList("R", rowNumFromRejectList)));
        PoiSSUtil.createCellAndSetFormula(row, 18, Formula.fromRejectList("S",rowNumFromRejectList));
    }

    protected void addTotalRejectRowByCurrency(Workbook workbook, int indexSheet, int rowNum, String currency,
                                             String invoiceNumber, int rowNumFromRejectList, Multimap<CellStyleDescriptor, Cell> styles) {
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
        /*
        PoiSSUtil.createCellAndSetValue(row, 9, alphabeticCode);

        styles.put(getCellStyleDescriptorByCurrency(new CurrencyStyleDescriptor(currency, false)), PoiSSUtil.createCellAndSetFormula(row, 11, Formula.fromRejectList("L", rowNumFromRejectList)));
        styles.put(getCellStyleDescriptorByCurrency(new CurrencyStyleDescriptor(currency, false)), PoiSSUtil.createCellAndSetFormula(row, 12, Formula.fromRejectList("M", rowNumFromRejectList)));
        styles.put(getCellStyleDescriptorByCurrency(new CurrencyStyleDescriptor(currency, true)), PoiSSUtil.createCellAndSetFormula(row, 13, Formula.fromRejectList("N", rowNumFromRejectList)));
        */

        PoiSSUtil.createCellAndSetFormula(row, 18, Formula.fromRejectList("S", rowNumFromRejectList));
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

        styles.put(rejectStyle,PoiSSUtil.createCellAndSetFormula(row,6, Formula.subtotalsMinusRejects("G",sumFromToStartToFinish,rowNums)));
        styles.put(rejectStyle,PoiSSUtil.createCellAndSetFormula(row,7, Formula.subtotalsMinusRejects("H",sumFromToStartToFinish,rowNums)));
        styles.put(rejectStyle2,PoiSSUtil.createCellAndSetFormula(row,8, Formula.subtotalsMinusRejects("I",sumFromToStartToFinish,rowNums)));

        /*
        String alphabeticCodeMps = null;
        if ( null != currencyMps ) {
            Currency currencyMpsByAlphabeticCode = CurrencyService.findByNumericCode(currencyMps);
            if ( null != currencyMpsByAlphabeticCode ) {
                alphabeticCodeMps = currencyMpsByAlphabeticCode.getCurrencyCode();
            }
            else logger.warn("Unable to find currency [mps] by numericCode: {}",currencyMps);

        }

        PoiSSUtil.createCellAndSetValue(row, 9, alphabeticCodeMps);
        styles.put(rejectStyle, PoiSSUtil.createCellAndSetFormula(row, 11, Formula.subtotalsMinusRejects("L", sumFromToStartToFinish, rowNums)));
        styles.put(rejectStyle, PoiSSUtil.createCellAndSetFormula(row, 12, Formula.subtotalsMinusRejects("M", sumFromToStartToFinish, rowNums)));
        styles.put(rejectStyle2, PoiSSUtil.createCellAndSetFormula(row, 13, Formula.subtotalsMinusRejects("N", sumFromToStartToFinish, rowNums)));
        */

        styles.put(rateStyle, PoiSSUtil.createCellAndSetValue(row, 14, rateBank));
        styles.put(rubStyle, PoiSSUtil.createCellAndSetFormula(row, 15, Formula.subtotalsMinusRejects("P", sumFromToStartToFinish, rowNums)));
        styles.put(rubStyle, PoiSSUtil.createCellAndSetFormula(row, 16, Formula.subtotalsMinusRejects("Q", sumFromToStartToFinish, rowNums)));
        styles.put(rubStyle2, PoiSSUtil.createCellAndSetFormula(row, 17, Formula.subtotalsMinusRejects("R", sumFromToStartToFinish, rowNums)));

        PoiSSUtil.createCellAndSetFormula(row, 18, Formula.feeRateFormula(rowNum, feeCell));
    }

    @Override
    public FileType getFileType() {
        return FileType.REVENUE_REPORT_EXCEL_NSPC;
    }

    @Override
    public String getSystemName() {
        return messageSource.getMessage(ISystem.SYSTEM_PREFIX + "report.nspcExcelRevenueSv", new Object[]{},
                "NSCP Excel revenue report for Smart Vista", LocaleContextHolder.getLocale());
    }
}
