package ru.bpc.billing.controller.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import ru.bpc.billing.domain.ProcessingFile;
import ru.bpc.billing.domain.billing.BillingFile;
import ru.bpc.billing.domain.posting.PostingFile;
import ru.bpc.billing.service.billing.BillingConverterResult;

import java.util.*;

/**
 * User: Krainov
 * Date: 29.08.14
 * Time: 10:55
 */
public class BillingConverterResultDto {

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Boolean success;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String text;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private BillingFileDto billing;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private List<PostingDto> postings;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Map<String,Integer> alreadyHandledRecords;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Integer errorAddedToDatabaseRecordsCount;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String innerErrorMessage;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private List<ProcessingFileDto> simpleFiles;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private List<BillingConverterResultDto> billingConverterResultDtos = new ArrayList<>();
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Integer refundCount;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Integer reverseCount;


    public BillingConverterResultDto(){}
    public BillingConverterResultDto(BillingConverterResult billingConverterResult){
        setBillingConverterResult(billingConverterResult);
    }
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public BillingFileDto getBilling() {
        return billing;
    }

    public void setBilling(BillingFileDto billing) {
        this.billing = billing;
    }

    public List<PostingDto> getPostings() {
        return postings;
    }

    public void setPostings(List<PostingDto> postings) {
        this.postings = postings;
    }

    public Map<String, Integer> getAlreadyHandledRecords() {
        return alreadyHandledRecords;
    }

    public void setAlreadyHandledRecords(Map<String, Integer> alreadyHandledRecords) {
        this.alreadyHandledRecords = alreadyHandledRecords;
    }

    public Integer getErrorAddedToDatabaseRecordsCount() {
        return errorAddedToDatabaseRecordsCount;
    }

    public void setErrorAddedToDatabaseRecordsCount(Integer errorAddedToDatabaseRecordsCount) {
        this.errorAddedToDatabaseRecordsCount = errorAddedToDatabaseRecordsCount;
    }

    public String getInnerErrorMessage() {
        return innerErrorMessage;
    }

    public void setInnerErrorMessage(String innerErrorMessage) {
        this.innerErrorMessage = innerErrorMessage;
    }

    public List<ProcessingFileDto> getSimpleFiles() {
        return simpleFiles;
    }

    public void setSimpleFiles(List<ProcessingFileDto> simpleFiles) {
        this.simpleFiles = simpleFiles;
    }

    public List<BillingConverterResultDto> getBillingConverterResultDtos() {
        return billingConverterResultDtos;
    }

    public void setBillingConverterResultDtos(List<BillingConverterResultDto> billingConverterResultDtos) {
        this.billingConverterResultDtos = billingConverterResultDtos;
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

    public void setBillingConverterResult(BillingConverterResult result) {
        for (BillingConverterResult billingConverterResult : result.getBillingConverterResults()) {
            BillingConverterResultDto billingConverterResultDto = new BillingConverterResultDto();
            BillingFile billingFile = billingConverterResult.getBillingFile();
            if ( null != billingFile ) {
                billingConverterResultDto.billing = new BillingFileDto(billingFile);
                billingConverterResultDto.billing.setNotFinancialOperationCount(billingFile.notFinancialOperationCount);
                billingConverterResultDto.billing.setDepositCount(billingFile.depositCount);
                billingConverterResultDto.billing.setRefundCount(billingFile.refundCount);
                billingConverterResultDto.billing.setReverseCount(billingFile.reverseCount);
                billingConverterResultDto.billing.setAllRecordWithoutNotFinancialOperationCount(billingFile.allRecordWithoutNotFinancialOperationCount);
            }

            if ( null != billingConverterResult.getProcessingFiles() ) {
                billingConverterResultDto.postings = new ArrayList<>();
                for (ProcessingFile processingFile : billingConverterResult.getProcessingFiles()) {
                    if ( processingFile instanceof PostingFile ) {
                        billingConverterResultDto.postings.add(new PostingDto((PostingFile)processingFile));
                    }
                }
            }

            if ( null != billingConverterResult.getAlreadyHandledRecords() ) {
                billingConverterResultDto.alreadyHandledRecords = new HashMap<>();
                for (String postingFileName : billingConverterResult.getAlreadyHandledRecords().keySet()) {
                    billingConverterResultDto.alreadyHandledRecords.put(postingFileName,billingConverterResult.getAlreadyHandledRecords().get(postingFileName).size());
                }
            }
            billingConverterResultDto.errorAddedToDatabaseRecordsCount = billingConverterResult.getErrorAddedToDatabaseRecords().size();
            billingConverterResultDto.innerErrorMessage = billingConverterResult.getInnerErrorMessage();

            if ( null != billingConverterResult.getProcessingFiles() && !billingConverterResult.getProcessingFiles().isEmpty() ) {
                billingConverterResultDto.simpleFiles = new ArrayList<>();
                for (ProcessingFile processingFile : billingConverterResult.getProcessingFiles()) {
                    billingConverterResultDto.simpleFiles.add(new ProcessingFileDto(processingFile));
                }
            }

            billingConverterResultDtos.add(billingConverterResultDto);
        }
        refundCount = result.refundCount;
        reverseCount = result.reverseCount;
    }
}