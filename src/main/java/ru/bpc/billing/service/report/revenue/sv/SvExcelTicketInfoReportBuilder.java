package ru.bpc.billing.service.report.revenue.sv;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import ru.bpc.billing.domain.Carrier;
import ru.bpc.billing.domain.FileType;
import ru.bpc.billing.domain.ProcessingFile;
import ru.bpc.billing.domain.billing.BillingFile;
import ru.bpc.billing.domain.bo.BORecord;
import ru.bpc.billing.domain.report.ReportRecord;
import ru.bpc.billing.service.ApplicationService;
import ru.bpc.billing.service.CurrencyService;
import ru.bpc.billing.service.ISystem;
import ru.bpc.billing.service.report.*;
import ru.bpc.billing.service.report.revenue.Formula;
import ru.bpc.billing.util.BillingFileUtils;
import ru.bpc.billing.util.poi.PoiSSUtil;
import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.apache.poi.ss.usermodel.CellStyle.*;
import static org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD;

public class SvExcelTicketInfoReportBuilder implements ReportBuilder {

    private static final String TEMPLATE_PATH = "ticketInfoReportTemplate.xlsx";
    private static final int MAIN_SHEET_INDEX = 0;
    private static final int REJECT_SHEET_INDEX = 1;
    private static final String MAIN_SHEET_NAME = "successful";
    private static final String REJECT_SHEET_NAME = "rejects";
    private static final int MAIN_START_ROW = 18;
    private static final int REJECT_START_ROW = 16;
    private CellStyle feeCellStyle;
    private CellStyle curCellStyle;
    private CellStyle bigCellStyle;
    private CellStyle anyCellStyle;
    private CellStyle capCellStyle;
    private CellStyle boldCellStyle;
    private CellStyle footerCellStyle;
    private CellStyle boldCurCellStyle;
    private static Logger logger = LoggerFactory.getLogger(SvExcelRevenueOperationRegisterReportBuilder.class);
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_");
    private HashSet<String> currencyCodes = new HashSet<>();

    @Resource
    private ApplicationService applicationService;
    @Resource
    protected MessageSource messageSource;

    protected ArrayList<ReportRecord> dataPreparation(Sheet sheet, AtomicBoolean stopped, LoadAndGroupTickets loadAndGroupTickets) throws InterruptedException {
        String processingDate = null;
        //множества для сбора информации для шапки отчета
        currencyCodes.clear();//очищаем множество с кодами валют
        TreeSet<String> transDates = new TreeSet<>();//множество с уникальными датами операций
        HashSet<String> fileTypes = new HashSet<>();//множество с уникальными типами ARC/BSP
        HashSet<String> cardBrands = new HashSet<>();//множество с уникальными мпс Visa/MasterCard
        HashSet<String> currencyNames = new HashSet<>();//множество с уникальными названиями валют
        ArrayList<ReportRecord> selectedReportRecords = new ArrayList<>();//массив отобранных для отчета записей
        //формирование списка с записями для отчета
        List<ReportRecord> reportRecords = loadAndGroupTickets.getReportRecords();//получение общего списка из файла
        int i = 1;
        for (ReportRecord revenueRecord : reportRecords) {//идем по списку
            if (i % 100 == 0 && stopped.get()) {
                throw new InterruptedException("Interrupted");
            }
            if (null == revenueRecord.getBoRecord()) continue;//пропускаем несуществующие БО записи
            if (sheet.getSheetName().equals(MAIN_SHEET_NAME)) {//для листа с успешными записями...
                if (!revenueRecord.isSuccess() || !revenueRecord.getBoRecord().isSuccess()) continue;   //пропускаем реджекты
            }
            if (sheet.getSheetName().equals(REJECT_SHEET_NAME)) {//для листа с реджект-записями...
                if (!revenueRecord.isReject()) continue;   //пропускаем успешные
            }
            BORecord revenue = revenueRecord.getBoRecord();
            i++;
            selectedReportRecords.add(revenueRecord);//добавляем запись в список, если прошли все верхние условия
            if (null == processingDate) processingDate = revenue.getOPER_DATE();//сохранение даты операции
            currencyCodes.add(revenue.getCURRENCY_MPS()); //сохранение кода валюты мпс
            transDates.add(revenue.getAUTH_DATE()); //сохранение даты авторизации
            fileTypes.add(revenue.getTYPE_FILE()); //сохранение типа файла
            cardBrands.add(getMpsFullName(revenue.getOperationType().getType())); //преобразование и сохранение Visa/MasterCard
            currencyNames.add(getCurrencyName(revenue.getCURRENCY_MPS()));//сохранение названия валюты мпс
        }

        //Шапка отчета///////////////////////////////////////////////////////////////////////////////////
        PoiSSUtil.createCellAndSetValue(sheet.getRow(4), 2, processingDate, capCellStyle);  //Processing Date
        PoiSSUtil.createCellAndSetValue(sheet.getRow(5), 2, printDashSet(transDates), capCellStyle);  //Transaction Date
        Carrier carrier = BillingFileUtils.getCarrier(loadAndGroupTickets.getBillingFiles());
        String carrierName = carrier == null ? "" : carrier.getName();
        PoiSSUtil.createCellAndSetValue(sheet.getRow(6), 2, carrierName, capCellStyle);  //Название АК
        PoiSSUtil.createCellAndSetValue(sheet.getRow(7), 2, printComaSet(fileTypes), capCellStyle);  //Тип транзакции
        PoiSSUtil.createCellAndSetValue(sheet.getRow(8), 2, printComaSet(cardBrands), capCellStyle);  //Название МПС
        PoiSSUtil.createCellAndSetValue(sheet.getRow(9), 2, printComaSet(currencyNames), capCellStyle);  //Название валюты
        return selectedReportRecords;
    }

    protected void buildMainTicketInfoSheet(Workbook workbook, AtomicBoolean stopped, LoadAndGroupTickets loadAndGroupTickets) throws InterruptedException {
        //создание листа с успешными записями
        Sheet sheet = workbook.getSheetAt(MAIN_SHEET_INDEX);
        workbook.setSheetName(MAIN_SHEET_INDEX, MAIN_SHEET_NAME);
        ArrayList<ReportRecord> reportRecords = dataPreparation(sheet,stopped,loadAndGroupTickets);
        ArrayList<Integer> totalGroupRows = new ArrayList<>();//список c номерами строк с итогами каждой группы валюты
        int rowNum = MAIN_START_ROW;
        for (String code : currencyCodes) {
            int numBefore = sheet.getNumMergedRegions();//сохранить количество обьединений ячеек на начало группы (для отрисовки рамок в группе)
            createHeaders(sheet, rowNum, code); //создать хидер для группы с валютой
            rowNum = rowNum + 5;//хидер занимает 5 строк, прибавить к текущему номеру строки
            int startGroupRow = rowNum;//сохранить номер строки, с которой начинается группа с валютой
            for (ReportRecord revenueRecord : reportRecords) {
                if (revenueRecord.getCurrencyMPS().equals(code)) {
                    Row row = sheet.createRow(rowNum);
                    fillTicketInfoRecordToExcelRow(workbook, row, revenueRecord);
                    rowNum++;
                }
            }
            createFooters(sheet, startGroupRow, rowNum, code);//создать футер для группы с валютой
            rowNum++;
            totalGroupRows.add(rowNum);//сохранить номер строки с итогами по группе для использования в формулах шапки
            setBordersToMergedCells(workbook, sheet, numBefore + 1);//дорисовать границы новым обьединениям группы
        }
        //формулы для подсчета общего количества Gross amount и Fee amount в шапке
        if (!totalGroupRows.isEmpty()) {
            PoiSSUtil.createCellAndSetFormula(sheet.getRow(15), 9, Formula.totalSettlementTicketInfo("J", totalGroupRows), curCellStyle);
            PoiSSUtil.createCellAndSetFormula(sheet.getRow(15), 10, Formula.totalSettlementTicketInfo("K", totalGroupRows), curCellStyle);
        }
        sheet.setForceFormulaRecalculation(true);//включить принудительный пересчет формул в свойствах листа
    }

    protected void buildRejectTicketInfoSheet(Workbook workbook, AtomicBoolean stopped, LoadAndGroupTickets loadAndGroupTickets) throws InterruptedException {
        //создание листа с реджект записями
        Sheet sheet = workbook.getSheetAt(REJECT_SHEET_INDEX);
        workbook.setSheetName(REJECT_SHEET_INDEX, REJECT_SHEET_NAME);
        ArrayList<ReportRecord> reportRecords = dataPreparation(sheet,stopped,loadAndGroupTickets);
        ArrayList<Integer> totalGroupRows = new ArrayList<>();//список c номерами строк с итогами каждой группы валюты
        int rowNum = REJECT_START_ROW;
        for (String code : currencyCodes) {
            int numBefore = sheet.getNumMergedRegions();//сохранить количество обьединений ячеек на начало группы (для отрисовки рамок в группе)
            createHeaders(sheet, rowNum, code); //создать хидер для группы с валютой
            rowNum = rowNum + 5;//хидер занимает 5 строк, прибавить к текущему номеру строки
            int startGroupRow = rowNum;//сохранить номер строки, с которой начинается группа с валютой
            for (ReportRecord revenueRecord : reportRecords) {
                if (revenueRecord.getCurrencyMPS().equals(code)) {
                    Row row = sheet.createRow(rowNum);
                    fillTicketInfoRecordToExcelRow(workbook, row, revenueRecord);
                    rowNum++;
                }
            }
            createFooters(sheet, startGroupRow, rowNum, code);//создать футер для группы с валютой
            rowNum++;
            totalGroupRows.add(rowNum);//сохранить номер строки с итогами по группе для использования в формулах шапки
            setBordersToMergedCells(workbook, sheet, numBefore + 1);//дорисовать границы новым обьединениям группы
        }
        //формулы для подсчета общего количества Gross amount и Fee amount в шапке
        if (!totalGroupRows.isEmpty()) {
            PoiSSUtil.createCellAndSetFormula(sheet.getRow(15), 9, Formula.totalSettlementTicketInfo("J", totalGroupRows), boldCurCellStyle);
            PoiSSUtil.createCellAndSetFormula(sheet.getRow(15), 10, Formula.totalSettlementTicketInfo("K", totalGroupRows), boldCurCellStyle);
        }
        sheet.setForceFormulaRecalculation(true);//включить принудительный пересчет формул в свойствах листа
    }


    @Override
    public File build(LoadAndGroupTickets loadAndGroupTickets, AtomicBoolean stopped) throws ReportBuildException{
        StringBuilder fileFormats = new StringBuilder();
        for (BillingFile billingFile : loadAndGroupTickets.getBillingFiles()) {
            fileFormats.append(billingFile.getFormat().name()).append("_");
        }
        boolean isConsolidate = 1 < loadAndGroupTickets.getBillingFiles().size();
        Workbook workbook = PoiSSUtil.createWorkBookFromTemplate(TEMPLATE_PATH);
        //создание всех необходимых стилей ячеек, используемых в побилетном отчете
        feeCellStyle = createNewCellStyle(workbook, "Arial", "11", ALIGN_CENTER, false, true);
        curCellStyle = createNewCellStyle(workbook, "Arial", "11", ALIGN_RIGHT, false, true);
        anyCellStyle = createNewCellStyle(workbook, "Arial", "11", ALIGN_CENTER, false, true);
        capCellStyle = createNewCellStyle(workbook, "Arial", "12", ALIGN_LEFT, false, false);
        bigCellStyle = createNewCellStyle(workbook, "Arial", "16", ALIGN_CENTER, true, false);
        boldCellStyle = createNewCellStyle(workbook, "Arial", "11", ALIGN_CENTER, true, true);
        footerCellStyle = createNewCellStyle(workbook, "Arial", "11", ALIGN_RIGHT, true, true);
        boldCurCellStyle = createNewCellStyle(workbook, "Arial", "11", ALIGN_RIGHT, true, true);
        feeCellStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
        curCellStyle.setDataFormat(workbook.createDataFormat().getFormat(Formula.formatString(2)));
        boldCurCellStyle.setDataFormat(workbook.createDataFormat().getFormat(Formula.formatString(2)));
        curCellStyle.setIndention((short) 1);
        boldCurCellStyle.setIndention((short) 1);

        try {
            buildRejectTicketInfoSheet(workbook, stopped, loadAndGroupTickets);//создание листа реджектов
            buildMainTicketInfoSheet(workbook, stopped, loadAndGroupTickets);//создание листа успешных
        } catch (InterruptedException e) {
            throw new ReportBuildException("Error build operation register file",e);
        }

        Carrier carrier = BillingFileUtils.getCarrier(loadAndGroupTickets.getBillingFiles());
        String iataCode = carrier == null ? "" : carrier.getIataCode();//узнаем IATA код
        File ticketInfoFile = new File(applicationService.getHomeDir(getFileType()) +
                dateFormat.format(loadAndGroupTickets.getCreatedDate()) + iataCode + "_BSP.xlsx");//формируем имя excel файла
        PoiSSUtil.saveWorkbook(workbook, ticketInfoFile);//сохраняем книгу excel

        return ticketInfoFile;
    }

    @Override
    public FileType getFileType() {
        return FileType.TICKET_INFO_REPORT;
    }

    @Override
    public boolean linkFileToRecord(ProcessingFile processingFile, ReportRecord reportRecord) {
        return reportRecord.isSuccess();
    }

    /**
     * Задаётся порядок следования столбцов
     */
    protected enum RowName {
        DATE_PROCESSING("Processing date"),
        DATE_OPERATION("Transaction date"),
        DOCUMENT_NUMBER("Ticket No"),
        PAN("Primary Account Number"),
        MPS("Card Brand"),
        CURRENCY_MPS("Currency"),
        AMOUNT_IN_CURRENCY_MPS("Gross amount"),
        CURRENCY_RUB("Currency"),
        AMOUNT_IN_RUB("Gross amount"),
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
                if (this.equals(rowName)) return i+1;
                i++;
            }
            return i+1;
        }
    }

    protected class RowNumParams {
        BigDecimal amountMps;
        BigDecimal amountRub;
        BigDecimal feeRub;
    }

    protected void createHeaders(Sheet sheet, int startHeaderRow, String code) {
        //создать верхние строки заголовка
        Row rowHeaderEmpty1 = sheet.createRow(startHeaderRow);
        Row rowHeaderCombining1 = sheet.createRow(startHeaderRow+1);
        Row rowHeaderEmpty2 = sheet.createRow(startHeaderRow+2);
        Row rowHeaderCombining2 = sheet.createRow(startHeaderRow+3);
        rowHeaderEmpty1.setHeight((short) 200);
        rowHeaderEmpty2.setHeight((short) 200);
        //обьединить ячейки в верхних строках заголовка
        sheet.addMergedRegion(CellRangeAddress.valueOf("$B$" + (startHeaderRow+2) + ":$M$" + (startHeaderRow+2)));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$G$" + (startHeaderRow+4) + ":$H$" + (startHeaderRow+4)));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$I$" + (startHeaderRow+4) + ":$M$" + (startHeaderRow+4)));
        //внести данные в верхние ячейки заголовка
        bigCellStyle.setFillPattern(SOLID_FOREGROUND);
        bigCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        PoiSSUtil.createCellAndSetValue(rowHeaderCombining1, 1, "Successful transactions | " + getCurrencyName(code), bigCellStyle);
        PoiSSUtil.createCellAndSetValue(rowHeaderCombining2, 6, "Submission currency ", boldCellStyle);
        PoiSSUtil.createCellAndSetValue(rowHeaderCombining2, 8, "Settlement currency", boldCellStyle);
        //создать нижнюю полную строку заголовка
        Row rowHeader= sheet.createRow(startHeaderRow+4);
        for (RowName rowName : RowName.values()) {
            PoiSSUtil.createCellAndSetValue(rowHeader, rowName.getColumnNumber(), rowName.getrowNameEng(), boldCellStyle);
        }
    }

    protected void createFooters(Sheet sheet, int startRow, int row, String code) {
        //создание футера для блока валюты
        Row footerRow = sheet.createRow(row);
        sheet.addMergedRegion(CellRangeAddress.valueOf("$B$" + (row+1) + ":$F$" + (row+1)));
        PoiSSUtil.createCellAndSetValue(footerRow, 1, "Total for "+ getCurrencyName(code) + " (" + code + "):", footerCellStyle);
        PoiSSUtil.createCellAndSetValue(footerRow, 6, getCurrencyName(code), boldCellStyle);
        PoiSSUtil.createCellAndSetValue(footerRow, 8, "RUR", boldCellStyle);
        if ((row-startRow)>0) {
            PoiSSUtil.createCellAndSetFormula(footerRow, 7, Formula.summGroupForTicketInfo("H", startRow + 1, row), boldCurCellStyle);
            PoiSSUtil.createCellAndSetFormula(footerRow, 9, Formula.summGroupForTicketInfo("J", startRow + 1, row), boldCurCellStyle);
            PoiSSUtil.createCellAndSetFormula(footerRow, 10, Formula.summGroupForTicketInfo("K", startRow + 1, row), boldCurCellStyle);
            PoiSSUtil.createCellAndSetFormula(footerRow, 11, Formula.summGroupForTicketInfo("L", startRow + 1, row), boldCurCellStyle);
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
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(), revenueRecord.getDocumentNumber(), anyCellStyle);
                break;
            }
            case PAN: {//номер карты
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(),
                        maskCardNumber(revenueRecord.getPan()), anyCellStyle);
                break;
            }
            case MPS: {//МПС
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(),
                        getMpsFullName(revenue.getOperationType().getType()), anyCellStyle);
                break;
            }
            case CURRENCY_MPS: {//валюта мпс
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(),
                        getCurrencyName(revenue.getCURRENCY_MPS()), anyCellStyle);
                break;
            }
            case AMOUNT_IN_CURRENCY_MPS: {//сумма в валюте мпс
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(),
                        rowNumParams.amountMps.doubleValue(), curCellStyle);
                break;
            }
            case CURRENCY_RUB: {//валюта расчетов с ТСП
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(),
                        getCurrencyName(revenue.getCURRENCY()), anyCellStyle);
                break;
            }
            case AMOUNT_IN_RUB: { //Сумма в рублях
                PoiSSUtil.createCellAndSetValue(row, rowName.getColumnNumber(),
                        rowNumParams.amountRub.doubleValue(), curCellStyle);
                break;
            }
            case AMOUNT_FEE_IN_RUB: { //Сумма комиссии в рублях
                PoiSSUtil.createCellAndSetFormula(row, rowName.getColumnNumber(),
                        Formula.feeAmountInRubTicketInfo(row.getRowNum(), RowName.AMOUNT_IN_RUB.getColumnNumber(),
                                rowNumParams.feeRub.doubleValue()), curCellStyle);
                break;
            }
            case AMOUNT_NET_IN_RUB: { //Сумма без комиссии в рублях
                PoiSSUtil.createCellAndSetFormula(row, rowName.getColumnNumber(),
                        Formula.netInRubTicketInfo(row.getRowNum()), curCellStyle);
                break;
            }
            case RATE_COMMISSION: {//Ставка комиссии
                PoiSSUtil.createCellAndSetFormula(row, rowName.getColumnNumber(),
                        Formula.feeRateFormula(row.getRowNum(), RowName.AMOUNT_FEE_IN_RUB.getColumnNumber(),
                                RowName.AMOUNT_IN_RUB.getColumnNumber()), feeCellStyle);
                break;
            }
        }
    }

    protected void fillTicketInfoRecordToExcelRow(Workbook workbook, Row row, ReportRecord revenueRecord) {
        RowNumParams rowNumParams = new RowNumParams();
        BORecord revenue = revenueRecord.getBoRecord();
        int minorMps = 0;
        int minorRub = 0;
        int minorOperationRub = 2;
        if (null != revenue.getCURRENCY_MPS()) {
            Currency currencyMps = CurrencyService.findByNumericCode(revenue.getCURRENCY_MPS());
            if (null != currencyMps) minorMps = currencyMps.getDefaultFractionDigits();
            else logger.warn("Unable to find currency by numericCode: {} and set minorMps as default value = {}", revenue.getCURRENCY_MPS(), minorMps);
        } else logger.warn("Unable to find currency because currency_mps is null");
        if (null != revenue.getCURRENCY()) {
            Currency currencyRub = CurrencyService.findByNumericCode(revenue.getCURRENCY());
            if (null != currencyRub) minorRub = currencyRub.getDefaultFractionDigits();
            else logger.warn("Unable to find currency by numericCode: {} and set minorRub as default value = {}", revenue.getCURRENCY_MPS(), minorRub);
        } else logger.warn("Unable to find currency because currency_rub is null");

        BigDecimal feeRub = BigDecimal.ZERO;
        BigDecimal amountRub = BigDecimal.ZERO;
        BigDecimal amountMps = BigDecimal.ZERO;

        if (StringUtils.isNotBlank(revenue.getAMOUNT_IN_CURRENCY_MPS()))
            amountMps = amountWithSign(new BigDecimal(revenue.getAMOUNT_IN_CURRENCY_MPS()), minorMps, revenue.isCredit());
        if (StringUtils.isNotBlank(revenue.getAMOUNT_IN_RUB()))
            amountRub = amountWithSign(new BigDecimal(revenue.getAMOUNT_IN_RUB()), minorRub, revenue.isCredit());
        if (StringUtils.isNotBlank(revenue.getMSC()))
            feeRub = new BigDecimal(revenue.getMSC()).movePointLeft(minorOperationRub);

        rowNumParams.amountMps = amountMps;
        rowNumParams.amountRub = amountRub;
        rowNumParams.feeRub = feeRub;

        createRecords(workbook, row, revenueRecord, rowNumParams);
    }

    protected BigDecimal amountWithSign(BigDecimal amountRecord, int minorUnit, boolean isCredit) {
        if (null == amountRecord) return BigDecimal.ZERO;
        BigDecimal amount = amountRecord.movePointLeft(minorUnit);
        return isCredit ? amount.negate() : amount;
    }

    protected BigDecimal sign(boolean isCredit, BigDecimal sign) {
        return isCredit ? sign.abs() : sign.negate();
    }

    protected String maskCardNumber(String pan) {
        //маскирование части номера кредитной карты
        if (null != pan) {
            return  pan.substring(0, 4) + "********" + pan.substring(12);
        }
        return null;
    }

    protected String getMpsFullName(String mps) {
        //преобразование кода МПС в полное название
        if (null != mps) {
            if (mps.contains("VI")) {
                return "Visa";
            }
            if (mps.contains("MC")) {
                return "MasterCard";
            }
        }
        return null;
    }

    protected String getCurrencyName(String code) {
        //преобразование кода валюты в название
        String alphabeticCode = null;
        if ( null != code ) {
            Currency currency = CurrencyService.findByNumericCode(code);
            if ( null != currency ) {
                alphabeticCode = currency.getCurrencyCode();
            }
            else logger.warn("Unable to find currency [client] by numericCode: {}",code);
        }
        return alphabeticCode;
    }

    protected CellStyle createNewCellStyle(Workbook wb, String fontName, String fontHeight, short alignment, boolean bold, boolean border) {
        //установка значений стилей ячейки
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName(fontName);
        font.setFontHeightInPoints(new Short(fontHeight));
        if (bold) font.setBoldweight(BOLDWEIGHT_BOLD);
        cs.setFont(font);
        cs.setAlignment(alignment);
        if (border) {
            if (bold) {
                cs.setBorderBottom(BORDER_MEDIUM);
                cs.setBorderTop(BORDER_MEDIUM);
                cs.setBorderLeft(BORDER_MEDIUM);
                cs.setBorderRight(BORDER_MEDIUM);
            } else {
                cs.setBorderBottom(BORDER_THIN);
                cs.setBorderTop(BORDER_THIN);
                cs.setBorderLeft(BORDER_THIN);
                cs.setBorderRight(BORDER_THIN);
            }
        }
        return cs;
    }

    protected  String printDashSet(TreeSet<String> ts) {
        //формирование строки со значениями отсортированного множества
        if (ts.size() == 1) return ts.first();
        if (ts.size() > 1) return ts.first() + " - " + ts.last();
        return "";
    }

    protected  String printComaSet(HashSet<String> hs) {
        //формирование строки со значениями множества
        if (hs.isEmpty()) return "";
        return hs.toString().replace("[","").replace("]","");
    }

    private void setBordersToMergedCells(Workbook workBook, Sheet sheet, int start) {
        //добавление границ для обьединенных ячеек
        int n = sheet.getNumMergedRegions();//получить новое количество обьединенных регионов
        if (n > start) {//если новое количество больше старого, т.е. есть что обрисовывать...
            for (int i=start; i< n; i++) {//пройтись по всем новым обьединениям ячеек и дорисовать границы
                CellRangeAddress rangeAddress = sheet.getMergedRegion(i);
                RegionUtil.setBorderTop(BORDER_MEDIUM, rangeAddress, sheet, workBook);
                RegionUtil.setBorderLeft(BORDER_MEDIUM, rangeAddress, sheet, workBook);
                RegionUtil.setBorderRight(BORDER_MEDIUM, rangeAddress, sheet, workBook);
                RegionUtil.setBorderBottom(BORDER_MEDIUM, rangeAddress, sheet, workBook);
            }
        }
    }

    @Override
    public String getSystemName() {
        return messageSource.getMessage(ISystem.SYSTEM_PREFIX + "report.excelTicketInfoSv",new Object[]{},
                "Excel ticket info report for Smart Vista", LocaleContextHolder.getLocale());
    }



}
