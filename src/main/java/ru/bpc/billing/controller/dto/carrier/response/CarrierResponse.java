package ru.bpc.billing.controller.dto.carrier.response;

import ru.bpc.billing.controller.dto.carrier.dto.CarrierDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Smirnov_Y on 01.04.2016.
 */
public class CarrierResponse extends Response {
    private CarrierDto data;

    public CarrierDto getData() {
        return data;
    }

    public void setData(CarrierDto data) {
        this.data = data;
    }
}
