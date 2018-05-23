package ru.bpc.billing.service.report;

import ru.bpc.billing.domain.report.ReportRecord;

/**
 * User: Krainov
 * Date: 04.12.13
 * Time: 12:31
 */
public class RejectReportGroup {
    private final String countryCode;
    private final String mpsName;
    private String currency;
    private String currencyMps;
    private String rateBank;
    private String rateMps;
    public RejectReportGroup(ReportRecord record) {
        if ( null != record.getCountryCode() )
            this.countryCode = record.getCountryCode();
        else this.countryCode = "UNKNOWN";
        if ( null != record.getOperationType() )
            this.mpsName = record.getOperationType().getType();
        else
            this.mpsName = "UNKNOWN";
        this.currency = record.getCurrency();
        currencyMps = record.getCurrencyMPS();
        rateBank = record.getRateBank();
        rateMps = record.getRateMPS();
    }
    public RejectReportGroup(String countryCode, String mpsName) {
        this.countryCode = countryCode;
        this.mpsName = mpsName;
    }
    public RejectReportGroup(String countryCode, String mpsName, String currency) {
        this.countryCode = countryCode;
        this.mpsName = mpsName;
        this.currency = currency;
    }
    public String getCountryCode() {
        return countryCode;
    }
    public String getMpsName() {
        return mpsName;
    }

    public String getCurrencyMps() {
        return currencyMps;
    }

    public String getRateBank() {
        return rateBank;
    }

    public String getRateMps() {
        return rateMps;
    }

    public String getCurrency() {
        return currency;
    }

    /*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RejectReportGroup that = (RejectReportGroup) o;

        if (!countryCode.equals(that.countryCode)) return false;
        if (!currency.equals(that.currency)) return false;
        if (!mpsName.equals(that.mpsName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = countryCode.hashCode();
        result = 31 * result + mpsName.hashCode();
        result = 31 * result + currency.hashCode();
        return result;
    }
    */


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RejectReportGroup that = (RejectReportGroup) o;

        if (!countryCode.equals(that.countryCode)) return false;
        if (!mpsName.equals(that.mpsName)) return false;

        return true;
    }
    @Override
    public int hashCode() {
        int result = countryCode.hashCode();
        result = 31 * result + mpsName.hashCode();
        return result;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("RejectReportGroup");
        sb.append("{countryCode='").append(countryCode).append('\'');
        sb.append(", mpsName='").append(mpsName).append('\'');
        sb.append(", currency='").append(currency).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
