package ru.bpc.billing.converter;

import org.springframework.core.convert.converter.Converter;
import ru.bpc.billing.controller.dto.carrier.dto.BillingSystemDto;
import ru.bpc.billing.domain.BillingSystem;
import ru.bpc.billing.domain.Carrier;
import ru.bpc.billing.service.BillingSystemService;
import ru.bpc.billing.service.CarrierService;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BillingSystemDtoToEntityConverter implements Converter<BillingSystemDto, BillingSystem> {

    @Resource
    private BillingSystemService billingSystemService;

    @Resource
    private CarrierService carrierService;

    @Override
    public BillingSystem convert(BillingSystemDto dto) {
        if (dto == null) return null;
        BillingSystem bs = new BillingSystem();

        bs.setName(dto.getName());
        bs.setCarrier(getCarrier(dto.getCarrierId()));

        bs.setCreatedDate(dto.getCreatedDate());

        bs.setHostAddress(dto.getHost());

        String port = dto.getPort();
        if (port != null)
            bs.setSftpPort(Integer.parseInt(port));

        if (dto.getPath() != null)
            if (dto.getPath().endsWith("/"))
                bs.setPath(dto.getPath());
            else
                bs.setPath(dto.getPath() + "/");

        bs.setLogin(dto.getLogin());
        bs.setPassword(dto.getPassword());

        bs.setEmails(getEmails(dto.getEmailsCSV()));

        bs.setMaskRegexp(dto.getMaskRegexp());
        bs.setEnabled(dto.isEnabled());

        return bs;
    }

    private Set<String> getEmails(String emailsCSV) {
        if (emailsCSV == null) return null;
        Set<String> set = new HashSet<>();
        set.addAll(Arrays.asList(emailsCSV.split(", ")));
        return set;
    }

    private Carrier getCarrier(Long carrierId) {
        if (carrierId == null) return null;
        return carrierService.get(carrierId);
    }
}
