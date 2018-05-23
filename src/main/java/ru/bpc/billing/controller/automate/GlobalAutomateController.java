package ru.bpc.billing.controller.automate;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.bpc.billing.service.SystemSettingsService;
import ru.bpc.billing.service.automate.GlobalAutomateService;

import javax.annotation.Resource;

import static ru.bpc.billing.service.automate.AutomateConstants.AUTOMATE_ENABLED_GLOBAL;


@Controller
@RequestMapping(value = "/automate")
public class GlobalAutomateController {

    @Resource
    private GlobalAutomateService globalAutomateService;

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    @ResponseBody
    public String getStatus() {
        return globalAutomateService.getSchedulingStatusString();
    }

}