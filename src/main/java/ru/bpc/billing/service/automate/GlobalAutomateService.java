package ru.bpc.billing.service.automate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.bpc.billing.service.SystemSettingsService;

import javax.annotation.Resource;

import static ru.bpc.billing.service.automate.AutomateConstants.AUTOMATE_ENABLED_GLOBAL;

@Service
@Slf4j
public class GlobalAutomateService {

    @Resource
    private SystemSettingsService systemSettingsService;

    public String getSchedulingStatusString() {
        return systemSettingsService.getBoolean(AUTOMATE_ENABLED_GLOBAL, false) ? "automatization enabled" : "automatization disabled";
    }

    public boolean isDisabled() {
        return !systemSettingsService.getBoolean(AUTOMATE_ENABLED_GLOBAL, false);
    }

}
