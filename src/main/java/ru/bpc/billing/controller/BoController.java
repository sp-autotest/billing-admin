package ru.bpc.billing.controller;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.bpc.billing.controller.dto.BoDto;
import ru.bpc.billing.domain.User;
import ru.bpc.billing.domain.UserAction;
import ru.bpc.billing.domain.UserHistory;
import ru.bpc.billing.domain.bo.BOFileUploadRequest;
import ru.bpc.billing.exception.FileUploadException;
import ru.bpc.billing.repository.ProcessingFileFilter;
import ru.bpc.billing.repository.UserHistoryRepository;
import ru.bpc.billing.service.bo.BOService;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.Locale;

/**
 * User: Krainov
 * Date: 28.08.14
 * Time: 13:34
 */
@Controller
@RequestMapping(value = "/bo")
public class BoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private BOService boService;
    @Resource
    private UserHistoryRepository userHistoryRepository;
    @Resource
    private MessageSource messageSource;
    @Value("${bo.expiryDays:10}")
    private Integer dayExpiry;

    @RequestMapping(value = "/read")
    public @ResponseBody BoDto read(@RequestParam(value = "node", required = false) String node,
                                    @RequestParam(value = "all", required = false) boolean all) {
        ProcessingFileFilter filter = null;
        if ( all )
            filter = new ProcessingFileFilter(null,null,null);
        else
            filter = new ProcessingFileFilter(null,new Date(System.currentTimeMillis() - DateUtils.MILLIS_PER_DAY * dayExpiry),null);
        return boService.prepareBos(filter);
    }

    @RequestMapping(value = "/upload")
    public @ResponseBody BoDto upload(BOFileUploadRequest boFileUploadRequest, UsernamePasswordAuthenticationToken principal) {
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


    @RequestMapping(value = "/find")
    public @ResponseBody BoDto find(ProcessingFileFilter filter) {
        return boService.prepareBos(filter);
    }

}
