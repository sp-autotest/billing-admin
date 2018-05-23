package ru.bpc.billing.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.bpc.billing.domain.FileType;
import ru.bpc.billing.domain.User;
import ru.bpc.billing.domain.UserAction;
import ru.bpc.billing.domain.UserHistory;
import ru.bpc.billing.repository.UserHistoryRepository;
import ru.bpc.billing.service.ApplicationService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * User: Krainov
 * Date: 04.09.2014
 * Time: 17:02
 */
@Controller
@RequestMapping(value = "/file")
public class FileController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private ApplicationService applicationService;
    @Resource
    private UserHistoryRepository userHistoryRepository;

    @RequestMapping(value = "/download")
    public void download(String fileName, FileType fileType, HttpServletResponse response, UsernamePasswordAuthenticationToken principal) {
        UserHistory userHistory = new UserHistory();
        if ( null != principal && principal.getPrincipal() instanceof User ) {
            userHistory.setUser((User)principal.getPrincipal());
        }
        userHistory.setOldValue(fileName);
        userHistory.setNewValue(fileType.name());
        userHistory.setAction(UserAction.DOWNLOAD_FILE);
        userHistory.setStatus(false);
        String homeDir = applicationService.getHomeDir(fileType);
        if ( null != homeDir ) {
            File file = new File(homeDir + fileName);
            if ( file.exists() && file.canRead() ) {
                try {
                    Path path = Paths.get(file.toURI());
                    String contentType = Files.probeContentType(path);
                    if ( null == contentType ) contentType = "application/octet-stream";
                    response.setHeader("Content-Disposition","attachment;filename=" + file.getName());
                    response.setContentLength((int)file.length());
                    response.setContentType(contentType);
                    InputStream is = new FileInputStream(file);
                    org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
                    response.flushBuffer();
                    userHistory.setStatus(true);
                } catch (IOException ex) {
                    logger.info("Error writing file to output stream. Filename was '{}'", fileName, ex);
                    throw new RuntimeException("IOError writing file to output stream");
                }
            }
            else {
                logger.error("Unable to download billing file: {} and fileType: {} from homeDir: {} because file either not exist or cannot read.",file,fileType,homeDir);
            }
        }
        else {
            logger.error("Unable to determine homeDir for fileType: {}",fileType);
        }
        userHistoryRepository.save(userHistory);
    }


    public File downloadAutomate(String fileName, FileType fileType, UsernamePasswordAuthenticationToken principal) throws Exception {
        UserHistory userHistory = new UserHistory();
        if ( null != principal && principal.getPrincipal() instanceof User ) {
            userHistory.setUser((User)principal.getPrincipal());
        }
        userHistory.setOldValue(fileName);
        userHistory.setNewValue(fileType.name());
        userHistory.setAction(UserAction.DOWNLOAD_FILE);
        userHistory.setStatus(false);
        String homeDir = applicationService.getHomeDir(fileType);
        if (null != homeDir) {
            File file = new File(homeDir + fileName);
            if (file.exists() && file.canRead()) {
                return file;
            }
        }
        throw new Exception("unable to download file: " + fileName);
    }

    @ExceptionHandler
    public String exception(HttpServletRequest request, Exception e, UsernamePasswordAuthenticationToken principal) {
        String fileName = request.getParameter("fileName");
        FileType fileType = FileType.valueOf(request.getParameter("fileType"));
        UserHistory userHistory = new UserHistory();
        if ( null != principal && principal.getPrincipal() instanceof User ) {
            userHistory.setUser((User)principal.getPrincipal());
        }
        userHistory.setOldValue(fileName);
        if ( null != fileType ) userHistory.setNewValue(fileType.name());
        userHistory.setAction(UserAction.DOWNLOAD_FILE);
        userHistory.setStatus(false);
        userHistory.setMessage(e.getMessage());
        userHistoryRepository.save(userHistory);
        logger.error("Error download file: " + fileName + " and fileType: " + fileType + ". Redirect to index.html",e);
        return "redirect:/index.html";
    }
}
