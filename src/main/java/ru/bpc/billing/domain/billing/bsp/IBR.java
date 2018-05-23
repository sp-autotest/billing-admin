package ru.bpc.billing.domain.billing.bsp;

import org.apache.commons.lang.StringUtils;
import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;
import org.jsefa.flr.lowlevel.Align;

import java.util.Arrays;
import java.util.List;

/**
 * Transaction Basic Record – IBR
 */
@FlrDataType(defaultPrefix = "IBR")
public class IBR {
    private static final List<String> dbcrValues = Arrays.asList("DB", "CR");

    @FlrField(pos = 1, length = 30)
    private String FILLER_2_4; // (fields 2 - 4)

    @FlrField(pos = 2, length = 14)
    private String CDCA; // Settlement debit/credit amount (field 5)

    @FlrField(pos = 3, length = 4)
    private String CUTP; // CUTP field generated for IIH record (field 6)

    @FlrField(pos = 4, length = 2)
    private String DBCR; // Debit/credit code: “DB” for 5 or 9 transaction types; otherwise, “CR” (field 7)

    @FlrField(pos = 5, length = 41)
    private String FILLER_8_13; // (fields 8 - 13)

    @FlrField(pos = 6, length = 2)
    private String ISOC; // ISO country code (fields 14)

    @FlrField(pos = 7, length = 9)
    private String FILLER_15_16; // (fields 15 - 16)

    @FlrField(pos = 8, length = 19)
    private String CCAC; // Credit card account number. (field 17)

    @FlrField(pos = 9, length = 4)
    private String EXDA; // Expiry date. If Vivaldi token number is shown, the expiry date field is “0000”, except for sub-formats ATCCCSP and ATC20CCSP that is filled in with blank spaces. (field 18)

    @FlrField(pos = 10, length = 6)
    private String APLC; // Approval code. If no value in database, the field shows “000000” (field 19)

    @FlrField(pos = 11, length = 2)
    private String FILLER_20; // (field 20)

    @FlrField(pos = 12, length = 15)
    private String TDNR; // Ticket/document number (field 21)

    @FlrField(pos = 13, length = 1)
    private String FILLER_22; // (field 22)

    @FlrField(pos = 14, length = 8)
    private String DAIS; // Date of Issue in format YYYYMMDD (field 23)

    @FlrField(pos = 15, length = 14)
    private String INVN; // Invoice number (field 24)

    @FlrField(pos = 16, length = 8)
    private String INVD; //Invoice date. (field 25)

    @FlrField(pos = 17, length = 18)
    private String FILLER_24_27; // (fields 26 - 27)

    @FlrField(pos = 18, length = 25)
    private String POSN; // Point of Sale Name (field 28)

    @FlrField(pos = 19, length = 15)
    private String FILLER_29; // (field 29)

    @FlrField(pos = 20, length = 49)
    private String PXNM; // Passenger name (field 30)

    @FlrField(pos = 21, length = 39)
    private String FILLER_31_36; // (fields 31 - 41)

    @FlrField(pos = 22, length = 25, align = Align.RIGHT, padCharacter = ' ')
    private String FPTI; // (field 37)

    @FlrField(pos = 23, length = 78)
    private String FILLER_38_41; //(fileds 38-41)

    @FlrField(pos = 24, length = 15)
    private String RECO; // Reference code (field 42)


    public String getCDCA() {
        return CDCA;
    }

    public String getCUTP() {
        return CUTP;
    }

    public String getDBCR() {
        return DBCR;
    }

    public String getCCAC() {
        return CCAC;
    }

    public String getEXDA() {
        return EXDA;
    }

    public String getAPLC() {
        return APLC;
    }

    public String getTDNR() {
        return TDNR;
    }

    public String getDAIS() {
        return DAIS;
    }

    public String getPOSN() {
        return POSN;
    }

    public String getPXNM() {
        return PXNM;
    }

    public String getRECO() {
        return RECO;
    }

    public String getISOC() {
        return ISOC;
    }

    public String getINVN() {
        return INVN;
    }

    public boolean isDBCRSupported(String dbcr) {
        return dbcrValues.contains(dbcr);
    }

    public String getINVD() {
        return INVD;
    }

    public boolean isDebit() {
        return null != DBCR && "DB".equalsIgnoreCase(DBCR);
    }

    public String getFPTI() {
        return FPTI;
    }

    public void setFPTI(String FPTI) {
        this.FPTI = FPTI;
    }

    public String getFPTIIfNull() {
        return StringUtils.leftPad(" ",25);
    }
}
