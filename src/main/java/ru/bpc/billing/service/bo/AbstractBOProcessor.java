package ru.bpc.billing.service.bo;

import org.apache.commons.lang.StringUtils;
import org.jsefa.Deserializer;
import org.jsefa.flr.FlrIOFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import ru.bpc.billing.domain.FileType;
import ru.bpc.billing.domain.ProcessingFile;
import ru.bpc.billing.domain.ProcessingRecord;
import ru.bpc.billing.domain.ProcessingStatus;
import ru.bpc.billing.domain.bo.BOFile;
import ru.bpc.billing.domain.bo.BOFileFormat;
import ru.bpc.billing.domain.bo.BORecord;
import ru.bpc.billing.repository.ProcessingRecordRepository;
import ru.bpc.billing.service.ApplicationService;

import javax.annotation.Resource;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static ru.bpc.billing.service.bo.BOProcessException.ProcessAction.*;

/**
 * User: Krainov
 * Date: 15.08.14
 * Time: 11:50
 */
public abstract class AbstractBOProcessor implements BOProcessor {

    private final static Logger logger = LoggerFactory.getLogger(AbstractBOProcessor.class);

    private static final String REM = "###";
    private static final String FILENAME_DATE_TIME_PATTERN = "yyyyMMddHHmmssSSS";
    private static final String MESSAGE_PREFIX = "bo.processor.";
    private static final String MESSAGE_PREFIX_RESULT = "bo.processor.result.";
    private boolean logEmpty = true;
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat(FILENAME_DATE_TIME_PATTERN);

    @Resource
    private ProcessingRecordRepository processingRecordRepository;
    @Resource
    protected ApplicationService applicationService;
    @Resource
    protected MessageSource messageSource;

    protected abstract Class[] getBOClasses();

    protected Path getBOLogPath(File file, Date createdDate) {
        String logFilename = "logForReport" + dateTimeFormat.format(createdDate);
        return Paths.get(applicationService.getHomeDir(FileType.BO_LOG) + logFilename);
    }

    protected Path getBOFilePath(File file, Date createdDate) {
        return Paths.get(file.toURI());
    }

    protected BOFileFormat getBoFileFormat(File file) {
        return file.getName().toLowerCase().contains("reject") ? BOFileFormat.REJECT : BOFileFormat.SUCCESS;
    }

    @Override
    public BOProcessingResult process(File file) throws IOException {
        BOProcessingResult processingResult = new BOProcessingResult();
        Date createdDate = new Date();
        Path boFilePath = getBOFilePath(file,createdDate);
        Path boLogPath = getBOLogPath(file,createdDate);

        BOFile boFile = new BOFile(FileType.BO);
        boFile.setOriginalFile(file);
        boFile.setOriginalFileName(file.getName());
        boFile.setBusinessDate(createdDate);
        boFile.setName(boFilePath.getFileName().toString());
        boFile.setFormat(getBoFileFormat(file));
        processingResult.getProcessingFiles().add(boFile);

        ProcessingFile boLogFile = new ProcessingFile(FileType.BO_LOG);
        boLogFile.setName(boLogPath.getFileName().toString());
        boLogFile.setOriginalFileName(boLogPath.getFileName().toString());
        boLogFile.setParentFile(boFile);
        processingResult.getProcessingFiles().add(boLogFile);

        Reader reader = new FileReader((boFile.getOriginalFile()));
        Writer logWriter = new FileWriter(boLogPath.toFile());

        int totalRecords = 0;
        int successRecords = 0;
        int errorRecords = 0;
        int fraudRecords = 0;
        int depositRecords = 0;
        int refundRecords = 0;

        try {
            if (reader == null || logWriter == null) {
                logger.error("No all parameters specified: reader [{}], logWriter [{}]", reader, logWriter);
                return processingResult.set(file, false, 0, 0, 0, 0);
            }
            BufferedReader br = new BufferedReader(reader);
            String line;
            Map<String,BORecord> processedObjects = new HashMap<>();
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(REM)) {
                    totalRecords++;
                    try {
                        BORecord boRecord = processLine(line);
                        boRecord.setProcessingFile(boFile);
                        if ( boRecord.isDebit() ) depositRecords++;
                        if ( boRecord.isCredit() ) refundRecords++;
                        //добавили запись в коллекцию. Если во время следующей обработки произошла ошибка над успешной записью, то статус записи будет изменен на реджект
                        processedObjects.put(boRecord.getRBS_ORDER(),boRecord);
                        processRecord(boRecord);
                        successRecords++;
                    } catch (BOProcessException pe) {
                        StringBuilder sb;
                        if (pe.getAction() == SKIP) {// Error record encountered, we'll continue from next record
                            errorRecords++;
                            sb = new StringBuilder(line);
                            sb.append("\n").append("### ").append(getMessage(pe.getErrorMessageCode())).append("\n");
                            logWriter.write(sb.toString());
                            logWriter.flush();
                            logEmpty = false;
                        } else if (pe.getAction() == ERROR) {// Fatal error occurred -abort processing
                            return processingResult.set(file, false, totalRecords, successRecords, errorRecords, fraudRecords, depositRecords, refundRecords);
                        } else if (pe.getAction() == FRAUD) { // Fraud record encountered, we'll countinue from next record
                            fraudRecords++;
                            sb = new StringBuilder(line);
                            sb.append("\n").append("### ").append(getMessage(pe.getErrorMessageCode())).append("\n");
                            logWriter.write(sb.toString());
                            logWriter.flush();
                            logEmpty = false;
                        } else {
                            logger.error("Unknown action [{}]", pe.getAction());
                            return processingResult.set(file, false, totalRecords, successRecords, errorRecords, fraudRecords, depositRecords, refundRecords);
                        }
                    }
                }
            }
            processingResult.set(file, true, totalRecords, successRecords, errorRecords, fraudRecords, depositRecords, refundRecords);
            processingResult.setProcessedObjects(processedObjects);

            StringBuilder sb = new StringBuilder();
            sb.append("------------------------------------------------------------------------").append("\r\n");
            sb.append(getMessageResult("count_records")).append(processingResult.getCountProcessObjects()).append("\r\n");
            sb.append(getMessageResult("count_success_records")).append(processingResult.getSuccessRecords()).append("\r\n");
            sb.append(getMessageResult("count_error_records")).append(processingResult.getErrorRecords()).append("\r\n");
            sb.append(getMessageResult("count_fraud_records")).append(processingResult.getFraudRecords()).append("\r\n");
            sb.append(getMessageResult("count_deposit_records")).append(processingResult.getDepositRecords()).append("\r\n");
            sb.append(getMessageResult("count_refund_records")).append(processingResult.getRefundRecords()).append("\r\n");

            logWriter.write(sb.toString());
            logWriter.flush();

            return processingResult;
        } catch (IOException ioe) {
            logger.error("Error during SVBO Revenue file processing", ioe);
            return processingResult.set(file, false, totalRecords, successRecords, errorRecords, fraudRecords, depositRecords, refundRecords);
        } finally {
            closeReader(reader);
            try {
                if (logEmpty) logWriter.write(getMessage("log.empty"));
                logWriter.flush();
            } catch (IOException ioe) {
                logger.error("Error during writer flush", ioe);
            }
            closeWriter(logWriter);
        }
    }

    protected BORecord processLine(String line) throws BOProcessException {
        if (StringUtils.isBlank(line)) {
            logger.error("Line is empty");
            throw new BOProcessException(SKIP, "line.empty");
        }
        Deserializer deserializer = null;
        StringReader reader = null;
        BORecord boRecord = null;
        try {
            reader = new StringReader(line);
            deserializer = FlrIOFactory.createFactory(getBOClasses()).createDeserializer();
            deserializer.open(reader);

            //тут всегда только одна запись
            if ( deserializer.hasNext() ) {
                try {
                    boRecord = (BORecord)deserializer.next();
                } catch (Exception e) {//ловим, если запись не валидная на стадии десириализации
                    logger.error("Error parse line: " + line,e);
                    throw new BOProcessException(SKIP,"line.wrong-format");
                }
            }
            if ( !boRecord.isValid() ) {
                logger.error("Line is not valid: {}", line);
                throw new BOProcessException(SKIP,"line.wrong-format");
            }
        } finally {
            if ( null != reader ) reader.close();
            if ( null != deserializer ) deserializer.close(true);
        }
        if ( null == boRecord ) {
            logger.error("Error parse line: " + line);
            throw new BOProcessException(SKIP,"line.wrong-format");
        }
        return boRecord;
    }

    protected void processRecord(BORecord boRecord) throws BOProcessException {
        if ( StringUtils.isBlank(boRecord.getRBS_ORDER()) ) {
            logger.error("[field 3 / RBS_ORDER] is empty");
            boRecord.setRejectStatus();
            throw new BOProcessException(SKIP, "rbsId.empty");
        }
        if ( !boRecord.isSuccess() ) processRejectRecord(boRecord);
        else processSuccessRecord(boRecord);
    }

    protected void processRejectRecord(BORecord boRecord) throws BOProcessException {
    }

    protected void processSuccessRecord(BORecord boRecord) throws BOProcessException {
        ProcessingRecord processingRecord = processingRecordRepository.findByRbsId(boRecord.getRBS_ORDER());
        //проверяем, есть ли вообще запись в таблице billing_record с таким rbs_id. Если её нет, то делать по неё отчёт бесмысленно.
        if ( null == processingRecord) {
            logger.error("Billing record " + boRecord.getRBS_ORDER() + " doesn't exist in database and we won't handle this record for next report");
            boRecord.setRejectStatus();
            throw new BOProcessException(SKIP,"billing_record.notExist");
        }
        boRecord.setProcessingRecord(processingRecord);
        //проверяем, если есть БСП запись с данным rbsId и если он находится в статусе REJECT_BILLING (то есть был реджект на стадии обработки БСП файла),
        //то выдаём ошибку, так как в БО файле не могли появится успешные записи на наши реджекты
        if ( null != processingRecord && null != processingRecord.getStatus() && processingRecord.getStatus().equals(ProcessingStatus.REJECT_BILLING) ) {
            logger.error("Billing record " + boRecord.getRBS_ORDER() + " has REJECT_BILLING status, but BO record has success status");
            boRecord.setRejectStatus();
            throw new BOProcessException(SKIP,"billing_record.reject_bsp");
        }

        checkBoRecord(boRecord);
    }

    protected void checkBoRecord(BORecord boRecord) throws BOProcessException {
        logger.trace("{}",boRecord);
        if ( null == boRecord.getOperationType() ) {
            logger.error("[field 1 / STTT_TYPE] is empty");
            boRecord.setRejectStatus();
            throw new BOProcessException(SKIP, "sttt_type.empty");
        }
        if ( StringUtils.isBlank(boRecord.getOPER_DATE()) ) {
            logger.error("[field 2 / OPER_DATE] is empty");
            boRecord.setRejectStatus();
            throw new BOProcessException(SKIP, "operDate.empty");
        }
        if ( StringUtils.isBlank(boRecord.getRBS_ORDER()) ) {
            logger.error("[field 3 / RBS_ORDER] is empty");
            boRecord.setRejectStatus();
            throw new BOProcessException(SKIP, "rbsId.empty");
        }
        if ( StringUtils.isBlank(boRecord.getAMOUNT_IN_CURRENCY_MPS()) ) {
            logger.error("[field 4 / AMOUNT_IN_CURRENCY_MPS] is empty");
            boRecord.setRejectStatus();
            throw new BOProcessException(SKIP, "amountInCurrencyMps.empty");
        }
        if ( StringUtils.isBlank(boRecord.getAMOUNT_IN_CURRENCY_CLIENT()) ) {
            logger.error("[field 5 / AMOUNT_IN_CURRENCY_CLIENT] is empty");
            boRecord.setRejectStatus();
            throw new BOProcessException(SKIP, "amountInCurrencyClient.empty");
        }
        if ( StringUtils.isBlank(boRecord.getAMOUNT_IN_RUB()) ) {
            logger.error("[field 6 / AMOUNT_IN_RUB] is empty");
            boRecord.setRejectStatus();
            throw new BOProcessException(SKIP, "amountInRub.empty");
        }
        if ( StringUtils.isBlank(boRecord.getCURRENCY_MPS()) ) {
            logger.error("[field 7 / CURRENCY_MPS] is empty");
            boRecord.setRejectStatus();
            throw new BOProcessException(SKIP, "currencyMps.empty");
        }
        if ( StringUtils.isBlank(boRecord.getCURRENCY_CLIENT()) ) {
            logger.error("[field 8 / CURRENCY_CLIENT] is empty");
            boRecord.setRejectStatus();
            throw new BOProcessException(SKIP, "currencyClient.empty");
        }
        if ( StringUtils.isBlank(boRecord.getCURRENCY()) ) {
            logger.error("[field 9 / CURRENCY] is empty");
            boRecord.setRejectStatus();
            throw new BOProcessException(SKIP, "currency.empty");
        }
        if ( StringUtils.isBlank(boRecord.getAUTH_DATE()) ) {
            logger.error("[field 10 / AUTH_DATE] is empty");
            boRecord.setRejectStatus();
            throw new BOProcessException(SKIP, "authDate.empty");
        }
        if ( !boRecord.isCredit() && StringUtils.isBlank(boRecord.getMSC()) ) {
            logger.error("[field 12 / MSC] is empty");
            boRecord.setRejectStatus();
            throw new BOProcessException(SKIP, "msc.empty");
        }
        if ( StringUtils.isBlank(boRecord.getOPER_SIGN()) ) {
            logger.error("[field 13 / OPER_SIGN] is empty");
            boRecord.setRejectStatus();
            throw new BOProcessException(SKIP, "operSign.empty");
        }
        if ( StringUtils.isBlank(boRecord.getBO_UTRNNO()) ) {
            logger.error("[field 15 / BO_UTRNNO] is empty");
            boRecord.setRejectStatus();
            throw new BOProcessException(SKIP, "boUtrnno.empty");
        }
        if ( StringUtils.isBlank(boRecord.getTRANS_TYPE()) ) {
            logger.error("[field 18 / TRANS_TYPE] is empty");
            boRecord.setRejectStatus();
            throw new BOProcessException(SKIP, "transType.empty");
        }
        if ( StringUtils.isBlank(boRecord.getTICKET_NUMBER()) ) {
            logger.error("[field 19 / TICKET_NUMBER] is empty");
            boRecord.setRejectStatus();
            throw new BOProcessException(SKIP, "ticketNumber.empty");
        }
        if ( StringUtils.isBlank(boRecord.getTYPE_FILE()) ) {
            logger.error("[field 20 / TYPE_FILE] is empty");
            boRecord.setRejectStatus();
            throw new BOProcessException(SKIP, "typeFile.empty");
        }
        if ( StringUtils.isBlank(boRecord.getRATE_MPS()) ) {
            logger.error("[field 21 / RATE_MPS] is empty");
            boRecord.setRejectStatus();
            throw new BOProcessException(SKIP, "rateMps.empty");
        }
        if ( StringUtils.isBlank(boRecord.getRATE_CB()) ) {
            logger.error("[field 22 / RATE_CB] is empty");
            boRecord.setRejectStatus();
            throw new BOProcessException(SKIP, "rateCB.empty");
        }
        if ( StringUtils.isBlank(boRecord.getSTATUS()) ) {
            logger.error("[field 24 / STATUS] is empty");
            boRecord.setRejectStatus();
            throw new BOProcessException(SKIP, "status.empty");
        }
    }

    private void closeReader(Reader reader) {
        try {
            if (reader != null) reader.close();
        } catch (IOException ioe) {
            logger.error("Error during closing Reader", ioe);
        }
    }

    private void closeWriter(Writer writer) {
        try {
            if (writer != null) writer.close();
        } catch (IOException ioe) {
            logger.error("Error during closing Writer", ioe);
        }
    }

    private String getMessage(String code) {
        return messageSource.getMessage(MESSAGE_PREFIX + code, new Object[]{}, Locale.getDefault());
    }

    private String getMessageResult(String code) {
        return messageSource.getMessage(MESSAGE_PREFIX_RESULT + code, new Object[]{}, Locale.getDefault());
    }
}
