package ru.bpc.billing.controller.dto.carrier.response;

import ru.bpc.billing.controller.dto.carrier.dto.CarrierDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Smirnov_Y on 01.04.2016.
 */
public class CarriersResponse extends Response {
    private List<CarrierDto> data = new ArrayList<>();

    public List<CarrierDto> getData() {
        return data;
    }

    public void setData(List<CarrierDto> data) {
        this.data = data;
    }
}
