package ru.bpc.billing.controller;

import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import ru.bpc.billing.controller.dto.carrier.dto.BillingSystemDto;
import ru.bpc.billing.controller.dto.carrier.dto.CarrierFilterDto;
import ru.bpc.billing.controller.dto.carrier.response.*;
import ru.bpc.billing.domain.BillingSystem;
import ru.bpc.billing.service.BillingSystemService;

import javax.annotation.Resource;
import javax.validation.Valid;

@Controller
@RequestMapping(value = "billingSystem")
public class BillingSystemController {

    @Resource
    private ConversionService conversionService;

    @Resource
    private BillingSystemService billingSystemService;

    @Resource
    private BillingSystemValidator billingSystemValidator;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    @Transactional
    public BillingSystemResponse get(@PathVariable Long id) {
        BillingSystemResponse response = new BillingSystemResponse();
        response.setSuccess(true);
        response.setData(conversionService.convert(billingSystemService.findOne(id), BillingSystemDto.class));
        return response;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @Transactional
    public BillingSystemsResponse get() throws BindException {
        BillingSystemsResponse response = new BillingSystemsResponse();
        response.setSuccess(true);
        for (BillingSystem each : billingSystemService.findAll()) {
            response.getData().add(conversionService.convert(each, BillingSystemDto.class));
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public Response create(@RequestBody @Valid BillingSystemDto dto) throws Exception {
        billingSystemValidator.validateDto(dto);
        billingSystemService.create(conversionService.convert(dto, BillingSystem.class));
        return Response.buildSuccessful();
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    @ResponseBody
    @Transactional
    public Response update(@PathVariable Long id, @RequestBody @Valid BillingSystemDto dto) throws Exception {
        billingSystemValidator.validateDto(dto);
        billingSystemService.update(id, conversionService.convert(dto, BillingSystem.class));
        return Response.buildSuccessful();
    }
}
