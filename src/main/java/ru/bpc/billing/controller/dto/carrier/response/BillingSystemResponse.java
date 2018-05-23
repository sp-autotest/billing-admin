package ru.bpc.billing.controller.dto.carrier.response;

import ru.bpc.billing.controller.dto.carrier.dto.BillingSystemDto;
import ru.bpc.billing.controller.dto.carrier.dto.CarrierDto;

/**
 * Created by Smirnov_Y on 01.04.2016.
 */
public class BillingSystemResponse extends Response {
    private BillingSystemDto data;

    public BillingSystemDto getData() {
        return data;
    }

    public void setData(BillingSystemDto data) {
        this.data = data;
    }
}
