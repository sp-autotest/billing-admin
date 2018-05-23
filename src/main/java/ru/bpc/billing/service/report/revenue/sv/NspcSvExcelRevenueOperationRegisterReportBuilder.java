package ru.bpc.billing.service.report.revenue.sv;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.i18n.LocaleContextHolder;
import ru.bpc.billing.domain.FileType;
import ru.bpc.billing.domain.bo.BORecord;
import ru.bpc.billing.domain.report.ReportRecord;
import ru.bpc.billing.service.ISystem;
import ru.bpc.billing.service.report.revenue.Formula;
import ru.bpc.billing.util.poi.PoiSSUtil;

/**
 * User: Krainov
 * Date: 28.04.2015
 * Time: 13:29
 */
public class NspcSvExcelRevenueOperationRegisterReportBuilder extends SvExcelRevenueOperationRegisterReportBuilder {

    protected enum NscpRowName {
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

        NscpRowName(String rowNameRus) {
            this.rowNameRus = rowNameRus;
        }

        public String getRowNameRus() {
            return rowNameRus;
        }

        public int getColumnNumber() {
            int i = 0;
            for (NscpRowName rowName : NscpRowName.values()) {
                if (this.equals(rowName)) return i;
                i++;
            }
            return i;
        }
    }

    @Override
    protected void createHeaders(Sheet sheet) {
        Row rowHeader = sheet.createRow(0);
        for (NscpRowName rowName : NscpRowName.values()) {
            PoiSSUtil.createCellAndSetValue(rowHeader, rowName.getColumnNumber(), rowName.getRowNameRus());//МПС
        }
    }

    @Override
    protected void createRecords(Workbook workbook, Row row, ReportRecord revenueRecord, RowNumParams rowNumParams) {
        for (NscpRowName rowName : NscpRowName.values()) {
            fillCell(workbook, row, revenueRecord, rowName, rowNumParams);
        }
    }

    private void fillCell(Workbook workbook, Row row, ReportRecord revenueRecord, NscpRowName rowName, RowNumParams rowNumParams) {
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
            case INVOICE_NUMBER: {//номер счёта
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(), rowNumParams.invoiceNumber);
                break;
            }
        }
    }

    @Override
    public String getSystemName() {
        return messageSource.getMessage(ISystem.SYSTEM_PREFIX + "report.nscpExcelRevenueOperationRegisterSv", new Object[]{},
                "NSCP Excel revenue operation register report for Smart Vista", LocaleContextHolder.getLocale());
    }

    @Override
    public FileType getFileType() {
        return FileType.OPERATION_REGISTER_NSPC;
    }
}
