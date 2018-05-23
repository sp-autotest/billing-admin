package ru.bpc.billing.controller.dto.carrier.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Smirnov_Y on 04.04.2016.
 */
public class TerminalDto {
    private Long id;
    private String name;
    private String agrn;
    private String terminal;
    private Long carrierId;
    private List<Long> currenciesIds = new ArrayList<>();

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

    public String getAgrn() {
        return agrn;
    }

    public void setAgrn(String agrn) {
        this.agrn = agrn;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public Long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }

    public List<Long> getCurrenciesIds() {
        return currenciesIds;
    }

    public void setCurrenciesIds(List<Long> currenciesIds) {
        this.currenciesIds = currenciesIds;
    }
}
