package ru.bpc.billing.domain.bo.sv;

import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;
import org.jsefa.flr.lowlevel.Align;
import ru.bpc.billing.domain.ProcessingFile;
import ru.bpc.billing.domain.ProcessingRecord;
import ru.bpc.billing.domain.TransactionType;
import ru.bpc.billing.domain.bo.BORecord;
import ru.bpc.billing.domain.bo.OperationType;

/**
 * User: Krainov
 * Date: 11.08.14
 * Time: 15:34
 */
@FlrDataType
public class SvBORevenue implements BORecord {

    @FlrField(pos = 1, length = 2, required = false, align = Align.RIGHT, padCharacter = '0')
    private String STTT_TYPE;
    @FlrField(pos = 3, length = 8, required = false, format = "yyyyMMdd", align = Align.LEFT, padCharacter = ' ')
    private String OPER_DATE;
    @FlrField(pos = 11, length = 36, required = false, align = Align.LEFT, padCharacter = ' ')
    private String RBS_ORDER;
    @FlrField(pos = 47, length = 12, required = false, align = Align.RIGHT, padCharacter = '0')
    private String AMOUNT_IN_CURRENCY_MPS;
    @FlrField(pos = 59, length = 12, required = false, align = Align.RIGHT, padCharacter = '0')
    private String AMOUNT_IN_CURRENCY_CLIENT;
    @FlrField(pos = 71, length = 12, required = false, align = Align.RIGHT, padCharacter = '0')
    private String AMOUNT_IN_RUB;
    @FlrField(pos = 83, length = 3, required = false, align = Align.LEFT, padCharacter = ' ')
    private String CURRENCY_MPS;
    @FlrField(pos = 86, length = 3, required = false, align = Align.LEFT, padCharacter = ' ')
    private String CURRENCY_CLIENT;
    @FlrField(pos = 89, length = 3, required = false, align = Align.LEFT, padCharacter = ' ')
    private String CURRENCY;
    @FlrField(pos = 92, length = 8, format = "yyyyMMdd", required = false, align = Align.RIGHT, padCharacter = '0')
    private String AUTH_DATE;
    @FlrField(pos = 100, length = 6, required = false, align = Align.RIGHT, padCharacter = '0')
    private String AUTH_TIME;
    @FlrField(pos = 106, length = 12, required = false, align = Align.RIGHT, padCharacter = '0')
    private String MSC;
    @FlrField(pos = 118, length = 2, required = false, align = Align.LEFT, padCharacter = ' ')
    private String OPER_SIGN;
    @FlrField(pos = 120, length = 12, align = Align.RIGHT, padCharacter = '0')
    private String FE_UTRNNO;
    @FlrField(pos = 132, length = 12, required = false, align = Align.RIGHT, padCharacter = '0')
    private String BO_UTRNNO;
    @FlrField(pos = 138, length = 6, align = Align.LEFT, padCharacter = ' ')
    private String AUTH_CODE;
    @FlrField(pos = 150, length = 12, align = Align.LEFT, padCharacter = ' ')
    private String RRN;
    @FlrField(pos = 162, length = 8, required = false, align = Align.LEFT, padCharacter = ' ')
    private String TRANS_TYPE;
    @FlrField(pos = 170, length = 13, required = false, align = Align.LEFT, padCharacter = ' ')
    private String TICKET_NUMBER;
    @FlrField(pos = 183, length = 4, required = false, align = Align.LEFT, padCharacter = ' ')
    private String TYPE_FILE;
    @FlrField(pos = 187, length = 12, required = false, align = Align.LEFT, padCharacter = ' ')
    private String RATE_MPS;
    @FlrField(pos = 199, length = 12, required = false, align = Align.LEFT, padCharacter = ' ')
    private String RATE_CB;
    @FlrField(pos = 211, length = 12, required = false, align = Align.LEFT, padCharacter = ' ')
    private String RATE_INNER;
    @FlrField(pos = 223, length = 1, required = false)
    private String STATUS;
    private Long billingSimpleFileId;

    private ProcessingFile processingFile;
    private ProcessingRecord processingRecord;

    public String getRBS_ORDER() {
        return RBS_ORDER;
    }

    public OperationType getOperationType() {
        OperationType operationType = SvOperationType.valueOfCode(STTT_TYPE);
        if ( operationType.equals(SvOperationType.US_ON_US_BSP) ) {//AFBRBS-2955 (если стоит 79, что операция us-on-us, но валюта другая, поэтому определяем так)
            if ( "840".equals(getCURRENCY_MPS())) operationType = SvOperationType.VISA_ON_US_BSP;
            else if ( "978".equals(getCURRENCY_MPS()) ) operationType = SvOperationType.MC_ON_US_BSP;
        }
        return operationType;
    }

    @Override
    public ProcessingFile getProcessingFile() {
        return this.processingFile;
    }

    @Override
    public void setProcessingFile(ProcessingFile processingFile) {
        this.processingFile = processingFile;
    }

    @Override
    public void setProcessingRecord(ProcessingRecord processingRecord) {
        this.processingRecord = processingRecord;
    }

    @Override
    public ProcessingRecord getProcessingRecord() {
        return processingRecord;
    }

    //AFBRBS-2955
    public OperationType getRealOperationType() {
        OperationType operationType = SvOperationType.valueOfCode(STTT_TYPE);
        return operationType;
    }

    public String getSTTT_TYPE() {
        return STTT_TYPE;
    }

    public boolean isCredit() {
        return "CR".equalsIgnoreCase(OPER_SIGN);
    }

    public boolean isDebit() {
        return "DR".equalsIgnoreCase(OPER_SIGN);
    }

    public String getOPER_DATE() {
        return OPER_DATE;
    }

    /**
     * Сумма Коммиссии MSC по данной одной операции
     * @return
     */
    public String getMSC() {
        return MSC;
    }

    /**
     * Код валюты операции в валюте расчётов с МПС
     * @return
     */
    public String getCURRENCY_MPS() {
        return this.CURRENCY_MPS;
    }

    /**
     * Код валюты счёта клиента
     * @return
     */
    public String getCURRENCY_CLIENT() {
        return this.CURRENCY_CLIENT;
    }

    /**
     * Код валюты для расчётов с Аэрофлотом
     * @return
     */
    public String getCURRENCY() {
        return this.CURRENCY;
    }

    /**
     * Сумма операции в валюте расётов с МПС
     * @return
     */
    public String getAMOUNT_IN_CURRENCY_MPS() {
        return AMOUNT_IN_CURRENCY_MPS;
    }

    public String getAMOUNT_IN_CURRENCY_CLIENT() {
        return AMOUNT_IN_CURRENCY_CLIENT;
    }

    /**
     * Сумма операции в рублях
     * @return
     */
    public String getAMOUNT_IN_RUB() {
        return AMOUNT_IN_RUB;
    }

    /**
     * Курс расчётов с МПС
     * @return
     */
    public String getRATE_MPS() {
        return RATE_MPS;
    }


    /**
     * Модифицированный курс ЦБ
     * @return
     */
    public String getRATE_CB() {
        return RATE_CB;
    }

    /**
     * Внутренний курс Банка
//     * @see BORevenue#getRATE_CB()
     * @return
     */
    @Deprecated
    public String getRATE_INNER() {
        return RATE_INNER;
    }

    public void setSTTT_TYPE(String STTT_TYPE) {
        this.STTT_TYPE = STTT_TYPE;
    }

    public void setOPER_DATE(String OPER_DATE) {
        this.OPER_DATE = OPER_DATE;
    }

    public void setRBS_ORDER(String RBS_ORDER) {
        this.RBS_ORDER = RBS_ORDER;
    }

    public void setAMOUNT_IN_CURRENCY_MPS(String AMOUNT_IN_CURRENCY_MPS) {
        this.AMOUNT_IN_CURRENCY_MPS = AMOUNT_IN_CURRENCY_MPS;
    }

    public void setAMOUNT_IN_CURRENCY_CLIENT(String AMOUNT_IN_CURRENCY_CLIENT) {
        this.AMOUNT_IN_CURRENCY_CLIENT = AMOUNT_IN_CURRENCY_CLIENT;
    }

    public void setAMOUNT_IN_RUB(String AMOUNT_IN_RUB) {
        this.AMOUNT_IN_RUB = AMOUNT_IN_RUB;
    }

    public void setCURRENCY_MPS(String CURRENCY_MPS) {
        this.CURRENCY_MPS = CURRENCY_MPS;
    }

    public void setCURRENCY_CLIENT(String CURRENCY_CLIENT) {
        this.CURRENCY_CLIENT = CURRENCY_CLIENT;
    }

    public void setCURRENCY(String CURRENCY) {
        this.CURRENCY = CURRENCY;
    }

    public void setAUTH_DATE(String AUTH_DATE) {
        this.AUTH_DATE = AUTH_DATE;
    }

    public void setAUTH_TIME(String AUTH_TIME) {
        this.AUTH_TIME = AUTH_TIME;
    }

    public void setMSC(String MSC) {
        this.MSC = MSC;
    }

    public void setOPER_SIGN(String OPER_SIGN) {
        this.OPER_SIGN = OPER_SIGN;
    }

    public void setFE_UTRNNO(String FE_UTRNNO) {
        this.FE_UTRNNO = FE_UTRNNO;
    }

    public void setBO_UTRNNO(String BO_UTRNNO) {
        this.BO_UTRNNO = BO_UTRNNO;
    }

    public void setAUTH_CODE(String AUTH_CODE) {
        this.AUTH_CODE = AUTH_CODE;
    }

    public void setRRN(String RRN) {
        this.RRN = RRN;
    }

    public void setTRANS_TYPE(String TRANS_TYPE) {
        this.TRANS_TYPE = TRANS_TYPE;
    }

    public void setTICKET_NUMBER(String TICKET_NUMBER) {
        this.TICKET_NUMBER = TICKET_NUMBER;
    }

    public void setTYPE_FILE(String TYPE_FILE) {
        this.TYPE_FILE = TYPE_FILE;
    }

    public void setRATE_MPS(String RATE_MPS) {
        this.RATE_MPS = RATE_MPS;
    }

    public void setRATE_CB(String RATE_CB) {
        this.RATE_CB = RATE_CB;
    }

    public void setRATE_INNER(String RATE_INNER) {
        this.RATE_INNER = RATE_INNER;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public boolean isSuccess() {
        return "0".equals(STATUS);
    }

    public void setRejectStatus() {
        this.STATUS = "1";
    }

    public String getAUTH_DATE() {
        return AUTH_DATE;
    }

    public String getAUTH_TIME() {
        return AUTH_TIME;
    }

    public String getOPER_SIGN() {
        return OPER_SIGN;
    }

    public String getFE_UTRNNO() {
        return FE_UTRNNO;
    }

    public String getBO_UTRNNO() {
        return BO_UTRNNO;
    }

    public String getAUTH_CODE() {
        return AUTH_CODE;
    }

    public String getRRN() {
        return RRN;
    }

    public String getTRANS_TYPE() {
        return TRANS_TYPE;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public String getTYPE_FILE() {
        return TYPE_FILE;
    }

    public String getTICKET_NUMBER() {
        return TICKET_NUMBER;
    }

    public Long getBillingSimpleFileId() {
        return billingSimpleFileId;
    }

    public void setBillingSimpleFileId(Long billingSimpleFileId) {
        this.billingSimpleFileId = billingSimpleFileId;
    }

    public TransactionType getTransactionType() {
        return TransactionType.valueOfType(OPER_SIGN);
    }

    @Override
    public boolean isValid() {
        OperationType operationType = getOperationType();
        if ( null == operationType || "-1".equals(operationType.getCode()) ) return false;
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SvBORevenue{");
        sb.append("STTT_TYPE='").append(STTT_TYPE).append('\'');
        sb.append(", OPER_DATE='").append(OPER_DATE).append('\'');
        sb.append(", RBS_ORDER='").append(RBS_ORDER).append('\'');
        sb.append(", AMOUNT_IN_CURRENCY_MPS='").append(AMOUNT_IN_CURRENCY_MPS).append('\'');
        sb.append(", AMOUNT_IN_CURRENCY_CLIENT='").append(AMOUNT_IN_CURRENCY_CLIENT).append('\'');
        sb.append(", AMOUNT_IN_RUB='").append(AMOUNT_IN_RUB).append('\'');
        sb.append(", CURRENCY_MPS='").append(CURRENCY_MPS).append('\'');
        sb.append(", CURRENCY_CLIENT='").append(CURRENCY_CLIENT).append('\'');
        sb.append(", CURRENCY='").append(CURRENCY).append('\'');
        sb.append(", AUTH_DATE='").append(AUTH_DATE).append('\'');
        sb.append(", AUTH_TIME='").append(AUTH_TIME).append('\'');
        sb.append(", MSC='").append(MSC).append('\'');
        sb.append(", OPER_SIGN='").append(OPER_SIGN).append('\'');
        sb.append(", FE_UTRNNO='").append(FE_UTRNNO).append('\'');
        sb.append(", BO_UTRNNO='").append(BO_UTRNNO).append('\'');
        sb.append(", AUTH_CODE='").append(AUTH_CODE).append('\'');
        sb.append(", RRN='").append(RRN).append('\'');
        sb.append(", TRANS_TYPE='").append(TRANS_TYPE).append('\'');
        sb.append(", TICKET_NUMBER='").append(TICKET_NUMBER).append('\'');
        sb.append(", TYPE_FILE='").append(TYPE_FILE).append('\'');
        sb.append(", RATE_MPS='").append(RATE_MPS).append('\'');
        sb.append(", RATE_CB='").append(RATE_CB).append('\'');
        sb.append(", RATE_INNER='").append(RATE_INNER).append('\'');
        sb.append(", STATUS='").append(STATUS).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
