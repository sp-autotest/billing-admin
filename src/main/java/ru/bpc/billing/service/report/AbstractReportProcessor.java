package ru.bpc.billing.service.report;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import ru.bpc.billing.domain.ProcessingFile;
import ru.bpc.billing.domain.ProcessingFileRecord;
import ru.bpc.billing.domain.ProcessingFileRecordPk;
import ru.bpc.billing.domain.ProcessingRecord;
import ru.bpc.billing.domain.billing.BillingFile;
import ru.bpc.billing.domain.bo.BORecord;
import ru.bpc.billing.domain.report.ReportFile;
import ru.bpc.billing.domain.report.ReportRecord;
import ru.bpc.billing.repository.ProcessingFileRecordRepository;
import ru.bpc.billing.repository.ProcessingFileRepository;
import ru.bpc.billing.repository.ProcessingRecordRepository;
import ru.bpc.billing.service.ApplicationService;
import ru.bpc.billing.service.bo.BOProcessingResult;
import ru.bpc.billing.service.bo.BOProcessor;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: Krainov
 * Date: 03.09.2014
 * Time: 16:44
 */
public abstract class AbstractReportProcessor implements ReportProcessor {

    private final static Logger logger = LoggerFactory.getLogger(AbstractReportProcessor.class);
    @Resource
    private BOProcessor boProcessor;
    @Resource
    private ProcessingFileRecordRepository processingFileRecordRepository;
    @Resource
    private ProcessingFileRepository processingFileRepository;
    @Resource
    private ProcessingRecordRepository processingRecordRepository;
    @Resource
    private ApplicationService applicationService;
    private List<ReportBuilder> reportBuilders;

    public void setReportBuilders(List<ReportBuilder> reportBuilders) {
        this.reportBuilders = reportBuilders;
    }

    @Override
    @Transactional
    public ReportProcessingResult process(List<BillingFile> billingFiles, List<File> boFiles) throws IOException, InterruptedException, ReportBuildException {
        AtomicBoolean stopped = new AtomicBoolean(false);//разобраться
        ReportProcessingResult result = new ReportProcessingResult(billingFiles);

        List<ReportRecord> reportRecords = new ArrayList<>();
        Map<String, BORecord> combinedBoProcessingResult = new HashMap<>();

        //обработка БО-ревенью файлов, матчинг данных из файлов с данными в базе
        List<BOProcessingResult> boProcessingResults = new ArrayList<>();
        for (File boFile : boFiles) {
            BOProcessingResult processingResult = boProcessor.process(boFile);
            combinedBoProcessingResult.putAll(processingResult.getProcessedObjects());
            boProcessingResults.add(processingResult);
            logger.debug("BO-file: {} have been {} handled : {} records, {} success, {} error, {} fraud, {} processedObjects, {} deposit, {} refund",
                    boFile, processingResult.isSuccess() ? "OK" : "FAIL", processingResult.getTotalRecords(), processingResult.getSuccessRecords(),
                    processingResult.getErrorRecords(), processingResult.getFraudRecords(), processingResult.getCountProcessObjects(), processingResult.getDepositRecords(), processingResult.getRefundRecords());
        }
        result.setBoProcessingResults(boProcessingResults);

        for (BillingFile billingFile : billingFiles) {
            for (BOProcessingResult boProcessingResult : boProcessingResults) {
                //бо-файлы были только обработаны, проставляем им парент значение и сохраняем в БД
                for (ProcessingFile processingFile : boProcessingResult.getProcessingFiles()) {
                    processingFile.setParentFile(billingFile);
                    processingFileRepository.save(processingFile);
                }
                //сохраняем линки записей, которые есть в бд и файлов, в которых они появились
                for (Map.Entry<String, BORecord> entry : boProcessingResult.getProcessedObjects().entrySet()) {
                    BORecord boRecord = entry.getValue();
                    //успешный бо-запись
                    if ( null != boRecord.getProcessingRecord() && boRecord.isSuccess() ) {
                        ProcessingRecord processingRecord = boRecord.getProcessingRecord();
                        processingFileRecordRepository.save(new ProcessingFileRecord(new ProcessingFileRecordPk(boRecord.getProcessingFile(),processingRecord)));
                        boProcessingResult.getProcessingRecords().add(processingRecord);
                    }
                    else {//реджект бо-запись, делаем проверку, есть ли она у нас и сохраняем линк
                        ProcessingRecord processingRecord = processingRecordRepository.findByRbsId(entry.getKey());
                        if ( null != processingRecord ) {
                            processingFileRecordRepository.save(new ProcessingFileRecord(new ProcessingFileRecordPk(boRecord.getProcessingFile(),processingRecord)));
                            boProcessingResult.getProcessingRecords().add(processingRecord);
                        }
                    }
                }
            }
            reportRecords.addAll(mergeBillingAndBoRecords(billingFile,combinedBoProcessingResult));
        }

        //группируем записи по билетам
        LoadAndGroupTickets loadAndGroupTickets = loadAndGroupTickets(reportRecords,stopped);
        loadAndGroupTickets.setBillingFiles(result.getBillingFiles());

        //формируем файлы отчётов
        for (ReportBuilder reportBuilder : reportBuilders) {
            File builtFile = reportBuilder.build(loadAndGroupTickets,stopped);
            if ( null == (builtFile = checkReportFile(reportBuilder,builtFile)) ) {
                logger.warn("Report for {} was created failed",reportBuilder.getFileType());
                continue;
            }

            //сохраняем сформированные отчёты в БД
            for (BillingFile billingFile : billingFiles) {
                ReportFile reportFile = new ReportFile(reportBuilder.getFileType());
                reportFile.setName(builtFile.getName());
                reportFile.setOriginalFileName(builtFile.getName());
                reportFile.setCreatedDate(new Date());
                reportFile.setParentFile(billingFile);
                reportFile.successCreditRecordsCount = loadAndGroupTickets.getSuccessCreditRecordsCount();
                reportFile.successDepositRecordsCount = loadAndGroupTickets.getSuccessDepositRecordsCount();
                reportFile.rejectCreditRecordsCount = loadAndGroupTickets.getRejectCreditRecordsCount();
                reportFile.rejectDepositRecordsCount = loadAndGroupTickets.getRejectDepositRecordsCount();
                processingFileRepository.save(reportFile);
                result.getReportFiles().add(reportFile);

                //сохраняем привязку записей, что они были в этих файлах
                for (ReportRecord reportRecord : reportRecords) {
                    //если запись для отчёта удовлетворяет условия данного билдера, то делаем привязку
                    if ( reportBuilder.linkFileToRecord(reportFile, reportRecord) ) {
                        ProcessingRecord processingRecord = reportRecord.getProcessingRecord();
                        processingFileRecordRepository.save(new ProcessingFileRecord(new ProcessingFileRecordPk(reportFile, processingRecord)));
                    }
                }
            }
            logger.debug("Report {} was successfully saved as {} file",reportBuilder.getFileType(),builtFile);
        }

        logger.debug(result.toString());
        return result;
    }

    protected abstract List<ReportRecord> mergeBillingAndBoRecords(BillingFile bf, Map<String,BORecord> processedObjects);

    protected LoadAndGroupTickets loadAndGroupTickets(List<ReportRecord> reportRecords, AtomicBoolean stopped) throws InterruptedException, ReportBuildException {
        return new LoadAndGroupTickets(reportRecords,stopped);
    }

    /**
     * Проверяем, что файл-отчёт находится в нужной директории, если не так, то перемещаем его
     * @param reportBuilder
     * @param file
     * @return
     */
    private File checkReportFile(ReportBuilder reportBuilder, File file) {
        if ( null == file || !file.exists() || !file.canRead() ) {
            logger.warn("Report file: {} doesn't exist",file);
            return null;
        }
        Path homePath = applicationService.getHomePath(reportBuilder.getFileType());
        Path path = Paths.get(file.toURI());
        if ( homePath.equals(path.getParent()) ) return file;

        File newFile = new File(homePath.toString() + FilenameUtils.getBaseName(file.getName()) + new Date().getTime() +
                (StringUtils.isNotBlank(FilenameUtils.getExtension(file.getName())) ? "." : "") + FilenameUtils.getExtension(file.getName()));
        try {
            FileUtils.copyFile(file, newFile);
        } catch (IOException e) {
            logger.warn("Error move file: " + file + " to new file: " + newFile,e);
            return null;
        }
        return newFile;
    }


}
