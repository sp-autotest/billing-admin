package ru.bpc.billing.converter;

import org.springframework.core.convert.converter.Converter;
import ru.bpc.billing.controller.dto.carrier.dto.TerminalDto;
import ru.bpc.billing.domain.Terminal;
import ru.bpc.billing.domain.CountryCurrency;

public class TerminalToDtoConverter implements Converter<Terminal, TerminalDto> {
    @Override
    public TerminalDto convert(Terminal terminal) {
        if (terminal == null) return null;
        TerminalDto terminalDto = new TerminalDto();
        terminalDto.setId(terminal.getId());
        terminalDto.setName(terminal.getName());
        terminalDto.setAgrn(terminal.getAgrn());
        terminalDto.setTerminal(terminal.getTerminal());
        terminalDto.setCarrierId(terminal.getCarrier().getId());
        for (CountryCurrency currency : terminal.getCurrencies()) {
            terminalDto.getCurrenciesIds().add(currency.getId());
        }
        return terminalDto;
    }
}
