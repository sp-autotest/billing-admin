package ru.bpc.billing.domain;

/**
 * User: Krainov
 * Date: 27.08.14
 * Time: 17:19
 */
public enum FileType {
    BILLING(false),
    BILLING_LOG(false),
    POSTING(false),
    BO(false),
    BO_LOG(false),
    @Deprecated BO_REVENUE_SUCCESS(false),
    @Deprecated BO_REVENUE_REJECT(false),
    @Deprecated BO_REVENUE_LOG(false),
    REVENUE_REPORT_EXCEL(true),
    REVENUE_REPORT_XML_ACCELYA(true),
    OPERATION_REGISTER(true),
    TICKET_INFO_REPORT(true),
    REVENUE_REPORT_EXCEL_NSPC(true),
    OPERATION_REGISTER_NSPC(true);

    private final boolean isReport;

    FileType(boolean isReport) {
        this.isReport = isReport;
    }

    public boolean isReport() {
        return isReport;
    }
}
