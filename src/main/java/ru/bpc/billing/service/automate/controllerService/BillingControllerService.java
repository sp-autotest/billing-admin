package ru.bpc.billing.service.automate.controllerService;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.bpc.billing.controller.dto.BillingConverterResultDto;
import ru.bpc.billing.controller.dto.BillingFileDto;
import ru.bpc.billing.domain.User;
import ru.bpc.billing.domain.UserAction;
import ru.bpc.billing.domain.UserHistory;
import ru.bpc.billing.domain.billing.BillingFile;
import ru.bpc.billing.domain.billing.BillingFileUploadRequest;
import ru.bpc.billing.exception.FileUploadException;
import ru.bpc.billing.repository.BillingFileRepository;
import ru.bpc.billing.repository.UserHistoryRepository;
import ru.bpc.billing.service.billing.BillingConverterResult;
import ru.bpc.billing.service.billing.BillingService;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * User: Krainov
 * Date: 20.08.14
 * Time: 12:11
 */
@Service
public class BillingControllerService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private BillingService billingService;
    @Resource
    private BillingFileRepository billingFileRepository;
    @Resource
    private UserHistoryRepository userHistoryRepository;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private MessageSource messageSource;
    private TypeReference<List<BillingFileDto>> referenceBilling = new TypeReference<List<BillingFileDto>>() {
    };

    public BillingFileDto upload(BillingFileUploadRequest billingFileUploadRequest, UsernamePasswordAuthenticationToken principal) throws FileUploadException {
        UserHistory userHistory = new UserHistory();
        if (null != principal && principal.getPrincipal() instanceof User) {
            userHistory.setUser((User) principal.getPrincipal());
            userHistory.setAction(UserAction.UPLOAD_BILLING_FILE);
        }
        BillingFileDto billingFileDto = null;
        try {
            BillingFile billingFile = billingService.upload(billingFileUploadRequest);
            billingFileDto = new BillingFileDto(billingFile, true);
        } catch (FileUploadException e) {
//            String messageError = messageSource.getMessage(e.getErrorCode(),
//                    e.getMessageErrorArgs(), "Error upload billing file: " + e.getMessage(), Locale.getDefault());
//            logger.error("Error upload billing file: " + messageError, e);
            billingFileDto = new BillingFileDto(null, false, e.getMessage());
            billingFileDto.setName(billingFileUploadRequest.getFile().getOriginalFilename());
            userHistory.setMessage(e.getMessage());
            throw e;
        }
        if (null != userHistory.getUser()) {
            userHistory.setStatus(billingFileDto.getSuccess());
            userHistoryRepository.save(userHistory);
        }
        return billingFileDto;
    }


    public BillingConverterResultDto convert( List<BillingFileDto> billingDtos, UsernamePasswordAuthenticationToken principal) {
        BillingConverterResultDto billingConverterResultDto = new BillingConverterResultDto();
        billingConverterResultDto.setSuccess(true);
        if (null == billingDtos) {
            billingConverterResultDto.setSuccess(false);
            billingConverterResultDto.setText("Invalid param 'billingFiles'");
            return billingConverterResultDto;
        }
        UserHistory userHistory = new UserHistory();
        if (null != principal && principal.getPrincipal() instanceof User) {
            userHistory.setUser((User) principal.getPrincipal());
            userHistory.setAction(UserAction.CONVERT_BILLING);
        }

        try {
            Set<BillingFile> billingFiles1 = new HashSet<>();
            for (BillingFileDto billingDto : billingDtos) {
                if (null == billingDto) {
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
            if (null != userHistory.getUser()) {
                userHistory.setMessage(billingConverterResultDto.getText());
                userHistory.setStatus(false);
                userHistoryRepository.save(userHistory);
            }
        }
        return billingConverterResultDto;
    }


}
