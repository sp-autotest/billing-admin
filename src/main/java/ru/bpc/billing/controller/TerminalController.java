package ru.bpc.billing.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.bpc.billing.controller.dto.carrier.dto.TerminalDto;
import ru.bpc.billing.controller.dto.carrier.dto.TerminalFilterDto;
import ru.bpc.billing.controller.dto.carrier.response.Response;
import ru.bpc.billing.controller.dto.carrier.response.TerminalsResponse;
import ru.bpc.billing.domain.Terminal;
import ru.bpc.billing.service.TerminalService;
import ru.bpc.billing.service.filter.TerminalFilter;
import ru.bpc.billing.validator.TerminalDtoValidator;

import javax.annotation.Resource;
import javax.validation.Valid;

@Controller
@RequestMapping(value = "/terminal")
public class TerminalController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private TerminalService terminalService;

    @Resource
    private ConversionService conversionService;

    @Resource
    private TerminalDtoValidator terminalDtoValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(terminalDtoValidator);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @Transactional
    public TerminalsResponse get(TerminalFilterDto filter) throws BindException {
        TerminalsResponse response = new TerminalsResponse();
        response.setSuccess(true);
        for (Terminal terminal : terminalService.get(conversionService.convert(filter, TerminalFilter.class))) {
            response.getData().add(conversionService.convert(terminal, TerminalDto.class));
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public Response create(@RequestBody @Valid TerminalDto terminalDto) {
        terminalService.create(conversionService.convert(terminalDto, Terminal.class));
        return Response.buildSuccessful();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    @Transactional
    public Response update(@PathVariable Long id, @RequestBody @Valid TerminalDto terminalDto) {
        terminalService.update(id, conversionService.convert(terminalDto, Terminal.class));
        return Response.buildSuccessful();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @Transactional
    public Response delete(@PathVariable Long id) {
        terminalService.delete(id);
        return Response.buildSuccessful();
    }
}
