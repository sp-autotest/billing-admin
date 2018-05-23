package ru.bpc.billing.service.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Smirnov_Y on 05.04.2016.
 */
public class CurrencyFilter {
    private List<Long> ids = new ArrayList<>();

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
