package ru.bpc.billing.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Petrov_M
 * Date: 06.09.13
 * Time: 15:09
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "billing_terminal")
public class BillingTerminal implements Serializable {
    @Id
    @Column(name = "country_code", nullable = false)
    private String countryCode;

    @Column(name = "terminal_id", nullable = false)
    private String terminalId;

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}