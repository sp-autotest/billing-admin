package ru.bpc.billing.service.billing;

import ru.bpc.billing.domain.ProcessingRecord;
import ru.bpc.billing.domain.posting.PostingRecord;

/**
 * User: Krainov
 * Date: 05.02.14
 * Time: 16:56
 */
public class ProcessingRecordBuilderResult {

    public enum ProcessingRecordStatus {
        EXIST,
        NEW,
        ERROR;
    }

    private PostingRecord postingRecord;
    private ProcessingRecordStatus status;
    private ProcessingRecord processingRecord;
    private String message;

    public ProcessingRecordStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessingRecordStatus status) {
        this.status = status;
    }

    public ProcessingRecord getProcessingRecord() {
        return processingRecord;
    }

    public void setProcessingRecord(ProcessingRecord processingRecord) {
        this.processingRecord = processingRecord;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PostingRecord getPostingRecord() {
        return postingRecord;
    }

    public void setPostingRecord(PostingRecord postingRecord) {
        this.postingRecord = postingRecord;
    }
}
