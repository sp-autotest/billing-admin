package ru.bpc.billing.service.automate.bsp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.bpc.billing.controller.automate.ServerParametersDto;
import ru.bpc.billing.controller.dto.BillingConverterResultDto;
import ru.bpc.billing.controller.dto.BillingFileDto;
import ru.bpc.billing.controller.dto.PostingDto;
import ru.bpc.billing.domain.BillingSystem;
import ru.bpc.billing.domain.FileType;
import ru.bpc.billing.domain.User;
import ru.bpc.billing.domain.billing.BillingFile;
import ru.bpc.billing.domain.billing.BillingFileUploadRequest;
import ru.bpc.billing.repository.BillingFileRepository;
import ru.bpc.billing.repository.UserRepository;
import ru.bpc.billing.service.BillingSystemService;
import ru.bpc.billing.service.SystemSettingsService;
import ru.bpc.billing.service.automate.MailReport;
import ru.bpc.billing.service.automate.MailReportUnit;
import ru.bpc.billing.service.automate.PostingAndBoServersParametersService;
import ru.bpc.billing.service.automate.controllerService.BillingControllerService;
import ru.bpc.billing.service.automate.controllerService.FileControllerService;
import ru.bpc.billing.service.io.SCPService;
import ru.bpc.billing.service.io.SFTPClient;
import ru.bpc.billing.service.mail.Mailer;
import ru.bpc.billing.service.pgp.PGPFileProcessor;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ru.bpc.billing.service.automate.AutomateConstants.SERVER_PARAMS_POSTING;

@Slf4j
public class DefaultAutomateBspTask implements AutomateBspTask {

    private static final String AUTOMATE_USERNAME = "automate";

    private final ApplicationContext context;
    private final UserRepository userRepository;
    private final BillingSystemService billingSystemService;
    private final PostingAndBoServersParametersService parametersService;
    private final SystemSettingsService systemSettingsService;
    private final Mailer mailer;


    private volatile boolean isRunning = false;


    public DefaultAutomateBspTask(ApplicationContext context, UserRepository userRepository, BillingSystemService billingSystemService, PostingAndBoServersParametersService parametersService, SystemSettingsService systemSettingsService, Mailer mailer) {
        this.context = context;
        this.userRepository = userRepository;
        this.billingSystemService = billingSystemService;
        this.parametersService = parametersService;
        this.systemSettingsService = systemSettingsService;
        this.mailer = mailer;
    }

    public void run() {
        isRunning = true;
        log.info("Start automate BSP task");
        MailReport mailReport = new MailReport();

        try {
            List<BillingSystem> bsList = billingSystemService.findAllAvailable();
            log.info("Found billing systems for processing: " + bsList);
            mailReport.addFoundSystems(bsList);
            for (BillingSystem bs : bsList) {
                MailReportUnit mailReportUnit = new MailReportUnit();

                try {

                    if (bs.getEnabled()) {
                        log.info("Starting process billing system: " + bs);
                        process(bs, mailReportUnit);
                        mailReport.addUnit(mailReportUnit);
                    }
                } catch (Exception e) {
                    log.error("Error processing billing system: " + bs, e);
                    mailReport.addErrorUnit(mailReportUnit);

                }
            }
        } finally {
            mailer.sendMail(context.getEnvironment().getRequiredProperty("main.mail.sender"), systemSettingsService.getString("mail.esupport"), mailReport.getSubject(), mailReport.getBody(), null, false, "text/plain", "UTF-8");
//            log.warn(mailReport.getBody());
            isRunning = false;
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }


    private void process(BillingSystem bs, MailReportUnit mailReportUnit) throws Exception {
        log.info("Connecting to: " + bs.getHostAddress());
        SFTPClient sftpClient = new SFTPClient(bs.getLogin(), bs.getPassword(), bs.getHostAddress(), bs.getSftpPort());
        log.info("Connected to: " + bs.getHostAddress());
        mailReportUnit.addConnected(bs);
        log.info("Getting file names from directory: " + bs.getPath());
        List<String> fileNames = sftpClient.getFileNames(bs.getPath());
        mailReportUnit.addFileNames(fileNames);
        if (fileNames.isEmpty()) {
            log.info("No file names to download, exiting...");
            return;
        } else
            log.info("Got file names: " + fileNames);
        List<String> filteredFileNames = checkAndFilter(fileNames, bs);
        mailReportUnit.addFilteredFileNames(filteredFileNames);

        if (filteredFileNames.isEmpty()) {
            log.info("No files to download, exiting...");
            return;
        } else
            log.info("Filtered files: " + filteredFileNames);

        String localPath = context.getEnvironment().getProperty("application.homeDir") + "automate/";
        List<File> downloadedFiles = sftpClient.getFiles(filteredFileNames, bs.getPath(), localPath);
        log.info("Downloaded files: " + downloadedFiles.stream().map(File::getName).collect(Collectors.toList()));
        sftpClient.close();
        mailReportUnit.addDownloaded(downloadedFiles);


        List<File> decryptedFiles = decrypt(downloadedFiles);
        log.info("Decrypted files: " + decryptedFiles.stream().map(File::getName).collect(Collectors.toList()));

        mailReportUnit.addDecryptedFiles(decryptedFiles);

        List<BillingFileDto> uploaded = upload(decryptedFiles);
        log.info("Uploaded files: " + uploaded.stream().map(BillingFileDto::getName).collect(Collectors.toList()));

        mailReportUnit.addUploaded(uploaded);

        List<BillingConverterResultDto> billingConverterResultDto = convert(uploaded).getBillingConverterResultDtos();
        log.info("Converted files: " + billingConverterResultDto.stream().map(BillingConverterResultDto::getPostings).collect(Collectors.toList()));
        mailReportUnit.addConverted(billingConverterResultDto);

        uploadAndCheckPostings(billingConverterResultDto);
        log.info("Uploaded postings to scp: " + billingConverterResultDto.stream().map(BillingConverterResultDto::getPostings).collect(Collectors.toList()));
        mailReportUnit.addPostingUploaded(billingConverterResultDto);

    }

    private void uploadAndCheckPostings(List<BillingConverterResultDto> dtos) throws Exception {
        for (BillingConverterResultDto dto : dtos) {
            for (PostingDto postingDto : dto.getPostings()) {
                File file = getPostingFileByName(postingDto.getName());
                uploadAndCheckPosting(file);
            }
        }
    }

    private void uploadAndCheckPosting(File file) throws Exception {
        ServerParametersDto serverParameters = parametersService.getParams(SERVER_PARAMS_POSTING);
        SCPService scpService = new SCPService(serverParameters.getAddress(), serverParameters.getPort(), serverParameters.getPath(), serverParameters.getLogin(), serverParameters.getPassword());
        scpService.sendFile(file);


        File fileReceived = scpService.getFile(file.getName());
        long fileReceivedLength = fileReceived.length();
        fileReceived.delete();

        if (fileReceivedLength != file.length()) {
            log.error("Posting file broken: " + file);
            throw new Exception("Error uploading posting: " + file.getName() + " to scp: " + serverParameters);
        }
    }

    private List<String> checkAndFilter(List<String> fileNames, BillingSystem bs) {
        List<String> result = new ArrayList<>();
        BillingFileRepository billingFileRepository = context.getBean(BillingFileRepository.class);
        List<BillingFile> alreadyUploadedFiles = billingFileRepository.findAllByBusinessDate(todayWithoutTime());
        for (String each : fileNames) {

            if (checkMask(each, bs) && checkDate(each) && alreadyUploadedFiles.stream().noneMatch(t -> t.getOriginalFileName().equals(getOutputFileName(each))))
                result.add(each);
        }
        return result;
    }

    private List<File> decrypt(List<File> source) {
        String keyFileName = systemSettingsService.getString("automate.ascKeyPath");
        String keySecret = systemSettingsService.getString("automate.keySecret");

        List<File> resultList = new ArrayList<>();
        try {
            PGPFileProcessor pgpFileProcessor = new PGPFileProcessor(keySecret, keyFileName);
            for (File each : source) {
                String outputFileName = getOutputFileName(each.getPath());
                File file = pgpFileProcessor.decrypt(each, outputFileName);
                resultList.add(file);
            }
        } catch (Exception e) {
            log.error("Error decrypting file, go to next", e);
        }

        return resultList;
    }

    private String getOutputFileName(String name) {
        if (name.endsWith(".pgp"))
            return name.substring(0, name.length() - 4);
        else
            return name + "decrypted";
    }

    private Date todayWithoutTime() {
        Calendar result = Calendar.getInstance();
        result.set(Calendar.HOUR_OF_DAY, 0);
        result.set(Calendar.MINUTE, 0);
        result.set(Calendar.SECOND, 0);
        result.set(Calendar.MILLISECOND, 0);
        return result.getTime();
    }

    private List<BillingFileDto> upload(List<File> files) throws Exception {
        List<BillingFileDto> resultList = new ArrayList<>();
        for (File each : files) {
            try {
                BillingControllerService billingController = context.getBean(BillingControllerService.class);
                User user = userRepository.findByUsername(AUTOMATE_USERNAME);
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, null);

                BillingFileUploadRequest f = new BillingFileUploadRequest();
                f.setBusinessDate(new Date());
                MultipartFile file = new MockMultipartFile(each.getName(), each.getName(), "txt", FileCopyUtils.copyToByteArray(each));
                f.setFile(file);
                BillingFileDto result = billingController.upload(f, token);
                resultList.add(result);
            } catch (Exception e) {
                log.error("Error uploading file: " + each.getName() + ", go to next", e);
            }
        }
        if (resultList.isEmpty())
            throw new Exception("No files uploaded, error");
        return resultList;

    }

    private boolean checkDate(String fileName) {
        String date = fileName.split("_")[2];
        LocalDate fileDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"));
        return fileDate.equals(LocalDate.now());
    }

    private boolean checkMask(String input, BillingSystem bs) {
//        Pattern p = Pattern.compile("(alphabank_[A-Z]{2}_\\d{8}_\\d{4})");
        Pattern p = Pattern.compile(bs.getMaskRegexp());

//        input = "alphabank_SU_20170217_0001";
        Matcher m = p.matcher(input);
        return m.find();
    }


    private BillingConverterResultDto convert(List<BillingFileDto> billingDtos) throws Exception {
        BillingControllerService billingController = context.getBean(BillingControllerService.class);
        User user = userRepository.findByUsername(AUTOMATE_USERNAME);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, null);

        BillingConverterResultDto result = billingController.convert(billingDtos, token);
        if (!result.getSuccess())
            throw new Exception();

        return result;
    }

    private File getPostingFileByName(String name) throws Exception {
        FileControllerService fileController = context.getBean(FileControllerService.class);
        User user = userRepository.findByUsername(AUTOMATE_USERNAME);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, null);


        File file = fileController.downloadAutomate(name, FileType.POSTING, token);
        return file;
    }
}
