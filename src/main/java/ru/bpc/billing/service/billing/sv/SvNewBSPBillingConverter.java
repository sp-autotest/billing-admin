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
import ru.bpc.billing.domain.billing.BillingFileFormat;
import ru.bpc.billing.domain.billing.bsp.IBR;
import ru.bpc.billing.domain.billing.bsp.IFH;
import ru.bpc.billing.domain.billing.bsp.IIH;
import ru.bpc.billing.domain.posting.*;
import ru.bpc.billing.domain.posting.sv.SvPostingHeader;
import ru.bpc.billing.domain.posting.sv.SvPostingRecord;
import ru.bpc.billing.domain.posting.sv.SvPostingTrailer;
import ru.bpc.billing.service.ISystem;
import ru.bpc.billing.service.billing.AbstractBSPBillingConverter;
import ru.bpc.billing.service.billing.BillingConverterException;
import ru.bpc.billing.service.billing.BillingConverterResult;

import java.io.*;
import java.nio.file.Path;
import java.util.Date;

/**
 * User: Krainov
 * Date: 18.04.2016
 * Time: 11:50
 */
public class SvNewBSPBillingConverter extends AbstractBSPBillingConverter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    protected BillingConverterResult convertBillingFile(BillingFile billingFile) throws IOException {
        BillingConverterResult result = new BillingConverterResult();
        result.setBillingFile(billingFile);
        logger.debug("Begin BSP file: {} converting", billingFile);

        File billingOriginalFile = getBillingPath(billingFile).toFile();
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
            deserializer = FlrIOFactory.createFactory(BillingFileFormat.BSP.getClasses()).createDeserializer();
            deserializer.open(readerBillingFile);

            headerSerializer = FlrIOFactory.createFactory(SvPostingHeader.class).createSerializer();
            recordSerializer = FlrIOFactory.createFactory(SvPostingRecord.class).createSerializer();
            trailerSerializer = FlrIOFactory.createFactory(SvPostingTrailer.class).createSerializer();

            rejectHeaderSerializer = FlrIOFactory.createFactory(SvPostingHeader.class).createSerializer();
            rejectRecordSerializer = FlrIOFactory.createFactory(SvPostingRecord.class).createSerializer();
            rejectTrailerSerializer = FlrIOFactory.createFactory(SvPostingTrailer.class).createSerializer();

            headerSerializer.open(new BufferedWriter(writerPostingFile));
            rejectHeaderSerializer.open(new BufferedWriter(writerPostingRejectFile));

            Object record;

            boolean errorInFileHeader = false;
            boolean fileHeaderWritten = false;
            String fileHeader = null;

            // Header
            record = deserializer.next();

            try {
                Object postingHeader = buildPostingHeader((IFH) record);
                headerSerializer.write(postingHeader);
                headerSerializer.flush();
                fileHeader = getCurrentLine(deserializer);// we will write it to log if error occurs

                rejectHeaderSerializer.write(postingHeader);
                rejectHeaderSerializer.flush();
            } catch (BillingConverterException ce) {
                String currentLine = getCurrentLine(deserializer);
                logger.error("Invalid [{}] record - [{}]: {}", getShortClassName(record), getMessage(ce.getErrorMessageCode(), EN), currentLine);
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
            IIH iih = null;
            while (deserializer.hasNext()) {
                record = deserializer.next();
                if ( record instanceof IIH ) {
                    iih = (IIH)record;
                    continue;
                }
                if ( !(record instanceof IBR) ) continue;
                if (!errorInFileHeader) {
                    if ( ((IBR)record).isDebit() ) billingFile.depositCount++;
                    else billingFile.refundCount++;
                    billingFile.allRecordWithoutNotFinancialOperationCount++;

                    PostingRecordBuilderResult postingRecordBuilderResult = buildPostingRecord(iih, (IBR)record, billingFile.getCarrier());
                    try {
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
                    } catch (BillingConverterException ce) {
                        String currentLine = getCurrentLine(deserializer);
                        logger.error("Invalid [{}] record - [{}]: {}",
                                getShortClassName(record),
                                ce.getRecordFilename() != null ? getMessage(ce.getErrorMessageCode(), EN, new String[]{ce.getRecordFilename()}) : getMessage(ce.getErrorMessageCode(), EN),
                                currentLine);
                        if (!fileHeaderWritten) {// first error in file - write file header to log
                            result.addLog(fileHeader + "\n");
                            fileHeaderWritten = true;
                        }
                        result.addLog(currentLine + "\n");
                        result.addLog(REM + (ce.getRecordFilename() != null ? getMessage(ce.getErrorMessageCode(), new String[]{ce.getRecordFilename()}) : getMessage(ce.getErrorMessageCode())) + "\n");
                    }
                    //write reject file if it need
                    if ( !postingRecordBuilderResult.isSuccess() ) {
                        rejectRecordSerializer.write(postingRecordBuilderResult.getPostingRecord());
                        rejectRecordSerializer.flush();
                    }
                } else {// error in header - all records to log
                    result.addLog(getCurrentLine(deserializer) + "\n");
                }
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
            logger.debug("End BSP file: {} converting", billingFile);

            return result.success();
        }
        catch (Exception e) {
            logger.error("Error during converting BSP-file: " + billingFile, e);
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
        return messageSource.getMessage(ISystem.SYSTEM_PREFIX + "billing.converter.bspSv",new Object[]{},
                "BSP Smart Vista billing converter", LocaleContextHolder.getLocale());
    }
}
