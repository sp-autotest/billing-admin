package ru.bpc.billing.domain.posting.sv;

import org.apache.commons.lang.StringUtils;
import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;
import org.jsefa.flr.lowlevel.Align;
import ru.bpc.billing.domain.TransactionType;
import ru.bpc.billing.domain.posting.PostingFile;
import ru.bpc.billing.domain.posting.PostingRecord;
import ru.bpc.billing.domain.posting.PostingRecordType;
import ru.bpc.billing.util.PostingUtils;
/**
 * Created with IntelliJ IDEA.
 * User: Petrov_M
 * Date: 21.08.13
 * Time: 16:53
 * To change this template use File | Settings | File Templates.
 */

@FlrDataType
public class SvPostingRecord implements PostingRecord {
    @FlrField(pos = 1, length = 19, align = Align.RIGHT, padCharacter = ' ')
    private String pan;

    @FlrField(pos = 2, length = 6, align = Align.RIGHT, padCharacter = '0')
    private String processingCode;

    @FlrField(pos = 3, length = 12, align = Align.RIGHT, padCharacter = '0')
    private String transactionAmount;

    @FlrField(pos = 4, length = 12, padCharacter = '0')
    private String FILLER_4;

    @FlrField(pos = 5, length = 12, padCharacter = '0')
    private String FILLER_5;

    @FlrField(pos = 6, length = 10, align = Align.LEFT, padCharacter = ' ')
    private String transmissionDate;

    @FlrField(pos = 7, length = 8, padCharacter = '0')
    private String FILLER_7;

    @FlrField(pos = 8, length = 8, padCharacter = '0')
    private String FILLER_8;

    @FlrField(pos = 9, length = 8, padCharacter = '0')
    private String CONVERSION_RATE;

    @FlrField(pos = 10, length = 6, padCharacter = '0')
    private String FILLER_10;

    @FlrField(pos = 11, length = 9, padCharacter = '0')
    private String FILLER_11;

    @FlrField(pos = 12, length = 1)
    private String reversalFlag = "0";

    @FlrField(pos = 13, length = 9, padCharacter = '0')
    private String FILLER_13;

    @FlrField(pos = 14, length = 4)
    private String FORCED_POST_FLAG = "1031";

    @FlrField(pos = 15, length = 1)
    private String PHASE = "0";

    @FlrField(pos = 16, length = 6, padCharacter = '0')
    private String FILLER_16;

    @FlrField(pos = 17, length = 4, align = Align.RIGHT, padCharacter = '0')
    private String svfeSystemDate;

    @FlrField(pos = 18, length = 6, align = Align.RIGHT, padCharacter = '0')
    private String expirationDate;

    @FlrField(pos = 19, length = 8, padCharacter = '0')
    private String FILLER_19;

    @FlrField(pos = 20, length = 6, padCharacter = '0')
    private String FILLER_20;

    @FlrField(pos = 21, length = 8, padCharacter = '0')
    private String FILLER_21;

    @FlrField(pos = 22, length = 6, padCharacter = '0')
    private String FILLER_22;

    @FlrField(pos = 23, length = 4, padCharacter = '0')
    private String FILLER_23;

    @FlrField(pos = 24, length = 8, padCharacter = '0')
    private String FILLER_24;

    @FlrField(pos = 25, length = 4, padCharacter = '0')
    private String FILLER_25;

    @FlrField(pos = 26, length = 4)
    private String MCC = "3011";

    @FlrField(pos = 27, length = 3)
    private String ACQUIRING_INSTITUTION_CC = "643";

    @FlrField(pos = 28, length = 3, padCharacter = '0')
    private String PAN_EXTENDED_CC;

    @FlrField(pos = 29, length = 3, padCharacter = '0')
    private String FORWARDING_INSTITUTION_CC;

    @FlrField(pos = 30, length = 3)
    private String POINT_OF_SERVICE_ENTRY_MODE = "812";

    @FlrField(pos = 31, length = 3, padCharacter = '0')
    private String CARD_SEQUENCE_NUMBER;

    @FlrField(pos = 32, length = 3, padCharacter = '0')
    private String FILLER_32;

    @FlrField(pos = 33, length = 2)
    private String POINT_OF_SERVICE_CONDITION_CODE = "59";

    @FlrField(pos = 34, length = 2)
    private String PIN_CAPTURE_CODE = "81";

    @FlrField(pos = 35, length = 8, padCharacter = '0')
    private String FILLER_35;

    @FlrField(pos = 36, length = 8, padCharacter = '0')
    private String FILLER_36;

    @FlrField(pos = 37, length = 8, padCharacter = '0')
    private String FILLER_37;

    @FlrField(pos = 38, length = 8, padCharacter = '0')
    private String FILLER_38;

    @FlrField(pos = 39, length = 11, align = Align.LEFT, padCharacter = ' ')
    private String ACQUIRING_INSTITUTION_CODE = "null";

    @FlrField(pos = 40, length = 11, padCharacter = ' ')
    private String FILLER_40;

    @FlrField(pos = 41, length = 8, padCharacter = ' ')
    private String FILLER_41;

    @FlrField(pos = 42, length = 20, align = Align.LEFT, padCharacter = ' ')
    private String networkRefNumber;

    @FlrField(pos = 43, length = 12, align = Align.LEFT, padCharacter = ' ')
    private String retrievalRefNumber;

    @FlrField(pos = 44, length = 6, align = Align.LEFT, padCharacter = ' ')
    private String approvalCode;

    @FlrField(pos = 45, length = 2)
    private String RESPONSE_CODE = "00";

    @FlrField(pos = 46, length = 3, padCharacter = ' ')
    private String FILLER_45;

    @FlrField(pos = 47, length = 16, align = Align.LEFT, padCharacter = ' ')
    private String terminalId;

    @FlrField(pos = 48, length = 15, padCharacter = ' ')
    private String FILLER_47;

    @FlrField(pos = 49, length = 31, padCharacter = ' ')
    private String FILLER_48;

    @FlrField(pos = 50, length = 31, padCharacter = ' ')
    private String FILLER_49;

    @FlrField(pos = 51, length = 31, padCharacter = ' ')
    private String FILLER_50;

    @FlrField(pos = 52, length = 15, padCharacter = ' ')
    private String FILLER_51;

    @FlrField(pos = 53, length = 15, padCharacter = ' ')
    private String FILLER_52;

    @FlrField(pos = 54, length = 15, padCharacter = ' ')
    private String FILLER_53;

    @FlrField(pos = 55, length = 25, align = Align.LEFT, padCharacter = ' ')
    private String ADDITIONAL_RESPONSED_DATA = "0";

    @FlrField(pos = 56, length = 3, align = Align.RIGHT, padCharacter = '0')
    private String transactionCurrency;

    @FlrField(pos = 57, length = 3, align = Align.LEFT, padCharacter = ' ')
    private String SETTLEMENT_CURRENCY = "0";

    @FlrField(pos = 58, length = 3, padCharacter = '0')
    private String FILLER_57;

    @FlrField(pos = 59, length = 120, padCharacter = ' ')
    private String FILLER_58;

    @FlrField(pos = 60, length = 3, padCharacter = '0')
    private String NETWORK_MANAGEMENT_INFO_CODE;

    @FlrField(pos = 61, length = 12, padCharacter = ' ')
    private String FILLER_60;

    @FlrField(pos = 62, length = 30, padCharacter = '0')
    private String FILLER_61;

    @FlrField(pos = 63, length = 42, padCharacter = ' ')
    private String FILLER_62;

    @FlrField(pos = 64, length = 11, padCharacter = '0')
    private String FILLER_63;

    @FlrField(pos = 65, length = 16, padCharacter = ' ')
    private String FILLER_64_1;

    @FlrField(pos = 66, length = 1, padCharacter = ' ')
    private String FILLER_64_2;

    @FlrField(pos = 67, length = 4, padCharacter = ' ')
    private String FILLER_64_3;

    @FlrField(pos = 68, length = 3, padCharacter = '0')
    private String FILLER_64_4;

    @FlrField(pos = 69, length = 13, padCharacter = ' ')
    private String FILLER_64_5;

    @FlrField(pos = 70, length = 5, align = Align.RIGHT, padCharacter = '0')
    private String svfeTransactionType;

    @FlrField(pos = 71, length = 3)
    private String SVFE_RESPONSE_CODE = "-01";

    @FlrField(pos = 72, length = 3, padCharacter = '0')
    private String ATM_RESPONSE_CODE;

    @FlrField(pos = 73, length = 24, padCharacter = '0')
    private String FILLER_68;

    @FlrField(pos = 74, length = 24, padCharacter = '0')
    private String FILLER_69;

    @FlrField(pos = 75, length = 5, padCharacter = '0')
    private String FILLER_70;

    @FlrField(pos = 76, length = 1, padCharacter = ' ')
    private String FILLER_71;

    @FlrField(pos = 77, length = 16, padCharacter = ' ')
    private String FILLER_72;

    @FlrField(pos = 78, length = 6, padCharacter = '0')
    private String FILLER_73;

    @FlrField(pos = 79, length = 12, align = Align.RIGHT, padCharacter = '0')
    private String actualTransactionAmount;

    @FlrField(pos = 80, length = 5, align = Align.LEFT, padCharacter = ' ')
    private String TERMINAL_TYPE = "MNL";

    @FlrField(pos = 81, length = 1)
    private String COMPLETION_STATUS = "1";

    @FlrField(pos = 82, length = 1, padCharacter = '0')
    private String STOOD_IN_FOR;

    @FlrField(pos = 83, length = 1, padCharacter = '0')
    private String ISSUER_POSTED;

    @FlrField(pos = 84, length = 2, align = Align.LEFT, padCharacter = ' ')
    private String actionCode;

    @FlrField(pos = 85, length = 6, padCharacter = '0')
    private String FILLER_80;

    @FlrField(pos = 86, length = 11, padCharacter = ' ')
    private String FILLER_81;

    @FlrField(pos = 87, length = 12, align = Align.LEFT, padCharacter = ' ')
    private String ACQUIRING_INSTITUTION_ID_CODE = "0010";

    @FlrField(pos = 88, length = 3, padCharacter = '0')
    private String FILLER_83;

    @FlrField(pos = 89, length = 1, padCharacter = ' ')
    private String FILLER_84;

    @FlrField(pos = 90, length = 12, padCharacter = ' ')
    private String FILLER_85;

    @FlrField(pos = 91, length = 250, padCharacter = ' ')
    private String FILLER_86;

    @FlrField(pos = 92, length = 2800, align = Align.LEFT, padCharacter = ' ')
    private String berTlvData;

    private String invoiceNumber;
    private String invoiceDate;
    private String countryCode;
    private String rbsId;
    private PostingRecordType type = PostingRecordType.SUCCESS;
    private String errorMessage;
    private String refNum;
    @Deprecated
    private String postingFileName;
    private PostingFile postingFile;
    private String utrnno;
    private String iataCode;
    private Object originalRecord;

    public PostingRecordType getType() {
        return type;
    }

    @Override
    public void setType(PostingRecordType type) {
        this.type = type;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
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

    public void setPan(String pan) {
        this.pan = pan;
    }

    public void setProcessingCode(String processingCode) {
        this.processingCode = processingCode;
    }

    @Override
    public String getProcessingCode() {
        return processingCode;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionAmount() {
        return this.transactionAmount;
    }

    public void setTransmissionDate(String transmissionDate) {
        this.transmissionDate = transmissionDate;
    }

    public void setSvfeSystemDate(String svfeSystemDate) {
        this.svfeSystemDate = svfeSystemDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setNetworkRefNumber(String networkRefNumber) {
        this.networkRefNumber = networkRefNumber;
    }

    public void setRetrievalRefNumber(String retrievalRefNumber) {
        this.retrievalRefNumber = retrievalRefNumber;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public void setTransactionCurrency(String transactionCurrency) {
        this.transactionCurrency = transactionCurrency;
    }

    public String getTransactionCurrency() {
        return this.transactionCurrency;
    }

    public void setSvfeTransactionType(String svfeTransactionType) {
        this.svfeTransactionType = svfeTransactionType;
    }

    public void setActualTransactionAmount(String actualTransactionAmount) {
        this.actualTransactionAmount = actualTransactionAmount;
    }

    //старое название, хотя сюда передается не action_code , а тип операции
    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public void setBerTlvData(String berTlvData) {
        this.berTlvData = berTlvData;
    }

    public String getDocumentNumber() {
        return networkRefNumber;
    }

    public String getBerTlvData() {
        return berTlvData;
    }

    public TransactionType getTransactionType() {
        try {
            if ( StringUtils.isNotBlank(actionCode) )
                return TransactionType.valueOf(actionCode);
        } catch (Exception e) {}
        return TransactionType.UN; //не удалось получить из БСП файла тип операции, поэтому ставим как "неизвестная"
    }

    public String getDocumentDate() {
        return svfeSystemDate;
    }

    public String getReversalFlag() {
        return reversalFlag;
    }

    public void setReversalFlag(String reversalFlag) {
        this.reversalFlag = reversalFlag;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getPan() {
        return pan;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public String getRefNum() {
        return refNum;
    }

    public void setRefNum(String refNum) {
        this.refNum = refNum;
    }

    @Deprecated
    @Override
    public String getPostingFileName() {
        return this.postingFileName;
    }

    @Deprecated
    @Override
    public void setPostingFileName(String postingFileName) {
        this.postingFileName = postingFileName;
    }

    public PostingFile getPostingFile() {
        return postingFile;
    }

    public void setPostingFile(PostingFile postingFile) {
        this.postingFile = postingFile;
    }

    @Override
    public String getUtrnno() {
        return utrnno;
    }

    @Override
    public void setUtrnno(String utrnno) {
        if ( null == utrnno ) return;
        this.utrnno = utrnno;
        StringBuilder sb = new StringBuilder(null != berTlvData ? berTlvData : "");
        sb.append("DF8269").append(PostingUtils.toHexString(utrnno)).append(utrnno);
        sb.append("DF8442").append(PostingUtils.toHexString(utrnno)).append(utrnno);
        setBerTlvData(sb.toString());
    }

    @Override
    public String getIataCode() {
        return iataCode;
    }

    public void setIataCode(String iataCode) {
        this.iataCode = iataCode;
    }

    public void setMcc(String mcc) {
        this.MCC = mcc;
    }

    public void setOriginalRecord(Object originalRecord) {
        this.originalRecord = originalRecord;
    }


}
