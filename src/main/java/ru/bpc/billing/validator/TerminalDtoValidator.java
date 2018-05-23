package ru.bpc.billing.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.bpc.billing.controller.dto.carrier.dto.TerminalDto;
import ru.bpc.billing.domain.Terminal;
import ru.bpc.billing.service.CarrierService;
import ru.bpc.billing.service.CountryCurrencyService;
import ru.bpc.billing.service.TerminalService;
import ru.bpc.billing.service.filter.TerminalFilter;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Smirnov_Y on 19.04.2016.
 */
@Component
public class TerminalDtoValidator implements Validator {

    @Resource
    private CarrierService carrierService;

    @Resource
    private TerminalService terminalService;

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {
        TerminalDto terminalDto = (TerminalDto) target;
        boolean isNew = terminalDto.getId() == null;

        TerminalFilter filter;
        if (StringUtils.isNotBlank(terminalDto.getName())){
            filter = new TerminalFilter();
            filter.setName(terminalDto.getName());
            rejectIfNotUniqueField(errors, filter, isNew, terminalDto.getId(), "terminal.name.notUnique");
        }
        if (StringUtils.isNotBlank(terminalDto.getAgrn())){
            filter = new TerminalFilter();
            filter.setAgrn(terminalDto.getAgrn());
            rejectIfNotUniqueField(errors, filter, isNew, terminalDto.getId(), "terminal.agrn.notUnique");
        }
        if (StringUtils.isNotBlank(terminalDto.getTerminal())){
            filter = new TerminalFilter();
            filter.setTerminal(terminalDto.getTerminal());
            rejectIfNotUniqueField(errors, filter, isNew, terminalDto.getId(), "terminal.terminal.notUnique");
        }

        if (terminalDto.getCarrierId() != null && carrierService.get(terminalDto.getCarrierId()) == null){
            errors.reject("carrier.carrierId.notFound","carrier.carrierId.notFound");
        }

        if (CollectionUtils.isEmpty(terminalDto.getCurrenciesIds())){
            errors.reject("carrier.currencies.empty","carrier.currencies.empty");
        }

        if (!isNew) return;

        if (StringUtils.isBlank(terminalDto.getName())){
            errors.reject("carrier.name.blank","carrier.name.blank");
        }
        if (StringUtils.isBlank(terminalDto.getAgrn())){
            errors.reject("carrier.agrn.blank","carrier.agrn.blank");
        }
        if (StringUtils.isBlank(terminalDto.getTerminal())){
            errors.reject("carrier.terminal.blank","carrier.terminal.blank");
        }
        if (terminalDto.getCarrierId() == null){
            errors.reject("carrier.carrierId.blank","carrier.carrierId.blank");
        }
        if (CollectionUtils.isEmpty(terminalDto.getCurrenciesIds())){
            errors.reject("carrier.currencies.empty","carrier.currencies.empty");
        }
    }

    private void rejectIfNotUniqueField(Errors errors, TerminalFilter filter, boolean isNew, Long id, String message){
        List<Terminal> terminals = terminalService.get(filter);
        if (    terminals.size() > 0 && isNew ||
                terminals.size() > 0 && !isNew && !terminals.get(0).getId().equals(id)
                ){
            errors.reject(message);
        }
    }
}
