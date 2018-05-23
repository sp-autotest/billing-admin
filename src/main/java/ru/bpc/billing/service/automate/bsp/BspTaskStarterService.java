package ru.bpc.billing.service.automate.bsp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import ru.bpc.billing.repository.UserRepository;
import ru.bpc.billing.service.BillingSystemService;
import ru.bpc.billing.service.SystemSettingsService;
import ru.bpc.billing.service.automate.GlobalAutomateService;
import ru.bpc.billing.service.automate.PostingAndBoServersParametersService;
import ru.bpc.billing.service.automate.SchedulingPlanService;
import ru.bpc.billing.service.mail.Mailer;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;

@EnableScheduling
@Service
@Slf4j
public class BspTaskStarterService {

    private ScheduledFuture<?> scheduledFuture;

    private TaskScheduler taskScheduler;

    private AutomateBspTask automateTask;

    private final SchedulingPlanService schedulingService;
    private final ApplicationContext context;
    private final UserRepository userRepository;
    private final BillingSystemService billingSystemService;
    private final PostingAndBoServersParametersService parametersService;
    private final SystemSettingsService systemSettingsService;
    private final Mailer mailer;
    private final GlobalAutomateService globalAutomateService;

    @Autowired
    public BspTaskStarterService(SchedulingPlanService schedulingService, ApplicationContext context, UserRepository userRepository,
                                 BillingSystemService billingSystemService, PostingAndBoServersParametersService parametersService,
                                 SystemSettingsService systemSettingsService, Mailer mailer, GlobalAutomateService globalAutomateService) {
        this.schedulingService = schedulingService;
        this.context = context;
        this.userRepository = userRepository;
        this.systemSettingsService = systemSettingsService;
        this.billingSystemService = billingSystemService;
        this.parametersService = parametersService;
        this.mailer = mailer;
        this.globalAutomateService = globalAutomateService;
    }

    @PostConstruct
    private void initializeScheduler() {
        reSchedule();
    }

    public void reSchedule() {
        if (globalAutomateService.isDisabled())
            return;

        if (taskScheduler == null) {
            taskScheduler = new ConcurrentTaskScheduler(Executors.newSingleThreadScheduledExecutor());
        }

        if (isWorkingInternal()) {
            log.info("Task is running, no rescheduling");
            return;
        }

        String cronExpression = schedulingService.getBspCronExpression();
        if (cronExpression != null) {
            automateTask = new DefaultAutomateBspTask(context, userRepository, billingSystemService, parametersService, systemSettingsService, mailer);
            scheduledFuture = taskScheduler.schedule(automateTask, new CronTrigger(cronExpression));
            log.warn("Rescheduled");
        }
    }

    public boolean isWorking() {
        StringBuilder sb = new StringBuilder();
        boolean isWorking = isWorkingInternal();
        sb.append(isWorking);
        Date nextExecutionTime;
        if ((nextExecutionTime = getNextExecTime()) != null) {
            sb.append(". Next Execution time: ").append(nextExecutionTime);
        }

        log.info("Is working: " + sb.toString());
        return isWorking;
    }

    public void stop() {
        if (scheduledFuture != null) {
            log.warn("Stopping...");
            boolean r = scheduledFuture.cancel(true);
            log.warn("Stopping result: " + r);
        }
    }

    private boolean isWorkingInternal() {
        return scheduledFuture != null && (!scheduledFuture.isDone() && automateTask.isRunning());
    }

    public Date getNextExecTime() {
        if (scheduledFuture != null) {
            String cronExpression = schedulingService.getBspCronExpression();
            return new CronSequenceGenerator(cronExpression).next(new Date());
        }
        return null;
    }

}