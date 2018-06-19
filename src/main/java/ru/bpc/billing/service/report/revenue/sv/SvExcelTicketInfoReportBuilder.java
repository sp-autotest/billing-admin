package ru.bpc.billing.service.report.revenue.sv;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.CollectionUtils;
import ru.bpc.billing.domain.Carrier;
import ru.bpc.billing.domain.FileType;
import ru.bpc.billing.domain.ProcessingFile;
import ru.bpc.billing.domain.billing.BillingFile;
import ru.bpc.billing.domain.bo.BORecord;
import ru.bpc.billing.domain.report.ReportRecord;
import ru.bpc.billing.service.ApplicationService;
import ru.bpc.billing.service.CurrencyService;
import ru.bpc.billing.service.ISystem;
import ru.bpc.billing.service.report.LoadAndGroupTickets;
import ru.bpc.billing.service.report.ReportBuilder;
import ru.bpc.billing.service.report.ReportBuildException;
import ru.bpc.billing.service.report.ReportType;
import ru.bpc.billing.service.report.revenue.Formula;
import ru.bpc.billing.util.BillingFileUtils;
import ru.bpc.billing.util.poi.PoiSSUtil;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD;

public class SvExcelTicketInfoReportBuilder implements ReportBuilder {

    private static final String TEMPLATE_PATH = "ticketInfoReportTemplate.xlsx";
    private static final String FILE_NAME = "ticket_report_for_sales_proceeds_for_";
    private static final int START_ROW = 20;
    private static Logger logger = LoggerFactory.getLogger(SvExcelRevenueOperationRegisterReportBuilder.class);
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HHmm");
    protected CellStyle feeCellStyle;
    protected CellStyle rubCellStyle;
    protected CellStyle anyCellStyle;
    protected CellStyle subHeaderCellStyle;

    @Resource
    private ApplicationService applicationService;
    @Resource
    protected MessageSource messageSource;

    protected void buildMainTicketInfoSheet(Workbook workbook, AtomicBoolean stopped, LoadAndGroupTickets loadAndGroupTickets) throws InterruptedException {

        List<ReportRecord> reportRecords = loadAndGroupTickets.getReportRecords();
        Sheet sheet = workbook.getSheetAt(0);
        int rowNum = START_ROW;
        feeCellStyle = workbook.createCellStyle();
        feeCellStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
        rubCellStyle = workbook.createCellStyle();
        rubCellStyle.setDataFormat(workbook.createDataFormat().getFormat(Formula.formatString(2)));
        anyCellStyle = workbook.createCellStyle();
        Font fontCell = workbook.createFont();
        fontCell.setFontName("Arial");
        //fontCell.setFontHeightInPoints(new Short("8"));
        anyCellStyle.setFont(fontCell);
        rubCellStyle.setFont(fontCell);
        feeCellStyle.setFont(fontCell);
        Font boldFontCell = workbook.createFont();
        boldFontCell.setBoldweight(BOLDWEIGHT_BOLD);
        boldFontCell.setFontName("Arial");
        subHeaderCellStyle = workbook.createCellStyle();
        subHeaderCellStyle.setFont(boldFontCell);
        subHeaderCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        anyCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        createHeaders(sheet);

        int i = 1;
        int successDepositRecords = 0;
        int successCreditRecords = 0;
        for (ReportRecord revenueRecord : reportRecords) {
            if (i % 100 == 0 && stopped.get()) {
                throw new InterruptedException("Interrupted");
            }
            if (null == revenueRecord.getBoRecord() || !revenueRecord.getBoRecord().isSuccess()) continue;
            if ( revenueRecord.isCredit() ) successCreditRecords++;
            else successDepositRecords ++;
            Row row = sheet.createRow(rowNum);
            fillRevenueRecordToExcelRow(workbook, row, revenueRecord);
            rowNum++;
            i++;
        }
    }


    @Override
    public File build(LoadAndGroupTickets loadAndGroupTickets, AtomicBoolean stopped) throws ReportBuildException{
        StringBuilder fileFormats = new StringBuilder();
        for (BillingFile billingFile : loadAndGroupTickets.getBillingFiles()) {
            fileFormats.append(billingFile.getFormat().name()).append("_");
        }
        boolean isConsolidate = 1 < loadAndGroupTickets.getBillingFiles().size();
        Workbook workbook = PoiSSUtil.createWorkBookFromTemplate(TEMPLATE_PATH);
        try {
            buildMainTicketInfoSheet(workbook, stopped, loadAndGroupTickets);
        } catch (InterruptedException e) {
            throw new ReportBuildException("Error build operation register file",e);
        }

        Carrier carrier = BillingFileUtils.getCarrier(loadAndGroupTickets.getBillingFiles());
        String iataCode = carrier == null ? "" : carrier.getIataCode();
        File operationRegisterFile = new File(applicationService.getHomeDir(getFileType()) + FILE_NAME + fileFormats +
                dateFormat.format(loadAndGroupTickets.getCreatedDate()) + (isConsolidate ? "_consolidate_" : "_") + iataCode + ".xlsx");
        PoiSSUtil.saveWorkbook(workbook, operationRegisterFile);

        return operationRegisterFile;
    }

    @Override
    public FileType getFileType() {
        return FileType.OPERATION_REGISTER;
    }

    @Override
    public boolean linkFileToRecord(ProcessingFile processingFile, ReportRecord reportRecord) {
        return reportRecord.isSuccess();
    }

    /**
     * Задаётся порядок следования столбцов
     */
    protected enum RowName {
        BLANK1(""),
        DATE_PROCESSING("Processing date"),
        DATE_OPERATION("Transaction date"),
        DOCUMENT_NUMBER("Ticket No"),
        PAN("Primary Account Number"),
        MPS("Card Brand"),
        CODE_CURRENCY_MPS("Currency"),
        AMOUNT_IN_CURRENCY_MPS("Gross amount"),
        CODE_CURRENCY_CLIENT("Currency"),
        AMOUNT_IN_CURRENCY_CLIENT("Gross amount"),
        AMOUNT_FEE_IN_RUB("Fee amount"),
        AMOUNT_NET_IN_RUB("Net amount"),
        RATE_COMMISSION("Fee");

        private final String rowNameEng;

        RowName(String rowNameEng) {
            this.rowNameEng = rowNameEng;
        }

        public String getrowNameEng() {
            return rowNameEng;
        }

        public int getColumnNumber() {
            int i = 0;
            for (RowName rowName : RowName.values()) {
                if (this.equals(rowName)) return i;
                i++;
            }
            return i;
        }
    }

    protected class RowNumParams {
        BigDecimal amountOperation;
        BigDecimal feeRub;
        BigDecimal amountMps;
        double rateCb;
        double rateMps;
        BigDecimal amountOperationInMinor;
        String invoiceNumber;
    }

    protected void createHeaders(Sheet sheet) {
        Row rowHeader = sheet.createRow(19);
        for (RowName rowName : RowName.values()) {
            PoiSSUtil.createCellAndSetValue(rowHeader, rowName.getColumnNumber(), rowName.getrowNameEng(), subHeaderCellStyle);//МПС
        }
    }

    protected void createRecords(Workbook workbook, Row row, ReportRecord revenueRecord, RowNumParams rowNumParams) {
        for (RowName rowName : RowName.values()) {
            fillCell(workbook, row, revenueRecord, rowName, rowNumParams);
        }
    }

    private void fillCell(Workbook workbook, Row row, ReportRecord revenueRecord, RowName rowName, RowNumParams rowNumParams) {
        BORecord revenue = revenueRecord.getBoRecord();
        switch (rowName) {
            case DATE_PROCESSING: {//Дата обработки
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(), revenue.getOPER_DATE(), anyCellStyle);
                break;
            }
            case DATE_OPERATION: {//Дата операции
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(), revenue.getAUTH_DATE(), anyCellStyle);
                break;
            }
            case DOCUMENT_NUMBER: {//Номер документа
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(), revenue.getTICKET_NUMBER(), anyCellStyle);
                break;
            }
            case PAN: {//номер карты
                String pan = null;
                if ( null != revenueRecord.getPan()) {
                    pan = revenueRecord.getPan();
                    pan = pan.substring(0, 4) + "********" + pan.substring(12);
                }
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(), pan, anyCellStyle);
                break;
            }
            case MPS: {//МПС
                String mps = null;
                if ( null != revenue.getOperationType().getType()) {
                    mps = revenue.getOperationType().getType();
                    if (mps.contains("VI")) {
                        mps = "Visa";
                    }
                    if (mps.contains("MC")) {
                        mps = "MasterCard";
                    }
                }
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(), mps, anyCellStyle);
                break;
            }
            case CODE_CURRENCY_MPS: {//код валюты мпс
                String alphabeticCode = null;
                if ( null != revenue.getCURRENCY_MPS() ) {
                    Currency currency = CurrencyService.findByNumericCode(revenue.getCURRENCY_MPS());
                    if ( null != currency ) {
                        alphabeticCode = currency.getCurrencyCode();
                    }
                    else logger.warn("Unable to find currency [client] by numericCode: {}",revenue.getCURRENCY_MPS());
                }
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(), alphabeticCode, anyCellStyle);
                break;
            }
            case AMOUNT_IN_CURRENCY_MPS: {//сумма в валюте мпс
                double amount = 0;
                if ( null != revenueRecord.getGrossMps()) {
                    amount = revenueRecord.getGrossMps().doubleValue()/100;
                    if ( null != revenue.getOPER_SIGN()) {
                        if (revenue.getOPER_SIGN().equals("CR")){
                            amount = amount*(-1);
                        }
                    }
                }
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(), amount/*.getAMOUNT_IN_CURRENCY_MPS()*/, rubCellStyle);
                break;
            }
            case CODE_CURRENCY_CLIENT: {//код валюты клиента
                String alphabeticCode = null;
                if ( null != revenue.getCURRENCY() ) {
                    Currency currency = CurrencyService.findByNumericCode(revenue.getCURRENCY());
                    if ( null != currency ) {
                        alphabeticCode = currency.getCurrencyCode();
                    }
                    else logger.warn("Unable to find currency by numericCode: {}",revenue.getCURRENCY());
                }
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(), alphabeticCode, anyCellStyle);
                break;
            }
            case AMOUNT_IN_CURRENCY_CLIENT: { //Сумма в рублях
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(), revenueRecord.getGrossBank().doubleValue()/*.getAMOUNT_IN_RUB()*/, rubCellStyle);
                break;
            }
            case AMOUNT_FEE_IN_RUB: { //Сумма комиссии в рублях
                PoiSSUtil.createCellAndSetFormula(row, rowName.getColumnNumber(),
                        Formula.feeAmountInRub(row.getRowNum(), RowName.AMOUNT_IN_CURRENCY_CLIENT.getColumnNumber(), rowNumParams.feeRub.doubleValue()), rubCellStyle);
                break;
            }
            case AMOUNT_NET_IN_RUB: { //Сумма без комиссии в рублях
                PoiSSUtil.createCellAndSetFormula(row, rowName.getColumnNumber(),
                        Formula.feeAmountInRub(row.getRowNum(), RowName.AMOUNT_IN_CURRENCY_CLIENT.getColumnNumber(), rowNumParams.feeRub.doubleValue()), rubCellStyle);
                break;
            }
            case RATE_COMMISSION: {//Ставка комиссии
                PoiSSUtil.createCellAndSetFormula(row, rowName.getColumnNumber(),
                        Formula.feeRateFormula(row.getRowNum(), RowName.AMOUNT_FEE_IN_RUB.getColumnNumber(), RowName.AMOUNT_IN_CURRENCY_CLIENT.getColumnNumber()),
                        feeCellStyle);
                break;
            }
        }
    }

    protected void fillRevenueRecordToExcelRow(Workbook workbook, Row row, ReportRecord revenueRecord) {
        RowNumParams rowNumParams = new RowNumParams();
        BORecord revenue = revenueRecord.getBoRecord();
        int minorMps = 0;
        int minorOperation = 0;
        int minorOperationRub = 2;
        if (null != revenue.getCURRENCY_CLIENT()) {
            Currency currencyOperation = CurrencyService.findByNumericCode(revenue.getCURRENCY_CLIENT());
            if (null != currencyOperation) minorOperation = currencyOperation.getDefaultFractionDigits();
            else logger.warn("Unable to find currency by numericCode: {} and set minorOperation as default value = {}", revenue.getCURRENCY_CLIENT(), minorOperation);
        } else logger.warn("Unable to find currency because currency_client is null");
        if (null != revenue.getCURRENCY_MPS()) {
            Currency currencyMps = CurrencyService.findByNumericCode(revenue.getCURRENCY_MPS());
            if (null != currencyMps) minorMps = currencyMps.getDefaultFractionDigits();
            else logger.warn("Unable to find currency by numericCode: {} and set minotMps as default value = {}", revenue.getCURRENCY_MPS(), minorMps);
        } else logger.warn("Unable to find currency because currency_mps us null");

        BigDecimal amountOperation = BigDecimal.ZERO;
        BigDecimal feeRub = BigDecimal.ZERO;
        BigDecimal amountInRub = BigDecimal.ZERO;
        BigDecimal amountMps = BigDecimal.ZERO;
        BigDecimal amountOperationInMinor = BigDecimal.ZERO;

        if (StringUtils.isNotBlank(revenue.getAMOUNT_IN_CURRENCY_CLIENT())) {
            amountOperation = amountWithSign(new BigDecimal(revenue.getAMOUNT_IN_CURRENCY_CLIENT()), minorOperation, revenue.isCredit());
            amountOperationInMinor = amountWithSign(new BigDecimal(revenue.getAMOUNT_IN_CURRENCY_CLIENT()), 0, revenue.isCredit());//берём как есть, не сдвигаем, только учитываем знак
        }
        if (StringUtils.isNotBlank(revenue.getMSC()))
            feeRub = new BigDecimal(revenue.getMSC()).movePointLeft(minorOperationRub);
        if (StringUtils.isNotBlank(revenue.getAMOUNT_IN_CURRENCY_MPS())) {
            amountMps = amountWithSign(new BigDecimal(revenue.getAMOUNT_IN_CURRENCY_MPS()), minorMps, false);
        }

        double rateCb = 1.0;
        if (null != revenue.getRATE_CB()) {
            rateCb = Double.parseDouble(revenue.getRATE_CB());
        } else logger.warn("Rate CB is null for record with rbsId: {} . Use rate CB = 1.0", revenue.getRBS_ORDER());
        double rateMps = 1.0;
        if (null != revenue.getRATE_MPS()) {
            rateMps = Double.parseDouble(revenue.getRATE_MPS());
        } else logger.warn("Rate MPS is null for record with rbsId: {} . Use rate MPS = 1.0", revenue.getRBS_ORDER());

        rowNumParams.amountMps = amountMps;
        rowNumParams.amountOperation = amountOperation;
        rowNumParams.feeRub = feeRub;
        rowNumParams.rateCb = rateCb;
        rowNumParams.rateMps = rateMps;
        rowNumParams.amountOperationInMinor = amountOperationInMinor;
        rowNumParams.invoiceNumber = revenueRecord.getInvoiceNumber();

        createRecords(workbook,row,revenueRecord,rowNumParams);
//        for (RowName rowName : RowName.values()) {
//            fillCell(workbook, row, revenueRecord, rowName, rowNumParams);
//        }
    }


    protected BigDecimal amountWithSign(BigDecimal amountRecord, int minorUnit, boolean isCredit) {
        if (null == amountRecord) return BigDecimal.ZERO;
        BigDecimal amount = amountRecord.movePointLeft(minorUnit);
        return isCredit ? amount.negate() : amount;
    }

    protected BigDecimal sign(boolean isCredit, BigDecimal sign) {
        return isCredit ? sign.abs() : sign.negate();
    }

    @Override
    public String getSystemName() {
        return messageSource.getMessage(ISystem.SYSTEM_PREFIX + "report.excelTicketInfoSv",new Object[]{},
                "Excel ticket info report for Smart Vista", LocaleContextHolder.getLocale());
    }

}
