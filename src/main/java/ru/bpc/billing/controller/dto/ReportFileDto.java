package ru.bpc.billing.controller.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import ru.bpc.billing.domain.report.ReportFile;

/**
 * User: Krainov
 * Date: 18.09.2014
 * Time: 11:42
 */
public class ReportFileDto extends ProcessingFileDto {

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public Integer successDepositRecordsCount;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public Integer successCreditRecordsCount;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public Integer rejectDepositRecordsCount;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public Integer rejectCreditRecordsCount;

    public Integer getSuccessDepositRecordsCount() {
        return successDepositRecordsCount;
    }

    public void setSuccessDepositRecordsCount(Integer successDepositRecordsCount) {
        this.successDepositRecordsCount = successDepositRecordsCount;
    }

    public Integer getSuccessCreditRecordsCount() {
        return successCreditRecordsCount;
    }

    public void setSuccessCreditRecordsCount(Integer successCreditRecordsCount) {
        this.successCreditRecordsCount = successCreditRecordsCount;
    }

    public Integer getRejectDepositRecordsCount() {
        return rejectDepositRecordsCount;
    }

    public void setRejectDepositRecordsCount(Integer rejectDepositRecordsCount) {
        this.rejectDepositRecordsCount = rejectDepositRecordsCount;
    }

    public Integer getRejectCreditRecordsCount() {
        return rejectCreditRecordsCount;
    }

    public void setRejectCreditRecordsCount(Integer rejectCreditRecordsCount) {
        this.rejectCreditRecordsCount = rejectCreditRecordsCount;
    }

    public void setReportFile(ReportFile reportFile) {
        setProcessingFile(reportFile);
        this.successCreditRecordsCount = reportFile.successCreditRecordsCount;
        this.successDepositRecordsCount = reportFile.successDepositRecordsCount;
        this.rejectCreditRecordsCount = reportFile.rejectCreditRecordsCount;
        this.rejectDepositRecordsCount = reportFile.rejectDepositRecordsCount;
    }
}
