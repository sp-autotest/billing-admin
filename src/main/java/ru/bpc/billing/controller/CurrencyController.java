package ru.bpc.billing.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import ru.bpc.billing.controller.dto.carrier.dto.CurrencyDto;
import ru.bpc.billing.controller.dto.carrier.dto.CurrencyFilterDto;
import ru.bpc.billing.controller.dto.carrier.response.CurrenciesResponse;
import ru.bpc.billing.domain.CountryCurrency;
import ru.bpc.billing.service.CountryCurrencyService;
import ru.bpc.billing.service.filter.CurrencyFilter;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/currency")
public class CurrencyController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private CountryCurrencyService countryCurrencyService;

    @Resource
    private ConversionService conversionService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @Transactional
    public CurrenciesResponse get(CurrencyFilterDto filter) throws BindException {
        CurrenciesResponse response = new CurrenciesResponse();
        response.setSuccess(true);
        for (CountryCurrency countryCurrency : countryCurrencyService.get(conversionService.convert(filter, CurrencyFilter.class))) {
            response.getData().add(conversionService.convert(countryCurrency, CurrencyDto.class));
        }
        return response;
    }
}
