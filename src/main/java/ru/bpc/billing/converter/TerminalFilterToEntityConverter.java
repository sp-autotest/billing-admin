package ru.bpc.billing.converter;

import org.springframework.core.convert.converter.Converter;
import ru.bpc.billing.controller.dto.carrier.dto.TerminalFilterDto;
import ru.bpc.billing.service.filter.TerminalFilter;

public class TerminalFilterToEntityConverter implements Converter<TerminalFilterDto, TerminalFilter> {

    @Override
    public TerminalFilter convert(TerminalFilterDto terminalFilterDto) {
        if (terminalFilterDto == null) return null;
        TerminalFilter terminalFilter = new TerminalFilter();
        terminalFilter.setCarrierId(terminalFilterDto.getCarrierId());
        return terminalFilter;
    }
}
