package ru.bpc.billing.service.report;

import java.math.BigDecimal;

/**
 * User: Krainov
 * Date: 04.12.13
 * Time: 12:31
 */
public class ReportRow {
    public String invoiceNumber;
    public String documentNumber;
    public String countryCode;
    public String mps;
    public String currencyOperation;
    public BigDecimal grossOperation   = BigDecimal.ZERO;
    public BigDecimal feeOperation     = BigDecimal.ZERO;
    public BigDecimal netOperation     = BigDecimal.ZERO;
    public BigDecimal grossMps         = BigDecimal.ZERO;
    public BigDecimal feeMps           = BigDecimal.ZERO;
    public BigDecimal netMps           = BigDecimal.ZERO;
    public String currencyMps          = null;
    public String rateMps              = null;
    public String rateBank             = null;
    public BigDecimal grossBank        = BigDecimal.ZERO;
    public BigDecimal feeBank          = BigDecimal.ZERO;
    public BigDecimal netBank          = BigDecimal.ZERO;
    public double feeRate                = 0;
    public int qty;
    public String errorMessage;
}
