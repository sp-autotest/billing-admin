package ru.bpc.billing.converter;

import org.springframework.core.convert.converter.Converter;
import ru.bpc.billing.controller.dto.carrier.dto.CarrierDto;
import ru.bpc.billing.domain.Carrier;

public class CarrierToEntityConverter implements Converter<CarrierDto, Carrier> {

    @Override
    public Carrier convert(CarrierDto carrierDto) {
        if (carrierDto == null) return null;
        Carrier carrier = new Carrier();
        carrier.setName(carrierDto.getName());
        carrier.setIataCode(carrierDto.getIataCode());
        carrier.setMcc(carrierDto.getMcc());
        return carrier;
    }
}
