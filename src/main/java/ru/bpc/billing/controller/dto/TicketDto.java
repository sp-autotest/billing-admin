package ru.bpc.billing.controller.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import ru.bpc.billing.domain.FileType;
import ru.bpc.billing.domain.ProcessingRecord;
import ru.bpc.billing.domain.TransactionType;

import java.util.Date;
import java.util.List;

/**
 * User: Krainov
 * Date: 04.09.2014
 * Time: 18:01
 */
public class TicketDto {

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Boolean success;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String text;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Object id;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private TransactionType transactionType;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String documentNumber;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String approvalCode;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String pan;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String refNum;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Integer amount;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String currency;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL, using = JsonDateSerializer.class)
    private Date createdAt;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String invoiceNumber;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String fileName;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private FileType fileType;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Boolean leaf = true;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private List<TicketDto> children;

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

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getRefNum() {
        return refNum;
    }

    public void setRefNum(String refNum) {
        this.refNum = refNum;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public List<TicketDto> getChildren() {
        return children;
    }

    public void setChildren(List<TicketDto> children) {
        this.children = children;
    }

    public boolean isLeaf() {
        return null != children && 0 < children.size() ? false : true;
    }

    public void setProcessingRecord(ProcessingRecord processingRecord) {
        if ( null == processingRecord) return;
        this.id = processingRecord.getId();
        this.transactionType = processingRecord.getTransactionType();
        this.documentNumber = processingRecord.getDocumentNumber();
        this.approvalCode = processingRecord.getApprovalCode();
        this.pan = processingRecord.getMaskedPan();
        this.refNum = processingRecord.getRefNum();
        this.amount = processingRecord.getAmount();
        this.currency = processingRecord.getCurrency();
        this.createdAt = processingRecord.getCreatedAt();
        this.invoiceNumber = processingRecord.getInvoiceNumber();

    }
}
