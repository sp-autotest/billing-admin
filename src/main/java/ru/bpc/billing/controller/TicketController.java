package ru.bpc.billing.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.bpc.billing.controller.dto.TicketDto;
import ru.bpc.billing.repository.ProcessingRecordFilter;
import ru.bpc.billing.repository.ProcessingRecordRepository;
import ru.bpc.billing.service.ApplicationService;
import ru.bpc.billing.service.ProcessingRecordService;

import javax.annotation.Resource;

/**
 * User: Krainov
 * Date: 04.09.2014
 * Time: 17:59
 */
@Controller
@RequestMapping(value = "/ticket")
public class TicketController {

    @Resource
    private ApplicationService applicationService;
    @Resource
    private ProcessingRecordRepository processingRecordRepository;
    @Resource
    private ProcessingRecordService processingRecordService;

    @RequestMapping(value = "/find")
    public @ResponseBody TicketDto find(ProcessingRecordFilter filter) {
//        filter.setPage(1);
//        filter.setSize(100);
        return prepareTickets(filter);
    }

    @RequestMapping(value = "/read")
    public @ResponseBody TicketDto read() {
        ProcessingRecordFilter filter = new ProcessingRecordFilter();
        filter.setPage(1);
        filter.setSize(100);
        return prepareTickets(filter);
    }

    private TicketDto prepareTickets(ProcessingRecordFilter filter) {
        return processingRecordService.prepareTickets(filter);
    }
}
