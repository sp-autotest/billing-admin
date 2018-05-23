package ru.bpc.billing.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.bpc.billing.controller.dto.carrier.dto.CarrierDto;
import ru.bpc.billing.controller.dto.carrier.dto.CarrierFilterDto;
import ru.bpc.billing.controller.dto.carrier.response.CarrierResponse;
import ru.bpc.billing.controller.dto.carrier.response.CarriersResponse;
import ru.bpc.billing.controller.dto.carrier.response.Response;
import ru.bpc.billing.domain.Carrier;
import ru.bpc.billing.service.CarrierService;
import ru.bpc.billing.service.filter.CarrierFilter;
import ru.bpc.billing.validator.CarrierDtoValidator;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(value = "carrier")
public class CarrierController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private CarrierService carrierService;

    @Resource
    private ConversionService conversionService;

    @Resource
    private CarrierDtoValidator carrierDtoValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(carrierDtoValidator);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    @Transactional
    public CarrierResponse get(@PathVariable Long id) {
        CarrierResponse response = new CarrierResponse();
        response.setSuccess(true);
        response.setData(conversionService.convert(carrierService.get(id), CarrierDto.class));
        return response;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @Transactional
    public CarriersResponse get(CarrierFilterDto filter) throws BindException {
        CarriersResponse response = new CarriersResponse();
        response.setSuccess(true);
        List<Carrier> carriers = carrierService.get(conversionService.convert(filter, CarrierFilter.class));
        for (Carrier carrier : carriers) {
            response.getData().add(conversionService.convert(carrier, CarrierDto.class));
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public Response create(@RequestBody @Valid CarrierDto carrierDto) {
        carrierService.create(conversionService.convert(carrierDto, Carrier.class));
        return Response.buildSuccessful();
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    @ResponseBody
    @Transactional
    public Response update(@PathVariable Long id, @RequestBody @Valid CarrierDto carrierDto) {
        carrierService.update(id, conversionService.convert(carrierDto, Carrier.class));
        return Response.buildSuccessful();
    }
}
