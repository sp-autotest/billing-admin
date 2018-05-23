package ru.bpc.billing.controller.automate.bsp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.bpc.billing.service.automate.bsp.BspTaskStarterService;

import javax.annotation.Resource;


@Controller
@RequestMapping(value = "/automate")
public class BspTaskStatusController {

    @Resource
    private BspTaskStarterService bspTaskStarterService;

    @RequestMapping(value = "/bspTask", method = RequestMethod.GET)
    @ResponseBody
    public String getStatus() {
        return bspTaskStarterService.isWorking() ? "running" : "stopped";
    }

    @RequestMapping(value = "/bspTask/stop", method = RequestMethod.GET)
    @ResponseBody
    public String setStop() {
        bspTaskStarterService.stop();
        return "ok";
    }

}
