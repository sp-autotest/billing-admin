package ru.bpc.billing.service.report;

/**
 * User: Krainov
 * Date: 31.03.14
 * Time: 13:52
 */
public class RejectReportCurrencyGroup {

    private final String country;
    private final String currency;

    public RejectReportCurrencyGroup(String country, String currency) {
        if ( null != country ) this.country = country;
        else this.country = "UNKNOWN";
        if ( null != currency ) this.currency = currency;
        else this.currency = "UNKNOWN";
    }

    public String getCountry() {
        return country;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RejectReportCurrencyGroup that = (RejectReportCurrencyGroup) o;

        if (!country.equals(that.country)) return false;
        if (!currency.equals(that.currency)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = country.hashCode();
        result = 31 * result + currency.hashCode();
        return result;
    }
}
