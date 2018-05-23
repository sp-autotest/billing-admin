package ru.bpc.billing.service.report;

import ru.bpc.billing.domain.billing.BillingFile;

import java.io.File;
import java.util.List;

/**
 * User: Krainov
 * Date: 02.12.13
 * Time: 16:48
 */
public class ReportBuildException extends Exception {

    private BillingFile billingFile;
    private List<File> boRevenueFiles;//todo: rename

    public ReportBuildException() {
    }

    public ReportBuildException(String message) {
        super(message);
    }
    public ReportBuildException(String message, BillingFile billingFile, List<File> boRevenueFiles) {
        super(message);
        this.billingFile = billingFile;
        this.boRevenueFiles = boRevenueFiles;
    }

    public ReportBuildException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReportBuildException(Throwable cause) {
        super(cause);
    }

    public BillingFile getBillingFile() {
        return billingFile;
    }

    public void setBillingFile(BillingFile billingFile) {
        this.billingFile = billingFile;
    }

    public List<File> getBoRevenueFiles() {
        return boRevenueFiles;
    }

    public void setBoRevenueFiles(List<File> boRevenueFiles) {
        this.boRevenueFiles = boRevenueFiles;
    }
}
