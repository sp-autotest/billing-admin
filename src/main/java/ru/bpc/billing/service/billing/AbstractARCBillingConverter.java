package ru.bpc.billing.service.billing;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bpc.billing.domain.Carrier;
import ru.bpc.billing.domain.PaymentSystem;
import ru.bpc.billing.domain.TransactionType;
import ru.bpc.billing.domain.billing.BillingFile;
import ru.bpc.billing.domain.billing.BillingFileFormat;
import ru.bpc.billing.domain.billing.arc.*;
import ru.bpc.billing.domain.posting.PostingRecordBuilderResult;
import ru.bpc.billing.domain.posting.PostingRecordType;
import ru.bpc.billing.domain.posting.sv.SvPostingHeader;
import ru.bpc.billing.domain.posting.sv.SvPostingRecord;
import ru.bpc.billing.service.CurrencyService;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Currency;
import java.util.Date;
import java.util.UUID;

import static ru.bpc.billing.util.PostingUtils.toHexString;

/**
 * User: Krainov
 * Date: 11.08.14
 * Time: 16:28
 */
public abstract class AbstractARCBillingConverter extends AbstractBillingConverter {

    private static final Logger logger = LoggerFactory.getLogger(AbstractARCBillingConverter.class);

    private SimpleDateFormat arcProcessingDateFormat = new SimpleDateFormat("yyMMddmmss");

    @Override
    protected Class[] getFormatClasses() {
        return BillingFileFormat.ARC.getClasses();
    }

    @Override
    protected Date getProcessingDateByFirstRecordOfBillingFile(Object record) {
        if ( record instanceof TFH ) {
            try {
                TFH tfh = (TFH)record;
                return arcProcessingDateFormat.parse(tfh.getProcessingDate());
            } catch (Exception e) {
                logger.error("Error find billing ARC file in database", e);
            }
        }
        return null;
    }

    @Override
    protected boolean checkRecordType(Object currentRecord, Object lastRecord) {
        boolean result;
        if (lastRecord == null) {
            result = currentRecord instanceof TFH;
        }
        else if ( currentRecord == null ) {
            result = lastRecord instanceof TFS;
        }
        else {
            Class currentRecordType = currentRecord.getClass();
            Class lastRecordType = lastRecord.getClass();
            if (lastRecordType == TFH.class) {
                result = currentRecordType == TBH.class;
            }
            else if (lastRecordType == TBH.class) {
                result = Arrays.asList(TBH.class, TAB.class, TFH.class, TFS.class).contains(currentRecordType);//add TFH in AFBRBS-2656
            }
            else if (lastRecordType == TAB.class) {
                TAB lastTab = (TAB) lastRecord;
                if (lastTab.isNotFinancialOperation()) {
                    result = Arrays.asList(TBH.class, TFH.class, TAB.class).contains(currentRecordType);
                } else {
                    result = Arrays.asList(TAA.class, TBH.class, TFH.class).contains(currentRecordType); //add TBH and TFH in AFBRBS-2656
                }
            }
            else if (lastRecordType == TAA.class) {
                result = Arrays.asList(TBH.class, TAB.class, TAA.class, TFH.class, TFS.class).contains(currentRecordType);
            }
            else if ( lastRecordType == TFS.class ) {
                result = currentRecordType == TFH.class;
            }
            else {
                result = false;
            }
            if (!result)
                logger.error("Record sequence error: [{}] after [{}]", getShortClassName(currentRecordType), getShortClassName(lastRecordType));
        }
        return result;
    }

    @Override
    protected String getAgrn(Object currentRecord) {
        Class currentRecordType = currentRecord.getClass();
        if (currentRecordType == TBH.class) {
            TBH tbh = (TBH)currentRecord;
            return tbh.getAGRN();
        }
        return null;
    }

    @Override
    protected BillingConverterResult convertBillingFile(BillingFile billingFile) throws IOException {
        return null;
    }

    @Override
    public String getSystemName() {
        return null;
    }

    protected Object buildPostingHeader(TFH tfh) throws BillingConverterException {
        SvPostingHeader header = new SvPostingHeader();
        Date now = new Date();

        header.setFileCreationDate(new SimpleDateFormat(POSTING_DATE_PATTERN).format(now));
        header.setFileCreationTime(new SimpleDateFormat(POSTING_TIME_PATTERN).format(now));

        if (StringUtils.isBlank(tfh.getPRDA())) {
            throw new BillingConverterException("PRDA.empty");
        } else {
            try {
                header.setSettlementDate(new SimpleDateFormat(POSTING_DATE_PATTERN).format(new SimpleDateFormat(TFH.PRDA_PATTERN).parse(tfh.getPRDA())));
            } catch (ParseException pe) {
                throw new BillingConverterException("PRDA.invalid");
            }
        }

        if (isBlankOrEqualsNull(tfh.getTIME())) {
            throw new BillingConverterException("TIME.empty");
        } else {
            header.setSettlementTime(tfh.getTIME() + "00");
        }
        return header;
    }

    protected PostingRecordBuilderResult buildPostingRecord(TBH tbh, TAB tab, TAA taa, Carrier carrier) {
        SvPostingRecord record = new SvPostingRecord();
        record.setOriginalRecord(taa);
        PostingRecordBuilderResult result = new PostingRecordBuilderResult(record);
        //AFBRBS-2730
        boolean isExchangeOperation = tab.isExchangeOperation();
        String ticketNumber = isExchangeOperation ? tab.getTDNR() : taa.getTDNR();
        String totalAmount = isExchangeOperation ? tab.getFPAM() : taa.getTDAM();
        String airlineCode =  isExchangeOperation ? tab.getTACN() : taa.getTACN();

        // Approval code
        if (isBlankOrEqualsNull(tab.getAPLC())) {
            //result.addConverterException(new BillingConverterException("APLC.empty", TAB.class));
            record.setApprovalCode("000000");//AFBRBS-2455
        } else {
            record.setApprovalCode(tab.getAPLC().trim());
        }

        // Expiration date
        // posting.setExpirationDate(); // Not fills for ARC
        StringBuilder networkRefNumber = new StringBuilder();

        if (isBlankOrEqualsNull(airlineCode)) {
            result.addConverterException(new BillingConverterException("TACN.empty", isExchangeOperation ? TAB.class : TAA.class));
        } else {
            networkRefNumber.append(airlineCode);
        }

        if (isBlankOrEqualsNull(ticketNumber)) { // ticketNumber from AFBRBS-2730
            result.addConverterException(new BillingConverterException("TDNR.empty", isExchangeOperation ? TAB.class : TAA.class));
        } else {
            networkRefNumber.append(ticketNumber);
        }

        if (!isBlankOrEqualsNull(ticketNumber)) {
            try {
                record.setNetworkRefNumber(networkRefNumber.toString());
            } catch (Exception e) {
                result.addConverterException(new BillingConverterException("TDNR_TACN.invalid"));
            }
        }

        // PAN
        if (isBlankOrEqualsNull(tab.getFPAC())) {
            result.addConverterException(new BillingConverterException("FPAC.empty", TAB.class));
        } else {
            record.setPan(tab.getFPAC().trim());
        }

        // Processing code
        // Action code
        // SVFE transaction type
        if (StringUtils.isBlank(tab.getTTID())) {
            result.addConverterException(new BillingConverterException("TTID.empty", TAB.class));
        } else if (!tab.isTTIDSupported(tab.getTTID())) {
            result.addConverterException(new BillingConverterException("TTID.invalid", TAB.class));
        } else {
            record.setProcessingCode(tab.isDebitOperation() ? "000000" : "200000");
            TransactionType transactionType = tab.getTransactionType();
            record.setActionCode(transactionType.name());
            //record.setActionCode("05".equals(tab.getTTID()) ? TransactionType.DR.name() : TransactionType.CR.name());
            record.setSvfeTransactionType(tab.isDebitOperation() ? "77400" : "77500");
        }

        // SVFE system date
        // Transmission date
        if (isBlankOrEqualsNull(tab.getDAIS())) {
            result.addConverterException(new BillingConverterException("DAIS.empty", TAB.class));
        } else {
            record.setSvfeSystemDate(tab.getDAIS().substring(2));
            record.setTransmissionDate(tab.getDAIS().substring(2) + "000000");
        }

        // Terminal id
        String terminalId;
        if ((terminalId = getTerminalIdByArgn(tbh.getAGRN())) == null) {
            result.addConverterException(new BillingConverterException("AGRN.invalid", TAB.class));
        }
        else {
            record.setTerminalId(terminalId);
        }
        /*
        if ((terminalId = getTerminalId("US")) == null) {//country code is hardcoded
            result.addConverterException(new BillingConverterException("no-terminal.for-US", TAB.class));
        } else {
            record.setTerminalId(terminalId);
        }
        */

        // Transaction amount
        // Actual transaction amount
        if (StringUtils.isBlank(totalAmount)) { //totalAmount from AFBRBS-2730
            result.addConverterException(new BillingConverterException("TDAM.empty", isExchangeOperation ? TAB.class : TAA.class));
        } else {
            record.setTransactionAmount(totalAmount);
            record.setActualTransactionAmount(totalAmount);
            try {
                Integer amount = Integer.parseInt(totalAmount);
                if ( amount <= 0 ) {
                    result.addConverterException(new BillingConverterException("TDAM.invalid",isExchangeOperation ? TAB.class : TAA.class));
                }
            }catch (Exception e) {
                result.addConverterException(new BillingConverterException("TDAM.not-number"));
            }

        }

        //7(TAB)TTID operation is not the same 40(TAA) TDAM operation (AFBRBS-2122)
        if (    !tab.isExchangeOperation() && //если tab не является isExchangeOperation
                (
                        !((taa.isCreditOperation() && tab.isCreditOperation()) || (taa.isDebitOperation() && tab.isDebitOperation()))
                )
                ) {
            result.addConverterException(new BillingConverterException("TTID.operationNotTheSameTDAM",TAB.class));
        }

        // Transaction currency
        if (StringUtils.isBlank(tab.getCUTP())) {
            result.addConverterException(new BillingConverterException("CUTP.empty", TAB.class));
        } else {
            Currency currency = CurrencyService.findByAlphaCode(tab.getCUTP());
            if (currency == null) {
                result.addConverterException(new BillingConverterException("CUTP.invalid", TAB.class));
            } else {
                record.setTransactionCurrency(String.valueOf(currency.getNumericCode()));
            }
        }

        // iata code
        String iataCode = carrier.getIataCode();
        record.setIataCode(iataCode);

        //mcc
        record.setMcc(carrier.getMcc());

        //unique number
        record.setRbsId(UUID.randomUUID().toString());//AFBRBS-2069

        String traceId = getTraceId(tbh, tab, taa);

        // Retrieval reference number
        // BER-TLV data
        String passengerName = taa.getPXNM();
        if ( isBlankOrEqualsNull(passengerName) ) passengerName = PASSENGER_NAME_DEFAULT;
        if (isBlankOrEqualsNull(passengerName)) { //PASSENGER NAME
            result.addConverterException(new BillingConverterException("PXNM.empty", TAA.class));
        } else if (isBlankOrEqualsNull(tbh.getINVN())) {
            result.addConverterException(new BillingConverterException("INVN.empty", TBH.class));
        } else {
//            record.setRetrievalRefNumber(tab.getREFN());
            String refn = !StringUtils.isBlank(tab.getREFN()) ? tab.getREFN() : taa.getTDNR(); // AFBRBS-1774
            record.setRefNum(refn);
            String pstn = !StringUtils.isBlank(tab.getPSTN()) ? tab.getPSTN() : "aeroflot " + taa.getTDNR(); // AFBRBS-1774
            StringBuilder sb = new StringBuilder("DF855203ARC"); // Тип платёжного агрегатора
            sb.append("DF8552").append(toHexString(iataCode)).append(iataCode);
            sb.append("DF8553").append(toHexString(passengerName)).append(passengerName); // Имя пассажира
            sb.append("DF8555").append(toHexString(pstn)).append(pstn); // Наименование точки продажи билета
            sb.append("DF8558").append(toHexString(refn)).append(refn); // Retrieval reference number
            sb.append("DF8559").append(toHexString(tbh.getINVN())).append(tbh.getINVN()); // Invoice number
            sb.append("DF8556").append(toHexString(record.getRbsId())).append(record.getRbsId());//AFBRBS-2069
            sb.append("DF8428").append(toHexString(traceId)).append(traceId);//traceId AFBRBS-3124
            if ( !result.isSuccess() ) {
                sb.append("DF8429").append(toHexString("1")).append("1");
            }
            else {
                sb.append("DF8429").append(toHexString("0")).append("0");
            }
            record.setBerTlvData(sb.toString());
        }

        if (!record.getTransactionType().equals(TransactionType.CR_REVERSE)) {
            String utrnno = String.valueOf(sequenceService.nextForUtrnno());
            record.setUtrnno(utrnno);
        }

        //invoice number
        record.setInvoiceNumber(tbh.getINVN());

        //country code
        record.setCountryCode("US");

        if ( !isCurrencyBelongToThisCountry(record) ) {
            result.addConverterException(new BillingConverterException("COUNTRY_CUTP.invalid",SvPostingRecord.class));
        }

        if ( result.isSuccess() ) {
            if ( tab.isReversalOperation() ) {//если отклоненная отмена, то выставляем 1, иначе 0 (оставляем по умолчанию)
                record.setReversalFlag("1");
            }
        }
        else {
            record.setType(PostingRecordType.REJECT);
            if ( tab.isReversalOperation() ) {//если отклоненная отмена, то выставляем 1, иначе 0 (оставляем по умолчанию)
                record.setReversalFlag("1");
            }
            record.setErrorMessage(result.getFirstErrorIfExists());
        }

        return result;
    }

    protected String getTraceId(TBH tbh, TAB tab, TAA taa) {
        String trin = taa.getTRIN();
        if ( null == trin ) {
            return taa.getTRINIfNull();
        }
        String pan = tab.getFPAC();
        if ( null == pan ) return trin;
        String traceId = trin;
        PaymentSystem paymentSystem = getPaymentSystem(pan);
        if ( null == paymentSystem ) return traceId;
        switch (paymentSystem) {
            case MASTERCARD: {
                if ( 4 > trin.length() ) {
                    logger.warn("TraceId [TRIN]: {} has length less 4",trin);
                    return trin;
                }
                String trinDate = trin.substring(0,4);
                traceId = trin.substring(4).trim() + trinDate;
                break;
            }
            case VISA: {
                traceId = trin;
                break;
            }
        }
        return traceId;
    }


}
