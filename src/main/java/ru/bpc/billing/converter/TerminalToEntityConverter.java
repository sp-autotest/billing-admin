package ru.bpc.billing.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.CollectionUtils;
import ru.bpc.billing.controller.dto.carrier.dto.TerminalDto;
import ru.bpc.billing.domain.Terminal;
import ru.bpc.billing.service.CarrierService;
import ru.bpc.billing.service.CountryCurrencyService;
import ru.bpc.billing.service.filter.CurrencyFilter;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;

public class TerminalToEntityConverter implements Converter<TerminalDto, Terminal> {
    @Resource
    private CountryCurrencyService currencyService;
    @Resource
    private CarrierService carrierService;
    @Override
    public Terminal convert(TerminalDto terminalDto) {
        if (terminalDto == null) return null;
        Terminal terminal = new Terminal();
        terminal.setName(terminalDto.getName());
        terminal.setAgrn(terminalDto.getAgrn());
        terminal.setTerminal(terminalDto.getTerminal());

        List<Long> currencyIds = terminalDto.getCurrenciesIds();
        if (!CollectionUtils.isEmpty(currencyIds)){
            CurrencyFilter currencyFilter = new CurrencyFilter();
            currencyFilter.setIds(currencyIds);
            terminal.setCurrencies(new HashSet<>(currencyService.get(currencyFilter)));
        }

        if (terminalDto.getCarrierId() != null) {
            terminal.setCarrier(carrierService.get(terminalDto.getCarrierId()));
        }

        return terminal;
    }
}
