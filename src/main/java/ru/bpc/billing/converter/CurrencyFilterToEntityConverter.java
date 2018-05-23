package ru.bpc.billing.converter;

import org.springframework.core.convert.converter.Converter;
import ru.bpc.billing.controller.dto.carrier.dto.CurrencyFilterDto;
import ru.bpc.billing.service.filter.CurrencyFilter;

public class CurrencyFilterToEntityConverter implements Converter<CurrencyFilterDto, CurrencyFilter> {

    @Override
    public CurrencyFilter convert(CurrencyFilterDto currencyFilterDto) {
        if (currencyFilterDto == null) return null;
        CurrencyFilter currencyFilter = new CurrencyFilter();
        currencyFilter.setIds(currencyFilterDto.getIds());
        return currencyFilter;
    }
}
