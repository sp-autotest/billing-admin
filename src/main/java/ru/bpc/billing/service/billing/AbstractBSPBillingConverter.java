package ru.bpc.billing.service.billing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bpc.billing.domain.Carrier;
import ru.bpc.billing.domain.PaymentSystem;
import ru.bpc.billing.domain.TransactionType;
import ru.bpc.billing.domain.billing.BillingFile;
import ru.bpc.billing.domain.billing.BillingFileFormat;
import ru.bpc.billing.domain.billing.bsp.*;
import ru.bpc.billing.domain.posting.PostingRecordBuilderResult;
import ru.bpc.billing.domain.posting.PostingRecordType;
import ru.bpc.billing.domain.posting.sv.SvPostingHeader;
import ru.bpc.billing.domain.posting.sv.SvPostingRecord;
import ru.bpc.billing.domain.posting.sv.SvPostingTrailer;
import ru.bpc.billing.service.CurrencyService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.UUID;

import static ru.bpc.billing.util.PostingUtils.*;
/**
 * User: Krainov
 * Date: 14.08.14
 * Time: 17:02
 */
public abstract class AbstractBSPBillingConverter extends AbstractBillingConverter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private SimpleDateFormat bspProcessingDateFormat = new SimpleDateFormat("yyyyMMddmmss");

    @Override
    protected Class[] getFormatClasses() {
        return BillingFileFormat.BSP.getClasses();
    }

    @Override
    protected Date getProcessingDateByFirstRecordOfBillingFile(Object record) {
        if ( record instanceof IFH ) {
            try {
                IFH ifh = (IFH)record;
                return bspProcessingDateFormat.parse(ifh.getProcessingDate());
            } catch (Exception e) {
                logger.error("Error find billing BSP file in database", e);
            }
        }
        return null;
    }

    public boolean checkRecordType(Object currentRecord, Object lastRecord) {
        boolean result;
        if ( lastRecord == null ) {//first record in file
            result = currentRecord instanceof IFH;
        }
        else if ( currentRecord == null ) {//last record
            result = lastRecord instanceof IFT;
        }
        else {
            Class currentRecordType = currentRecord.getClass();
            Class lastRecordType = lastRecord.getClass();
            if (lastRecordType == IFH.class) {//second record in file and start file
                result = currentRecordType == IBR.class || currentRecordType == IIH.class || currentRecordType == IFT.class;
            }
            else if (lastRecordType == IIH.class) {//start invoice
                result = currentRecordType == IBH.class || currentRecordType == IIT.class;
            }
            else if (lastRecordType == IBH.class) {//start batch
                result = currentRecordType == IBR.class || currentRecordType == IBT.class;
            }
            else if (lastRecordType == IBR.class) {//Transaction Basic Record
                result = currentRecordType == IBR.class || currentRecordType == IBT.class || currentRecordType == IFT.class;
            }
            else if ( lastRecordType == IBT.class ) {//finish batch
                result = currentRecordType == IBH.class || currentRecordType == IIT.class;
            }
            else if ( lastRecordType == IIT.class ) {//finish invoice
                result = currentRecordType == IIH.class || currentRecordType == IFT.class;
            }
            else if ( lastRecordType == IFT.class) {//finish segment
                result = currentRecordType == IFH.class;
            }
            else {
                result = false;
            }
            if (!result) logger.error("Record sequence error: [{}] after [{}]", getShortClassName(currentRecordType), getShortClassName(lastRecordType));
        }

        return result;
    }

    @Override
    protected String getAgrn(Object currentRecord) {
        Class currentRecordType = currentRecord.getClass();
        if (  currentRecordType == IIH.class ) {
            IIH iih = (IIH)currentRecord;
            return iih.getAGRN();
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

    protected Object buildPostingHeader(IFH ifh) throws BillingConverterException {
        SvPostingHeader header = new SvPostingHeader();
        Date now = new Date();

        header.setFileCreationDate(new SimpleDateFormat(POSTING_DATE_PATTERN).format(now));
        header.setFileCreationTime(new SimpleDateFormat(POSTING_TIME_PATTERN).format(now));

        if (isBlankOrEqualsNull(ifh.getPRDA())) {
            throw new BillingConverterException("PRDA.empty");
        } else {
            header.setSettlementDate(ifh.getPRDA());
        }

        if (isBlankOrEqualsNull(ifh.getTIME())) {
            throw new BillingConverterException("TIME.empty");
        } else {
            header.setSettlementTime(ifh.getTIME() + "00");
        }
        return header;
    }

    protected PostingRecordBuilderResult buildPostingRecord(IIH iih, IBR ibr, Carrier carrier) {
        SvPostingRecord record = new SvPostingRecord();
        PostingRecordBuilderResult result = new PostingRecordBuilderResult(record);

        // Approval code
        if (isBlankOrEqualsNull(ibr.getAPLC())) {
            //result.addConverterException(new BillingConverterException("APLC.empty"));
            record.setApprovalCode("000000");//AFBRBS-2455
        } else {
            record.setApprovalCode(ibr.getAPLC().trim());
        }

        // Expiration date
        if ( ibr.isDebit() ) { //AFBRBS-2537
            if (isBlankOrEqualsNull(ibr.getEXDA())) {
//                result.addConverterException(new BillingConverterException("EXDA.empty"));
                record.setExpirationDate(ibr.getEXDA());
            }
            else if (ibr.getEXDA().length() != 4) {
//                result.addConverterException(new BillingConverterException("EXDA.invalid"));
                record.setExpirationDate(ibr.getEXDA());
            }
            else if ( "0000".equals(ibr.getEXDA()) && ibr.isDebit() ) {
//                result.addConverterException(new BillingConverterException("EXDA.invalid"));
                record.setExpirationDate(ibr.getEXDA());
            }
            else {
                record.setExpirationDate(Calendar.getInstance().get(Calendar.YEAR) / 100 + ibr.getEXDA().substring(2) + ibr.getEXDA().substring(0, 2));//todo: this algorithm not so good
            }
        }
        else {
            record.setExpirationDate(ibr.getEXDA());
        }

        // Network reference number
        if (isBlankOrEqualsNull(ibr.getTDNR())) {
            result.addConverterException(new BillingConverterException("TDNR.empty"));
        } else {
            try {
                String tdnr = ibr.getTDNR();
                StringBuilder sb = new StringBuilder();
                if (tdnr.startsWith("0"))
                    sb.append("0");
                sb.append(Long.valueOf(tdnr).toString());
                record.setNetworkRefNumber(sb.toString());
            } catch (Exception e) {
                result.addConverterException(new BillingConverterException("TDNR.invalid"));
            }
        }

        // PAN
        if (isBlankOrEqualsNull(ibr.getCCAC())) {
            result.addConverterException(new BillingConverterException("CCAC.empty"));
        } else {
            record.setPan(ibr.getCCAC().trim());
        }

        // Processing code
        // Action code
        // SVFE transaction type
        if (isBlankOrEqualsNull(ibr.getDBCR())) {
            result.addConverterException(new BillingConverterException("DBCR.empty"));
        } else if (!ibr.isDBCRSupported(ibr.getDBCR())) {
            result.addConverterException(new BillingConverterException("DBCR.invalid"));
        } else {
            record.setProcessingCode("DB".equals(ibr.getDBCR()) ? "000000" : "200000");
            record.setActionCode("DB".equals(ibr.getDBCR()) ? TransactionType.DR.name() : TransactionType.CR.name());
            record.setSvfeTransactionType("DB".equals(ibr.getDBCR()) ? "77400" : "77500");
        }

        // SVFE system date
        // Transmission date
        if (isBlankOrEqualsNull(ibr.getDAIS())) {
            result.addConverterException(new BillingConverterException("DAIS.empty"));
        } else {
            record.setSvfeSystemDate(ibr.getDAIS().substring(4));
            record.setTransmissionDate(ibr.getDAIS().substring(4) + "000000");
        }

        // Terminal id
        /*
        String terminalId;
        if (isBlankOrEqualsNull(ibr.getISOC())) {
            result.addConverterException(new BillingConverterException("ISOC.empty"));
        } else if ((terminalId = getTerminalId(ibr.getISOC())) == null) {
            result.addConverterException(new BillingConverterException("ISOC.invalid"));
        } else {
            record.setTerminalId(terminalId);
        }
        */
        String terminalId;
        if (isBlankOrEqualsNull(iih.getAGRN())) {
            result.addConverterException(new BillingConverterException("AGRN.empty"));
        } else if ( null == (terminalId = getTerminalIdByArgn(iih.getAGRN())) ) {
            result.addConverterException(new BillingConverterException("AGRN.invalid"));
        } else {
            record.setTerminalId(terminalId);
        }


        // Transaction amount
        // Actual transaction amount
        String sAmount = null;
        if (isBlankOrEqualsNull(ibr.getCDCA())) {
            result.addConverterException(new BillingConverterException("CDCA.empty"));
        } else if (ibr.getCDCA().length() > 12 && !ibr.getCDCA().startsWith("00")) {// length > 12, not starts with "00"
            result.addConverterException(new BillingConverterException("CDCA.too-long"));
        } else if (ibr.getCDCA().length() > 12) {// length > 12, starts with "00"
            sAmount = ibr.getCDCA().substring(ibr.getCDCA().length() - 12);
        } else {// length <= 12
            sAmount = ibr.getCDCA();
        }
        if ( null != sAmount ) {
            record.setTransactionAmount(sAmount);
            record.setActualTransactionAmount(sAmount);
            try {
                Integer amount = Integer.parseInt(sAmount);
                if ( amount <= 0 ) {
                    result.addConverterException(new BillingConverterException("CDCA.invalid"));
                }
            }catch (Exception e) {
                result.addConverterException(new BillingConverterException("CDCA.not-number"));
            }
        }


        // Transaction currency
        if (isBlankOrEqualsNull(ibr.getCUTP())) {
            result.addConverterException(new BillingConverterException("CUTP.empty"));
        } else {
            Currency currency = CurrencyService.findByAlphaCode(ibr.getCUTP().substring(0, 3));
            if (currency == null) {
                result.addConverterException(new BillingConverterException("CUTP.invalid"));
            } else {
                record.setTransactionCurrency(String.valueOf(currency.getNumericCode()));
            }
        }

        //Utrnno
        String utrnno = String.valueOf(sequenceService.nextForUtrnno());
        record.setUtrnno(utrnno);

        //iata code
        String iataCode = carrier.getIataCode();

        //mcc
        record.setMcc(carrier.getMcc());

        //unique number
        record.setRbsId(UUID.randomUUID().toString());//AFBRBS-2069

        String traceId = getTraceId(ibr);

        // Retrieval reference number
        // BER-TLV data
        String passengerName = ibr.getPXNM();
        if ( isBlankOrEqualsNull(passengerName) ) passengerName = PASSENGER_NAME_DEFAULT;
        if (isBlankOrEqualsNull(passengerName)) {
            result.addConverterException(new BillingConverterException("PXNM.empty"));
        } else if (isBlankOrEqualsNull(ibr.getINVN())) {
            result.addConverterException(new BillingConverterException("INVN.empty"));
        } else {
//            record.setRetrievalRefNumber(ibr.getRECO());
            String reco = !isBlankOrEqualsNull(ibr.getRECO()) ? ibr.getRECO() : ibr.getTDNR(); // AFBRBS-1774
            record.setRefNum(reco);
            String posn = !isBlankOrEqualsNull(ibr.getPOSN()) ? ibr.getPOSN() : "aeroflot " + ibr.getTDNR(); // AFBRBS-1774
            StringBuilder sb = new StringBuilder("DF855203BSP"); // Тип платёжного агрегатора
            sb.append("DF8552").append(toHexString(iataCode)).append(iataCode);
            sb.append("DF8553").append(toHexString(passengerName)).append(passengerName); // Имя пассажира
            sb.append("DF8555").append(toHexString(posn)).append(posn); // Наименование точки продажи билета
            sb.append("DF8558").append(toHexString(reco)).append(reco); // Retrieval reference number
            sb.append("DF8559").append(toHexString(ibr.getINVN())).append(ibr.getINVN()); // Invoice number
            sb.append("DF8556").append(toHexString(record.getRbsId())).append(record.getRbsId());//AFBRBS-2069
            sb.append("DF8428").append(toHexString(traceId)).append(traceId);//traceId AFBRBS-3124
            sb.append("DF8269").append(toHexString(utrnno)).append(utrnno);
            sb.append("DF8442").append(toHexString(utrnno)).append(utrnno);
            record.setBerTlvData(sb.toString());
        }

        //invoice number
        record.setInvoiceNumber(ibr.getINVN());

        //invoice date
        record.setInvoiceDate(ibr.getINVD());

        //country code
        record.setCountryCode(ibr.getISOC());


        if ( !isCurrencyBelongToThisCountry(record) ) {
            result.addConverterException(new BillingConverterException("ISOC_CUTP.invalid"));
        }

        if (!result.isSuccess()) {
            record.setType(PostingRecordType.REJECT);
            record.setReversalFlag("1");
            record.setErrorMessage(result.getFirstErrorIfExists());
        }
        return result;
    }

    protected Object buildPostingTrailer(int recordCount) {
        SvPostingTrailer trailer = new SvPostingTrailer();
        trailer.setNumberOfRecordsInBody(Integer.toString(recordCount));
        return trailer;
    }

    protected String getTraceId(IBR ibr) {
        String fpti = ibr.getFPTI();
        if ( null == fpti ) {
            return ibr.getFPTIIfNull();
        }
        String pan = ibr.getCCAC();
        if ( null == pan ) return fpti;
        String traceId = fpti;
        PaymentSystem paymentSystem = getPaymentSystem(pan);
        if ( null == paymentSystem ) return traceId;
        paymentSystem = PaymentSystem.VISA;
        switch (paymentSystem) {
            case MASTERCARD: {
                if ( 1 > fpti.length() ) {
                    logger.warn("TraceId [FPTI]: {} length less 1",fpti);
                    return traceId;
                }
                traceId = fpti.substring(0,15);
                break;
            }
            case VISA: {
                if ( 16 > fpti.length() ) {
                    logger.warn("TraceId [FPTI]: {} length less 16",fpti);
                    return traceId;
                }
                traceId = fpti.substring(0,15);
                break;
            }
        }
        return traceId;
    }

}
