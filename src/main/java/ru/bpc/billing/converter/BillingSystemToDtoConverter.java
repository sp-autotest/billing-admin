package ru.bpc.billing.converter;

import org.springframework.core.convert.converter.Converter;
import ru.bpc.billing.controller.dto.carrier.dto.BillingSystemDto;
import ru.bpc.billing.domain.BillingSystem;
import ru.bpc.billing.domain.Carrier;

import java.util.Set;

public class BillingSystemToDtoConverter implements Converter<BillingSystem, BillingSystemDto> {

    @Override
    public BillingSystemDto convert(BillingSystem bs) {
        if (bs == null) return null;
        BillingSystemDto dto = new BillingSystemDto();
        dto.setId(bs.getId());
        dto.setName(bs.getName());
        Carrier carrier = bs.getCarrier();
//        + " " + carrier.getName()
        dto.setCarrierId(carrier.getId());
        dto.setPort(String.valueOf(bs.getSftpPort()));
        dto.setCreatedDate(bs.getCreatedDate());

        dto.setHost(bs.getHostAddress());
        dto.setPath(bs.getPath());
        dto.setLogin(bs.getLogin());
        dto.setPassword(bs.getPassword());

        dto.setEmailsCSV(getEmails(bs.getEmails()));

        dto.setMaskRegexp(bs.getMaskRegexp());

        dto.setEnabled(bs.getEnabled());

        return dto;
    }


    private String getEmails(Set<String> emails) {
        StringBuilder sb = new StringBuilder();
        for (String each : emails) {
            sb.append(each).append(", ");
        }
        return sb.toString().replaceAll(", $", "");
    }

}
