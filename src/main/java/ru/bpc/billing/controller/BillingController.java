package ru.bpc.billing.controller;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.type.TypeReference;
import org.hibernate.metamodel.source.annotations.xml.mocker.MockHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.bpc.billing.controller.dto.*;
import ru.bpc.billing.domain.*;
import ru.bpc.billing.domain.billing.BillingFile;
import ru.bpc.billing.domain.billing.BillingFileUploadRequest;
import ru.bpc.billing.domain.posting.PostingFile;
import ru.bpc.billing.exception.FileUploadException;
import ru.bpc.billing.repository.BillingFileRepository;
import ru.bpc.billing.repository.ProcessingFileFilter;
import ru.bpc.billing.repository.ProcessingFileRepository;
import ru.bpc.billing.repository.UserHistoryRepository;
import ru.bpc.billing.service.ApplicationService;
import ru.bpc.billing.service.PostingService;
import ru.bpc.billing.service.SystemSettingsService;
import ru.bpc.billing.service.billing.BillingConverterResult;
import ru.bpc.billing.service.billing.BillingService;
import ru.bpc.billing.service.report.ReportProcessingResult;
import ru.bpc.billing.service.report.ReportProcessor;
import ru.bpc.billing.service.report.ReportType;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * User: Krainov
 * Date: 20.08.14
 * Time: 12:11
 */
@Controller
@RequestMapping(value = "/billing")
public class BillingController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private ApplicationService applicationService;
    @Resource
    private BillingService billingService;
    @Resource
    private BillingFileRepository billingFileRepository;
    @Resource
    private ProcessingFileRepository processingFileRepository;
    @Resource
    private Map<ReportType,ReportProcessor> reportProcessors;
    @Resource
    private UserHistoryRepository userHistoryRepository;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private MessageSource messageSource;
    @Resource
    private SystemSettingsService systemSettingsService;
    @Resource
    private PostingService postingService;
    private TypeReference<List<BillingFileDto>> referenceBilling = new TypeReference<List<BillingFileDto>>() {};
    private TypeReference<List<BoDto>> referenceBo = new TypeReference<List<BoDto>>(){};

    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    public @ResponseBody
    BillingFileDto upload(BillingFileUploadRequest billingFileUploadRequest, UsernamePasswordAuthenticationToken principal) {
        UserHistory userHistory = new UserHistory();
        if ( null != principal && principal.getPrincipal() instanceof User) {
            userHistory.setUser((User) principal.getPrincipal());
            userHistory.setAction(UserAction.UPLOAD_BILLING_FILE);
        }
        BillingFileDto billingFileDto = null;
        try {
            BillingFile billingFile = billingService.upload(billingFileUploadRequest);
            billingFileDto = new BillingFileDto(billingFile,true);
        } catch (FileUploadException e) {
            String messageError = messageSource.getMessage(e.getErrorCode(),
                    e.getMessageErrorArgs(), "Error upload billing file: " + e.getMessage(),Locale.getDefault());
            logger.error("Error upload billing file: " + messageError,e);
            billingFileDto = new BillingFileDto(null,false,messageError);
            billingFileDto.setName(billingFileUploadRequest.getFile().getOriginalFilename());
            userHistory.setMessage(messageError);
        }
        if ( null != userHistory.getUser() ) {
            userHistory.setStatus(billingFileDto.getSuccess());
            userHistoryRepository.save(userHistory);
        }
        return billingFileDto;
    }

    @RequestMapping(value = "/fileTypes")
    public @ResponseBody Object getAvailableFileTypes() {
        ArrayNode nodes = objectMapper.createArrayNode();
        for (FileType fileType : FileType.values()) {
            ObjectNode objectNode = new ObjectNode(JsonNodeFactory.instance);
            objectNode.put("name",fileType.name());
            nodes.add(objectNode);
        }
        return nodes;
    }

    @RequestMapping(value = "/find")
    public @ResponseBody
    BillingFileDto find(ProcessingFileFilter processingFileFilter) {
        return prepareBillings(processingFileFilter);
    }

    @RequestMapping(value = "/read")
    public @ResponseBody
    BillingFileDto read(@RequestParam(value = "node", required = false) String node) {
        if ( null != node ) {
            try{//непонятно как чтобы node был только числом, поэтому надо делать так
                Long id = Long.parseLong(node);
                return prepareProcessingFile(id);
            } catch (NumberFormatException e) {}
        }
        Calendar oneWeekAgo = Calendar.getInstance();
        oneWeekAgo.add(Calendar.DAY_OF_MONTH,-7);
        ProcessingFileFilter processingFileFilter = new ProcessingFileFilter(null,oneWeekAgo.getTime(),new Date());
        return prepareBillings(processingFileFilter);
    }

//    @RequestMapping(value = "/convert")
//    public @ResponseBody BillingConverterResultDto convert(@RequestParam(value = "node") Long id,
//                                                           @RequestParam(value = "fileType") String fileType,
//                                                           UsernamePasswordAuthenticationToken principal) {
//
//        BillingConverterResultDto billingConverterResultDto = new BillingConverterResultDto();
//        billingConverterResultDto.setSuccess(false);
//        billingConverterResultDto.setText("This method unsupported, use /convert with 'billingFiles' as param");
//        return billingConverterResultDto;
//    }

    @RequestMapping(value = "/convert")
    public @ResponseBody BillingConverterResultDto convert(
                                            String billingFiles,
                                            UsernamePasswordAuthenticationToken principal) {
        BillingConverterResultDto billingConverterResultDto = new BillingConverterResultDto();
        billingConverterResultDto.setSuccess(true);
        List<BillingFileDto> billingDtos = null;
        try {
            billingDtos = objectMapper.readValue(billingFiles, referenceBilling);
        } catch (Exception e) {
            logger.error("Unable to read json values from request for 'billingFiles' parameters",e);
        }
        if ( null == billingDtos) {
            billingConverterResultDto.setSuccess(false);
            billingConverterResultDto.setText("Invalid param 'billingFiles'");
            return billingConverterResultDto;
        }
        UserHistory userHistory = new UserHistory();
        if ( null != principal && principal.getPrincipal() instanceof User ) {
            userHistory.setUser((User) principal.getPrincipal());
            userHistory.setAction(UserAction.CONVERT_BILLING);
        }

        try {
            Set<BillingFile> billingFiles1 = new HashSet<>();
            for (BillingFileDto billingDto : billingDtos) {
                if ( null == billingDto ) {
                    billingConverterResultDto.setSuccess(false);
                    billingConverterResultDto.setText("Invalid param 'billingFiles'");
                    break;
                }
                BillingFile billingFile = billingFileRepository.findOne(billingDto.getId());
                if (null == billingFile) {
                    billingConverterResultDto.setSuccess(false);
                    billingConverterResultDto.setText("Billing file with id: " + billingDto.getId() + " doesn't exist");
                    break;
                }
                billingFiles1.add(billingFile);
            }
            if (billingConverterResultDto.getSuccess()) {
                try {
                    BillingConverterResult billingConverterResult = billingService.convert(billingFiles1);
                    billingConverterResultDto.setSuccess(true);
                    billingConverterResultDto.setBillingConverterResult(billingConverterResult);
                } catch (IOException e) {
                    logger.error("Error convert " + billingFiles1, e);
                    billingConverterResultDto.setSuccess(false);
                    billingConverterResultDto.setText("Error convert billing file with id:" + billingFiles1 + " , because " + e.getMessage());
                }
            }
        } finally {
            if ( null != userHistory.getUser() ) {
                userHistory.setMessage(billingConverterResultDto.getText());
                userHistory.setStatus(false);
                userHistoryRepository.save(userHistory);
            }
        }
        return billingConverterResultDto;
    }


    @RequestMapping(value = "/report")
    public @ResponseBody
    ReportProcessingResultDto report(HttpServletRequest request, UsernamePasswordAuthenticationToken principal) {
        UserHistory userHistory = new UserHistory();
        if ( null != principal && principal.getPrincipal() instanceof User ) {
            userHistory.setUser((User)principal.getPrincipal());
            userHistory.setAction(UserAction.BUILD_REPORT);
        }
        ReportProcessingResultDto resultDto = new ReportProcessingResultDto();
        resultDto.setSuccess(true);

        List<BillingFileDto> billingDtos = null;
        List<BoDto> boDtos = null;
        ReportType reportType = ReportType.STANDARD;
        try {
            billingDtos = objectMapper.readValue(request.getParameter("billingFiles"),referenceBilling);
            boDtos = objectMapper.readValue(request.getParameter("boFiles"),referenceBo);
            String reportTypeParam = systemSettingsService.getString(ReportType.SYSTEM_SETTINGS_PARAM_NAME);
            if (StringUtils.isNotBlank(reportTypeParam) ) {
                reportType = ReportType.safeValueOf(reportTypeParam);
            }
        } catch (IOException e) {
            logger.error("Unable to read json values from request for 'billingFiles' and 'boFiles' parameters",e);
        }
        if ( null == billingDtos || null == boDtos || billingDtos.isEmpty() || boDtos.isEmpty() ) {
            resultDto.setSuccess(false);
            return resultDto;
        }
        if ( null == reportType ) reportType = ReportType.STANDARD;

        List<BillingFile> billingFiles = new ArrayList<>();
        for (BillingFileDto billingDto : billingDtos) {
            billingFiles.add((BillingFile)processingFileRepository.findOne(billingDto.getId()));
        }
        List<File> boFiles = new ArrayList<>();
        for (BoDto boDto : boDtos) {
            boFiles.add(new File(applicationService.getHomeDir(FileType.BO) + boDto.getFileName()));
        }
        try {
            ReportProcessor reportProcessor = reportProcessors.get(reportType);
            if ( null == reportProcessor ) {
                logger.warn("Error find report processor for reportType: {}, available only {}",reportType,reportProcessors);
                resultDto.setSuccess(false);
                resultDto.setText("Error find report processor for reportType: " + reportType);
                return resultDto;
            }
            logger.debug("Found reportProcessor: {} for reportType: {}",reportProcessor,reportType);
            ReportProcessingResult result = reportProcessor.process(billingFiles, boFiles);
            resultDto.setReportProcessingResult(result);
        } catch (Exception e) {
            logger.error("Error build report for " + billingDtos + " and " + boDtos,e);
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

    @RequestMapping(value = "/revert_posting_file")
    public @ResponseBody RevertPostingFileResultDto revertPostingFile(@RequestParam(value = "node") Long id,
                                                  UsernamePasswordAuthenticationToken principal) {
        UserHistory userHistory = new UserHistory();
        if ( null != principal && principal.getPrincipal() instanceof User ) {
            userHistory.setUser((User) principal.getPrincipal());
            userHistory.setAction(UserAction.REVERT_POSTING_FILE);
        }
        RevertPostingFileResultDto revertPostingFileResultDto = new RevertPostingFileResultDto();
        try {
            ProcessingFile processingFile = processingFileRepository.findOne(id);
            if (null == processingFile || !(processingFile instanceof PostingFile)) {
                revertPostingFileResultDto.setSuccess(false);
                revertPostingFileResultDto.setText("File with id: " + id + " not found or not postingFile");
                return revertPostingFileResultDto;
            }
            revertPostingFileResultDto = postingService.revert(id);
        } catch (Exception e) {
            logger.error("Error revert posting file with id: " + id, e);
            revertPostingFileResultDto.setSuccess(false);
            revertPostingFileResultDto.setText(e.getMessage());
        } finally {
            if ( null != userHistory.getUser() ) {
                userHistory.setStatus(revertPostingFileResultDto.getSuccess());
                userHistory.setMessage(revertPostingFileResultDto.getText());
                userHistoryRepository.save(userHistory);
            }
            return revertPostingFileResultDto;
        }
    }

    @RequestMapping(value = "/can_revert_posting_file")
    @Transactional(readOnly = true)
    public @ResponseBody RevertPostingFileResultDto canRevertPostingFile(@RequestParam(value = "node") Long id,
                                                                      UsernamePasswordAuthenticationToken principal) {
        RevertPostingFileResultDto revertPostingFileResultDto = new RevertPostingFileResultDto();
        revertPostingFileResultDto.setSuccess(postingService.canRevert(id));
        return revertPostingFileResultDto;
    }

    private boolean isFileExist(FileType fileType, String filePath) {
        File file = new File(applicationService.getHomeDir(fileType) + filePath);
        return file.exists() && file.canRead();
    }

    private BillingFileDto prepareProcessingFile(Long id) {
        BillingFileDto billingDto = new BillingFileDto();
        billingDto.setText(".");

        ProcessingFile processingFile = processingFileRepository.findOne(id);
        BillingFileDto billingDtoFile = new BillingFileDto();
        billingDtoFile.setId(processingFile.getId());
        billingDtoFile.setName(processingFile.getName());
        billingDtoFile.setFileType(processingFile.getFileType());
        billingDtoFile.setBusinessDate(null);
        billingDtoFile.setCreatedDate(processingFile.getCreatedDate());
        billingDtoFile.setFormat(null);
        billingDtoFile.setCanDownloaded(isFileExist(processingFile.getFileType(), processingFile.getName()));

        if ( processingFile.getFileType().equals(FileType.BILLING) ) {
            BillingFile billingFile = billingFileRepository.findOne(id);
            billingDtoFile.setFormat(billingFile.getFormat());
        }
        List<ProcessingFileDto> children = new ArrayList<>();
        children.add(billingDtoFile);
        billingDto.setChildren(children);
        billingDto.setSuccess(true);

        return billingDto;

    }

    private BillingFileDto prepareBillings(ProcessingFileFilter filter) {
        return billingService.prepareBillings(filter);
    }

}
