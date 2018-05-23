package ru.bpc.billing.domain;

import ru.bpc.billing.util.MaskUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * User: Krainov
 * Date: 29.11.13
 * Time: 12:04
 * AFBRBS-2142
 * Хранение информации о каждой записи, обработанной из биллинг-файла
 */
@Entity
@Table(name = "processing_record")
public class ProcessingRecord {

    public static final String SEQUENCE_NAME = "SEQ_PROCESSING_RECORD";

    @Id
    @Column(updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "processingRecordSequence")
    @SequenceGenerator(name = "processingRecordSequence", sequenceName = SEQUENCE_NAME, allocationSize = 1)
    private Long id;
    @Column(name = "invoice_number")
    private String invoiceNumber;
    @Column(name = "invoice_date")
    private String invoiceDate;
    @Column(name = "amount")
    private Integer amount;
    @Column(name = "currency")
    private String currency;
    @Column(name = "country_code")
    private String countryCode;
    @Column(name = "document_number")
    private String documentNumber;
    @Column(name = "document_date")
    private String documentDate;
    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    @Column(name = "rbs_id", unique = true)
    private String rbsId;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ProcessingStatus status;
    @Column(name = "error_message")
    private String errorMessage;
    @Column(name = "expiry")
    private String expiry;
    @Column(name = "pan")
    private String pan;
    @Column(name = "approval_code")
    private String approvalCode;
    @Column(name = "create_date")
    private Date createdAt;
    @Column(name = "ref_num")
    private String refNum;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentRecord")
    private List<ProcessingRecord> records;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true)
    private ProcessingRecord parentRecord;
    @Column(name = "amount_mps")
    private Integer amountMps;
    @Column(name = "amount_rub")
    private Integer amountRub;
    @Column(name = "rate_mps")
    private String rateMps;
    @Column(name = "rate_cb")
    private String rateCb;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.record")
    private List<ProcessingFileRecord> processingFileRecords = new LinkedList<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_carrier_id", nullable = true)
    private Carrier carrier;
    @Column(name = "utrnno", nullable = false)
    private String utrnno;

    public List<ProcessingFileRecord> getProcessingFileRecords() {
        return processingFileRecords;
    }

    public void setProcessingFileRecords(List<ProcessingFileRecord> processingFileRecords) {
        this.processingFileRecords = processingFileRecords;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getInvoiceNumber() {
        return invoiceNumber;
    }


    /**
     * Номер счёта
     * @param invoiceNumber
     */
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
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

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Номер билета
     * @param documentNumber
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(String documentDate) {
        this.documentDate = documentDate;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getRbsId() {
        return rbsId;
    }

    public void setRbsId(String rbsId) {
        this.rbsId = rbsId;
    }

    public ProcessingStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessingStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    @Transient
    public String getMaskedPan() {
        return MaskUtils.getMaskedPan(this.pan);
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getRefNum() {
        return refNum;
    }

    public void setRefNum(String refNum) {
        this.refNum = refNum;
    }

    public Integer getAmountMps() {
        return amountMps;
    }

    public void setAmountMps(Integer amountMps) {
        this.amountMps = amountMps;
    }

    public Integer getAmountRub() {
        return amountRub;
    }

    public void setAmountRub(Integer amountRub) {
        this.amountRub = amountRub;
    }

    public String getRateMps() {
        return rateMps;
    }

    public void setRateMps(String rateMps) {
        this.rateMps = rateMps;
    }

    public String getRateCb() {
        return rateCb;
    }

    public void setRateCb(String rateCb) {
        this.rateCb = rateCb;
    }

    public List<ProcessingRecord> getRecords() {
        return records;
    }

    public void setRecords(List<ProcessingRecord> records) {
        this.records = records;
    }

    public ProcessingRecord getParentRecord() {
        return parentRecord;
    }

    public void setParentRecord(ProcessingRecord parentRecord) {
        this.parentRecord = parentRecord;
    }

    public Carrier getCarrier() {
        return carrier;
    }

    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    public String getUtrnno() {
        return utrnno;
    }

    public void setUtrnno(String utrnno) {
        this.utrnno = utrnno;
    }

    @Transient
    public List<ProcessingFile> getFiles() {
        List<ProcessingFile> processingFiles = new ArrayList<>();
        for (ProcessingFileRecord processingFileRecord : processingFileRecords) {
            processingFiles.add(processingFileRecord.getFile());
        }
        return processingFiles;
    }



    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("BillingFileRecord");
        sb.append("{id=").append(id);
        sb.append(", invoiceNumber='").append(invoiceNumber).append('\'');
        sb.append(", invoiceDate='").append(invoiceDate).append('\'');
        sb.append(", amount=").append(amount);
        sb.append(", currency='").append(currency).append('\'');
        sb.append(", countryCode='").append(countryCode).append('\'');
        sb.append(", documentNumber='").append(documentNumber).append('\'');
        sb.append(", documentDate='").append(documentDate).append('\'');
        sb.append(", transactionType=").append(transactionType);
        sb.append(", rbsId='").append(rbsId).append('\'');
        sb.append(", status=").append(status);
        sb.append(", errorMessage='").append(errorMessage).append('\'');
        sb.append(", expiry='").append(expiry).append('\'');
        sb.append(", pan='").append(pan).append('\'');
        sb.append(", approvalCode='").append(approvalCode).append('\'');
        sb.append(", createdAt=").append(createdAt);
        sb.append('}');
        return sb.toString();
    }
}
