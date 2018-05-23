package ru.bpc.billing.controller.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import ru.bpc.billing.domain.ProcessingFile;
import ru.bpc.billing.domain.billing.BillingFile;
import ru.bpc.billing.domain.billing.BillingFileFormat;

/**
 * User: Krainov
 * Date: 18.09.2014
 * Time: 11:38
 */
public class BillingFileDto extends ProcessingFileDto {

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private BillingFileFormat format;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Integer notFinancialOperationCount;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Integer depositCount;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Integer refundCount;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Integer reverseCount;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Integer allRecordWithoutNotFinancialOperationCount;
    private String iataCode;

    public BillingFileDto(){}
    public BillingFileDto(BillingFile billingFile){
        if ( null != billingFile ) {
            setProcessingFile(billingFile);
            this.format = billingFile.getFormat();
        }
    }
    public BillingFileDto(ProcessingFile processingFile, boolean success) {
        super(processingFile, success);
        if ( processingFile instanceof BillingFile ) {
            BillingFile billingFile = (BillingFile) processingFile;
            setFormat(billingFile.getFormat());
        }
    }
    public BillingFileDto(ProcessingFile processingFile, boolean success, String text) {
        super(processingFile, success, text);
    }

    public BillingFileFormat getFormat() {
        return format;
    }

    public void setFormat(BillingFileFormat format) {
        this.format = format;
    }

    public Integer getNotFinancialOperationCount() {
        return notFinancialOperationCount;
    }

    public void setNotFinancialOperationCount(Integer notFinancialOperationCount) {
        this.notFinancialOperationCount = notFinancialOperationCount;
    }

    public Integer getDepositCount() {
        return depositCount;
    }

    public void setDepositCount(Integer depositCount) {
        this.depositCount = depositCount;
    }

    public Integer getRefundCount() {
        return refundCount;
    }

    public void setRefundCount(Integer refundCount) {
        this.refundCount = refundCount;
    }

    public Integer getReverseCount() {
        return reverseCount;
    }

    public void setReverseCount(Integer reverseCount) {
        this.reverseCount = reverseCount;
    }

    public Integer getAllRecordWithoutNotFinancialOperationCount() {
        return allRecordWithoutNotFinancialOperationCount;
    }

    public void setAllRecordWithoutNotFinancialOperationCount(Integer allRecordWithoutNotFinancialOperationCount) {
        this.allRecordWithoutNotFinancialOperationCount = allRecordWithoutNotFinancialOperationCount;
    }

    public void setIataCode(String iataCode) {
        this.iataCode = iataCode;
    }

    public String getIataCode() {
        return iataCode;
    }
}
