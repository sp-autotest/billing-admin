package ru.bpc.billing.domain.billing.arc;

import org.apache.commons.lang.StringUtils;
import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;
import org.jsefa.flr.lowlevel.Align;

import java.util.HashMap;
import java.util.Map;

/**
 * TRANSACTION ADVICE ADDENDUM (TAA) RECORD
 */
@FlrDataType(defaultPrefix = "TAA")
public class TAA {
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


    @FlrField(pos = 1, length = 6)
    private String FILLER_2; // (field 2)

    @FlrField(pos = 2, length = 19)
    private String FPAC; // FORM OF PAYMENT ACCOUNT NUMBER (field 3)

    @FlrField(pos = 3, length = 3)
    private String FILLER_4; // (fields 4)

    @FlrField(pos = 4, length = 20)
    private String PXNM; // PASSENGER NAME (field 5)

    @FlrField(pos = 5, length = 64)
    private String FILLER_6_27; // fields (6 - 26)

    @FlrField(pos = 6, length = 3)
    private String TACN; // TICKETING AIRLINE CODE NUMBER

    @FlrField(pos = 7, length = 10)
    private String TDNR; // TICKET / DOCUMENT NUMBER (field 28)

    @FlrField(pos = 8, length = 28)
    private String FILLER_29_33; // (fields 29 - 33)

    @FlrField(pos = 9, length = 15, align = Align.RIGHT, padCharacter = ' ')
    private String TRIN; //Transaction Identifier (field 34)

    @FlrField(pos = 10, length = 20)
    private String FILLER_35_39; //(fields 35-39)

    @FlrField(pos = 11, length = 11)
    private String TDAM; // TICKET / DOCUMENT AMOUNT (field 40)

    public String getTRINIfNull() {
        return StringUtils.leftPad(" ",15);
    }

    public String getFPAC() {
        return FPAC;
    }

    public String getTDNR() {
        return TDNR;
    }

    public String getTDAM() {
        if ( StringUtils.isBlank(TDAM) || StringUtils.containsIgnoreCase(TDAM,"null") ) return null;
        char lastChar = lastCharTDAM();
        if ( amountConverterMap.containsKey(lastChar) )
            return TDAM.replace(lastChar, amountConverterMap.get(lastChar));
        else return null;
    }

    public String getPXNM() {
        return PXNM;
    }

    private char lastCharTDAM() {
        return TDAM.charAt(TDAM.length() - 1);
    }

    public boolean isDebitOperation() {
        return debitConverterMap.containsKey(lastCharTDAM());
    }

    public boolean isCreditOperation() {
        return creditConverterMap.containsKey(lastCharTDAM());
    }

    public String getTRIN() {
        return TRIN;
    }

    public String getTACN() {
        return TACN;
    }

    public void setTACN(String TACN) {
        this.TACN = TACN;
    }
}
