package ru.bpc.billing.service.automate.bo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import ru.bpc.billing.service.automate.GlobalAutomateService;
import ru.bpc.billing.service.automate.SchedulingPlanService;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;

//@EnableScheduling
//@Service
@Slf4j
public class BoTaskStarterService {



}