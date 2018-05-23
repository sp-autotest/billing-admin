package ru.bpc.billing.service.filter;

/**
 * Created by Smirnov_Y on 05.04.2016.
 */
public class TerminalFilter {
    private Long carrierId;
    private String name;
    private String agrn;
    private String terminal;

    public Long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
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
}
