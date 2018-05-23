package ru.bpc.billing.controller.automate.bsp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.bpc.billing.controller.dto.SchedulingDto;
import ru.bpc.billing.service.automate.SchedulingPlanService;
import ru.bpc.billing.service.automate.bsp.BspTaskStarterService;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;


@Controller
@RequestMapping(value = "/automate")
public class BspSchedulingPlanController {

    @Resource
    private SchedulingPlanService schedulingService;
    @Resource
    private BspTaskStarterService bspTaskStarterService;

    @RequestMapping(value = "/schedulingPlanBsp", method = RequestMethod.GET)
    @ResponseBody
    public SchedulingDto getSchedulingPlanBsp() {


        String cronExpression = schedulingService.getBspCronExpression();

        Date nextExecTime = bspTaskStarterService.getNextExecTime();
        String nextExecTimeString;

        if (nextExecTime == null)
            nextExecTimeString = "";
        else
            nextExecTimeString = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd").format(nextExecTime);

        return new SchedulingDto(cronExpression, nextExecTimeString);
    }

    @RequestMapping(value = "/schedulingPlanBsp", method = RequestMethod.POST)
    @ResponseBody
    public String setSchedulingPlanBsp(@RequestBody SchedulingDto dto) {
        try {
            schedulingService.setBspSchedulingPlan(dto);
            bspTaskStarterService.stop();
            bspTaskStarterService.reSchedule();
        } catch (Exception e) {
            return e.getMessage();
        }
        return "ok";
    }
}
