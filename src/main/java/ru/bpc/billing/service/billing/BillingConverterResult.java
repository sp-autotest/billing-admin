package ru.bpc.billing.service.billing;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import ru.bpc.billing.domain.ProcessingFile;
import ru.bpc.billing.domain.TransactionType;
import ru.bpc.billing.domain.billing.BillingFile;
import ru.bpc.billing.domain.posting.PostingRecord;
import ru.bpc.billing.domain.posting.sv.SvPostingRecord;

import java.util.*;
import java.util.stream.IntStream;

/**
 * User: Krainov
 * Date: 18.11.13
 * Time: 14:46
 */
public class BillingConverterResult {

    private List<BillingConverterResult> billingConverterResults = new ArrayList<>();
    private BillingFile billingFile;
    private Multimap<String,PostingRecord> alreadyHandledRecords = ArrayListMultimap.create();
    private Set<PostingRecord> errorAddedToDatabaseRecords = new HashSet<>();
    private String innerErrorMessage;
    private List<ProcessingFile> processingFiles = new ArrayList<>();
    private List<String> logStrings = new ArrayList<>();
    private boolean status;
    public int recordCount;
    public int reverseCount;
    public int refundCount;

    public BillingConverterResult() {
    }
    public BillingConverterResult(boolean status) {
        this.status = status;
    }
    public Set<PostingRecord> getAllAlreadyHandledRecords() {
        Set<PostingRecord> records = new HashSet<>();
        for (String postingFileName : alreadyHandledRecords.keySet()) {
            records.addAll(alreadyHandledRecords.get(postingFileName));
        }
        return records;
    }

    public Multimap<String, PostingRecord> getAlreadyHandledRecords() {
        return alreadyHandledRecords;
    }

    public Set<PostingRecord> getErrorAddedToDatabaseRecords() {
        return errorAddedToDatabaseRecords;
    }

    public BillingFile getBillingFile() {
        return billingFile;
    }

    public void setBillingFile(BillingFile billingFile) {
        this.billingFile = billingFile;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getInnerErrorMessage() {
        return innerErrorMessage;
    }

    public void setInnerErrorMessage(String innerErrorMessage) {
        this.innerErrorMessage = innerErrorMessage;
    }

    public BillingConverterResult success() {
        this.status = true;
        return this;
    }

    public BillingConverterResult fail() {
        this.status = false;
        return this;
    }

    public List<ProcessingFile> getProcessingFiles() {
        return processingFiles;
    }

    public void setProcessingFiles(List<ProcessingFile> processingFiles) {
        this.processingFiles = processingFiles;
    }

    public boolean getStatus() {
        return status;
    }

    public List<String> getLogStrings() {
        return logStrings;
    }

    public void setLogStrings(List<String> logStrings) {
        this.logStrings = logStrings;
    }

    public BillingConverterResult addLog(String string) {
        logStrings.add(string);
        return this;
    }

    public List<BillingConverterResult> getBillingConverterResults() {
        return billingConverterResults;
    }

    public void setBillingConverterResults(List<BillingConverterResult> billingConverterResults) {
        this.billingConverterResults = billingConverterResults;
    }

}
