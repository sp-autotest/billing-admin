package ru.bpc.billing.service.billing;

import org.apache.commons.io.FilenameUtils;
import org.jsefa.flr.FlrDeserializer;
import org.jsefa.flr.FlrIOFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bpc.billing.controller.dto.BillingFileDto;
import ru.bpc.billing.controller.dto.ProcessingFileDto;
import ru.bpc.billing.domain.Carrier;
import ru.bpc.billing.domain.FileType;
import ru.bpc.billing.domain.ProcessingFile;
import ru.bpc.billing.domain.Terminal;
import ru.bpc.billing.domain.billing.BillingFile;
import ru.bpc.billing.domain.billing.BillingFileFormat;
import ru.bpc.billing.domain.billing.BillingFileUploadRequest;
import ru.bpc.billing.domain.billing.arc.TFH;
import ru.bpc.billing.domain.billing.bsp.IFH;
import ru.bpc.billing.exception.FileUploadException;
import ru.bpc.billing.repository.*;
import ru.bpc.billing.service.ApplicationService;

import javax.annotation.Resource;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: Krainov
 * Date: 15.08.14
 * Time: 10:45
 */
@Service
public class BillingService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private ApplicationService applicationService;
    @Resource
    private Map<BillingFileFormat,BillingConverter> billingConverters;
    @Resource
    private BillingFileRepository billingFileRepository;
    @Resource
    private ProcessingFileRepository processingFileRepository;
    @Resource
    private CarrierRepository carrierRepository;

    public BillingConverterResult convert(Set<BillingFile> billingFiles) throws IOException {
        Set<BillingFileFormat> formats = new HashSet<>();
        for (BillingFile billingFile : billingFiles) {
            File originalFile = new File(applicationService.getHomeDir(FileType.BILLING) + billingFile.getName());
            if ( !originalFile.exists() || !originalFile.canRead() ) throw new IOException("File " + billingFile.getName() + " doesn't exist or cannot read");
            billingFile.setOriginalFile(originalFile);
            formats.add(billingFile.getFormat());
        }
        if ( 1 != formats.size() ) {
            BillingConverterResult billingConverterResult = new BillingConverterResult();
            billingConverterResult.setStatus(false);
            billingConverterResult.setInnerErrorMessage("Billing files: " + billingFiles + " have not same format");
            return billingConverterResult;
        }
        return billingConverters.get(formats.iterator().next()).convert(billingFiles.toArray(new BillingFile[]{}));
    }

    public BillingFile upload(BillingFileUploadRequest billingFileUploadRequest) throws FileUploadException {
        File file = applicationService.uploadFile(applicationService.getHomeDir(FileType.BILLING), billingFileUploadRequest.getFile(),
                multipartFile -> {
                    String filename = multipartFile.getOriginalFilename();
                    String name = FilenameUtils.getName(filename);
                    String timestamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                    return name + "-" + timestamp;
                });

        BillingFileFormat billingFileFormat = determineFileFormat(file);
        checkBillingFileFormat(billingFileFormat,billingFileUploadRequest,file);

        BillingConverter billingConverter = billingConverters.get(billingFileFormat);
        checkBillingConverter(billingConverter,billingFileFormat,file);

        BillingValidateResult billingValidateResult = null;
        try {
            billingValidateResult = billingConverter.validate(file);
            checkBillingValidateResult(billingValidateResult,billingFileFormat,file);
        } catch (IOException e) {
            throw new FileUploadException("file.upload.error",new Object[]{file.getName()},file);
        }

        BillingFile billingFile = new BillingFile();
        billingFile.setOriginalFile(file);
        billingFile.setFormat(billingFileFormat);
        billingFile.setOriginalFileName(billingFileUploadRequest.getFile().getOriginalFilename());
        billingFile.setName(file.getName());
        billingFile.setProcessingDate(billingValidateResult.getProcessingDate());
        billingFile.setBusinessDate(billingFileUploadRequest.getBusinessDate());
        billingFile.setCountLines(billingValidateResult.getCountLines());
        billingFile.setCarrier(billingValidateResult.getCarrier());

        return billingFileRepository.save(billingFile);
    }

    @Transactional(readOnly = true)
    public BillingFileDto prepareBillings(ProcessingFileFilter filter) {
        BillingFileDto billingDto = new BillingFileDto();
        billingDto.setText(".");

        //BillingFileFilter billingFileFilter = new BillingFileFilter(filter.getFilename(),filter.getFromCreateDate(),filter.getToCreateDate());
//        BillingFileFilter billingFileFilter = new BillingFileFilter(filter);
//        List<BillingFile> billingFiles = billingFileRepository.findAll(billingFileFilter);
        List<ProcessingFile> processingFiles = processingFileRepository.findAll(filter);
        List<ProcessingFileDto> billingDtos = new ArrayList<>();
        for (ProcessingFile processingFile : processingFiles) {
            billingDtos.add(addNode(filter,processingFile));
        }
        billingDto.setChildren(billingDtos);
        billingDto.setSuccess(true);
        return billingDto;
    }

    private BillingFileDto addNode(ProcessingFileFilter filter, ProcessingFile prFile) {
        ProcessingFile processingFile = null != prFile.getParentFile() ? prFile.getParentFile() : prFile;

        BillingFileDto dto = new BillingFileDto();
        dto.setId(processingFile.getId());

        dto.setCreatedDate(processingFile.getCreatedDate());
        dto.setBusinessDate(processingFile.getBusinessDate());
        dto.setName(processingFile.getName());
        dto.setFileType(processingFile.getFileType());
        if ( filter.isCheckAvailability() ) dto.setCanDownloaded(isFileExist(processingFile.getFileType(), processingFile.getName()));
        if ( processingFile instanceof BillingFile ) {
            dto.setFormat(((BillingFile)processingFile).getFormat());
            Carrier carrier = ((BillingFile)processingFile).getCarrier();
            if (carrier != null) dto.setIataCode(carrier.getIataCode());
        }
        if ( null != processingFile.getFiles() && !processingFile.getFiles().isEmpty() ) {
            List<ProcessingFile> files = processingFile.getFiles();
            if (null != files && !files.isEmpty()) {
                for (ProcessingFile file : files) {
                    BillingFileDto dtoSimpleFile = new BillingFileDto();
                    dtoSimpleFile.setId(file.getId());
                    dtoSimpleFile.setName(file.getName());
                    dtoSimpleFile.setFileType(file.getFileType());
                    dtoSimpleFile.setBusinessDate(null);
                    dtoSimpleFile.setCreatedDate(file.getCreatedDate());
                    dtoSimpleFile.setFormat(null);
                    if (filter.isCheckAvailability())
                        dtoSimpleFile.setCanDownloaded(isFileExist(file.getFileType(), file.getName()));

                    dto.addChild(dtoSimpleFile);
                }
            }
        }
        return dto;
    }

    private boolean isFileExist(FileType fileType, String filePath) {
        File file = new File(applicationService.getHomeDir(fileType) + filePath);
        return file.exists() && file.canRead();
    }

    protected void checkBillingFileFormat(BillingFileFormat billingFileFormat, BillingFileUploadRequest billingFileUploadRequest, File file) throws FileUploadException {
        if ( null == billingFileFormat )
            handleErrorUpload("file.upload.billingFileFormat.invalid","Unable to determine billingFileFormat for file: " + billingFileUploadRequest.getFile().getOriginalFilename(),file);
    }

    protected void checkBillingConverter(BillingConverter billingConverter, BillingFileFormat billingFileFormat, File file) throws FileUploadException {
        if ( null == billingConverter )
            handleErrorUpload("converter.notFound","Unable to find billing converter for billingFileFormat: " + billingFileFormat + ";enable only " + billingConverters.keySet() + " formats",file);
    }

    protected void checkExistBillingFiles(BillingValidateResult billingValidateResult, BillingFileFormat billingFileFormat, File file) throws FileUploadException {
        ProcessingFileFilter filter = new ProcessingFileFilter();
        filter.setBillingFileFormat(billingFileFormat);
        filter.setProcessingDate(billingValidateResult.getProcessingDate());
        filter.setCountLines(billingValidateResult.getCountLines());

        List<ProcessingFile> billingFiles = processingFileRepository.findAll(filter);
        if ( null != billingFiles && 0 < billingFiles.size() ) {
            StringBuilder sb = new StringBuilder();
            for (ProcessingFile billingFile : billingFiles) {
                sb.append(billingFile.getId()).append(":").append(billingFile.getCreatedDate()).append(";");
            }
            handleErrorUpload("file.upload.alreadyUploaded",
                    "File: [" + file + "] with fileFormat: [" + billingFileFormat + "] was already upload and handled and check box is false. List already uploaded files: [" + sb.toString() + "]",
                    file,
                    new Object[]{file.getName(),billingFileFormat,sb.toString()});
        }
    }

    @Resource
    private TerminalRepository terminalRepository;

    protected void checkBillingValidateResult(BillingValidateResult billingValidateResult, BillingFileFormat billingFileFormat, File file) throws FileUploadException {
        if ( !billingValidateResult.isSuccess() )
            handleErrorUpload("file.upload.billingValidateResult.invalid","File: " + file + " is not correct",file);
        if ( null == billingValidateResult.getProcessingDate() ) {
            handleErrorUpload("file.upload.billingValidateResult.processingDate.invalid","Unable to check processingDate for billing file: " + file + " and billingFormatType: " + billingFileFormat,file);
        }
        Set<String> agrnCodes = billingValidateResult.getAgrnCodes();
        Carrier carrier = null;
        for (String agrnCode : agrnCodes) {
            Terminal terminal = terminalRepository.findByAgrn(agrnCode);
            if ( null == terminal ) {
                logger.error("Unknown terminal in file : {} with agrn: {}", file, agrnCode);
                handleErrorUpload("file.upload.billingValidateResult.terminal.notFound","",file, new Object[]{file,agrnCode});
            }
            if ( null == terminal.getCarrier() ) {
                logger.error("File: {} , Terminal with agrn: {} hasn't carrier", file, agrnCode);
                handleErrorUpload("file.upload.billingValidateResult.terminal.invalid","",file, new Object[]{file,agrnCode});
            }
            if ( null != carrier && !carrier.getIataCode().equals(terminal.getCarrier().getIataCode()) ) {
                logger.error("File: {} contains agrn codes: {} which belongs different carrier.", file, agrnCodes);
                handleErrorUpload("file.upload.billingValidateResult.file.invalidAgrnCodes","",file, new Object[]{file,agrnCodes.size()});
            }
            carrier = terminal.getCarrier();
        }
        if ( null == carrier ) {
            logger.debug("Unknown carrier for file: {} which contains agrn codes: {}", file, agrnCodes);
            handleErrorUpload("file.upload.billingValidateResult.carrier.notFound","",file, new Object[]{file,agrnCodes.size()});
        }
        billingValidateResult.setCarrier(carrier);
        checkExistBillingFiles(billingValidateResult, billingFileFormat,file);
    }

    protected void handleErrorUpload(String errorCode, String errorMessage, File file, Object[] messagesArgs) throws FileUploadException {
        if ( !file.delete() ) logger.error("Error delete error-uploaded file: {}",file);
        logger.error("Occur error during handle file : {}, is '{}'",file, errorMessage);
        throw new FileUploadException(errorCode,messagesArgs,file);
    }

    protected void handleErrorUpload(String errorCode, String errorMessage, File file) throws FileUploadException {
        handleErrorUpload(errorCode, errorMessage, file,new Object[]{file.getName()});
    }


    public BillingFileFormat determineFileFormat(File file) {
        FlrDeserializer deserializer = null;
        Reader reader = null;
        BufferedReader br = null;
        try {
            deserializer = FlrIOFactory.createFactory(BillingFileFormat.getAllClasses()).createDeserializer();
            reader = new FileReader(file);
            br = new BufferedReader(reader);
            deserializer.open(br);
            Object record = deserializer.next();
            if ( record instanceof TFH) return BillingFileFormat.ARC;
            else if ( record instanceof IFH) return BillingFileFormat.BSP;
            return null;
        } catch (Exception e) {
            logger.error("Error determine billing format type for file: " + file,e);
        } finally {
            try {
                if (null != reader) reader.close();
                if (br != null) br.close();
            } catch (IOException ioe) {
                logger.error("Error during closing reader", ioe);
            }
            AbstractBillingConverter.safeCloseDeserializer(deserializer);
        }
        return null;
    }

}
