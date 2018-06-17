package ru.bpc.billing.service.automate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Service;
import ru.bpc.billing.controller.dto.SchedulingDto;
import ru.bpc.billing.service.SystemSettingsService;
import ru.bpc.billing.service.automate.bo.BoTaskStarterService;
import ru.bpc.billing.service.automate.bsp.BspTaskStarterService;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

import static ru.bpc.billing.service.automate.AutomateConstants.SCHEDULING_CRON_BO;
import static ru.bpc.billing.service.automate.AutomateConstants.SCHEDULING_CRON_BSP;

@Service
@Slf4j
public class SchedulingPlanService {

    @Resource
    private SystemSettingsService systemSettingsService;

    public String getBspCronExpression() {
        return systemSettingsService.getString(SCHEDULING_CRON_BSP, "15 30 23 * * *");
    }

    public String getBoCronExpression() {
        return systemSettingsService.getString(SCHEDULING_CRON_BO, "30 40 23 * * *");
    }

    public void setBspSchedulingPlan(SchedulingDto dto) throws Exception {
        String cronExpressionNew = dto.getSchedulingCronExpression();
        try {
            new CronSequenceGenerator(cronExpressionNew);
        } catch (IllegalArgumentException e) {
            throw new Exception(e.getMessage());
        }
        String oldValue = systemSettingsService.getString(SCHEDULING_CRON_BSP, "15 30 23 * * *");
        if (!oldValue.equals(cronExpressionNew)) {
            log.info("Scheduling bsp plan changed, stopping current and rescheduling...");
            systemSettingsService.update(SCHEDULING_CRON_BSP, cronExpressionNew);

        }
    }

    public String getBoSchedulingPlan() {
        return systemSettingsService.getString(SCHEDULING_CRON_BO, "15 30 23 * * *");
    }

    public void setBoSchedulingPlan(SchedulingDto dto) throws Exception {
        String cronExpressionNew = dto.getSchedulingCronExpression();
        try {
            new CronSequenceGenerator(cronExpressionNew);
        } catch (IllegalArgumentException e) {
            throw new Exception(e.getMessage());
        }
        String oldValue = systemSettingsService.getString(SCHEDULING_CRON_BO, "15 30 23 * * *");
        if (!oldValue.equals(cronExpressionNew)) {
            log.info("Scheduling bo plan changed, stopping current and rescheduling...");
            systemSettingsService.update(SCHEDULING_CRON_BO, cronExpressionNew);
        }
    }
}
