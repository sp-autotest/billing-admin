package ru.bpc.billing.controller.dto.carrier.dto;

import java.util.Date;

/**
 * Created by Smirnov_Y on 05.04.2016.
 */
public class CarrierFilterDto {
    private String name;
    private Date createdAtFrom;
    private Date createdAtTo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedAtFrom() {
        return createdAtFrom;
    }

    public void setCreatedAtFrom(Date createdAtFrom) {
        this.createdAtFrom = createdAtFrom;
    }

    public Date getCreatedAtTo() {
        return createdAtTo;
    }

    public void setCreatedAtTo(Date createdAtTo) {
        this.createdAtTo = createdAtTo;
    }
}
