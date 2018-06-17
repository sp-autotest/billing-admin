package ru.bpc.billing.controller.automate.bo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.bpc.billing.service.automate.bo.BoTaskStarterService;

import javax.annotation.Resource;


@Controller
@RequestMapping(value = "/automate")
public class BoTaskStatusController {

    @Resource
    private BoTaskStarterService boTaskStarterService;

    @RequestMapping(value = "/boTask", method = RequestMethod.GET)
    @ResponseBody
    public String getStatus() {
        return boTaskStarterService.isWorking() ? "running" : "stopped";
    }

    @RequestMapping(value = "/boTask/stop", method = RequestMethod.GET)
    @ResponseBody
    public String setStop() {
//        boTaskStarterService.stop();
        return "ok";
    }


}
