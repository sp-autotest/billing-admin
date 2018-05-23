package ru.bpc.billing.service.report;

import ru.bpc.billing.domain.report.ReportRecord;

/**
 * User: Krainov
 * Date: 04.12.13
 * Time: 12:30
 */
public class ReportGroup {
    private final String invoiceNumber;
    private final String countryCode;
    private final String mpsName;
    private final String currencyCode;

    public ReportGroup(ReportRecord record) {
        this.invoiceNumber = record.getInvoiceNumber();
        if ( null != record.getCountryCode() )
            this.countryCode = record.getCountryCode();
        else this.countryCode = "UNKNOWN";
        if ( null != record.getOperationType() )
            this.mpsName = record.getOperationType().getType();
        else
            this.mpsName = "UNKNOWN";
        if ( null != record.getCurrencyOperation() )
            this.currencyCode = record.getCurrencyOperation();
        else this.currencyCode = "UNKNOWN";
    }
    public String getInvoiceNumber() {
        return invoiceNumber;
    }
    public String getCountryCode() {
        return countryCode;
    }
    public String getType() {
        return mpsName;
    }
    public String getCurrencyCode() {
        return currencyCode;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportGroup that = (ReportGroup) o;

        if (!countryCode.equals(that.countryCode)) return false;
        if (!currencyCode.equals(that.currencyCode)) return false;
        if (!invoiceNumber.equals(that.invoiceNumber)) return false;
        if (!mpsName.equals(that.mpsName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = invoiceNumber.hashCode();
        result = 31 * result + countryCode.hashCode();
        result = 31 * result + mpsName.hashCode();
        result = 31 * result + currencyCode.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ReportGroup");
        sb.append("{invoiceNumber='").append(invoiceNumber).append('\'');
        sb.append(", countryCode='").append(countryCode).append('\'');
        sb.append(", mpsName='").append(mpsName).append('\'');
        sb.append(", currencyCode='").append(currencyCode).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
