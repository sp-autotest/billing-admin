package ru.bpc.billing.domain.billing;

import ru.bpc.billing.domain.ProcessingRecord;
import ru.bpc.billing.domain.FileType;
import ru.bpc.billing.domain.ProcessingFile;
import ru.bpc.billing.domain.TransactionType;

import javax.persistence.*;
import java.util.Date;

/**
 * User: Krainov
 * Date: 05.02.14
 * Time: 15:41
 * Если поля пустые, то они полностью соответсвуют соотвествующим billing_file_record и billing_simple_file
 */
//@Entity
//@Table(name = "billing_file_record_history")
public class BillingFileRecordHistory  {
    public final static String SEQUENCE_NAME = "SEQ_BILLING_RECORD_HISTORY";

    @Transient
    private Long billingFileRecordId;

    @Transient
    private Long processingFileId;

    @Id
    @Column(updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "billingFileRecordHistorySequence")
    @SequenceGenerator(name = "billingFileRecordHistorySequence", sequenceName = SEQUENCE_NAME, allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_file_record_id", nullable = false)
    private ProcessingRecord processingRecord;

    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(name = "document_number")
    private String documentNumber;

    @Column(name = "approval_code")
    private String approvalCode;

    @Column(name = "pan")
    private String pan;

    @Column(name = "ref_num")
    private String refNum;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "currency")
    private String currency;

    @Column(name = "create_date")
    private Date createdAt;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processing_file_id", nullable = false)
    private ProcessingFile processingFile;

    // MP-24 - if BillingSimpleFile was get from BillingFileRecordHistory.getProcessingFile() BillingSimpleFile.getId() can return 'null' for unknown reasons
    @Column(name = "processing_file_id", updatable = false, insertable = false)
    private Long mappedProcessingFileId;

    @Column(name = "file_type")
    @Enumerated(EnumType.STRING)
    private FileType fileType;

    public BillingFileRecordHistory() {
    }

    public BillingFileRecordHistory(Long id, Long billingFileRecordId, Long processingFileId) {
        this.billingFileRecordId = billingFileRecordId;
        this.processingFileId = processingFileId;
        this.id = id;
    }

    public BillingFileRecordHistory(ProcessingRecord processingRecord, ProcessingFile processingFile) {
        this.processingRecord = processingRecord;
        this.processingFile = processingFile;
    }

    public Long getBillingFileRecordId() {
        return billingFileRecordId;
    }

    public BillingFileRecordHistory setBillingFileRecordId(Long billingFileRecordId) {
        this.billingFileRecordId = billingFileRecordId;
        return this;
    }



    public BillingFileRecordHistory setBillingSimpleFileId(Long processingFileId) {
        this.processingFileId = processingFileId;
        return this;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProcessingRecord getProcessingRecord() {
        return processingRecord;
    }

    public void setProcessingRecord(ProcessingRecord processingRecord) {
        this.processingRecord = processingRecord;
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

    public BillingFileRecordHistory setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }


    public ProcessingFile getProcessingFile() {
        return processingFile;
    }

    public BillingFileRecordHistory setProcessingFile(ProcessingFile processingFile) {
        this.processingFile = processingFile;
        this.processingFileId = processingFile.getId();
        return this;
    }

//    public BillingFileRecordHistory setBillingSimpleFile(BillingSimpleFile billingSimpleFile) {
//        this.billingSimpleFile = billingSimpleFile;
//        this.billingSimpleFileId = billingSimpleFile.getId();
//        return this;
//    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

//    public Long getMappedBillingSimpleFileId() {
//        return mappedBillingSimpleFileId;
//    }

    public static class BillingFileRecordHistoryBuilder {
        private BillingFileRecordHistory history;

        public BillingFileRecordHistoryBuilder() {
            this.history = new BillingFileRecordHistory();
        }

        protected BillingFileRecordHistoryBuilder billingFileRecordId(Long id) {
            history.setBillingFileRecordId(id);
            return this;
        }

        protected BillingFileRecordHistoryBuilder billingFileRecord(ProcessingRecord processingRecord) {
            if (null == processingRecord) throw new IllegalArgumentException("BillingFileRecord is null");
            history.processingRecord = processingRecord;
            history.setBillingFileRecordId(processingRecord.getId());
            return this;
        }

        protected BillingFileRecordHistoryBuilder billingSimpleFile(ProcessingFile billingSimpleFile) {
            if (null == billingSimpleFile) throw new IllegalArgumentException("BillingSimpleFile is null");
            //history.setBillingSimpleFile(billingSimpleFile);
            history.setBillingSimpleFileId(billingSimpleFile.getId());
            fileType(billingSimpleFile.getFileType());
            createdAt(billingSimpleFile.getCreatedDate());
            return this;
        }

        public BillingFileRecordHistoryBuilder transactionType(TransactionType transactionType) {
            history.transactionType = transactionType;
            return this;
        }

        public BillingFileRecordHistoryBuilder documentNumber(String documentNumber) {
            history.documentNumber = documentNumber;
            return this;
        }

        public BillingFileRecordHistoryBuilder approvalCode(String approvalCode) {
            history.approvalCode = approvalCode;
            return this;
        }

        public BillingFileRecordHistoryBuilder pan(String pan) {
            history.pan = pan;
            return this;
        }

        //don't know
        public BillingFileRecordHistoryBuilder refNum(String refNum) {
            history.refNum = refNum;
            return this;
        }

        public BillingFileRecordHistoryBuilder amount(int amount) {
            history.amount = amount;
            return this;
        }

        public BillingFileRecordHistoryBuilder currency(String currency) {
            history.currency = currency;
            return this;
        }

        public BillingFileRecordHistoryBuilder createdAt(Date createdAt) {
            history.createdAt = createdAt;
            return this;
        }

        public BillingFileRecordHistoryBuilder invoiceNumber(String invoiceNumber) {
            history.invoiceNumber = invoiceNumber;
            return this;
        }

        private BillingFileRecordHistoryBuilder fileType(FileType fileType) {
            history.fileType = fileType;
            return this;
        }

        public BillingFileRecordHistoryBuilder createUsingData(ProcessingRecord fileRecord, ProcessingFile simpleFile) {
            return createUsingFileRecordData(fileRecord).createUsingSimpleFileData(simpleFile);
        }

        public BillingFileRecordHistoryBuilder createUsingFileRecordData(ProcessingRecord fileRecord) {
            return billingFileRecord(fileRecord)
                    .transactionType(fileRecord.getTransactionType())
                    .documentNumber(fileRecord.getDocumentNumber())
                    .approvalCode(fileRecord.getApprovalCode())
                    .pan(fileRecord.getPan())
                    .amount(fileRecord.getAmount())
                    .currency(fileRecord.getCurrency())
                    .invoiceNumber(fileRecord.getInvoiceNumber());

        }

        public BillingFileRecordHistoryBuilder createUsingSimpleFileData(ProcessingFile simpleFile) {
            return billingSimpleFile(simpleFile);
        }
        /*
        public BillingFileRecordHistoryBuilder revenueRecord(RevenueRecord revenueRecord) {
            if (null != revenueRecord.getBoRecord() && null != revenueRecord.getBoRecord().getTransactionType())
                transactionType(revenueRecord.getBoRecord().getTransactionType());
            else if (null != revenueRecord.getBillingFileRecord()) transactionType(revenueRecord.getBillingFileRecord().getTransactionType());
            return documentNumber(revenueRecord.getDocumentNumber())
                    .approvalCode(revenueRecord.getApprovalCode())
                    .pan(null)
                    .amount(revenueRecord.getGrossOperation().intValue())
                    .currency(revenueRecord.getCurrencyOperation())
                    .invoiceNumber(revenueRecord.getInvoiceNumber())
                    .billingFileRecordId(null != revenueRecord.getBillingFileRecord() ? revenueRecord.getBillingFileRecord().getId() : null)
                    .refNum(null != revenueRecord.getBillingFileRecord() ? revenueRecord.getBillingFileRecord().getRefNum() : null);
        }
        */

        public BillingFileRecordHistory build() {
            return history;
        }
    }

    public void updateFieldsIfNull(ProcessingRecord record) {
        if (null == transactionType) transactionType = record.getTransactionType();
        if (null == documentNumber) documentNumber = record.getDocumentNumber();
        if (null == approvalCode) approvalCode = record.getApprovalCode();
        if (null == pan) pan = record.getPan();
        if (null == amount) amount = record.getAmount();
        if (null == currency) currency = record.getCurrency();
        if (null == createdAt) createdAt = record.getCreatedAt();
        if (null == invoiceNumber) invoiceNumber = record.getInvoiceNumber();
        if (null == refNum) refNum = record.getRefNum();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("BillingFileRecordHistory");
        sb.append("{billingFileRecordId=").append(billingFileRecordId);
        sb.append(", id=").append(id);
        sb.append(", transactionType=").append(transactionType);
        sb.append(", documentNumber='").append(documentNumber).append('\'');
        sb.append(", approvalCode='").append(approvalCode).append('\'');
        sb.append(", refNum='").append(refNum).append('\'');
        sb.append(", amount=").append(amount);
        sb.append(", currency='").append(currency).append('\'');
        sb.append(", createdAt=").append(createdAt);
        sb.append(", invoiceNumber='").append(invoiceNumber).append('\'');
        sb.append(", fileType=").append(fileType);
        sb.append('}');
        return sb.toString();
    }
}
