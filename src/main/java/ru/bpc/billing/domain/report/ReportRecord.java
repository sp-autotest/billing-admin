package ru.bpc.billing.domain.report;

import ru.bpc.billing.domain.ProcessingRecord;
import ru.bpc.billing.domain.bo.BORecord;
import ru.bpc.billing.domain.bo.OperationType;

import java.math.BigDecimal;

/**
 * User: Krainov
 * Date: 15.08.14
 * Time: 14:30
 */
public class ReportRecord {

    private String documentNumber;
    private String invoiceNumber;
    private String countryCode;
    private String rbsId;
    private String currency;
    private OperationType operationType;
    private BigDecimal fee = BigDecimal.ZERO;
    private String currencyOperation;
    private String currencyMPS;
    private String currencyBank;
    private String rateMPS;
    private String rateBank;
    private BigDecimal amount;
    private BigDecimal grossOperation;
    private BigDecimal grossMps;
    private BigDecimal grossBank;
    private String errorMessage;
    private BigDecimal feeRate;
    private String expirationDate;
    private String approvalCode;
    private String operSign;
    private String pan;
    private boolean success = true;
    private boolean reject;
    private boolean successOnReject;
    private boolean isCredit;
    private BORecord boRecord;
    private ProcessingRecord processingRecord;

    public boolean isCredit() {
        return isCredit;
    }
    public boolean isSuccess() {
        return success;
    }
    public boolean isReject() {
        return reject;
    }
    public boolean isSuccessOnReject() {
        return successOnReject;
    }
    public OperationType getOperationType() {
        return operationType;
    }
    public void setReject() {
        this.success = false;
        this.successOnReject = false;
        this.reject = true;
    }
    public void setSuccessOnReject() {
        this.success = false;
        this.reject = false;
        this.successOnReject = true;
    }
    public void setSuccess() {
        this.success = true;
        this.reject = false;
        this.successOnReject = false;
    }
    public void setGrossOperation(int amount, boolean isCredit) {
        this.grossOperation = isCredit ? new BigDecimal(amount).negate() : new BigDecimal(amount);
    }
    public BigDecimal getGrossOperation() {
        return grossOperation;
    }

    public BigDecimal getGrossMps() {
        return grossMps;
    }

    public BigDecimal getGrossBank() {
        return grossBank;
    }

    public void setBORevenue(BORecord record) {
        this.boRecord = record;
        approvalCode = record.getAUTH_CODE();
        operSign = record.getOPER_SIGN();
        operationType = record.getOperationType();
        currencyOperation = record.getCURRENCY_CLIENT();
        currencyMPS = record.getCURRENCY_MPS();
        currencyBank = record.getCURRENCY();
        rateMPS = record.getRATE_MPS();
        rateBank = record.getRATE_CB();
        rbsId = record.getRBS_ORDER();

        if ( null != record.getMSC() ) fee = new BigDecimal(record.getMSC());
        isCredit = record.isCredit();

        if ( null != record.getAMOUNT_IN_CURRENCY_CLIENT() ) {
            BigDecimal amountInCurrencyClient = new BigDecimal(record.getAMOUNT_IN_CURRENCY_CLIENT());
            amount = record.isCredit() ? amountInCurrencyClient.negate() : amountInCurrencyClient;
            grossOperation = null != record.getAMOUNT_IN_CURRENCY_CLIENT() ? new BigDecimal(record.getAMOUNT_IN_CURRENCY_CLIENT()) : null;
        }
        grossMps = null != record.getAMOUNT_IN_CURRENCY_MPS() ? new BigDecimal(record.getAMOUNT_IN_CURRENCY_MPS()) : null;
        grossBank = null != record.getAMOUNT_IN_RUB() ? new BigDecimal(record.getAMOUNT_IN_RUB()) : null;
    }

    public BORecord getBoRecord() {
        return boRecord;
    }

    public BigDecimal getAmount() {
        return amount;
    }


    public BigDecimal getFee() {
        return fee;
    }

    public String getRateMPS() {
        return rateMPS;
    }

    public void setRateMPS(String rateMPS) {
        this.rateMPS = rateMPS;
    }

    public String getRateBank() {
        return rateBank;
    }

    public void setRateBank(String rateBank) {
        this.rateBank = rateBank;
    }

    public String getCurrencyOperation() {
        return currencyOperation;
    }

    public String getCurrencyMPS() {
        return currencyMPS;
    }

    public void setCurrencyOperation(String currencyOperation) {
        this.currencyOperation = currencyOperation;
    }
    public void setCurrencyMPS(String currencyMPS) {
        this.currencyMPS = currencyMPS;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getRbsId() {
        return rbsId;
    }

    public void setRbsId(String rbsId) {
        this.rbsId = rbsId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }

    public String getOperSign() {
        return operSign;
    }

    public void setOperSign(String operSign) {
        this.operSign = operSign;
    }

    public ProcessingRecord getProcessingRecord() {
        return processingRecord;
    }

    public void setProcessingRecord(ProcessingRecord processingRecord) {
        this.processingRecord = processingRecord;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ReportRecord{");
        sb.append("documentNumber='").append(documentNumber).append('\'');
        sb.append(", invoiceNumber='").append(invoiceNumber).append('\'');
        sb.append(", countryCode='").append(countryCode).append('\'');
        sb.append(", rbsId='").append(rbsId).append('\'');
        sb.append(", currency='").append(currency).append('\'');
        sb.append(", operationType=").append(operationType);
        sb.append(", fee=").append(fee);
        sb.append(", currencyOperation='").append(currencyOperation).append('\'');
        sb.append(", currencyMPS='").append(currencyMPS).append('\'');
        sb.append(", currencyBank='").append(currencyBank).append('\'');
        sb.append(", rateMPS='").append(rateMPS).append('\'');
        sb.append(", rateBank='").append(rateBank).append('\'');
        sb.append(", amount=").append(amount);
        sb.append(", grossOperation=").append(grossOperation);
        sb.append(", grossMps=").append(grossMps);
        sb.append(", grossBank=").append(grossBank);
        sb.append(", errorMessage='").append(errorMessage).append('\'');
        sb.append(", feeRate=").append(feeRate);
        sb.append(", expirationDate='").append(expirationDate).append('\'');
        sb.append(", approvalCode='").append(approvalCode).append('\'');
        sb.append(", operSign='").append(operSign).append('\'');
        sb.append(", success=").append(success);
        sb.append(", reject=").append(reject);
        sb.append(", successOnReject=").append(successOnReject);
        sb.append(", isCredit=").append(isCredit);
        sb.append('}');
        return sb.toString();
    }
}
