package ru.bpc.billing.service.report;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import ru.bpc.billing.domain.billing.BillingFile;
import ru.bpc.billing.domain.report.ReportRecord;
import ru.bpc.billing.service.CurrencyService;
import ru.bpc.billing.util.CountryUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: Krainov
 * Date: 04.12.13
 * Time: 11:19
 */
@Configurable(preConstruction = true)
public class LoadAndGroupTickets {

    private static final String COUNTRY_MPS = "%s %s";
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private int successDepositRecordsCount = 0;
    private int successCreditRecordsCount = 0;
    private int rejectDepositRecordsCount = 0;
    private int rejectCreditRecordsCount = 0;
    private List<BillingFile> billingFiles;
    private Date createdDate = new Date();
    private List<ReportRecord> reportRecords;

    public Date getCreatedDate() {
        return createdDate;
    }

    public List<BillingFile> getBillingFiles() {
        return billingFiles;
    }

    public void setBillingFiles(List<BillingFile> billingFiles) {
        this.billingFiles = billingFiles;
    }

    public static final String countryMps(String country, String mps) {
        if ( null == mps ) mps = "UNKNOWN";
        return (null == country) ? null : String.format(COUNTRY_MPS, CountryUtils.getCountryName(country, "ru"),mps);
    }

    //все страны для отчёта
    public Set<String> countries = new TreeSet<String>();
    //сгруппированные успешные билеты
    public TreeMap<String, Multimap<ReportGroup, ReportRecord>> groupedSuccessTickets = new TreeMap<String, Multimap<ReportGroup, ReportRecord>>();
    //сгруппированные реджекты
    public TreeMap<String, Multimap<RejectReportGroup, ReportRecord>> groupedRejectTickets = new TreeMap<String, Multimap<RejectReportGroup, ReportRecord>>();
    //сгруппированные успешные на реджекты по стране
    public TreeMap<String, Multimap<ReportGroup, ReportRecord>> groupedSuccessOnRejectTickets = new TreeMap<String, Multimap<ReportGroup, ReportRecord>>();

    public int getSuccessDepositRecordsCount() {
        return successDepositRecordsCount;
    }

    public int getSuccessCreditRecordsCount() {
        return successCreditRecordsCount;
    }

    public int getRejectDepositRecordsCount() {
        return rejectDepositRecordsCount;
    }

    public int getRejectCreditRecordsCount() {
        return rejectCreditRecordsCount;
    }

    private void countTransactionTypeRecords(ReportRecord revenueRecord) {
        if ( revenueRecord.isReject() ) {
            if ( revenueRecord.isCredit() ) rejectCreditRecordsCount++;
            else rejectDepositRecordsCount++;
        } else {
            if ( revenueRecord.isCredit() ) successCreditRecordsCount++;
            else successDepositRecordsCount++;
        }

    }

    protected void addToGroups(ReportRecord revenueRecord) {
        countTransactionTypeRecords(revenueRecord);
        countries.add(revenueRecord.getCountryCode());
        if (revenueRecord.isSuccessOnReject()) {
            Multimap<ReportGroup, ReportRecord> successOnRejectRevenueGroup = addToSuccessOnRejectByCountryCode(revenueRecord.getCountryCode());
            successOnRejectRevenueGroup.put(new ReportGroup(revenueRecord), revenueRecord);
        } else if (revenueRecord.isReject()) {
            Multimap<RejectReportGroup, ReportRecord> rejectRevenueGroup = addRejectToCountryCodeAndMps(revenueRecord.getCountryCode());
            rejectRevenueGroup.put(new RejectReportGroup(revenueRecord), revenueRecord);

            //AFBRBS-3147 : так как реджект записи мы тоже должны учитывать, то мы их добавляем к успешным (хотя эти записи не являются успешными, но мы должны их учесть в отчёте)
            Multimap<ReportGroup, ReportRecord> revenueGroup = addToSuccessByCountryCode(revenueRecord.getCountryCode());
            revenueRecord.setErrorMessage(null);//сбрасываем сообщение об ошибке, чтобы не отображать её в отчёте.
            revenueGroup.put(new ReportGroup(revenueRecord), revenueRecord);
        } else {
            Multimap<ReportGroup, ReportRecord> revenueGroup = addToSuccessByCountryCode(revenueRecord.getCountryCode());
            revenueGroup.put(new ReportGroup(revenueRecord), revenueRecord);
        }
    }

    protected Multimap<ReportGroup, ReportRecord> addToSuccessOnRejectByCountryCode(String country) {
        Multimap<ReportGroup, ReportRecord> successOnRejectRevenueGroup = groupedSuccessOnRejectTickets.get(country);
        if (null == successOnRejectRevenueGroup) {
            successOnRejectRevenueGroup = ArrayListMultimap.create(8, 100);
            groupedSuccessOnRejectTickets.put(country, successOnRejectRevenueGroup);
        }
        return successOnRejectRevenueGroup;
    }

    protected Multimap<ReportGroup, ReportRecord> addToSuccessByCountryCode(String country) {
        Multimap<ReportGroup, ReportRecord> revenueGroup = groupedSuccessTickets.get(country);
        if (null == revenueGroup) {
            revenueGroup = ArrayListMultimap.create(8, 100);
            groupedSuccessTickets.put(country, revenueGroup);
        }
        return revenueGroup;
    }

    protected Multimap<RejectReportGroup, ReportRecord> addRejectToCountryCodeAndMps(String country) {
        Multimap<RejectReportGroup, ReportRecord> rejectRevenueGroup = groupedRejectTickets.get(country);
        if (null == rejectRevenueGroup) {
            rejectRevenueGroup = ArrayListMultimap.create(8, 100);
            groupedRejectTickets.put(country, rejectRevenueGroup);
        }
        return rejectRevenueGroup;
    }

    /**
     * Получаем комиссию по билету
     *
     * @param record
     * @return
     */
    protected BigDecimal ticketFeeWithSign(ReportRecord record) {
        if ( null == record || null == record.getFee() ) return null;
        return record.isCredit() ? record.getFee() : record.getFee().negate();
    }

    /**
     * Получаем стоимость билеты, учитывая тип операции (кредит или дебит)
     *
     * @param amountRecord
     * @param minorUnit
     * @param isCredit
     * @return
     */
    protected long ticketAmountLongWithSign(BigDecimal amountRecord, int minorUnit, boolean isCredit) {
        if (null == amountRecord) return 0;
        //long amount = amountRecord.movePointRight(minorUnit).longValueExact();
        long amount = amountRecord.longValueExact();
        //long amount = amountRecord.longValue();
        return isCredit ? -amount : amount;
    }

    protected BigDecimal sign(boolean isCredit, BigDecimal sign) {
        return isCredit ? sign.abs() : sign.negate();
    }

    /**
     * Просчитать одну строчку в отчёте. Если передаётся коллекция из нескольких записей, то происходит суммирование значений и возвращается одна строка
     * @param revenueRecords
     * @param submissionMinorUnit
     * @return
     */
    public ReportRow calculateRow(Collection<ReportRecord> revenueRecords, int submissionMinorUnit) {
        ReportRow result = new ReportRow();

        long totalGrossOperation = 0;
        long totalGrossMps = 0;
        long totalGrossBank = 0;
        BigDecimal totalFeeOperation = BigDecimal.ZERO;
        BigDecimal totalFeeMps = BigDecimal.ZERO;
        BigDecimal totalFeeBank = BigDecimal.ZERO;
        Currency currencyOperation = null;
        Currency currencyMps = null;
        int minorOperation = 0;
        int minorMps = 0;

        //идём по списку уже сгруппированных записей
        for (ReportRecord revenueRecord : revenueRecords) {
            result.invoiceNumber = revenueRecord.getInvoiceNumber();
            result.documentNumber = revenueRecord.getDocumentNumber();
            if (null == result.countryCode)
                result.countryCode = revenueRecord.getCountryCode();
            if (null == result.mps && null != revenueRecord.getOperationType())
                result.mps = revenueRecord.getOperationType().getType();
            if (null == result.currencyOperation)
                result.currencyOperation = revenueRecord.getCurrencyOperation();
            if (null == result.currencyMps)
                result.currencyMps = revenueRecord.getCurrencyMPS();
            if ( null != revenueRecord.getRateMPS() )
                result.rateMps = revenueRecord.getRateMPS();
            if ( null != revenueRecord.getRateBank() )
                result.rateBank = revenueRecord.getRateBank();
            if (null == result.errorMessage && revenueRecord.isReject())
                result.errorMessage = revenueRecord.getErrorMessage();
            if ( null == currencyOperation && null != result.currencyOperation )
                currencyOperation = CurrencyService.findByNumericCode(result.currencyOperation);
            if ( null == currencyMps && null != result.currencyMps )
                currencyMps = CurrencyService.findByNumericCode(result.currencyMps);

            if ( null != currencyOperation ) minorOperation = currencyOperation.getDefaultFractionDigits();
            if ( null != currencyMps ) minorMps = currencyMps.getDefaultFractionDigits();

            result.qty++;

            //считаем общий грос операции
            long grossOperation = ticketAmountLongWithSign(revenueRecord.getGrossOperation(), minorOperation, revenueRecord.isCredit());
            totalGrossOperation += grossOperation;

            //считаем общий грос мпс
            long grossMps = ticketAmountLongWithSign(revenueRecord.getGrossMps(), minorMps, revenueRecord.isCredit());
            totalGrossMps += grossMps;

            //считаем общий грос банка
            long grossBank = ticketAmountLongWithSign(revenueRecord.getGrossBank(), submissionMinorUnit, revenueRecord.isCredit());
            totalGrossBank += grossBank;

            //комиссия считается один раз и применяется для всех типов (мпс и по банку)
            BigDecimal fee = ticketFeeWithSign(revenueRecord);
            if (null == fee) {
                logger.warn("Unable to get fee for [{}]", revenueRecord);
                continue; //не удалось получить комиссию
            }

            //считаем ставку комиссии
            if ( null == revenueRecord.getGrossBank() ) {
                logger.warn("Unable to get gross bank for [{}]", revenueRecord);
                continue;
            }
            double feeRate = fee.abs().doubleValue() / revenueRecord.getGrossBank().abs().doubleValue();
            if ( !revenueRecord.isCredit() ) {//по кредитовой операции не обновляем значение комиссии, так как оно может быть равно 0
                result.feeRate = feeRate;
            }

            //считаем суммирующие комиссии
            //для раздела банка, мы получили абсолютное значение в БО файле
            totalFeeBank = totalFeeBank.add(fee);
            //для раздела МПС и операции считаем как произведение гроса и ставки комиссии
            totalFeeMps = totalFeeMps.add(sign(revenueRecord.isCredit(), revenueRecord.getGrossMps().multiply(BigDecimal.valueOf(feeRate))));
            totalFeeOperation = totalFeeOperation.add(sign(revenueRecord.isCredit(), revenueRecord.getGrossOperation().multiply(BigDecimal.valueOf(feeRate))));
        }

        result.grossOperation = new BigDecimal(totalGrossOperation).movePointLeft(minorOperation);
        result.feeOperation = totalFeeOperation.movePointLeft(minorOperation);
        result.netOperation = result.grossOperation.add(result.feeOperation);

        //AFBRBS-9047: округление ирландских кронн
        if(currencyOperation.getCurrencyCode().equals("ISK")) {
            result.grossOperation = result.grossOperation.divide(new BigDecimal(100.), 0, RoundingMode.DOWN);
            result.feeOperation = result.feeOperation.divide(new BigDecimal(100.), 0, RoundingMode.DOWN);
            result.netOperation = result.netOperation.divide(new BigDecimal(100.), 0, RoundingMode.DOWN);
        }

        result.grossMps = new BigDecimal(totalGrossMps).movePointLeft(minorMps);
        result.feeMps = totalFeeMps.movePointLeft(minorMps);
        result.netMps = result.grossMps.add(result.feeMps);

        result.grossBank = new BigDecimal(totalGrossBank).movePointLeft(submissionMinorUnit);
        result.feeBank = totalFeeBank.movePointLeft(submissionMinorUnit);
        result.netBank = result.grossBank.add(result.feeBank);

        return result;
    }

    public LoadAndGroupTickets(List<ReportRecord> reportRecords, AtomicBoolean stopped) throws ReportBuildException, InterruptedException {
        loadAndGroupTickets(reportRecords, stopped);
    }

    private synchronized void loadAndGroupTickets(List<ReportRecord> reportRecords, AtomicBoolean stopped) throws ReportBuildException, InterruptedException {
        this.reportRecords = reportRecords;
        try {
            logger.debug("Start build revenue report for {} records", reportRecords.size());
            int i = 1;
            for (ReportRecord reportRecord : reportRecords) {
                if (i % 100 == 0 && stopped.get()) {
                    throw new InterruptedException("Interrupted");
                }
                addToGroups(reportRecord);
                i++;
            }
            logger.debug("Have handled records");
        } catch (InterruptedException ie) {
            throw ie;
        } catch (Exception e) {
            logger.error("Error load and group tickets",e);
            throw new ReportBuildException("Error build revenue report for " + reportRecords.size() + " records", e);
        }
    }

    public List<ReportRecord> getReportRecords() {
        return reportRecords;
    }
}
