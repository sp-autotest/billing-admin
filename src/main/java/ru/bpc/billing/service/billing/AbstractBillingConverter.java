package ru.bpc.billing.service.billing;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.jsefa.Deserializer;
import org.jsefa.Serializer;
import org.jsefa.flr.FlrDeserializer;
import org.jsefa.flr.FlrIOFactory;
import org.jsefa.flr.FlrSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import ru.bpc.billing.domain.*;
import ru.bpc.billing.domain.billing.BillingFile;
import ru.bpc.billing.domain.posting.PostingFile;
import ru.bpc.billing.domain.posting.PostingFileFormat;
import ru.bpc.billing.domain.posting.PostingRecord;
import ru.bpc.billing.domain.posting.PostingRecordType;
import ru.bpc.billing.domain.posting.sv.SvPostingHeader;
import ru.bpc.billing.domain.posting.sv.SvPostingRecord;
import ru.bpc.billing.domain.posting.sv.SvPostingTrailer;
import ru.bpc.billing.repository.*;
import ru.bpc.billing.service.*;
import ru.bpc.billing.util.BillingFileUtils;
import ru.bpc.billing.util.ClassUtils;

import javax.annotation.Resource;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: Krainov
 * Date: 11.08.14
 * Time: 16:00
 */
public abstract class AbstractBillingConverter implements BillingConverter {

    private static final Logger logger = LoggerFactory.getLogger(AbstractBillingConverter.class);
    protected static final String POSTING_DATE_PATTERN = "yyyyMMdd";
    protected static final String POSTING_TIME_PATTERN = "HHmmss";
    protected static final String PASSENGER_NAME_DEFAULT = "IVANOV/IVAN MR";
    protected static final String REM = "### ";
    protected static final Locale EN = new Locale("en"); // for logging
    private SimpleDateFormat datePostfixFormat = new SimpleDateFormat(FILENAME_DATE_TIME_PATTERN);

    @Resource
    private SystemSettingsService systemSettingsService;
    @Resource
    private ApplicationService applicationService;
    private static final String FILENAME_DATE_TIME_PATTERN = "yyyyMMddHHmmssSSS";
    protected static final String MESSAGE_PREFIX = "billing.converter.";

    @Resource
    protected ProcessingFileRepository processingFileRepository;
    @Resource
    protected ProcessingRecordRepository processingRecordRepository;
    @Resource
    private ProcessingFileRecordRepository processingFileRecordRepository;
    @Resource
    private CountryCurrencyService countryCurrencyService;
    @Resource
    protected MessageSource messageSource;
    @Resource
    protected SequenceService sequenceService;
    @Resource
    protected CarrierRepository carrierRepository;
    @Resource
    private TerminalRepository terminalRepository;

    protected static final String COUNT_AVAILABLE_REJECT_RECORD = "billing.converter.count_available_reject_record";

    protected abstract boolean checkRecordType(Object currentRecord, Object lastRecord);
    protected abstract Date getProcessingDateByFirstRecordOfBillingFile(Object record);
    protected abstract Class[] getFormatClasses();
    protected abstract String getAgrn(Object currentRecord);

    @Override
    public BillingValidateResult validate(File file) throws IOException {
        BillingValidateResult billingValidateResult = new BillingValidateResult(file);
        FlrDeserializer deserializer = null;
        BufferedReader br = null;
        LineNumberReader lineNumberReader = null;
        Reader reader = null;
        Reader lineReader = null;

        try {
            reader = new FileReader(file);
            lineReader = new FileReader(file);
            Validate.notNull(reader);
            Validate.notNull(lineReader);

            deserializer = FlrIOFactory.createFactory(getFormatClasses()).createDeserializer();
            br = new BufferedReader(reader);
            deserializer.open(br);

            Object lastRecord = null;
            Date processingDate = null;

            lineNumberReader = new LineNumberReader(lineReader);
            lineNumberReader.skip(Long.MAX_VALUE);
            int countLines = lineNumberReader.getLineNumber();
            boolean isSuccess = true;
            Set<String> agrnCodes = new HashSet<>();
            while (deserializer.hasNext()) {
                Object record = deserializer.next();
                if ( null == processingDate ) {// first record - obtain format
                    processingDate = getProcessingDateByFirstRecordOfBillingFile(record);
                    if ( null == processingDate ) {
                        isSuccess = false;
                        break;
                    }
                } else {// non first record - check format
                    if ( !checkRecordType(record,lastRecord) ) {
                        logger.error("Line number in file: {}", deserializer.getInputPosition().getLineNumber());
                        isSuccess = false;
                        break;
                    }
                    String agrn = getAgrn(record);
                    if ( StringUtils.isNotBlank(agrn) ) agrnCodes.add(agrn);
                }
                lastRecord = record;
            }
            isSuccess = !isSuccess ? isSuccess : checkRecordType(null,lastRecord);//check last record, expect trailer of file
            billingValidateResult.setSuccess(isSuccess);
            billingValidateResult.setProcessingDate(processingDate);
            billingValidateResult.setCountLines(countLines);
            billingValidateResult.setAgrnCodes(agrnCodes);
            return billingValidateResult;
        } finally {
            try {
                if (null != reader) reader.close();
                if (null != lineReader) lineReader.close();
                if (br != null) br.close();
                if (null != lineNumberReader) lineNumberReader.close();
            } catch (IOException ioe) {
                logger.error("Error during closing reader", ioe);
            }
            safeCloseDeserializer(deserializer);
        }
    }

    protected boolean isBlankOrEqualsNull(String str) {
        return StringUtils.isBlank(str) || StringUtils.equalsIgnoreCase(str.trim(),"null");
    }
    protected boolean isCurrencyBelongToThisCountry(PostingRecord record) {
        if ( null == record ) return false;
        if (null == record.getTransactionCurrency() || null == record.getCountryCode() ) {
            logger.warn("Invalid currencyNumericCode = {} or countryCode = {} : [{}]",record.getTransactionCurrency(),record.getCountryCode(),record.getRbsId());
            return false;
        }
        CountryCurrency countryCurrency = countryCurrencyService.findByCountryAndCurrencyNumericCode(record.getCountryCode(), record.getTransactionCurrency());
        if ( null == countryCurrency ) logger.warn("Unable to find binding between countryCode: {} and currencyNumericCode: {}",record.getCountryCode(),record.getTransactionCurrency());
        return null != countryCurrency;
    }

    protected String getTerminalIdByArgn(String agrn) {
        if (StringUtils.isBlank(agrn)) return null;
        Terminal terminal = terminalRepository.findByAgrn(agrn);
        if ( null == terminal ) return null;
        return terminal.getTerminal();
    }

    protected PaymentSystem getPaymentSystem(String pan) {
        return CardService.getPaymentSystem(pan);
    }

    protected Path getBillingPath(BillingFile billingFile) {
        return Paths.get(applicationService.getHomeDir(FileType.BILLING) + billingFile.getName());
    }
    protected Path getPostingPath(BillingFile billingFile, Date convertDate) {
        String datePostfix = datePostfixFormat.format(convertDate);
        return Paths.get(applicationService.getHomeDir(FileType.POSTING) + "posting" + billingFile.getFormat().name() + datePostfix);
    }
    protected Path getPostingRejectPath(BillingFile billingFile, Date convertDate) {
        String datePostfix = datePostfixFormat.format(convertDate);
        return Paths.get(applicationService.getHomeDir(FileType.POSTING) + "posting" + billingFile.getFormat().name() + datePostfix + "_rejects");
    }
    protected Path getLogPath(BillingFile billingFile, Date convertDate) {
        String datePostfix = datePostfixFormat.format(convertDate);
        return Paths.get(applicationService.getHomeDir(FileType.BILLING_LOG) + "logForPosting" + billingFile.getFormat().name() + datePostfix);
    };

    protected Path getPostingPath(BillingFile[] billingFiles, Date convertDate) {
        String datePostfix = datePostfixFormat.format(convertDate);
        return Paths.get(applicationService.getHomeDir(FileType.POSTING) + "posting" + billingFiles[0].getFormat().name()  + datePostfix);
    }
    protected Path getPostingRejectPath(BillingFile[] billingFiles, Date convertDate) {
        String datePostfix = datePostfixFormat.format(convertDate);
        return Paths.get(applicationService.getHomeDir(FileType.POSTING) + "posting" + billingFiles[0].getFormat().name() + datePostfix + "_rejects");
    }
    protected Path getLogPath(BillingFile[] billingFiles, Date convertDate) {
        String datePostfix = datePostfixFormat.format(convertDate);
        return Paths.get(applicationService.getHomeDir(FileType.BILLING_LOG) + "logForPosting" + billingFiles[0].getFormat().name() + datePostfix);
    };



    protected Integer getCountAvailableRejectRecords() {
        return systemSettingsService.getInteger(COUNT_AVAILABLE_REJECT_RECORD,3);
    }

    protected String getMessage(String code) {
        return messageSource.getMessage(MESSAGE_PREFIX + code,null,Locale.getDefault());
    }

    protected String getMessage(String code, String[] args) {
        return messageSource.getMessage(MESSAGE_PREFIX + code,args,Locale.getDefault());
    }

    protected String getMessage(String code, Locale locale) {
        return messageSource.getMessage(MESSAGE_PREFIX + code,null,locale);
    }

    protected String getMessage(String code, Locale locale, String[] args) {
        return messageSource.getMessage(MESSAGE_PREFIX + code,args,locale);
    }


    protected String getCurrentLine(FlrDeserializer deserializer) {
        return BillingFileUtils.getCurrentLine(deserializer);
    }

    public static void safeCloseSerializer(Serializer serializer) {
        if (serializer != null) {
            try {
                serializer.close(true);
            } catch (Exception e) {
            }
        }
    }

    public static void safeCloseDeserializer(Deserializer deserializer) {
        if (deserializer != null) {
            try {
                deserializer.close(true);
            } catch (Exception e) {
            }
        }
    }

    protected void addExistRecordsToFileAndDatabase(FlrSerializer recordSerializer, FlrSerializer rejectRecordSerializer,
                                                    PostingFile postingFile, PostingFile postingRejectFile, BillingConverterResult billingConverterResult) {
        Integer countAvailableRejectRecords = getCountAvailableRejectRecords();
        if ( null == countAvailableRejectRecords ) {
            logger.warn("System variable {} isn't set ",COUNT_AVAILABLE_REJECT_RECORD);
            return;
        }
        Set<PostingRecord> alreadyHandledRecords = billingConverterResult.getAllAlreadyHandledRecords();
        //добавляем в постинг файл ранее обработанные записи
        if ( alreadyHandledRecords.size() <= countAvailableRejectRecords ) {//n <= COUNT_AVAILABLE_REJECT_RECORD
            writeExistRecordToFile(recordSerializer,postingFile,alreadyHandledRecords);
        }
        else {//n > COUNT_AVAILABLE_REJECT_RECORD
            writeExistRecordToFile(rejectRecordSerializer,postingRejectFile,alreadyHandledRecords);
        }
        //ранее обработанные записи мы сохраняем в БД
        try {
            for (PostingRecord postingRecord : alreadyHandledRecords) {
                ProcessingRecordBuilderResult processingRecordBuilderResult = addRecordAnyWay(postingRecord);
                if ( processingRecordBuilderResult.getStatus().equals(ProcessingRecordBuilderResult.ProcessingRecordStatus.NEW) ) {
                    logger.debug("Posting record with rbsId = {}  and parameters which have in database, was added to database",postingRecord.getRbsId());
                }
                else {
                    logger.warn("Posting record with rbsId = {} and parameters which have in database, wasn't added to database because already exists in postingFile: {}",
                            postingRecord.getRbsId(),
                            postingRecord.getPostingFile()
                    );
                    billingConverterResult.getErrorAddedToDatabaseRecords().add(postingRecord);
                }
            }
        } catch (Exception e) {
            logger.error("Inner error during add exist record to database",e);
            billingConverterResult.setInnerErrorMessage(e.getMessage());
        }

    }

    private void writeExistRecordToFile(FlrSerializer serializer, PostingFile postingFile, Collection<PostingRecord> postingRecords) {
        for (PostingRecord postingRecord : postingRecords) {
            serializer.write(postingRecord);
            serializer.flush();
            postingFile.addPostingRecord(postingRecord);
        }
    }

    protected ProcessingRecordBuilderResult addRecordAnyWay(PostingRecord postingRecord) {
        ProcessingRecordBuilderResult result = new ProcessingRecordBuilderResult();
        try {
            ProcessingRecord processingRecord = processingRecordRepository.findByRbsId(postingRecord.getRbsId());
            if ( null != processingRecord) {
                result.setStatus(ProcessingRecordBuilderResult.ProcessingRecordStatus.EXIST);
                result.setProcessingRecord(processingRecord);
                return result;
            }
            processingRecord = createBillingFileRecordFromPostingData(postingRecord);
            result.setStatus(ProcessingRecordBuilderResult.ProcessingRecordStatus.NEW);
            result.setProcessingRecord(processingRecord);
            return result;
        } catch (Exception e) {
            logger.error("Error add billingFileRecord",e);
            result.setStatus(ProcessingRecordBuilderResult.ProcessingRecordStatus.ERROR);
            result.setMessage(e.getMessage());
        }
        return null;
    }

    protected void processingRecordToFiles(ProcessingRecord processingRecord, ProcessingFile... processingFiles) {
        if ( null != processingRecord && null != processingFiles ) {
            for (ProcessingFile processingFile : processingFiles) {
                processingFileRecordRepository.save(new ProcessingFileRecord(new ProcessingFileRecordPk(processingFile, processingRecord)));
            }
        }
    }

    protected ProcessingRecordBuilderResult addRecordIfNotExists(PostingRecord postingRecord, ProcessingFile...processingFiles) {
        ProcessingRecordBuilderResult result = new ProcessingRecordBuilderResult();
        result.setPostingRecord(postingRecord);
        try {
            ProcessingRecordFilter filter = new ProcessingRecordFilter(postingRecord.getDocumentDate(), postingRecord.getDocumentNumber(), postingRecord.getTransactionType());
            ProcessingRecord processingRecord = processingRecordRepository.findOne(filter);
            if ( null == processingRecord ) {
                result.setStatus(ProcessingRecordBuilderResult.ProcessingRecordStatus.NEW);
                processingRecord = createBillingFileRecordFromPostingData(postingRecord);
                Carrier carrier = carrierRepository.findByIataCode(postingRecord.getIataCode());
                processingRecord.setCarrier(carrier);
                result.setProcessingRecord(processingRecord);
                processingRecordRepository.save(processingRecord);
                processingRecordToFiles(processingRecord, processingFiles);
            }
            else {
                result.setStatus(ProcessingRecordBuilderResult.ProcessingRecordStatus.EXIST);
                result.setProcessingRecord(processingRecord);
            }
        }
        catch (Exception e) {
            logger.error("Error add processingRecord",e);
            result.setStatus(ProcessingRecordBuilderResult.ProcessingRecordStatus.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Deprecated
    protected ProcessingRecordBuilderResult addRecordIfNotExists(PostingRecord postingRecord, BillingFile billingFile, boolean saveRecordToDatabase) {
        ProcessingRecordBuilderResult result = new ProcessingRecordBuilderResult();
        try {
            ProcessingRecordFilter filter = new ProcessingRecordFilter(postingRecord.getDocumentDate(), postingRecord.getDocumentNumber(), postingRecord.getTransactionType());
            ProcessingRecord processingRecord = processingRecordRepository.findOne(filter);
            if ( null == processingRecord) {// record not exists
                result.setStatus(ProcessingRecordBuilderResult.ProcessingRecordStatus.NEW);
                processingRecord = createBillingFileRecordFromPostingData(postingRecord);
                processingRecord.setCarrier(billingFile.getCarrier());
                result.setProcessingRecord(processingRecord);
                if ( saveRecordToDatabase ) {
                    processingRecordRepository.save(processingRecord);
                    processingRecordToFiles(processingRecord, billingFile, postingRecord.getPostingFile());
                }
            }
            else {
                result.setStatus(ProcessingRecordBuilderResult.ProcessingRecordStatus.EXIST);
                result.setProcessingRecord(processingRecord);
            }
        } catch (Exception e) {
            logger.error("Error add processingRecord",e);
            result.setStatus(ProcessingRecordBuilderResult.ProcessingRecordStatus.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    protected ProcessingRecord createBillingFileRecordFromPostingData(PostingRecord postingRecord) {
        ProcessingRecord processingRecord = new ProcessingRecord();
        processingRecord.setDocumentDate(postingRecord.getDocumentDate());
        processingRecord.setDocumentNumber(postingRecord.getDocumentNumber());
        processingRecord.setTransactionType(postingRecord.getTransactionType());
        processingRecord.setCreatedAt(new Date());
        processingRecord.setInvoiceNumber(postingRecord.getInvoiceNumber());
        processingRecord.setInvoiceDate(postingRecord.getInvoiceDate());
        processingRecord.setAmount(Integer.parseInt(postingRecord.getTransactionAmount()));
        processingRecord.setCurrency(postingRecord.getTransactionCurrency());
        processingRecord.setCountryCode(postingRecord.getCountryCode());
        processingRecord.setRbsId(postingRecord.getRbsId());
        processingRecord.setStatus(postingRecord.getType().equals(PostingRecordType.SUCCESS) ? ProcessingStatus.SUCCESS : ProcessingStatus.REJECT_BILLING);
        processingRecord.setErrorMessage(postingRecord.getErrorMessage());
        processingRecord.setExpiry(postingRecord.getExpirationDate());
        processingRecord.setPan(postingRecord.getPan());
        processingRecord.setApprovalCode(postingRecord.getApprovalCode());
        processingRecord.setRefNum(postingRecord.getRefNum());
        processingRecord.setUtrnno(postingRecord.getUtrnno());

        return processingRecord;
    }

    protected void addConverterResultsToLogFile(Writer logWriter, List<BillingConverterResult> billingConverterResults) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("------------------------------------------------------------------------").append("\r\n");
        for (BillingConverterResult billingConverterResult : billingConverterResults) {
            sb.append(getMessage("billing_file_name")).append(billingConverterResult.getBillingFile().getName());
            if ( !billingConverterResult.getAlreadyHandledRecords().isEmpty() ) {
                for (String postingFileName : billingConverterResult.getAlreadyHandledRecords().keySet()) {
                    Collection<PostingRecord> postingRecords = billingConverterResult.getAlreadyHandledRecords().get(postingFileName);
                    sb.append(getMessage("record_already_handled_in_another_posting",new String[]{postingFileName})).append(":").append(postingRecords.size()).append("\r\n");
                }
            }
            else {
                sb.append(getMessage("record_already_handled_in_another_posting",new String[]{" "})).append(":").append(0).append("\r\n");
            }
            BillingFile billingFile = billingConverterResult.getBillingFile();
            sb.append(getMessage("non_financial_transaction")).append(":").append(billingFile.notFinancialOperationCount).append("\r\n");
            sb.append(getMessage("deposit_in_billing")).append(":").append(billingFile.depositCount).append("\r\n");
            sb.append(getMessage("refund_in_billing")).append(":").append(billingFile.refundCount).append("\r\n");
            sb.append(getMessage("reverse_in_billing")).append(":").append(billingFile.reverseCount).append("\r\n");
            sb.append(getMessage("all_records_in_billing_without_non_financial")).append(":").append(billingFile.allRecordWithoutNotFinancialOperationCount).append("\r\n");
            for (ProcessingFile processingFile : billingConverterResult.getProcessingFiles()) {
                if ( processingFile instanceof PostingFile ) {
                    PostingFile postingFile = (PostingFile)processingFile;
                    sb.append(getMessage("posting_filename")).append(":").append(postingFile.getName()).append("\r\n");
                    sb.append(getMessage("deposit_in_posting")).append(":").append(postingFile.depositCount).append("\r\n");
                    sb.append(getMessage("refund_in_posting")).append(":").append(postingFile.refundCount).append("\r\n");
                    sb.append(getMessage("all_records_in_posting")).append(":").append(postingFile.getPostingRecords().size()).append("\r\n");
                }
            }
            logWriter.write(sb.toString());
            logWriter.flush();
        }
    }

    protected void addConverterResultToLogFile(Writer logWriter, BillingConverterResult billingConverterResult) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("------------------------------------------------------------------------").append("\r\n");
        if ( !billingConverterResult.getAlreadyHandledRecords().isEmpty() ) {
            for (String postingFileName : billingConverterResult.getAlreadyHandledRecords().keySet()) {
                Collection<PostingRecord> postingRecords = billingConverterResult.getAlreadyHandledRecords().get(postingFileName);
                sb.append(getMessage("record_already_handled_in_another_posting",new String[]{postingFileName})).append(":").append(postingRecords.size()).append("\r\n");
            }
        }
        else {
            sb.append(getMessage("record_already_handled_in_another_posting",new String[]{" "})).append(":").append(0).append("\r\n");
        }
        Map<String, ConsolidateParam> consolidateParams = new HashMap<>();
        for (BillingConverterResult result : billingConverterResult.getBillingConverterResults()) {
            BillingFile billingFile = result.getBillingFile();
            sb.append(getMessage("billing_file_name")).append(billingFile.getName()).append("\r\n");
            sb.append(getMessage("non_financial_transaction")).append(":").append(billingFile.notFinancialOperationCount).append("\r\n");
            sb.append(getMessage("deposit_in_billing")).append(":").append(billingFile.depositCount).append("\r\n");
            sb.append(getMessage("refund_in_billing")).append(":").append(billingFile.refundCount).append("\r\n");
            sb.append(getMessage("reverse_in_billing")).append(":").append(billingFile.reverseCount).append("\r\n");
            sb.append(getMessage("all_records_in_billing_without_non_financial")).append(":").append(billingFile.allRecordWithoutNotFinancialOperationCount).append("\r\n");
        }
        for (ProcessingFile processingFile : billingConverterResult.getProcessingFiles()) {
            if ( processingFile instanceof PostingFile ) {
                PostingFile postingFile = (PostingFile)processingFile;

                ConsolidateParam consolidateParam = consolidateParams.getOrDefault(postingFile.getName(), new ConsolidateParam());
                consolidateParam.consolidateDepositCount += postingFile.depositCount;
                consolidateParam.consolidateRefundCount += postingFile.refundCount;
                consolidateParam.consolidatePostingRecords += postingFile.getPostingRecords().size();
                consolidateParams.put(postingFile.getName(), consolidateParam);
            }
        }
        sb.append("\r\n");
        for (Map.Entry<String, ConsolidateParam> entry : consolidateParams.entrySet()) {
            sb.append(getMessage("posting_filename")).append(":").append(entry.getKey()).append("\r\n");
            sb.append(getMessage("deposit_in_posting")).append(":").append(entry.getValue().consolidateDepositCount).append("\r\n");
            sb.append(getMessage("refund_in_posting")).append(":").append(entry.getValue().consolidateRefundCount).append("\r\n");
            sb.append(getMessage("all_records_in_posting")).append(":").append(entry.getValue().consolidatePostingRecords).append("\r\n");
        }

        logWriter.write(sb.toString());
        logWriter.flush();
    }

    private static class ConsolidateParam {
        int consolidateDepositCount = 0;
        int consolidateRefundCount = 0;
        int consolidatePostingRecords = 0;
    }

    protected String getShortClassName(Class clazz) {
        return ClassUtils.getShortClassName(clazz);
    }

    protected String getShortClassName(Object object) {
        if (object == null) return null;
        return getShortClassName(object.getClass());
    }

    protected Object buildPostingHeader(Date now) {
        SvPostingHeader header = new SvPostingHeader();

        header.setFileCreationDate(new SimpleDateFormat(POSTING_DATE_PATTERN).format(now));
        header.setFileCreationTime(new SimpleDateFormat(POSTING_TIME_PATTERN).format(now));
        header.setSettlementDate(new SimpleDateFormat(POSTING_DATE_PATTERN).format(now));
        header.setSettlementTime(new SimpleDateFormat("HHmm").format(now) + "00");

        return header;
    }

    protected Object buildPostingTrailer(int recordCount) {
        SvPostingTrailer trailer = new SvPostingTrailer();
        trailer.setNumberOfRecordsInBody(Integer.toString(recordCount));
        return trailer;
    }

    protected abstract BillingConverterResult convertBillingFile(BillingFile billingFile) throws IOException;

    @Override
    public BillingConverterResult convert(BillingFile... billingFiles) throws IOException {
        BillingConverterResult billingConverterResult = new BillingConverterResult();
        for (BillingFile billingFile : billingFiles) {
            billingConverterResult.getBillingConverterResults().add(convertBillingFile(billingFile));
        }

        Date convertDate = new Date();
        Path postingPath = getPostingPath(billingFiles, convertDate);
        Path postingRejectPath = getPostingRejectPath(billingFiles, convertDate);
        Path logPath = getLogPath(billingFiles,convertDate);
        logger.debug("Posting path: {}",postingPath);
        logger.debug("Reject posting path: {}", postingRejectPath);
        logger.debug("Log path: {}",logPath);
        Writer writerPostingFile = new FileWriter(postingPath.toFile());
        Writer writerPostingRejectFile = new FileWriter(postingRejectPath.toFile());
        Writer writerLogFile = new FileWriter(logPath.toFile());

        // Because posting record hasn't a prefix and we can't write it to the header/trailer writer
        FlrSerializer headerSerializer = FlrIOFactory.createFactory(SvPostingHeader.class).createSerializer();
        FlrSerializer recordSerializer = FlrIOFactory.createFactory(SvPostingRecord.class).createSerializer();
        FlrSerializer trailerSerializer = FlrIOFactory.createFactory(SvPostingTrailer.class).createSerializer();

        FlrSerializer rejectHeaderSerializer = FlrIOFactory.createFactory(SvPostingHeader.class).createSerializer();
        FlrSerializer rejectRecordSerializer = FlrIOFactory.createFactory(SvPostingRecord.class).createSerializer();
        FlrSerializer rejectTrailerSerializer = FlrIOFactory.createFactory(SvPostingTrailer.class).createSerializer();

        headerSerializer.open(new BufferedWriter(writerPostingFile));
        rejectHeaderSerializer.open(new BufferedWriter(writerPostingRejectFile));

        Object postingHeader = buildPostingHeader(convertDate);
        headerSerializer.write(postingHeader);
        headerSerializer.flush();
        rejectHeaderSerializer.write(postingHeader);
        rejectHeaderSerializer.flush();

        recordSerializer.open(new BufferedWriter(writerPostingFile));
        rejectRecordSerializer.open(new BufferedWriter(writerPostingRejectFile));

        AtomicInteger successPostingRecords = new AtomicInteger();
        AtomicInteger rejectPostingRecords = new AtomicInteger();
        billingConverterResult.getBillingConverterResults().stream().forEach(result -> {
            BillingFile billingFile = result.getBillingFile();
            billingConverterResult.getLogStrings().addAll(result.getLogStrings());

            ProcessingFile billingFileLog = new ProcessingFile(FileType.BILLING_LOG);
            billingFileLog.setParentFile(billingFile);
            billingFileLog.setCreatedDate(new Date());
            billingFileLog.setName(logPath.toFile().getName());

            PostingFile successPostingFile = new PostingFile(billingFile,convertDate,postingPath.getFileName().toString(), PostingFileFormat.SUCCESS);
            PostingFile rejectPostingFile = new PostingFile(billingFile,convertDate,postingRejectPath.getFileName().toString(),PostingFileFormat.REJECT);

            billingConverterResult.getProcessingFiles().add(successPostingFile);
            billingConverterResult.getProcessingFiles().add(rejectPostingFile);
            billingConverterResult.getProcessingFiles().add(billingFileLog);
            processingFileRepository.save(successPostingFile);
            processingFileRepository.save(rejectPostingFile);
            processingFileRepository.save(billingFileLog);

            result.getProcessingFiles().stream()
                    .filter(f -> f instanceof PostingFile)
                    .flatMap(p -> ((PostingFile) p).getPostingRecords().stream())
                    .sorted(Comparator.comparing(p -> p.getTransactionType(), (o1, o2) -> o2.equals(TransactionType.CR_REVERSE) ? -1 : 1))//reverse operation goes to down of result
                    .forEach(postingRecord -> {
                        billingConverterResult.recordCount++;
                        if ( postingRecord.getTransactionType().equals(TransactionType.CR_REVERSE) ) billingConverterResult.reverseCount++;
                        if ( "200000".equals(postingRecord.getProcessingCode()) && "1".equals(postingRecord.getReversalFlag()) ) {
                            billingConverterResult.refundCount++;
                        }
                        //set utrnno
                        if (postingRecord.getTransactionType().equals(TransactionType.CR_REVERSE) && null == postingRecord.getUtrnno()) {
                            List<ProcessingRecord> originalProcessingRecords = processingRecordRepository.findOriginalProcessingRecordBy(postingRecord.getDocumentNumber(), Integer.parseInt(postingRecord.getTransactionAmount()), postingRecord.getTransactionCurrency());
                            if ( null == originalProcessingRecords || originalProcessingRecords.isEmpty() ) {
                                postingRecord.setType(PostingRecordType.REJECT);
                                billingConverterResult.addLog(getMessage("originalNotFound"));
                            }
                            else if ( 1 < originalProcessingRecords.size() ) {
                                postingRecord.setType(PostingRecordType.REJECT);
                                billingConverterResult.addLog(getMessage("originalNotFound"));
                                logger.error("Error to find original record by {}, found {} records",
                                        postingRecord.getDocumentNumber(), Integer.parseInt(postingRecord.getTransactionAmount()), postingRecord.getTransactionCurrency(),originalProcessingRecords.size());
                            }
                            else {
                                ProcessingRecord originalProcessingRecord = originalProcessingRecords.iterator().next();
                                if (null == originalProcessingRecord) {
                                    postingRecord.setType(PostingRecordType.REJECT);
                                    billingConverterResult.addLog(getMessage("originalNotFound"));
                                } else if (!originalProcessingRecord.getTransactionType().equals(TransactionType.DR)) {
                                    postingRecord.setType(PostingRecordType.REJECT);
                                    billingConverterResult.addLog(getMessage("originalNotDR"));
                                } else {
                                    String utrnno = originalProcessingRecord.getUtrnno();
                                    if (StringUtils.isBlank(utrnno)) {
                                        postingRecord.setType(PostingRecordType.REJECT);
                                        billingConverterResult.addLog(getMessage("utrnnoNotFound"));
                                    } else {
                                        postingRecord.setUtrnno(utrnno);
                                    }
                                }
                            }
                        }

                        PostingFile postingFile = postingRecord.getType().equals(PostingRecordType.SUCCESS) ? successPostingFile : rejectPostingFile;
                        postingRecord.setPostingFile(postingFile);
                        ProcessingRecordBuilderResult processingRecordBuilderResult = addRecordIfNotExists(postingRecord, billingFile, postingFile);

                        if (postingRecord.getType().equals(PostingRecordType.SUCCESS)) {
                            successPostingFile.addPostingRecord(postingRecord);
                            successPostingRecords.incrementAndGet();
                            if (processingRecordBuilderResult.getStatus().equals(ProcessingRecordBuilderResult.ProcessingRecordStatus.NEW)) {//new record
                                recordSerializer.write(postingRecord);
                                recordSerializer.flush();
                            } else {
                                billingConverterResult.getAlreadyHandledRecords().put(postingRecord.getPostingFile().getName(), postingRecord);
                                billingConverterResult.addLog(getMessage("record.exists", new String[]{postingRecord.getPostingFile().getName()}));
                            }
                        } else {
                            rejectPostingRecords.incrementAndGet();
                            rejectPostingFile.addPostingRecord(postingRecord);
                            rejectRecordSerializer.write(postingRecord);
                            rejectRecordSerializer.flush();
                        }

                    });

            recordSerializer.flush();
            recordSerializer.close(false);

            //add exist records to file and database
            addExistRecordsToFileAndDatabase(recordSerializer,rejectRecordSerializer,successPostingFile,rejectPostingFile,billingConverterResult);

            rejectRecordSerializer.flush();
            rejectRecordSerializer.close(false);
        });

        trailerSerializer.open(new BufferedWriter(writerPostingFile));
        trailerSerializer.write(buildPostingTrailer(successPostingRecords.get()));
        trailerSerializer.flush();
        trailerSerializer.close(false);

        rejectTrailerSerializer.open(new BufferedWriter(writerPostingRejectFile));
        rejectTrailerSerializer.write(buildPostingTrailer(rejectPostingRecords.get()));
        rejectTrailerSerializer.flush();
        rejectTrailerSerializer.close(false);

        //logs
        if ( billingConverterResult.getLogStrings().isEmpty() ) {
            billingConverterResult.addLog(getMessage("log.empty"));
        }
        for (String logString : billingConverterResult.getLogStrings()) {
            logger.debug(logString);
            writerLogFile.write(logString);
        }
        addConverterResultToLogFile(writerLogFile,billingConverterResult);//add converter result to end of logFile
        writerLogFile.flush();
        writerLogFile.close();

        //close serializers
        safeCloseSerializer(headerSerializer);
        safeCloseSerializer(recordSerializer);
        safeCloseSerializer(trailerSerializer);
        safeCloseSerializer(rejectHeaderSerializer);
        safeCloseSerializer(rejectRecordSerializer);
        safeCloseSerializer(rejectTrailerSerializer);

        logger.debug("End billing files converting");

        return billingConverterResult.success();
    }
}
