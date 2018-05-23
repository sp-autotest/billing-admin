package ru.bpc.billing.controller.dto.carrier.response;

import ru.bpc.billing.controller.dto.carrier.dto.BillingSystemDto;
import ru.bpc.billing.controller.dto.carrier.dto.CarrierDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Smirnov_Y on 01.04.2016.
 */
public class BillingSystemsResponse extends Response {
    private List<BillingSystemDto> data = new ArrayList<>();

    public List<BillingSystemDto> getData() {
        return data;
    }

    public void setData(List<BillingSystemDto> data) {
        this.data = data;
    }
}
