package ru.bpc.billing.controller.dto.carrier.dto;

import java.util.List;

/**
 * Created by Smirnov_Y on 05.04.2016.
 */
public class TerminalFilterDto {
    private Long carrierId;

    public Long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }
}
