package ru.bpc.billing.converter;

import org.springframework.core.convert.converter.Converter;
import ru.bpc.billing.controller.dto.carrier.dto.CurrencyDto;
import ru.bpc.billing.domain.CountryCurrency;

public class CurrencyToDtoConverter implements Converter<CountryCurrency, CurrencyDto> {
    @Override
    public CurrencyDto convert(CountryCurrency currency) {
        if (currency == null) return null;
        CurrencyDto currencyDto = new CurrencyDto();
        currencyDto.setId(currency.getId());
        currencyDto.setCountryCode(currency.getCountryCode());
        currencyDto.setCurrencyNumericCode(currency.getCurrencyNumericCode());
        return currencyDto;
    }
}
