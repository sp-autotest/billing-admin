package ru.bpc.billing.service.automate.bo;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.bpc.billing.controller.automate.ServerParametersDto;
import ru.bpc.billing.controller.dto.*;
import ru.bpc.billing.domain.BillingSystem;
import ru.bpc.billing.domain.Carrier;
import ru.bpc.billing.domain.FileType;
import ru.bpc.billing.domain.User;
import ru.bpc.billing.domain.bo.BOFileUploadRequest;
import ru.bpc.billing.domain.report.ReportFile;
import ru.bpc.billing.repository.BOFileRepository;
import ru.bpc.billing.repository.BillingSystemRepository;
import ru.bpc.billing.repository.CarrierRepository;
import ru.bpc.billing.repository.UserRepository;
import ru.bpc.billing.service.ApplicationService;
import ru.bpc.billing.service.SystemSettingsService;
import ru.bpc.billing.service.automate.bo.MailReportBo;
import ru.bpc.billing.service.automate.bo.MailReportUnitBo;
import ru.bpc.billing.service.automate.PostingAndBoServersParametersService;
import ru.bpc.billing.service.automate.controllerService.BOControllerService;
import ru.bpc.billing.service.automate.controllerService.FileControllerService;
import ru.bpc.billing.service.bo.BOService;
import ru.bpc.billing.service.io.SFTPClient;
import ru.bpc.billing.service.mail.Mailer;

import javax.annotation.Resource;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ru.bpc.billing.service.automate.AutomateConstants.SERVER_PARAMS_BO;

@Slf4j
public class DefaultAutomateBoTask implements AutomateBoTask {

    private static final String AUTOMATE_USERNAME = "automate";
    private static final long BO_FILE_DAYS_DIFFER = 2;

    private final ApplicationContext context;
    private final UserRepository userRepository;
    private final SystemSettingsService systemSettingsService;
    private final PostingAndBoServersParametersService parametersService;
    private final Mailer mailer;
    private final BOService boService;
    private final ApplicationService applicationService;
    private final CarrierRepository carrierRepository;
    private final BillingSystemRepository billingSystemRepository;


    private volatile boolean isRunning = false;


    public DefaultAutomateBoTask(ApplicationContext context, UserRepository userRepository,
                                 PostingAndBoServersParametersService parametersService,
                                 BOService boService,
                                 ApplicationService applicationService,
                                 CarrierRepository carrierRepository,
                                 BillingSystemRepository billingSystemRepository,
                                 SystemSettingsService systemSettingsService, Mailer mailer) {
        this.context = context;
        this.userRepository = userRepository;
        this.systemSettingsService = systemSettingsService;
        this.parametersService = parametersService;
        this.mailer = mailer;
        this.boService = boService;
        this.applicationService = applicationService;
        this.carrierRepository = carrierRepository;
        this.billingSystemRepository = billingSystemRepository;
    }

    public void run() {
        isRunning = true;
        log.info("Start automate BO task");
        MailReportBo mailReport = new MailReportBo();
        MailReportUnitBo mailReportUnit = new MailReportUnitBo();

        try {
            log.info("Processing BO task...");
            ServerParametersDto serverParameters = parametersService.getParams(SERVER_PARAMS_BO);
            process(serverParameters, mailReportUnit);
            mailReport.addUnit(mailReportUnit);
        } catch(Exception e) {
            log.error("Error processing bo files!!!", e);
            mailReport.addUnit(mailReportUnit);
        } finally {
            mailer.sendMail(context.getEnvironment().getRequiredProperty("main.mail.sender"),
                    systemSettingsService.getString("mail.esupport"), mailReport.getSubject(),
                    mailReport.getBody(), getReportFiles(mailReportUnit.getAttachmentFiles()), false, "text/plain", "UTF-8");
            mailer.sendMail(context.getEnvironment().getRequiredProperty("main.mail.sender"),
                    systemSettingsService.getString("mail.raschet"), mailReport.getSubject(),
                    mailReport.getBody(), getReportFiles(mailReportUnit.getAttachmentFiles()), false, "text/plain", "UTF-8");
            sendToBS(mailReportUnit, mailReport); /* послать отчет биллинговым сисетмам */
            isRunning = false;
        }

//        try {
//            List<BillingSystem> bsList = billingSystemService.findAllAvailable();
//            log.info("Found billing systems for processing: " + bsList);
//            mailReport.addFoundSystems(bsList);
//            for (BillingSystem bs : bsList) {
//                MailReportUnit mailReportUnit = new MailReportUnit();
//
//                try {
//
//                    if (bs.getEnabled()) {
//                        log.info("Starting process billing system: " + bs);
//                        process(bs, mailReportUnit);
//                        mailReport.addUnit(mailReportUnit);
//                    }
//                } catch (Exception e) {
//                    log.error("Error processing billing system: " + bs, e);
//                    mailReport.addErrorUnit(mailReportUnit);
//
//                }
//            }
//        } finally {
//            mailer.sendMail(context.getEnvironment().getRequiredProperty("main.mail.sender"), systemSettingsService.getString("mail.esupport"), mailReport.getSubject(), mailReport.getBody(), null, false, "text/plain", "UTF-8");
////            log.warn(mailReport.getBody());
//            isRunning = false;
//        }
    }

    private void sendToBS(MailReportUnitBo mailReportUnit, MailReportBo mailReport) {
        List<String> iataList = mailReportUnit.getIataList();
        if(iataList != null)
        for(String iata : iataList) {
            Carrier carrier = carrierRepository.findByIataCode(iata);
            if(carrier != null) {
                List<BillingSystem>  bsList = billingSystemRepository.findAllByCarrier(carrier);
                for(BillingSystem bs : bsList) {
                    Set<String> emails = bs.getEmails();
                    for(String email : emails) {
                        mailer.sendMail(context.getEnvironment().getRequiredProperty("main.mail.sender"),
                                email, mailReport.getSubject(), mailReport.getBody(),
                                getReportFiles(mailReportUnit.getAttachmentFiles()), false, "text/plain", "UTF-8");
                    }
                }
            }

        }
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    private Set<File> getReportFiles(List<ReportFile> dtos) {
        Set<File> result = new HashSet<>();
        if(dtos !=  null)
        for(ReportFile rf : dtos) {
            result.add(new File(applicationService.getHomeDir(FileType.REVENUE_REPORT_EXCEL) + rf.getName()));
        }
    return result;
    }


    private void process(ServerParametersDto sp, MailReportUnitBo mailReportUnit) throws Exception {
        log.info("Connecting to: " + sp.getAddress());
        SFTPClient sftpClient = new SFTPClient(sp.getLogin(), sp.getPassword(), sp.getAddress(), sp.getPort());
        log.info("Connected to: " + sp.getAddress());
        //mailReportUnit.addConnected(bs);
        //log.info("Getting bo file names from directory: " + bs.getPath());
        List<String> fileNames = sftpClient.getFileNames(sp.getPath());
        mailReportUnit.addFileNames(fileNames);
        if (fileNames.isEmpty()) {
            log.info("No bo file names to download, exiting...");
            return;
        } else
            log.info("Got bo file names: " + fileNames);
        List<String> filteredFileNames = checkAndFilter(fileNames);
        mailReportUnit.addFilteredFileNames(filteredFileNames);

        if (filteredFileNames.isEmpty()) {
            log.info("No files to download, exiting...");
            return;
        } else
            log.info("Filtered files: " + filteredFileNames);

        String localPath = context.getEnvironment().getProperty("application.homeDir") + "automate/";
        List<File> downloadedFiles = sftpClient.getFiles(filteredFileNames, sp.getPath(), localPath);
        log.info("Downloaded files: " + downloadedFiles.stream().map(File::getName).collect(Collectors.toList()));
        sftpClient.close();
        mailReportUnit.addDownloaded(downloadedFiles);


//        List<File> decryptedFiles = decrypt(downloadedFiles);
//        log.info("Decrypted files: " + decryptedFiles.stream().map(File::getName).collect(Collectors.toList()));
//
//        mailReportUnit.addDecryptedFiles(decryptedFiles);
//
//        List<BillingFileDto> uploaded = upload(decryptedFiles);

        List<BoDto> uploaded = upload(downloadedFiles);

        log.info("Uploaded files: " + uploaded.stream().map(BoDto::getFileName).collect(Collectors.toList()));

        mailReportUnit.addUploaded(uploaded);

        ReportProcessingResultDto repResult = genReport(uploaded);
        //convert
//        List<BillingConverterResultDto> billingConverterResultDto = convert(uploaded).getBillingConverterResultDtos();
//
//        log.info("Converted files: " + billingConverterResultDto.stream().map(BillingConverterResultDto::getPostings).collect(Collectors.toList()));

        mailReportUnit.addConverted(repResult);
        mailReportUnit.addReportFiles(repResult.reportProcessingResult.getReportFiles());

        mailReportUnit.addIataList(getBSList(repResult.getBillingFiles()));
//
//        uploadAndCheckPostings(billingConverterResultDto);
//        log.info("Uploaded postings to scp: " + billingConverterResultDto.stream().map(BillingConverterResultDto::getPostings).collect(Collectors.toList()));
//        mailReportUnit.addPostingUploaded(billingConverterResultDto);


    }

    private List<String> getBSList(List<BillingFileDto> billingFiles) {
        List<String> result = new ArrayList<>();
        for(BillingFileDto bf : billingFiles) {
            result.add(bf.getIataCode());
        }

        return result;
    }

    private List<String> checkAndFilter(List<String> fileNames) {
        List<String> result = new ArrayList<>();
        BOFileRepository boFileRepository = context.getBean(BOFileRepository.class);
        List<BoDto> alreadyUploadedFiles = getUploadedBo();
        for (String each : fileNames) {

            if (checkMask(each) && checkDate(each) && alreadyUploadedFiles.stream().noneMatch(t -> getOriginalFilename(t.getFileName()).equals(each)))
                result.add(each);
        }
        return result;
    }

    private List<BoDto> getUploadedBo() {
        return boService.getUploadedBo(new IOFileFilter() {
            @Override
            public boolean accept(File file) {
                return true;
            }

            @Override
            public boolean accept(File file, String s) {
                return true;
            }
        });
    }

//    private List<File> decrypt(List<File> source) {
//        String keyFileName = systemSettingsService.getString("automate.ascKeyPath");
//        String keySecret = systemSettingsService.getString("automate.keySecret");
//
//        List<File> resultList = new ArrayList<>();
//        try {
//            PGPFileProcessor pgpFileProcessor = new PGPFileProcessor(keySecret, keyFileName);
//            for (File each : source) {
//                String outputFileName = getOutputFileName(each.getPath());
//                File file = pgpFileProcessor.decrypt(each, outputFileName);
//                resultList.add(file);
//            }
//        } catch (Exception e) {
//            log.error("Error decrypting file, go to next", e);
//        }
//
//        return resultList;
//    }

    private String getOutputFileName(String name) {
        if (name.endsWith(".pgp"))
            return name.substring(0, name.length() - 4);
        else
            return name + "decrypted";
    }

    private String getOriginalFilename(String name) {
        String nameBefore = StringUtils.substringBeforeLast(name,"-");
        String iataCode = StringUtils.substringAfterLast(name, "_");
        return nameBefore + "_" + iataCode;
    }

    private Date todayWithoutTime() {
        Calendar result = Calendar.getInstance();
        result.set(Calendar.HOUR_OF_DAY, 0);
        result.set(Calendar.MINUTE, 0);
        result.set(Calendar.SECOND, 0);
        result.set(Calendar.MILLISECOND, 0);
        return result.getTime();
    }

    private List<BoDto> upload(List<File> files) throws Exception {
        List<BoDto> resultList = new ArrayList<>();
        for (File each : files) {
            try {
                BOControllerService billingController = context.getBean(BOControllerService.class);
                User user = userRepository.findByUsername(AUTOMATE_USERNAME);
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, null);

                BOFileUploadRequest f = new BOFileUploadRequest();
                //f.setBusinessDate(new Date());
                MultipartFile file = new MockMultipartFile(each.getName(), each.getName(), "txt", FileCopyUtils.copyToByteArray(each));
                f.setFile(file);
                BoDto result = billingController.upload(f, token);
                resultList.add(result);
            } catch (Exception e) {
                log.error("Error uploading file: " + each.getName() + ", go to next", e);
            }
        }
        if (resultList.isEmpty())
            throw new Exception("No files uploaded, error");
        return resultList;

    }

    private ReportProcessingResultDto genReport(List<BoDto> boDtos) throws Exception {
        BOControllerService billingController = context.getBean(BOControllerService.class);
        User user = userRepository.findByUsername(AUTOMATE_USERNAME);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, null);

        ReportProcessingResultDto result = billingController.genReport(boDtos, token);
        if (!result.getSuccess())
            throw new Exception();

        return result;
    }

    private boolean checkDate(String fileName) {
        String date = fileName.split("_")[1];
        if(date.length() > 8)
            date = date.substring(0, 8);

        LocalDate fileDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"));
        //return fileDate.equals(LocalDate.now().minusDays(BO_FILE_DAYS_DIFFER));
        return fileDate.equals(LocalDate.now());
    }

    private boolean checkMask(String input) {
        Pattern p = Pattern.compile("(ALL_\\d{8})");
        Matcher m = p.matcher(input);
        return m.find();
    }


    private File getPostingFileByName(String name) throws Exception {
        FileControllerService fileController = context.getBean(FileControllerService.class);
        User user = userRepository.findByUsername(AUTOMATE_USERNAME);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, null);


        File file = fileController.downloadAutomate(name, FileType.POSTING, token);
        return file;
    }
}
