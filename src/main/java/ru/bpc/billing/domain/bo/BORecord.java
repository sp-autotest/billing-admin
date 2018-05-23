package ru.bpc.billing.domain.bo;

import ru.bpc.billing.domain.ProcessingFile;
import ru.bpc.billing.domain.ProcessingRecord;
import ru.bpc.billing.domain.TransactionType;

/**
 * User: Krainov
 * Date: 11.08.14
 * Time: 15:35
 */
public interface BORecord {

    public String getRBS_ORDER();
    public OperationType getOperationType();
    public OperationType getRealOperationType();
    public boolean isCredit();
    public boolean isDebit();
    public String getOPER_DATE();
    public String getMSC();
    public String getCURRENCY_MPS();
    public String getCURRENCY_CLIENT();
    public String getCURRENCY();
    public String getAMOUNT_IN_CURRENCY_MPS();
    public String getAMOUNT_IN_CURRENCY_CLIENT();
    public String getAMOUNT_IN_RUB();
    public String getRATE_MPS();
    public String getRATE_CB();
    public boolean isSuccess();
    public String getAUTH_DATE();
    public String getAUTH_TIME();
    public String getOPER_SIGN();
    public String getFE_UTRNNO();
    public String getBO_UTRNNO();
    public String getAUTH_CODE();
    public String getRRN();
    public String getTRANS_TYPE();
    public String getSTATUS();
    public String getTYPE_FILE();
    public String getTICKET_NUMBER();
    public TransactionType getTransactionType();
    public void setRejectStatus();
    public ProcessingFile getProcessingFile();
    public void setProcessingFile(ProcessingFile processingFile);
    public ProcessingRecord getProcessingRecord();
    public void setProcessingRecord(ProcessingRecord processingRecord);

    public boolean isValid();
}
