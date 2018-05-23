package ru.bpc.billing.controller.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import ru.bpc.billing.domain.posting.PostingFile;

/**
 * User: Krainov
 * Date: 29.08.14
 * Time: 11:23
 */
public class PostingDto extends ProcessingFileDto {

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Long billingId;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String logFileName;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Integer depositCount;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Integer refundCount;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Integer allRecordCount;

    public PostingDto(){}
    public PostingDto(PostingFile postingFile){
        setProcessingFile(postingFile);
        if ( null != postingFile.getParentFile() ) {
            this.billingId = postingFile.getParentFile().getId();
        }
        this.depositCount = postingFile.depositCount;
        this.refundCount = postingFile.refundCount;
        this.allRecordCount = postingFile.getPostingRecords().size();
    }

    public Long getBillingId() {
        return billingId;
    }

    public void setBillingId(Long billingId) {
        this.billingId = billingId;
    }

    public String getLogFileName() {
        return logFileName;
    }

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
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

    public Integer getAllRecordCount() {
        return allRecordCount;
    }

    public void setAllRecordCount(Integer allRecordCount) {
        this.allRecordCount = allRecordCount;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("PostingDto{");
        sb.append("billingId=").append(billingId);
        sb.append(", logFileName='").append(logFileName).append('\'');
        sb.append(", depositCount=").append(depositCount);
        sb.append(", refundCount=").append(refundCount);
        sb.append(", allRecordCount=").append(allRecordCount);
        sb.append('}');
        return sb.toString();
    }
}
