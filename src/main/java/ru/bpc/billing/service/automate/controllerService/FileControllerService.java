package ru.bpc.billing.service.automate.controllerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.bpc.billing.domain.FileType;
import ru.bpc.billing.domain.User;
import ru.bpc.billing.domain.UserAction;
import ru.bpc.billing.domain.UserHistory;
import ru.bpc.billing.repository.UserHistoryRepository;
import ru.bpc.billing.service.ApplicationService;

import javax.annotation.Resource;
import java.io.File;

/**
 * User: Krainov
 * Date: 04.09.2014
 * Time: 17:02
 */
@Service
public class FileControllerService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private ApplicationService applicationService;
    @Resource
    private UserHistoryRepository userHistoryRepository;



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
                userHistoryRepository.save(userHistory);
                return file;
            }
        }
        throw new Exception("unable to download file: " + fileName);
    }


}
