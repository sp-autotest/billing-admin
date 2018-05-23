package ru.bpc.billing.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bpc.billing.controller.dto.TicketDto;
import ru.bpc.billing.domain.ProcessingFile;
import ru.bpc.billing.domain.ProcessingRecord;
import ru.bpc.billing.repository.ProcessingRecordFilter;
import ru.bpc.billing.repository.ProcessingRecordRepository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Krainov
 * Date: 08.09.2014
 * Time: 13:54
 */
@Service
public class ProcessingRecordService {

    @Resource
    private ProcessingRecordRepository processingRecordRepository;

    @Transactional
    public TicketDto prepareTickets(ProcessingRecordFilter filter) {
        List<TicketDto> children = new ArrayList<>();
        Iterable<ProcessingRecord> it = null;
        if ( null != filter.getPage() && null != filter.getSize() ) {
            it = processingRecordRepository.findAll(filter, new PageRequest(filter.getPage(),filter.getSize()));
        }
        else {
            it = processingRecordRepository.findAll(filter);
        }

        for (ProcessingRecord record : it) {
            TicketDto ticketDto = new TicketDto();
            ticketDto.setProcessingRecord(record);
            ticketDto.setChildren(new ArrayList<TicketDto>());
            for (ProcessingFile processingFile : record.getFiles()) {
                TicketDto ticketFile = new TicketDto();
                ticketFile.setId(processingFile.getFileType() + ":" + processingFile.getId());
                ticketFile.setFileType(processingFile.getFileType());
                ticketFile.setFileName(processingFile.getName());
                ticketDto.getChildren().add(ticketFile);
            }
            children.add(ticketDto);
        }
        TicketDto ticket = new TicketDto();
        ticket.setText(".");
        ticket.setChildren(children);
        ticket.setSuccess(true);
        return ticket;
    }
}
