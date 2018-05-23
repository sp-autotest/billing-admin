package ru.bpc.billing.controller.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import ru.bpc.billing.domain.bo.BOFile;
import ru.bpc.billing.domain.bo.BOFileFormat;
import ru.bpc.billing.service.bo.BOProcessingResult;

/**
 * User: Krainov
 * Date: 18.09.2014
 * Time: 11:39
 */
public class BOFileDto extends ProcessingFileDto {

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private BOFileFormat format;

    private int processedRecords;
    private int totalRecords;
    private int successRecords;
    private int errorRecords;
    private int fraudRecords;
    private int depositRecords;
    private int refundRecords;

    public BOFileFormat getFormat() {
        return format;
    }

    public void setFormat(BOFileFormat format) {
        this.format = format;
    }

    public BOFileDto setBOFile(BOFile boFile) {
        setProcessingFile(boFile);
        this.format = boFile.getFormat();
        return this;
    }

    public BOFileDto setBOProcessingResult(BOProcessingResult boProcessingResult) {
        this.totalRecords = boProcessingResult.getTotalRecords();
        this.successRecords = boProcessingResult.getSuccessRecords();
        this.errorRecords = boProcessingResult.getErrorRecords();
        this.fraudRecords = boProcessingResult.getFraudRecords();
        this.depositRecords = boProcessingResult.getDepositRecords();
        this.refundRecords = boProcessingResult.getRefundRecords();
        this.processedRecords = boProcessingResult.getProcessingRecords().size();
        return this;
    }

    public int getProcessedRecords() {
        return processedRecords;
    }

    public void setProcessedRecords(int processedRecords) {
        this.processedRecords = processedRecords;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getSuccessRecords() {
        return successRecords;
    }

    public void setSuccessRecords(int successRecords) {
        this.successRecords = successRecords;
    }

    public int getErrorRecords() {
        return errorRecords;
    }

    public void setErrorRecords(int errorRecords) {
        this.errorRecords = errorRecords;
    }

    public int getFraudRecords() {
        return fraudRecords;
    }

    public void setFraudRecords(int fraudRecords) {
        this.fraudRecords = fraudRecords;
    }

    public int getDepositRecords() {
        return depositRecords;
    }

    public void setDepositRecords(int depositRecords) {
        this.depositRecords = depositRecords;
    }

    public int getRefundRecords() {
        return refundRecords;
    }

    public void setRefundRecords(int refundRecords) {
        this.refundRecords = refundRecords;
    }
}
