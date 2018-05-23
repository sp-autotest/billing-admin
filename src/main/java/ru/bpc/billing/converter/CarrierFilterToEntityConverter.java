package ru.bpc.billing.converter;

import org.springframework.core.convert.converter.Converter;
import ru.bpc.billing.controller.dto.carrier.dto.CarrierFilterDto;
import ru.bpc.billing.service.filter.CarrierFilter;

public class CarrierFilterToEntityConverter implements Converter<CarrierFilterDto, CarrierFilter> {

    @Override
    public CarrierFilter convert(CarrierFilterDto carrierFilterDto) {
        if (carrierFilterDto == null) return null;
        CarrierFilter carrierFilter = new CarrierFilter();
        carrierFilter.setName(carrierFilterDto.getName());
        carrierFilter.setCreatedAtFrom(carrierFilterDto.getCreatedAtFrom());
        carrierFilter.setCreatedAtTo(carrierFilterDto.getCreatedAtTo());
        return carrierFilter;
    }
}
