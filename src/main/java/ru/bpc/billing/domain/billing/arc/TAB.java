package ru.bpc.billing.domain.billing.arc;

import org.apache.commons.lang.StringUtils;
import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;
import ru.bpc.billing.domain.TransactionType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TRANSACTION ADVICE BASIC (TAB) RECORD
 */
@FlrDataType(defaultPrefix = "TAB")
public class TAB {
    private static final List<String> ttidValues = Arrays.asList("05", "06", "25");//add 25 in AFBRBS-2655
    private static final Map<Character, Character> amountConverterMap = new HashMap<Character, Character>();
    private static final Map<Character, Character> debitConverterMap = new HashMap<Character, Character>();
    private static final Map<Character, Character> creditConverterMap = new HashMap<Character, Character>();

    static {
        debitConverterMap.put('{', '0');
        debitConverterMap.put('A', '1');
        debitConverterMap.put('B', '2');
        debitConverterMap.put('C', '3');
        debitConverterMap.put('D', '4');
        debitConverterMap.put('E', '5');
        debitConverterMap.put('F', '6');
        debitConverterMap.put('G', '7');
        debitConverterMap.put('H', '8');
        debitConverterMap.put('I', '9');

        creditConverterMap.put('}', '0');
        creditConverterMap.put('J', '1');
        creditConverterMap.put('K', '2');
        creditConverterMap.put('L', '3');
        creditConverterMap.put('M', '4');
        creditConverterMap.put('N', '5');
        creditConverterMap.put('O', '6');
        creditConverterMap.put('P', '7');
        creditConverterMap.put('Q', '8');
        creditConverterMap.put('R', '9');

        amountConverterMap.putAll(debitConverterMap);
        amountConverterMap.putAll(creditConverterMap);
    }

    @FlrField(pos = 1, length = 8)
    private String FILLER_2_3; // (fields 2 - 3)

    @FlrField(pos = 2, length = 19)
    private String FPAC; // FORM OF PAYMENT ACCOUNT NUMBER (field 4)

    @FlrField(pos = 3, length = 3)
    private String FILLER_5_6; // CONSTANT VALUE (fields 5 - 6)

    @FlrField(pos = 4, length = 2)
    private String TTID; // TRANSACTION TYPE IDENTIFIER (field 7)

    @FlrField(pos = 5, length = 8)
    private String FPAM; // FORM OF PAYMENT AMOUNT (field 8)

    @FlrField(pos = 6, length = 3)
    private String CUTP; // CURRENCY TYPE (field 9)

    @FlrField(pos = 7, length = 8)
    private String FILLER_10; // (field 10)

    @FlrField(pos = 8, length = 6)
    private String APLC; // Approval Code (field 11)

    @FlrField(pos = 9, length = 6)
    private String DAIS;

    @FlrField(pos = 10, length = 4)
    private String FILLER_13; // (field 13)

    @FlrField(pos = 11, length = 5)
    private String REFN; // REFERENCE NUMBER (field 14)

    @FlrField(pos = 12, length = 14)
    private String FILLER_15_17; // (fields 15 - 17)

    @FlrField(pos = 13, length = 25)
    private String PSTN; // POINT OF SALE TRADE NAME (field 18)

    @FlrField(pos = 14, length = 8)
    private String AGNT; //AGENT NUMERIC CODE(field 19)

    @FlrField(pos = 15, length = 3)
    private String TRNC; //TRANSACTION CODE (field 20)

    @FlrField(pos = 16, length = 3)
    private String TACN; // TICKETING AIRLINE CODE NUMBER (field 21)

    @FlrField(pos = 17, length = 10)
    private String TDNR; //TICKET / DOCUMENT NUMBER(field 22)


    public String getFPAC() {
        return FPAC;
    }

    public String getTTID() {
        return TTID;
    }

    public String getCUTP() {
        return CUTP;
    }

    public String getAPLC() {
        return APLC;
    }

    public String getREFN() {
        return REFN;
    }

    public String getDAIS() {
        return DAIS;
    }

    public String getPSTN() {
        return PSTN;
    }

    public boolean isTTIDSupported(String ttid) {
        return ttidValues.contains(ttid);
    }

    public String getTDNR() {
        return TDNR;
    }

    public void setTDNR(String TDNR) {
        this.TDNR = TDNR;
    }

    public String getTRNC() {
        return TRNC;
    }

    public void setTRNC(String TRNC) {
        this.TRNC = TRNC;
    }

    public String getFPAM() {
        if ( StringUtils.isBlank(FPAM) || StringUtils.containsIgnoreCase(FPAM,"null") ) return null;
        char lastChar = lastCharFPAM();
        if ( amountConverterMap.containsKey(lastChar) )
            return FPAM.replace(lastChar, amountConverterMap.get(lastChar));
        else return null;
    }

    private char lastCharFPAM() {
        return FPAM.charAt(FPAM.length() - 1);
    }

    public void setFPAM(String FPAM) {
        this.FPAM = FPAM;
    }

    /**
     * 05  Debit Transaction (sale transaction)
     * @return
     */
    public boolean isDebitOperation() {
        return TTID.equals("05");
    }

    /**
     * 06  Credit Transaction (refund transaction)
     * 25  Credit Transaction (reversal transaction)
     * @return
     */
    public boolean isCreditOperation() {
        return isRefundOperation() || isReversalOperation();
    }

    /**
     * 06  Credit Transaction (refund transaction)
     * @return
     */
    public boolean isRefundOperation() {
        return TTID.equals("06");
    }

    /**
     * 25  Credit Transaction (reversal transaction)
     * @return
     */
    public boolean isReversalOperation() {
        return TTID.equals("25") && "REV".equals(TRNC);
    }

    public boolean isNotFinancialOperation() {
        return TTID.equals("09");
    }

    public boolean isExchangeTransaction() {
        return null != TRNC && "EXC".equals(TRNC);
    }

    public boolean isExchangeOperation() {
        return isRefundOperation() || (isDebitOperation() && isExchangeTransaction());
    }

    public String getTACN() {
        return TACN;
    }

    public void setTACN(String TACN) {
        this.TACN = TACN;
    }

    public TransactionType getTransactionType() {
        if ( isDebitOperation() ) return TransactionType.DR;
        else if ( isReversalOperation() ) return TransactionType.CR_REVERSE;
        else if ( isRefundOperation() ) return TransactionType.CR_REFUND;
        return TransactionType.CR;
    }
}