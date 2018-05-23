package ru.bpc.billing.repository;

import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;

@CsvDataType()
public class CSVProcessingRecord {

    @CsvField(pos = 1)
    public Long ID;
    @CsvField(pos = 2)
    public Long AMOUNT;
    @CsvField(pos = 3)
    public Long AMOUNT_MPS;
    @CsvField(pos = 4)
    public Long AMOUNT_RUB;
    @CsvField(pos = 5)
    public String APPROVAL_CODE;
    @CsvField(pos = 6)
    public String COUNTRY_CODE;
    @CsvField(pos = 7)
    public String CREATE_DATE;
    @CsvField(pos = 8)
    public String CURRENCY;
    @CsvField(pos = 9)
    public String DOCUMENT_DATE;
    @CsvField(pos = 10)
    public String DOCUMENT_NUMBER;
    @CsvField(pos = 11)
    public String ERROR_MESSAGE;
    @CsvField(pos = 12)
    public String EXPIRY;
    @CsvField(pos = 13)
    public String INVOICE_DATE;
    @CsvField(pos = 14)
    public String INVOICE_NUMBER;
    @CsvField(pos = 15)
    public String PAN;
    @CsvField(pos = 16)
    public String RATE_CB;
    @CsvField(pos = 17)
    public String RATE_MPS;
    @CsvField(pos = 18)
    public String RBS_ID;
    @CsvField(pos = 19)
    public String REF_NUM;
    @CsvField(pos = 20)
    public String STATUS;
    @CsvField(pos = 21)
    public String TRANSACTION_TYPE;
    @CsvField(pos = 22)
    public Long PARENT_ID;
    @CsvField(pos = 23)
    public Long FK_CARRIER_ID;
    @CsvField(pos = 24)
    public String UTRNNO;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CSVProcessingRecord{");
        sb.append("ID=").append(ID);
        sb.append(", AMOUNT=").append(AMOUNT);
        sb.append(", AMOUNT_MPS=").append(AMOUNT_MPS);
        sb.append(", AMOUNT_RUB=").append(AMOUNT_RUB);
        sb.append(", APPROVAL_CODE='").append(APPROVAL_CODE).append('\'');
        sb.append(", COUNTRY_CODE='").append(COUNTRY_CODE).append('\'');
        sb.append(", CREATE_DATE='").append(CREATE_DATE).append('\'');
        sb.append(", CURRENCY='").append(CURRENCY).append('\'');
        sb.append(", DOCUMENT_DATE='").append(DOCUMENT_DATE).append('\'');
        sb.append(", DOCUMENT_NUMBER='").append(DOCUMENT_NUMBER).append('\'');
        sb.append(", ERROR_MESSAGE='").append(ERROR_MESSAGE).append('\'');
        sb.append(", EXPIRY='").append(EXPIRY).append('\'');
        sb.append(", INVOICE_DATE='").append(INVOICE_DATE).append('\'');
        sb.append(", INVOICE_NUMBER='").append(INVOICE_NUMBER).append('\'');
        sb.append(", PAN='").append(PAN).append('\'');
        sb.append(", RATE_CB='").append(RATE_CB).append('\'');
        sb.append(", RATE_MPS='").append(RATE_MPS).append('\'');
        sb.append(", RBS_ID='").append(RBS_ID).append('\'');
        sb.append(", REF_NUM='").append(REF_NUM).append('\'');
        sb.append(", STATUS='").append(STATUS).append('\'');
        sb.append(", TRANSACTION_TYPE='").append(TRANSACTION_TYPE).append('\'');
        sb.append(", PARENT_ID=").append(PARENT_ID);
        sb.append(", FK_CARRIER_ID=").append(FK_CARRIER_ID);
        sb.append(", UTRNNO='").append(UTRNNO).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
