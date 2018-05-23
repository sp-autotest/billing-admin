package ru.bpc.billing.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.bpc.billing.controller.dto.carrier.dto.CarrierDto;
import ru.bpc.billing.domain.Carrier;
import ru.bpc.billing.service.CarrierService;
import ru.bpc.billing.service.filter.CarrierFilter;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Smirnov_Y on 19.04.2016.
 */
@Component
public class CarrierDtoValidator implements Validator {

    @Resource
    private CarrierService carrierService;

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {
        CarrierDto carrierDto = (CarrierDto) target;
        boolean isNew = carrierDto.getId() == null;

        CarrierFilter filter;
        if (StringUtils.isNotBlank(carrierDto.getName())){
            filter = new CarrierFilter();
            filter.setName(carrierDto.getName());
            rejectIfNotUniqueField(errors, filter, isNew, carrierDto.getId(), "carrier.name.notUnique");
        }
        if (StringUtils.isNotBlank(carrierDto.getIataCode())){
            filter = new CarrierFilter();
            filter.setIataCode(carrierDto.getIataCode());
            rejectIfNotUniqueField(errors, filter, isNew, carrierDto.getId(), "carrier.iataCode.notUnique");
        }

        if (!isNew) return;

        if (StringUtils.isBlank(carrierDto.getName())){
            errors.reject("carrier.name.blank");
        }
        if (StringUtils.isBlank(carrierDto.getIataCode())){
            errors.reject("carrier.iataCode.blank");
        }
        if (StringUtils.isBlank(carrierDto.getMcc())) {
            errors.reject("carrier.mcc.blank");
        }
        if (carrierDto.getMcc().length() != 4) {
            errors.reject("carrier.mcc.invalidLength");
        }
    }

    private void rejectIfNotUniqueField(Errors errors, CarrierFilter filter, boolean isNew, Long id, String message){
        List<Carrier> carriers = carrierService.get(filter);
        if (    carriers.size() > 0 && isNew ||
                carriers.size() > 0 && !isNew && !carriers.get(0).getId().equals(id)
                ){
            errors.reject(message);
        }
    }
}
