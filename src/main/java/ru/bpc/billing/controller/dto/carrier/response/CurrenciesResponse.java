package ru.bpc.billing.controller.dto.carrier.response;

import ru.bpc.billing.controller.dto.carrier.dto.CurrencyDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Smirnov_Y on 01.04.2016.
 */
public class CurrenciesResponse extends Response {
    private List<CurrencyDto> data = new ArrayList<>();

    public List<CurrencyDto> getData() {
        return data;
    }

    public void setData(List<CurrencyDto> data) {
        this.data = data;
    }
}
