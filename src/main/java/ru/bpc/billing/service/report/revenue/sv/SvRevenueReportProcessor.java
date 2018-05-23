package ru.bpc.billing.service.report.revenue.sv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import ru.bpc.billing.domain.ProcessingRecord;
import ru.bpc.billing.domain.ProcessingStatus;
import ru.bpc.billing.domain.TransactionType;
import ru.bpc.billing.domain.billing.BillingFile;
import ru.bpc.billing.domain.bo.BORecord;
import ru.bpc.billing.domain.report.ReportRecord;
import ru.bpc.billing.repository.ProcessingFileRepository;
import ru.bpc.billing.repository.ProcessingRecordRepository;
import ru.bpc.billing.service.bo.BOProcessingResult;
import ru.bpc.billing.service.report.AbstractReportProcessor;
import ru.bpc.billing.service.report.ReportType;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * User: Krainov
 * Date: 15.09.2014
 * Time: 14:24
 */
public class SvRevenueReportProcessor extends AbstractReportProcessor {

    private final static Logger logger = LoggerFactory.getLogger(SvRevenueReportProcessor.class);
    public static final String REJECT_CAUSE_NO_DATA = "No data from backoffice";
    public static final String REJECT_CAUSE_NO_TRANSACTION = "NO_TRANSACTION";
    public static final String REJECT_BY_BACKOFFICE = "Reject by backoffice";
    private static final String COMMON_MESSAGE_PREFIX = "merchantconsole.common.";
    private static final String MESSAGE_PREFIX = "billing.converter.";

    @Resource
    private ProcessingRecordRepository processingRecordRepository;
    @Resource
    private ProcessingFileRepository processingFileRepository;

    @Override
    protected List<ReportRecord> mergeBillingAndBoRecords(BillingFile bf, Map<String,BORecord> processedObjects) {
        BillingFile billingFile = (BillingFile)processingFileRepository.findOne(bf.getId());
        Map<String,BORecord> boRevenueMap = new HashMap<>(processedObjects);
        List<ProcessingRecord> processingRecords = billingFile.getRecords();
        if (null == processingRecords || processingRecords.isEmpty()) {
            logger.debug("[{}] ==> Billing file doesn't have billing records. Return empty revenue records collection.", billingFile.getName());
            return new ArrayList<>();
        }
        if (null == boRevenueMap || boRevenueMap.isEmpty()) {
            logger.debug("[{}] ==> BO-revenue records are null or empty. Return empty revenue records collection.", billingFile.getName());
            return new ArrayList<>();
        }
        int billingRecordCount = processingRecords.size();
        int boRevenueRecordCount = boRevenueMap.size();
        BigDecimal totalBillingAmount = BigDecimal.ZERO;
        int lostCount = 0;
        int rejectCount = 0;
        int rejectInBoCount = 0;
        int rejectInBspCount = 0;
        int notFoundInBspCount = 0;
        int earlyHandled = 0;
        int unknownBoRecords = 0;
        int successOnReject = 0;
        int successBillingFileRecords = 0;
        int reincarnationBillingFileRecords = 0;

        logger.debug("[{}] ==> Start to merge billing records from {} file and {} revenue records.", billingFile.getName(), billingFile.getName(), boRevenueRecordCount);
        List<ReportRecord> reportRecords = new ArrayList<>();
        for (ProcessingRecord processingRecord : processingRecords) {
            logger.trace("[{}] ==> Take one billingRecord", processingRecord.getRbsId());
            ReportRecord reportRecord = new ReportRecord();
            reportRecord.setInvoiceNumber(processingRecord.getInvoiceNumber());
            reportRecord.setDocumentNumber(processingRecord.getDocumentNumber());
            reportRecord.setCountryCode(processingRecord.getCountryCode());
            reportRecord.setCurrencyOperation(processingRecord.getCurrency());
            reportRecord.setGrossOperation(processingRecord.getAmount(), processingRecord.getTransactionType().isCredit());
            totalBillingAmount = totalBillingAmount.add(reportRecord.getGrossOperation());
            reportRecord.setProcessingRecord(processingRecord);
            reportRecord.setPan(processingRecord.getPan());

            reportRecords.add(reportRecord);

            //потерянные данные, значит в БСП есть запись, а в бо файлах её нету, поэтому ставить что реджект и сообщение
            BORecord boRecord = boRevenueMap.get(processingRecord.getRbsId());
            if (null == boRecord) {
                logger.trace("[{}] ==> [Case 1]: BO-files don't have record with the same rbsId. ==> [Revenue record]: errorMessage = {}, status = REJECT. ==> Go to next billing record.",
                        processingRecord.getRbsId(), REJECT_CAUSE_NO_DATA);
                reportRecord.setErrorMessage(REJECT_CAUSE_NO_DATA);
                reportRecord.setReject();
                processingRecord.setStatus(ProcessingStatus.REJECT_BO_LOST); //если запись есть в Биллинге, но нет в БО, то помечаем его, что он неуспешный
                lostCount++;
                rejectCount++;
                continue;
            }
            logger.trace("[{}] ==> Have found BO-record in BO-files.", processingRecord.getRbsId());
            reportRecord.setBORevenue(boRecord);
            boRevenueMap.remove(boRecord.getRBS_ORDER());

            //проверяем, что если запись была помечена как потерянная, а щас появилась и статус у БО успешный, то реанкорнируем её
            if (processingRecord.getStatus().equals(ProcessingStatus.REJECT_BO_LOST) && boRecord.isSuccess()) {
                logger.trace("[{}] ==> [Case 1.1]: BO-files have record with the same rbsId and billing record has status REJECT_BO_LOST and BO-record has status SUCCESS. ==> [Revenue record]: status = SUCCESS, [Billing record]: status = SUCCESS",
                        processingRecord.getRbsId());
                processingRecord.setStatus(ProcessingStatus.SUCCESS);
                reportRecord.setSuccess();
                reincarnationBillingFileRecords++;
            }

            //реджект запись из БО файла
            if (!boRecord.isSuccess()) {
                logger.trace("[{}] ==> [Case 2]: BO-record has status = REJECT. ==> [Revenue record]: status = REJECT. [BillingRecord]: status = REJECT_BO.", processingRecord.getRbsId());
                reportRecord.setReject();
                reportRecord.setErrorMessage(REJECT_BY_BACKOFFICE);
                processingRecord.setStatus(ProcessingStatus.REJECT_BO);
                rejectCount++;
                rejectInBoCount++;
            }

            //был реджект на нашей стороне ещё в БСП
            if (processingRecord.getStatus().equals(ProcessingStatus.REJECT_BILLING)) {
                logger.trace("[{}] ==> [Case 3]: Billing record has status = REJECT_BSP. [Revenue record]: errorMessage = {}, status = REJECT.", processingRecord.getRbsId(), processingRecord.getErrorMessage());
                reportRecord.setErrorMessage(processingRecord.getErrorMessage());
                reportRecord.setReject();
                rejectCount++;
                rejectInBspCount++;
            }

            if (!reportRecord.isReject()) {
                processingRecord.setAmountMps(reportRecord.getGrossMps().intValue());
                processingRecord.setAmountRub(reportRecord.getGrossBank().intValue());
                processingRecord.setRateMps(reportRecord.getRateMPS());
                processingRecord.setRateCb(reportRecord.getRateBank());
                successBillingFileRecords++;
            }
        }
        logger.debug("[{}] ==> All billing records have been handled. Try to check extra BO-records which haven't been handled.", billingFile.getName());

        //в БО файлах прищло записей больше чем в биллинговом файле
        if (!boRevenueMap.isEmpty()) {
            logger.debug("[{}] ==> Extra BO-revenue records = {}", billingFile.getName(), boRevenueMap.size());
            for (Map.Entry<String, BORecord> entry : boRevenueMap.entrySet()) {
                BORecord boRevenue = entry.getValue();
                logger.trace("[{}] ==> take one BO-revenue record [status = {}]", entry.getKey(), boRevenue.isSuccess() ? "SUCCESS" : "REJECT");
                if (!boRevenue.isSuccess()) {
                    logger.trace("[{}] ==> [Case 4]: BO-revenue record is REJECT. We don't know about this record anything, so will skip and go to next BO-revenue record.", entry.getKey());
                    unknownBoRecords++;
                    continue; //не реагируем (мы ничего не знаем про этот реджект)
                }

                logger.trace("[{}] ==> Try to looking for billing record by the same rbsId.", entry.getKey());
                //пытаемся найти запись в других биллинговых файлах , возможно это успешно на реджект
                ProcessingRecord processingRecord = processingRecordRepository.findByRbsId(entry.getKey());
                if (null == processingRecord) {
                    logger.trace("[{}] ==> [Case 5]: Billing record haven't found by that rbsId. Won't be handle her. Go to next.", entry.getKey());
                    notFoundInBspCount++;
                    continue;
                }
                logger.trace("[{}] Found billing record by that rbsId. {}", entry.getKey(), processingRecord);

                //запись из другого биллингового файла и у неё был ранее реджект из БО (либо остался статус реджект в БСП) или уже проставили успешно на реджект (заносили ранее в отчёт)
                if (processingRecord.getStatus().equals(ProcessingStatus.REJECT_BO)
                        //|| billingRecord.getStatus().equals(BillingStatus.SUCCESS_AFTER_REJECT)
                        || processingRecord.getStatus().equals(ProcessingStatus.REJECT_BILLING)
                        || processingRecord.getStatus().equals(ProcessingStatus.REJECT_BO_LOST)) {
                    logger.trace("[{}] ==> [Case 6]: Found billing record has status = REJECT_BO or REJECT_BSP or REJECT_BO_LOST (real = {}). ==> This BO-revenue record is 'SUCCESS on early REJECT_BO'", entry.getKey(), processingRecord.getStatus());
                    //success_on_reject
                    ReportRecord reportRecord = new ReportRecord();
                    reportRecord.setInvoiceNumber(processingRecord.getInvoiceNumber());
                    reportRecord.setDocumentNumber(processingRecord.getDocumentNumber());
                    reportRecord.setCountryCode(processingRecord.getCountryCode());
                    reportRecord.setCurrencyOperation(processingRecord.getCurrency());
                    reportRecord.setBORevenue(boRevenue);
                    reportRecord.setSuccessOnReject();
                    reportRecord.setProcessingRecord(processingRecord);

                    reportRecords.add(reportRecord);

                    processingRecord.setStatus(ProcessingStatus.SUCCESS_AFTER_REJECT);
                    logger.trace("[{}] ==> Set status SUCCESS_AFTER_REJECT for that billing record.", entry.getKey());

                    successOnReject++;

                    if (!reportRecord.isReject()) {
                        processingRecord.setAmountMps(reportRecord.getGrossMps().intValue());
                        processingRecord.setAmountRub(reportRecord.getGrossBank().intValue());
                        processingRecord.setRateMps(reportRecord.getRateMPS());
                        processingRecord.setRateCb(reportRecord.getRateBank());
                        successBillingFileRecords++;
                    }
                } else {
                    logger.trace("[{}] ==> [Case 7]: Billing record has status {}. Won't be include that record to revenue-report.", entry.getKey(), processingRecord.getStatus());
                    earlyHandled++;
                    continue;
                }
            }
        }
        logger.debug("[{}] ==> Prepare {} revenue record for revenue report.", billingFile.getName(), reportRecords.size());
        logger.debug("[{}] ==> billingRecordCount = {},totalBillingAmount = {}, lostCount = {}, rejectCount = {}, rejectInBoCount = {}, " +
                        "rejectInBspCount = {}, notFoundInBspCount = {}, earlyHandled = {}, unknownBoRecords(bo reject records and isn't this billing file)  = {}, successOnReject = {}, successBillingFileRecords = {}, reincarnationBillingFileRecords = {}",
                billingFile.getName(), billingRecordCount, totalBillingAmount, lostCount, rejectCount, rejectInBoCount, rejectInBspCount, notFoundInBspCount,
                earlyHandled, unknownBoRecords, successOnReject, successBillingFileRecords, reincarnationBillingFileRecords);

        return reportRecords;
    }

}
