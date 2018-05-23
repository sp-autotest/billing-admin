package ru.bpc.billing.service.billing.sv;

import org.jsefa.flr.FlrDeserializer;
import org.jsefa.flr.FlrIOFactory;
import org.jsefa.flr.FlrSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import ru.bpc.billing.domain.FileType;
import ru.bpc.billing.domain.ProcessingFile;
import ru.bpc.billing.domain.billing.BillingFile;
import ru.bpc.billing.domain.billing.arc.TAA;
import ru.bpc.billing.domain.billing.arc.TAB;
import ru.bpc.billing.domain.billing.arc.TBH;
import ru.bpc.billing.domain.billing.arc.TFH;
import ru.bpc.billing.domain.posting.*;
import ru.bpc.billing.domain.posting.sv.SvPostingHeader;
import ru.bpc.billing.domain.posting.sv.SvPostingRecord;
import ru.bpc.billing.domain.posting.sv.SvPostingTrailer;
import ru.bpc.billing.service.ISystem;
import ru.bpc.billing.service.billing.AbstractARCBillingConverter;
import ru.bpc.billing.service.billing.BillingConverterException;
import ru.bpc.billing.service.billing.BillingConverterResult;
import ru.bpc.billing.util.ClassUtils;

import java.io.*;
import java.nio.file.Path;
import java.util.Date;

import static ru.bpc.billing.domain.billing.BillingFileFormat.ARC;

/**
 * User: Krainov
 * Date: 15.04.2016
 * Time: 12:23
 */
public class SvNewArcBillingConverter extends AbstractARCBillingConverter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    protected BillingConverterResult convertBillingFile(BillingFile billingFile) throws IOException {
        BillingConverterResult result = new BillingConverterResult();
        result.setBillingFile(billingFile);
        logger.debug("Begin ARC file: {} converting", billingFile.getName());

        File billingOriginalFile = billingFile.getOriginalFile();
        Date convertDate = new Date();
        Path postingPath = getPostingPath(billingFile, convertDate);
        Path postingRejectPath = getPostingRejectPath(billingFile, convertDate);
        Path logPath = getLogPath(billingFile,convertDate);

        ProcessingFile billingFileLog = new ProcessingFile(FileType.BILLING_LOG);
        billingFileLog.setParentFile(result.getBillingFile());
        billingFileLog.setCreatedDate(new Date());
        billingFileLog.setName(logPath.toFile().getName());

        PostingFile postingFile = new PostingFile(billingFile,convertDate,postingPath.getFileName().toString(), PostingFileFormat.SUCCESS);
        PostingFile postingRejectFile = new PostingFile(billingFile,convertDate,postingRejectPath.getFileName().toString(),PostingFileFormat.REJECT);

        result.getProcessingFiles().add(postingFile);
        result.getProcessingFiles().add(postingRejectFile);
        result.getProcessingFiles().add(billingFileLog);

        Reader readerBillingFile = new FileReader(billingOriginalFile);
        Writer writerPostingFile = new FileWriter(postingPath.toFile());
        Writer writerPostingRejectFile = new FileWriter(postingRejectPath.toFile());

        FlrDeserializer deserializer = null;
        // Because posting record hasn't a prefix and we can't write it to the header/trailer writer
        FlrSerializer headerSerializer = null;
        FlrSerializer recordSerializer = null;
        FlrSerializer trailerSerializer = null;
        FlrSerializer rejectHeaderSerializer = null;
        FlrSerializer rejectRecordSerializer = null;
        FlrSerializer rejectTrailerSerializer = null;

        try {
            deserializer = FlrIOFactory.createFactory(ARC.getClasses()).createDeserializer();
            deserializer.open(readerBillingFile);

            headerSerializer = FlrIOFactory.createFactory(SvPostingHeader.class).createSerializer();
            recordSerializer = FlrIOFactory.createFactory(SvPostingRecord.class).createSerializer();
            trailerSerializer = FlrIOFactory.createFactory(SvPostingTrailer.class).createSerializer();

            rejectHeaderSerializer = FlrIOFactory.createFactory(SvPostingHeader.class).createSerializer();
            rejectRecordSerializer = FlrIOFactory.createFactory(SvPostingRecord.class).createSerializer();
            rejectTrailerSerializer = FlrIOFactory.createFactory(SvPostingTrailer.class).createSerializer();

            headerSerializer.open(new BufferedWriter(writerPostingFile));
            rejectHeaderSerializer.open(new BufferedWriter(writerPostingRejectFile));

            boolean errorInFileHeader = false;
            boolean fileHeaderWritten = false;
            String fileHeader = null;

            boolean errorInBatchHeader = false;
            boolean batchHeaderWritten = false;
            String batchHeader = null;

            String tabRecord = null;

            // Header
            Object record = deserializer.next();

            try {
                Object postingHeader = buildPostingHeader((TFH) record);
                headerSerializer.write(postingHeader);
                headerSerializer.flush();
                fileHeader = getCurrentLine(deserializer);// we will write it to log if error occurs

                rejectHeaderSerializer.write(postingHeader);
                rejectHeaderSerializer.flush();
            } catch (BillingConverterException ce) {
                String currentLine = getCurrentLine(deserializer);
                logger.error("Invalid [{}] record - [{}]: {}", ClassUtils.getShortClassName(record), getMessage(ce.getErrorMessageCode(), EN), currentLine);
                result.addLog(currentLine + "\n");
                result.addLog(REM + getMessage(ce.getErrorMessageCode()) + ", " + getMessage("file-header-invalid.notification") + "\n");
                errorInFileHeader = true;
            }
            headerSerializer.flush();
            headerSerializer.close(false);

            rejectHeaderSerializer.flush();
            rejectHeaderSerializer.close(false);

            recordSerializer.open(new BufferedWriter(writerPostingFile));
            rejectRecordSerializer.open(new BufferedWriter(writerPostingRejectFile));

            TBH tbh = null;
            TAB tab = null;
            Object previousRecord = null;
            while (deserializer.hasNext()) {
                record = deserializer.next();

                if (!errorInFileHeader) {

                    if (record instanceof TBH) {
                        tbh = (TBH) record;
                        batchHeader = getCurrentLine(deserializer);// will needed in case of error in this batch
                        batchHeaderWritten = false;
                        errorInBatchHeader = false;// reset if were errors on previous batches
                    } else if (record instanceof TAB) {
                        boolean isNotFinancialOperation = ((TAB)record).isNotFinancialOperation();
                        if ( isNotFinancialOperation ) {//write only to log
                            String line = getCurrentLine(deserializer);
                            logger.warn("Invalid [{}] record - [{}]: {}",
                                    getShortClassName(TAB.class),
                                    getMessage("TTID.nonFinancial"),
                                    line);
                            if (!fileHeaderWritten) {// first error in file - write file header to log
                                result.addLog(fileHeader + "\n");
                                fileHeaderWritten = true;
                            }
                            if (!batchHeaderWritten) {// first record in batch - write batch header
                                result.addLog(batchHeader + "\n");
                                batchHeaderWritten = true;
                            }
                            result.addLog(line + "\n");// TAB record
                            result.addLog(REM + (getMessage("TTID.nonFinancial")) + "\n");
                            billingFile.notFinancialOperationCount++;
                            continue;
                        }
                        tab = (TAB) record;
                        tabRecord = getCurrentLine(deserializer);// will be needed in case of error
                    } else if (record instanceof TAA) {
                        //если tab является isExchangeOperation и это уже вторая ТАА , то мы игнорируем эти записи  (AFBRBS-2730)
                        if ( tab.isExchangeOperation() && null != previousRecord && previousRecord instanceof TAA ) {
                            previousRecord = record;
                            continue;
                        };
                        if ( tab.isDebitOperation() ) billingFile.depositCount++;
                        else if ( tab.isRefundOperation() ) billingFile.refundCount++;
                        else if ( tab.isReversalOperation() ) billingFile.reverseCount++;
                        billingFile.allRecordWithoutNotFinancialOperationCount++;

                        PostingRecordBuilderResult postingRecordBuilderResult = null;
                        try {
                            if (!errorInBatchHeader) {
                                postingRecordBuilderResult = buildPostingRecord(tbh, tab, (TAA) record, billingFile.getCarrier());
                                PostingRecord postingRecord = postingRecordBuilderResult.getPostingRecord();
                                boolean isSuccess = postingRecord.getType().equals(PostingRecordType.SUCCESS);
                                if ( isSuccess ) {
                                    postingRecord.setPostingFile(postingFile);
                                    postingFile.addPostingRecord(postingRecord);
                                }
                                else {
                                    postingRecord.setPostingFile(postingRejectFile);
                                    postingRejectFile.addPostingRecord(postingRecord);
                                }
                                postingRecordBuilderResult.throwIfNotSuccess();
                            } else {// error in batch header - all records from batch to log
                                result.addLog(tabRecord + "\n");// TAB record
                                result.addLog(getCurrentLine(deserializer) + "\n");// TAA record
                            }
                        } catch (BillingConverterException ce) {
                            logger.error("Invalid [{}] record - [{}]: {}",
                                    getShortClassName(ce.getErrorRecordType()),
                                    ce.getRecordFilename() != null ? getMessage(ce.getErrorMessageCode(), EN, new String[]{ce.getRecordFilename()}) : getMessage(ce.getErrorMessageCode()),
                                    ce.getErrorRecordType() == TBH.class ? batchHeader : ce.getErrorRecordType() == TAB.class ? tabRecord : getCurrentLine(deserializer));
                            if (!fileHeaderWritten) {// first error in file - write file header to log
                                result.addLog(fileHeader + "\n");
                                fileHeaderWritten = true;
                            }

                            if (ce.getErrorRecordType() == TBH.class) {// error in batch header
                                result.addLog(batchHeader + "\n");// TBH record
                                result.addLog(REM + getMessage(ce.getErrorMessageCode()) + ", " + getMessage("batch-header-invalid.notification") + "\n");
                                result.addLog(tabRecord + "\n");// TAB record
                                result.addLog(getCurrentLine(deserializer) + "\n");// TAA record
                                errorInBatchHeader = true;// error in batch header - all records in batch will be written to log
                            } else {
                                if (!batchHeaderWritten) {// first record in batch - write batch header
                                    result.addLog(batchHeader + "\n");
                                    batchHeaderWritten = true;
                                }
                                result.addLog(tabRecord + "\n");// TAB record

                                if (ce.getErrorRecordType() == TAB.class) {// error in TAB record
                                    result.addLog(REM + (ce.getRecordFilename() != null ? getMessage(ce.getErrorMessageCode(), new String[]{ce.getRecordFilename()}) : getMessage(ce.getErrorMessageCode())) + "\n");
                                    result.addLog(getCurrentLine(deserializer) + "\n");// TAA record
                                } else {// error in TAA record
                                    result.addLog(getCurrentLine(deserializer) + "\n");// TAA record
                                    result.addLog(REM + getMessage(ce.getErrorMessageCode()) + "\n");
                                }
                            }
                        }
                        //write reject file if it need
                        if ( null != postingRecordBuilderResult && !postingRecordBuilderResult.isSuccess() ) {
                            rejectRecordSerializer.write(postingRecordBuilderResult.getPostingRecord());
                            rejectRecordSerializer.flush();
                        }
                    }
                } else {// error in file header - all records writes to log
                    result.addLog(getCurrentLine(deserializer) + "\n");
                }
                previousRecord = record;
            }

            recordSerializer.flush();
            recordSerializer.close(false);

            //add exist records to file and database
            addExistRecordsToFileAndDatabase(recordSerializer,rejectRecordSerializer,postingFile,postingRejectFile,result);

            rejectRecordSerializer.flush();
            rejectRecordSerializer.close(false);

            trailerSerializer.open(new BufferedWriter(writerPostingFile));
            trailerSerializer.write(buildPostingTrailer(postingFile.getPostingRecords().size()));
            trailerSerializer.flush();
            trailerSerializer.close(false);

            rejectTrailerSerializer.open(new BufferedWriter(writerPostingRejectFile));
            rejectTrailerSerializer.write(buildPostingTrailer(postingRejectFile.getPostingRecords().size()));
            rejectTrailerSerializer.flush();
            rejectTrailerSerializer.close(false);
            logger.debug("End ARC file: {} converting", billingFile.getName());

            return result.success();

        }
        catch (Exception e) {
            logger.error("Error during converting ARC-file", e);
            return result.fail();
        } finally {
            safeCloseDeserializer(deserializer);

            safeCloseSerializer(headerSerializer);
            safeCloseSerializer(recordSerializer);
            safeCloseSerializer(trailerSerializer);

            safeCloseSerializer(rejectHeaderSerializer);
            safeCloseSerializer(rejectRecordSerializer);
            safeCloseSerializer(rejectTrailerSerializer);
        }
    }

    @Override
    public String getSystemName() {
        return messageSource.getMessage(ISystem.SYSTEM_PREFIX + "billing.converter.arcSv",
                new Object[]{},"ARC Smart Vista billing converter", LocaleContextHolder.getLocale());
    }
}
