package ru.bpc.billing.converter;

import org.springframework.core.convert.converter.Converter;
import ru.bpc.billing.controller.dto.carrier.dto.CarrierDto;
import ru.bpc.billing.domain.BillingSystem;
import ru.bpc.billing.domain.Carrier;

import java.util.List;
import java.util.Set;

public class CarrierToDtoConverter implements Converter<Carrier, CarrierDto> {

    @Override
    public CarrierDto convert(Carrier carrier) {
        if (carrier == null) return null;
        CarrierDto carrierDto = new CarrierDto();
        carrierDto.setId(carrier.getId());
        carrierDto.setName(carrier.getName());
        carrierDto.setIataCode(carrier.getIataCode());
        carrierDto.setCreatedAt(carrier.getCreatedAt());
        carrierDto.setMcc(carrier.getMcc());
        carrierDto.setBillingSystems(getBsList(carrier.getBsList()));
        return carrierDto;
    }

    private String getBsList(Set<BillingSystem> bsList) {
        StringBuilder sb = new StringBuilder();
        for (BillingSystem each : bsList) {
            sb.append(each.getName()).append("; ");
        }
        return sb.toString();
    }
}
