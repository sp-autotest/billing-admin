package ru.bpc.billing.service.automate.controllerService;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.bpc.billing.controller.dto.*;
import ru.bpc.billing.domain.*;
import ru.bpc.billing.domain.billing.BillingFile;
import ru.bpc.billing.domain.bo.BOFile;
import ru.bpc.billing.domain.bo.BOFileUploadRequest;
import ru.bpc.billing.exception.FileUploadException;
import ru.bpc.billing.repository.BOFileRepository;
import ru.bpc.billing.repository.BillingFileRepository;
import ru.bpc.billing.repository.ProcessingFileRepository;
import ru.bpc.billing.repository.UserHistoryRepository;
import ru.bpc.billing.service.ApplicationService;
import ru.bpc.billing.service.SystemSettingsService;
import ru.bpc.billing.service.billing.BillingConverterResult;
import ru.bpc.billing.service.billing.BillingService;
import ru.bpc.billing.service.bo.BOService;
import ru.bpc.billing.service.report.ReportProcessingResult;
import ru.bpc.billing.service.report.ReportProcessor;
import ru.bpc.billing.service.report.ReportType;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * User: Krainov
 * Date: 20.08.14
 * Time: 12:11
 */
@Service
public class BOControllerService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private BOService boService;
    @Resource
    private UserHistoryRepository userHistoryRepository;
    @Resource
    private MessageSource messageSource;
    @Resource
    private SystemSettingsService systemSettingsService;
    @Resource
    private ApplicationService applicationService;
    @Resource
    private ProcessingFileRepository processingFileRepository;
    @Resource
    private Map<ReportType,ReportProcessor> reportProcessors;

    private TypeReference<List<BillingFileDto>> referenceBilling = new TypeReference<List<BillingFileDto>>() {};

    public BoDto upload(BOFileUploadRequest boFileUploadRequest, UsernamePasswordAuthenticationToken principal) throws FileUploadException {
//        UserHistory userHistory = new UserHistory();
//        if (null != principal && principal.getPrincipal() instanceof User) {
//            userHistory.setUser((User) principal.getPrincipal());
//            userHistory.setAction(UserAction.UPLOAD_BO_FILE);
//        }
//        BoDto billingFileDto =  new BoDto();
//        try {
//            File billingFile = boService.upload(boFileUploadRequest);
//            boService.fillDto(billingFileDto, billingFile);
//        } catch (FileUploadException e) {
////            String messageError = messageSource.getMessage(e.getErrorCode(),
////                    e.getMessageErrorArgs(), "Error upload billing file: " + e.getMessage(), Locale.getDefault());
////            logger.error("Error upload billing file: " + messageError, e);
//            //billingFileDto.setName(boFileUploadRequest.getFile().getOriginalFilename());
//            userHistory.setMessage(e.getMessage());
//            throw e;
//        }
//        if (null != userHistory.getUser()) {
//            userHistory.setStatus(billingFileDto.getSuccess());
//            userHistoryRepository.save(userHistory);
//        }
//        return billingFileDto;

        UserHistory userHistory = new UserHistory();
        if ( null != principal && principal.getPrincipal() instanceof User ) {
            userHistory.setUser((User) principal.getPrincipal());
            userHistory.setAction(UserAction.UPLOAD_BO_FILE);
        }
        BoDto dto = new BoDto();
        try {
            File file = boService.upload(boFileUploadRequest);
            boService.fillDto(dto, file);
            dto.setSuccess(true);
            userHistory.setNewValue(file.getName());
        } catch (FileUploadException e) {
            String messageError = messageSource.getMessage(e.getErrorCode(),
                    e.getMessageErrorArgs(), "Error upload bo-file: " + boFileUploadRequest.getFile() + ", because : " + e.getMessage(), Locale.getDefault());
            logger.error("Error upload bo-file: " + messageError,e);
            dto.setSuccess(false);
            dto.setText(messageError);
            dto.setFileName(boFileUploadRequest.getFile().getOriginalFilename());
            userHistory.setMessage(messageError);
        } catch (Exception e) {
            logger.error("Error upload bo-file", e);
            dto.setSuccess(false);
            dto.setFileName(boFileUploadRequest.getFile().getOriginalFilename());
            dto.setText(e.getMessage());
            userHistory.setMessage("Error upload bo-file: " + e.getMessage());
        }
        if ( null != userHistory.getUser() ) {
            userHistory.setStatus(dto.getSuccess());
            userHistoryRepository.save(userHistory);
        }
        return dto;
    }

    public ReportProcessingResultDto genReport(List<BoDto> boDtos, UsernamePasswordAuthenticationToken principal) {
        UserHistory userHistory = new UserHistory();
        if ( null != principal && principal.getPrincipal() instanceof User ) {
            userHistory.setUser((User)principal.getPrincipal());
            userHistory.setAction(UserAction.BUILD_REPORT);
        }
        ReportProcessingResultDto resultDto = new ReportProcessingResultDto();
        resultDto.setSuccess(true);

        List<ProcessingFile> processingFiles = null;
        ReportType reportType = ReportType.STANDARD;
        try {
            //billingDtos = objectMapper.readValue(request.getParameter("billingFiles"), referenceBilling);
            //boDtos = objectMapper.readValue(request.getParameter("boFiles"),referenceBo);
            String reportTypeParam = systemSettingsService.getString(ReportType.SYSTEM_SETTINGS_PARAM_NAME);
            if (StringUtils.isNotBlank(reportTypeParam) ) {
                reportType = ReportType.safeValueOf(reportTypeParam);
            }
        } catch (Exception e) {
            logger.error("Unable to read json values from request for 'billingFiles' and 'boFiles' parameters",e);
        }
        processingFiles = getBspForBo(boDtos);

        if ( null == processingFiles || null == boDtos || processingFiles.isEmpty() || boDtos.isEmpty() ) {
            resultDto.setSuccess(false);
            return resultDto;
        }
        if ( null == reportType ) reportType = ReportType.STANDARD;

        List<BillingFile> billingFiles = new ArrayList<>();
        for (ProcessingFile pFile : processingFiles) {
            billingFiles.add((BillingFile)processingFileRepository.findOne(pFile.getId()));
        }
        List<File> boFiles = new ArrayList<>();
        for (BoDto boDto : boDtos) {
            boFiles.add(new File(applicationService.getHomeDir(FileType.BO) + boDto.getFileName()));
        }
        try {
            ReportProcessor reportProcessor = reportProcessors.get(reportType);
            if ( null == reportProcessor ) {
                logger.warn("Error find report processor for reportType: {}, available only {}",reportType, reportProcessors);
                resultDto.setSuccess(false);
                resultDto.setText("Error find report processor for reportType: " + reportType);
                return resultDto;
            }
            logger.debug("Found reportProcessor: {} for reportType: {}",reportProcessor,reportType);
            ReportProcessingResult result = reportProcessor.process(billingFiles, boFiles);
            resultDto.setReportProcessingResult(result);
        } catch (Exception e) {
            logger.error("Error build report for " + boFiles + " and " + billingFiles, e);
            resultDto.setSuccess(false);
            resultDto.setText(e.getMessage());
        }
        if ( null != userHistory.getUser() ) {
            userHistory.setStatus(resultDto.getSuccess());
            userHistory.setMessage(resultDto.getText());
            userHistoryRepository.save(userHistory);
        }
        return resultDto;
    }

    private List<ProcessingFile> getBspForBo(List<BoDto> boDtos) {
        List<ProcessingFile> result = new ArrayList<>();
        for(BoDto bo : boDtos) {
            String boDate = StringUtils.substringAfterLast(bo.getFileName(),"_").substring(0, 8);
            List<ProcessingFile> billings = processingFileRepository.getNotProcessedBillings(boDate);
            result.addAll(billings);
        }

        return result;
    }

}
