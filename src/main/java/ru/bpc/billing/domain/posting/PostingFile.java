package ru.bpc.billing.domain.posting;

import ru.bpc.billing.domain.FileType;
import ru.bpc.billing.domain.ProcessingFile;
import ru.bpc.billing.domain.TransactionType;
import ru.bpc.billing.domain.billing.BillingFile;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Petrov_M
 * Date: 02.09.13
 * Time: 11:49
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "posting_file")
public class PostingFile extends ProcessingFile {

    @Column(name = "format", nullable = false)
    @Enumerated(EnumType.STRING)
    private PostingFileFormat format;

    @Transient
    private Set<PostingRecord> postingRecords = new HashSet<>();
    @Transient
    public int depositCount       = 0;
    @Transient
    public int refundCount        = 0;

    public PostingFileFormat getFormat() {
        return format;
    }

    public void setFormat(PostingFileFormat format) {
        this.format = format;
    }

    public PostingFile addPostingRecord(PostingRecord postingRecord) {
        postingRecords.add(postingRecord);
        if ( postingRecord.getTransactionType().equals(TransactionType.DR) ) depositCount++;
        if ( postingRecord.getTransactionType().isCredit() ) refundCount++;
        postingRecord.setPostingFileName(getName());
        return this;
    }

    public Set<PostingRecord> getPostingRecords() {
        return postingRecords;
    }

    public PostingFile(){}
    public PostingFile(BillingFile billingFile,Date convertDate,String postingFileName, FileType fileType, PostingFileFormat format){
        super();
        setParentFile(billingFile);
        setCreatedDate(convertDate);
        setName(postingFileName);
        setFileType(fileType);
        setFormat(format);
    }

    public PostingFile(BillingFile billingFile,Date convertDate,String name,PostingFileFormat format){
        super();
        setParentFile(billingFile);
        setCreatedDate(convertDate);
        setName(name);
        setFileType(FileType.POSTING);
        setFormat(format);
    }


}
