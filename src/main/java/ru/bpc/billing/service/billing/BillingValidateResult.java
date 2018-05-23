package ru.bpc.billing.service.billing;

import ru.bpc.billing.domain.Carrier;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * User: Krainov
 * Date: 15.08.14
 * Time: 11:05
 */
public class BillingValidateResult {

    private File file;
    private Date processingDate;
    private Integer countLines;
    private boolean isSuccess;
    private Set<String> agrnCodes = new HashSet<>();
    private Carrier carrier;

    public BillingValidateResult(File file) {
        this.file = file;
    }

    public Date getProcessingDate() {
        return processingDate;
    }

    public void setProcessingDate(Date processingDate) {
        this.processingDate = processingDate;
    }

    public Integer getCountLines() {
        return countLines;
    }

    public void setCountLines(Integer countLines) {
        this.countLines = countLines;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public Carrier getCarrier() {
        return carrier;
    }

    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    public Set<String> getAgrnCodes() {
        return agrnCodes;
    }

    public void setAgrnCodes(Set<String> agrnCodes) {
        this.agrnCodes = agrnCodes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("BillingValidateResult");
        sb.append(", processingDate=").append(processingDate);
        sb.append(", countLines=").append(countLines);
        sb.append(", isSuccess=").append(isSuccess);
        sb.append(", carrier=").append(carrier);
        sb.append('}');
        return sb.toString();
    }
}
