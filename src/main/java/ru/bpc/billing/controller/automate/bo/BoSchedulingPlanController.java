package ru.bpc.billing.controller.automate.bo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.bpc.billing.controller.dto.SchedulingDto;
import ru.bpc.billing.service.automate.SchedulingPlanService;
import ru.bpc.billing.service.automate.bo.BoTaskStarterService;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;


@Controller
@RequestMapping(value = "/automate")
public class BoSchedulingPlanController {

    @Resource
    private SchedulingPlanService schedulingService;
    @Resource
    private BoTaskStarterService boTaskStarterService;

    @RequestMapping(value = "/schedulingPlanBo", method = RequestMethod.GET)
    @ResponseBody
    public SchedulingDto getSchedulingPlanBo() {
        String  cronExpression = schedulingService.getBoSchedulingPlan();
        Date nextExecTime = boTaskStarterService.getNextExecTime();
        String nextExecTimeString;

        if (nextExecTime == null)
            nextExecTimeString = "";
        else
            nextExecTimeString = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd").format(nextExecTime);

        return new SchedulingDto(cronExpression, nextExecTimeString);
    }

    @RequestMapping(value = "/schedulingPlanBo", method = RequestMethod.POST)
    @ResponseBody
    public String setSchedulingPlanBo(@RequestBody SchedulingDto dto) {
        try {
            schedulingService.setBoSchedulingPlan(dto);
            boTaskStarterService.stop();
            boTaskStarterService.reSchedule();
        } catch (Exception e) {
            return e.getMessage();
        }
        return "ok";
    }
}
