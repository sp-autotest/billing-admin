package ru.bpc.billing.controller.automate.bo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.bpc.billing.controller.dto.SchedulingDto;
import ru.bpc.billing.service.automate.SchedulingPlanService;

import javax.annotation.Resource;


@Controller
@RequestMapping(value = "/automate")
public class BoSchedulingPlanController {

    @Resource
    private SchedulingPlanService schedulingService;

    @RequestMapping(value = "/schedulingPlanBo", method = RequestMethod.GET)
    @ResponseBody
    public SchedulingDto getSchedulingPlanBo() {
        return schedulingService.getBoSchedulingPlan();
    }

    @RequestMapping(value = "/schedulingPlanBo", method = RequestMethod.POST)
    @ResponseBody
    public String setSchedulingPlanBo(@RequestBody SchedulingDto dto) {
        try {
            schedulingService.setBoSchedulingPlan(dto);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "ok";
    }
}
