package ru.bpc.billing.domain.posting;

import ru.bpc.billing.domain.TransactionType;

/**
 * User: Krainov
 * Date: 11.08.14
 * Time: 18:16
 */
public interface PostingRecord {

    public String getDocumentDate();
    public String getDocumentNumber();
    public TransactionType getTransactionType();
    public String getInvoiceNumber();
    public String getInvoiceDate();
    public String getTransactionAmount();
    public String getTransactionCurrency();
    public String getCountryCode();
    public String getRbsId();
    public PostingRecordType getType();
    public void setType(PostingRecordType postingRecordType);
    public String getErrorMessage();
    public String getExpirationDate();
    public String getPan();
    public String getApprovalCode();
    public String getRefNum();
    @Deprecated
    public String getPostingFileName();
    @Deprecated
    public void setPostingFileName(String postingFileName);
    public void setPostingFile(PostingFile postingFile);
    public PostingFile getPostingFile();
    public String getUtrnno();
    public void setUtrnno(String utrnno);
    public String getIataCode();
    public String getProcessingCode();
    public String getReversalFlag();
}
