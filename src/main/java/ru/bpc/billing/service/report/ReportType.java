package ru.bpc.billing.service.report;

/**
 * User: Krainov
 * Date: 28.04.2015
 * Time: 13:41
 */
public enum ReportType {
    STANDARD,
    NSPC;

    public final static String SYSTEM_SETTINGS_PARAM_NAME = "report.type";

    public static ReportType safeValueOf(String value) {
        for (ReportType reportType : values()) {
            if ( reportType.name().equalsIgnoreCase(value) ) return reportType;
        }
        return null;
    }
}
