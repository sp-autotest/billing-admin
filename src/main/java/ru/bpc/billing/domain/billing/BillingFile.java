package ru.bpc.billing.domain.billing;

import ru.bpc.billing.domain.Carrier;
import ru.bpc.billing.domain.ProcessingRecord;
import ru.bpc.billing.domain.FileType;
import ru.bpc.billing.domain.ProcessingFile;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Petrov_M
 * Date: 26.08.13
 * Time: 9:54
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "billing_file")
public class BillingFile extends ProcessingFile {

    @Column(name = "format", nullable = false)
    @Enumerated(EnumType.STRING)
    private BillingFileFormat format;

    @Column(name = "processing_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date processingDate;

    @Column(name = "count_lines")
    private Integer countLines;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_carrier_id", nullable = true)
    private Carrier carrier;

    @Transient
    public int notFinancialOperationCount  = 0;
    @Transient
    public int depositCount       = 0;
    @Transient
    public int refundCount        = 0;
    @Transient
    public int reverseCount       = 0;
    @Transient
    public int allRecordWithoutNotFinancialOperationCount = 0;

    public BillingFile(){
        setFileType(FileType.BILLING);
    }
    public BillingFile(Long id){
        this();
        setId(id);
    }

    public BillingFileFormat getFormat() {
        return format;
    }

    public void setFormat(BillingFileFormat format) {
        this.format = format;
    }

    public Integer getCountLines() {
        return countLines;
    }

    public void setCountLines(Integer countLines) {
        this.countLines = countLines;
    }

    public Date getProcessingDate() {
        return processingDate;
    }

    public void setProcessingDate(Date processingDate) {
        this.processingDate = processingDate;
    }

    public Carrier getCarrier() {
        return carrier;
    }

    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BillingFile{");
        sb.append("format=").append(format);
        sb.append(",name=").append(getName());
        sb.append('}');
        return sb.toString();
    }
}