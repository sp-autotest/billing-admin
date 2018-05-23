package ru.bpc.billing.controller.dto.carrier.dto;

/**
 * Created by Smirnov_Y on 05.04.2016.
 */
public class CurrencyDto {
    private Long id;
    private String countryCode;
    private String currencyNumericCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCurrencyNumericCode() {
        return currencyNumericCode;
    }

    public void setCurrencyNumericCode(String currencyNumericCode) {
        this.currencyNumericCode = currencyNumericCode;
    }
}
