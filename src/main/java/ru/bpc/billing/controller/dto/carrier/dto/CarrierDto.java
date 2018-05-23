package ru.bpc.billing.controller.dto.carrier.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import ru.bpc.billing.controller.dto.JsonDateSerializer;

import java.util.Date;

public class CarrierDto {
    private Long id;
    private String name;
    private String iataCode;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL, using = JsonDateSerializer.class)
    private Date createdAt;
    private String mcc;
    private String billingSystems;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIataCode() {
        return iataCode;
    }

    public void setIataCode(String iataCode) {
        this.iataCode = iataCode;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getBillingSystems() {
        return billingSystems;
    }

    public void setBillingSystems(String billingSystems) {
        this.billingSystems = billingSystems;
    }
}
