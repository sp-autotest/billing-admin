package ru.bpc.billing.service.report.revenue.sv;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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

/**
 * User: Krainov
 * Date: 15.09.2014
 * Time: 16:01
 */
public class SvExcelRevenueOperationRegisterReportBuilder implements ReportBuilder {

    private static final String TEMPLATE_PATH = "operation_report_template.xlsx";
    private static final String FILE_NAME = "operation_report_for_sales_proceeds_for_";
    private static final int START_ROW_NUM = 1;
    private static Logger logger = LoggerFactory.getLogger(SvExcelRevenueOperationRegisterReportBuilder.class);
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HHmm");
    protected CellStyle feeCellStyle;
    protected CellStyle rubCellStyle;

    @Resource
    private ApplicationService applicationService;
    @Resource
    protected MessageSource messageSource;

    protected void buildOperationRegisterSheet(Workbook workbook, AtomicBoolean stopped, LoadAndGroupTickets loadAndGroupTickets) throws InterruptedException {

        List<ReportRecord> reportRecords = loadAndGroupTickets.getReportRecords();
        Sheet sheet = workbook.getSheetAt(0);
        int rowNum = START_ROW_NUM;
        feeCellStyle = workbook.createCellStyle();
        feeCellStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
        rubCellStyle = workbook.createCellStyle();
        rubCellStyle.setDataFormat(workbook.createDataFormat().getFormat(Formula.formatString(2)));

        createHeaders(sheet);
//        Row rowHeader = sheet.createRow(0);
//        for (RowName rowName : RowName.values()) {
//            PoiSSUtil.createCellAndSetValue(rowHeader, rowName.getColumnNumber(), rowName.getRowNameRus());//МПС
//        }

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
//        boRevenueReportResult.setSuccessDepositRecordsCount(successDepositRecords);
//        boRevenueReportResult.setSuccessCreditRecordsCount(successCreditRecords);
        //todo: надо сохранять значения которые выше в комментах в loadAndGropedTickets для дальнейшего отображение на тсрснице результатов

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
            buildOperationRegisterSheet(workbook, stopped, loadAndGroupTickets);
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
        MPS("МПС"),
        DATE_PROCESSING("Дата обработки"),
        DATE_OPERATION("Дата операции"),
        TIME_OPERATION("Время операции"),
        CURRENCY("Валюта"),
        AMOUNT_OPERATION("Сумма операции"),
        AMOUNT_FEE("Сумма комиссии"),
        CODE_CURRENCY_MPS("Код валюты МПС"),
        RATE_MPS("Курс МПС"),
        AMOUNT_IN_CURRENCY_MPS("Сумма в валюте МПС"),
        RATE_CURRENCY("Курс валюты"),
        AMOUNT_IN_RUB("Сумма в рублях"),
        AMOUNT_FEE_IN_RUB("Сумма комиссии в рублях"),
        RATE_COMMISSION("Ставка комиссии"),
        PAN("Номер карты"),
        APPROVAL_CODE("Код авторизации"),
        RBS_ID("RBS ID"),
        FE_UTRNNO("FE UTRNNO"),
        BO_UTRNNO("BO UTRNNO"),
        REF_NUM("RefNum"),
        TYPE_OPERATION_TPTP("Тип операции(TPTP)"),
        SIGN_OPERATION("Знак операции"),
        INVOICE_NUMBER("Номер счёта");

        private final String rowNameRus;

        RowName(String rowNameRus) {
            this.rowNameRus = rowNameRus;
        }

        public String getRowNameRus() {
            return rowNameRus;
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
        Row rowHeader = sheet.createRow(0);
        for (RowName rowName : RowName.values()) {
            PoiSSUtil.createCellAndSetValue(rowHeader, rowName.getColumnNumber(), rowName.getRowNameRus());//МПС
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
            case MPS: {//МПС
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(), revenue.getRealOperationType().getType());
                break;
            }
            case DATE_PROCESSING: {//Дата обработки
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(), revenue.getOPER_DATE());
                break;
            }
            case DATE_OPERATION: {//Дата операции
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(), revenue.getAUTH_DATE());
                break;
            }
            case TIME_OPERATION: {//Время операции
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(), revenue.getAUTH_TIME());
                break;
            }
            case CURRENCY: {//Валюта
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(), revenue.getCURRENCY_CLIENT());
                break;
            }
            case AMOUNT_OPERATION: {//Сумма операции
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(), rowNumParams.amountOperation.doubleValue());
                break;
            }
            case AMOUNT_FEE: {//Сумма комиссии
                PoiSSUtil.createCellAndSetFormula(row, rowName.getColumnNumber(),
                        Formula.signFeeOperation(row.getRowNum(),
                                RowName.SIGN_OPERATION.getColumnNumber(), RowName.AMOUNT_OPERATION.getColumnNumber(), RowName.RATE_COMMISSION.getColumnNumber()));
                break;
            }
            case CODE_CURRENCY_MPS: {//код валюты мпс
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(), revenue.getCURRENCY_MPS());
                break;
            }
            case RATE_MPS: {//Курс мпс
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(), rowNumParams.rateMps);
                break;
            }
            case AMOUNT_IN_CURRENCY_MPS: {//сумма в валюте мпс
                PoiSSUtil.createCellAndSetFormula(row, rowName.getColumnNumber(), Formula.sign(row.getRowNum(), RowName.SIGN_OPERATION.getColumnNumber(), rowNumParams.amountMps.doubleValue()));
                break;
            }
            case RATE_CURRENCY: {//Курс валюты (в руб)
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(), rowNumParams.rateCb);
                break;
            }
            case AMOUNT_IN_RUB: { //Сумма в рублях
                PoiSSUtil.createCellAndSetFormula(row, rowName.getColumnNumber(),
                        Formula.amountInRubUseAllRates(row.getRowNum(), rowNumParams.amountOperationInMinor.toString(), RowName.RATE_CURRENCY.getColumnNumber(), RowName.RATE_MPS.getColumnNumber()),
                        rubCellStyle);
                break;
            }
            case AMOUNT_FEE_IN_RUB: { //Сумма комиссии в рублях
                PoiSSUtil.createCellAndSetFormula(row, rowName.getColumnNumber(),
                        Formula.feeAmountInRub(row.getRowNum(), RowName.AMOUNT_IN_RUB.getColumnNumber(), rowNumParams.feeRub.doubleValue()));
                break;
            }
            case RATE_COMMISSION: {//Ставка комиссии
                PoiSSUtil.createCellAndSetFormula(row, rowName.getColumnNumber(),
                        Formula.feeRateFormula(row.getRowNum(), RowName.AMOUNT_FEE_IN_RUB.getColumnNumber(), RowName.AMOUNT_IN_RUB.getColumnNumber()),
                        feeCellStyle);
                break;
            }
            case PAN: {//номер карты
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(), revenueRecord.getPan());
                break;
            }
            case APPROVAL_CODE: {//Код авторизации
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(), revenue.getAUTH_CODE());
                break;
            }
            case RBS_ID: {//RbsId
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(), revenue.getRBS_ORDER());
                break;
            }
            case FE_UTRNNO: {//FE UTRNNO
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(), revenue.getFE_UTRNNO());
                break;
            }
            case BO_UTRNNO: {//BO UTRNNO
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(), revenue.getBO_UTRNNO());
                break;
            }
            case REF_NUM: {//RefNum
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(), revenue.getRRN());
                break;
            }
            case TYPE_OPERATION_TPTP: {//Тип операции(TPTP)
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(), revenue.getTRANS_TYPE());
                break;
            }
            case SIGN_OPERATION: {//Знак операции
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(), revenue.getOPER_SIGN());
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
        return messageSource.getMessage(ISystem.SYSTEM_PREFIX + "report.excelRevenueOperationRegisterSv",new Object[]{},
                "Excel revenue operation register report for Smart Vista", LocaleContextHolder.getLocale());
    }
}
