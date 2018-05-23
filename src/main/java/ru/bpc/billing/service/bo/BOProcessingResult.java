package ru.bpc.billing.service.bo;

import ru.bpc.billing.domain.ProcessingFile;
import ru.bpc.billing.domain.ProcessingRecord;
import ru.bpc.billing.domain.bo.BOFile;
import ru.bpc.billing.domain.bo.BORecord;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Krainov
 * Date: 15.08.14
 * Time: 11:50
 */
public class BOProcessingResult {

    private List<ProcessingFile> processingFiles = new ArrayList<>();
    private List<ProcessingRecord> processingRecords = new ArrayList<>();

    private File originalFile;
    private boolean success;
    private int totalRecords;
    private int successRecords;
    private int errorRecords;
    private int fraudRecords;
    private int depositRecords;
    private int refundRecords;
    private Map<String,BORecord> processedObjects = new HashMap<>();
    private boolean isRejectFile;

    public List<ProcessingRecord> getProcessingRecords() {
        return processingRecords;
    }

    public void setProcessingRecords(List<ProcessingRecord> processingRecords) {
        this.processingRecords = processingRecords;
    }

    public List<ProcessingFile> getProcessingFiles() {
        return processingFiles;
    }

    public void setProcessingFiles(List<ProcessingFile> processingFiles) {
        this.processingFiles = processingFiles;
    }

    private boolean isRejectFile(String fileName) {
        return fileName.toLowerCase().contains("reject");
    }

    public BOProcessingResult set(File originalFile, boolean success, int totalRecords, int successRecords, int errorRecords, int fraudRecords) {
        this.originalFile = originalFile;
        this.success = success;
        this.totalRecords = totalRecords;
        this.successRecords = successRecords;
        this.errorRecords = errorRecords;
        this.fraudRecords = fraudRecords;
        isRejectFile(originalFile.getName());
        return this;
    }
    public BOProcessingResult set(File originalFile, boolean success, int totalRecords, int successRecords, int errorRecords, int fraudRecords, int depositRecords, int refundRecords) {
        set(originalFile, success, totalRecords, successRecords, errorRecords, fraudRecords);
        this.depositRecords = depositRecords;
        this.refundRecords = refundRecords;
        return this;
    }

    public File getOriginalFile() {
        return originalFile;
    }

    public void setOriginalFile(File originalFile) {
        this.originalFile = originalFile;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public int getSuccessRecords() {
        return successRecords;
    }

    public int getErrorRecords() {
        return errorRecords;
    }

    public int getFraudRecords() {
        return fraudRecords;
    }

    public Map<String, BORecord> getProcessedObjects() {
        return processedObjects;
    }

    public void setProcessedObjects(Map<String, BORecord> processedObjects) {
        this.processedObjects = processedObjects;
    }

    public int getCountProcessObjects() {
        return processedObjects != null ? processedObjects.size() : 0;
    }

    public int getDepositRecords() {
        return depositRecords;
    }

    public void setDepositRecords(int depositRecords) {
        this.depositRecords = depositRecords;
    }

    public int getRefundRecords() {
        return refundRecords;
    }

    public void setRefundRecords(int refundRecords) {
        this.refundRecords = refundRecords;
    }

    public boolean isRejectFile() {
        return isRejectFile;
    }

    public void setRejectFile(boolean rejectFile) {
        isRejectFile = rejectFile;
    }

    public BOFile getBoFile() {
        for (ProcessingFile processingFile : getProcessingFiles()) {
            if ( processingFile instanceof BOFile ) return (BOFile)processingFile;
        }
        return null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BOProcessingResult{");
        sb.append("originalFile=").append(originalFile);
        sb.append(", success=").append(success);
        sb.append(", totalRecords=").append(totalRecords);
        sb.append(", successRecords=").append(successRecords);
        sb.append(", errorRecords=").append(errorRecords);
        sb.append(", fraudRecords=").append(fraudRecords);
        sb.append(", depositRecords=").append(depositRecords);
        sb.append(", refundRecords=").append(refundRecords);
        sb.append(", isRejectFile=").append(isRejectFile);
        sb.append(", processedCount=").append(getProcessedObjects().size());
        sb.append('}');
        return sb.toString();
    }
}
