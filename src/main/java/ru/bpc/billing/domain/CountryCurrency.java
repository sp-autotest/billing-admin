package ru.bpc.billing.domain;

import ru.bpc.billing.service.CurrencyService;

import javax.persistence.*;
import java.util.Currency;

/**
 * User: Krainov
 * Date: 11.02.14
 * Time: 18:04
 */
@Entity
@Table(name = "country_currency"
        ,uniqueConstraints = @UniqueConstraint(columnNames = {"country_code","currency_numeric_code"})
)
public class CountryCurrency {

    @Id
    @Column(updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "countryCurrencySequence")
    @SequenceGenerator(name = "countryCurrencySequence", sequenceName = "SEQ_COUNTRY_CURRENCY")
    private Long id;
    @Column(name = "country_code",nullable = false)
    private String countryCode;
    @Column(name = "currency_numeric_code",nullable = false)
    private String currencyNumericCode;

    @Transient
    private Currency currency;

    public CountryCurrency(){}

    public CountryCurrency(String countryCode, String currencyNumericCode) {
        this.countryCode = countryCode;
        this.currencyNumericCode = currencyNumericCode;
        this.currency = CurrencyService.findByNumberCode(Integer.valueOf(currencyNumericCode));
    }

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

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String toSimpleString() {
        return buildSimpleString(countryCode,currencyNumericCode);
    }

    public static String buildSimpleString(String countryCode, String currencyNumericCode) {
        if ( null == countryCode || null == currencyNumericCode ) throw new IllegalArgumentException("countryCode = " + countryCode + " or currencyNumericCode = " + currencyNumericCode + " is invalid");
        return countryCode.toUpperCase() + ":" + currencyNumericCode;
    }

    public String getCurrencyNumericCode() {
        return currencyNumericCode;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("CountryCurrency");
        sb.append("{id=").append(id);
        sb.append(", countryCode='").append(countryCode).append('\'');
        sb.append(", currency=").append(currency);
        sb.append('}');
        return sb.toString();
    }
}
