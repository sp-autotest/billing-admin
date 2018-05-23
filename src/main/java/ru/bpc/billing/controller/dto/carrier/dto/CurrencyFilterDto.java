package ru.bpc.billing.controller.dto.carrier.dto;

import java.util.List;

/**
 * Created by Smirnov_Y on 05.04.2016.
 */
public class CurrencyFilterDto {
    private List<Long> ids;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
