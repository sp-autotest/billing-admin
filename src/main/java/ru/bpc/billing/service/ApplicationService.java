package ru.bpc.billing.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.bpc.billing.domain.FileType;
import ru.bpc.billing.exception.FileUploadException;

import javax.annotation.PostConstruct;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class ApplicationService {

    public static final String BILLING_DIR = "billing" + File.separator;
    public static final String BILLING_LOG_DIR = BILLING_DIR + "log" + File.separator;
    public static final String BILLING_UPLOAD_DIR = BILLING_DIR + "upload" + File.separator;
    public static final String POSTING_DIR = "posting" + File.separator;
    public static final String POSTING_LOG_DIR = "log" + File.separator;
    public static final String REPORT_DIR = "report" + File.separator;
    public static final String REVENUE_LOG_DIR = "log" + File.separator;
    public static final String REVENUE_BO_DIR = "bo" + File.separator;
    public static final String BO_DIR = "bo" + File.separator;
    public static final String BO_LOG_DIR = BO_DIR + "log" + File.separator;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${application.homeDir}")
    private String applicationHomeDir;
    private final Map<FileType,String> homeDirs = new HashMap<>();

    @PostConstruct
    public void init() {
        for (FileType fileType : FileType.values()) {
            homeDirs.put(fileType,initHomeDir(fileType));
        }
    }
    public String getBillingUploadHomeDir() {
        return applicationHomeDir + BILLING_UPLOAD_DIR;
    }

    protected String getBillingHomeDir() {
        return applicationHomeDir + BILLING_DIR;
    }
    protected String getBillingLogHomeDir() {
        return applicationHomeDir + BILLING_LOG_DIR;
    }
    protected String getBOHomeDir() {
        return applicationHomeDir + BO_DIR;
    }
    protected String getBOLogHomeDir() {
        return applicationHomeDir + BO_LOG_DIR;
    }
    protected String getPostingHomeDir() {
        return applicationHomeDir + POSTING_DIR;
    }
    protected String getPostingLogHomeDir() {
        return applicationHomeDir + POSTING_LOG_DIR;
    }
    protected String getReportDir() {
        return applicationHomeDir + REPORT_DIR;
    }

    protected String initHomeDir(FileType fileType) {
        switch (fileType ) {
            case BILLING: return getBillingHomeDir();
            case BILLING_LOG: return getBillingLogHomeDir();
            case BO_LOG: return getBOLogHomeDir();
            case BO: return getBOHomeDir();
            case BO_REVENUE_LOG: return getBOLogHomeDir();
            case BO_REVENUE_SUCCESS: return getBOHomeDir();
            case BO_REVENUE_REJECT: return getBOHomeDir();
            case POSTING: return getPostingHomeDir();
            case REVENUE_REPORT_EXCEL: return getReportDir();
            case REVENUE_REPORT_XML_ACCELYA: return getReportDir();
            case OPERATION_REGISTER: return getReportDir();
            case REVENUE_REPORT_EXCEL_NSPC: return getReportDir();
            case OPERATION_REGISTER_NSPC: return getReportDir();
        }
        logger.error("Unknown file type: {}, we can't get home dir",fileType);
        return null;
    }

    public String getHomeDir(FileType fileType) {
        return homeDirs.get(fileType);
    }

    public Path getHomePath(FileType fileType) {
        return Paths.get(getHomeDir(fileType));
    }

    public File uploadFile(String homeDir, MultipartFile multipartFile, Function<MultipartFile, String> nameFunction) throws FileUploadException {
        if ( !multipartFile.isEmpty() ) {
            String fullFilename = nameFunction.apply(multipartFile);
            File file = new File(homeDir + fullFilename);
            try {
                if ( !file.createNewFile() )
                    throw new FileUploadException("file.upload.createNewFile",new Object[]{file.getName()},file);

                byte[] bytes = multipartFile.getBytes();

                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
                stream.write(bytes);
                stream.close();

                return file;
            } catch (FileUploadException e) {
                throw e;
            } catch (Exception e) {
                throw new FileUploadException(e,"file.upload.error",new Object[]{file.getName()},file);
            }
        }
        throw new FileUploadException("file.upload.empty");
    }


}
