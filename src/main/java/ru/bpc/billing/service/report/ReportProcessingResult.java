package ru.bpc.billing.service.report;

import ru.bpc.billing.domain.billing.BillingFile;
import ru.bpc.billing.domain.report.ReportFile;
import ru.bpc.billing.service.bo.BOProcessingResult;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Krainov
 * Date: 03.09.2014
 * Time: 16:42
 */
public class ReportProcessingResult {

    private List<BillingFile> billingFiles = new ArrayList<>();
    private List<ReportFile> reportFiles = new ArrayList<>();
    private List<BOProcessingResult> boProcessingResults;

    public ReportProcessingResult() {
    }

    public ReportProcessingResult(List<BillingFile> billingFiles) {
        this.billingFiles = billingFiles;
    }

    public List<BillingFile> getBillingFiles() {
        return billingFiles;
    }

    public void setBillingFiles(List<BillingFile> billingFiles) {
        this.billingFiles = billingFiles;
    }

    public List<ReportFile> getReportFiles() {
        return reportFiles;
    }

    public void setReportFiles(List<ReportFile> reportFiles) {
        this.reportFiles = reportFiles;
    }

    public List<BOProcessingResult> getBoProcessingResults() {
        return boProcessingResults;
    }

    public void setBoProcessingResults(List<BOProcessingResult> boProcessingResults) {
        this.boProcessingResults = boProcessingResults;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ReportProcessingResult{");
        sb.append("billingFiles count = ").append(billingFiles.size());
        sb.append(", reportFiles count = ").append(reportFiles.size());
        sb.append(",");
        for (BOProcessingResult boProcessingResult : boProcessingResults) {
            sb.append(boProcessingResult);
        }
        sb.append('}');
        return sb.toString();
    }
}
