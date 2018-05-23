package ru.bpc.billing.service.billing;

/**
 * Created with IntelliJ IDEA.
 * User: Petrov_M
 * Date: 29.08.13
 * Time: 10:09
 * To change this template use File | Settings | File Templates.
 */
public class BillingConverterException extends Exception {
    private String errorMessageCode;// error message code
    private Class errorRecordType;// type of record where an error occurred
    private String recordFilename;// posting filename where this record already written (for record-repeated error)


    public BillingConverterException(String errorMessageCode) {
        this.errorMessageCode = errorMessageCode;
    }

    public BillingConverterException(String errorMessageCode, Class errorRecordType) {
        this.errorMessageCode = errorMessageCode;
        this.errorRecordType = errorRecordType;
    }

    public BillingConverterException(String errorMessageCode, String recordFilename) {
        this.errorMessageCode = errorMessageCode;
        this.recordFilename = recordFilename;
    }

    public BillingConverterException(String errorMessageCode, Class errorRecordType, String recordFilename) {
        this.errorMessageCode = errorMessageCode;
        this.errorRecordType = errorRecordType;
        this.recordFilename = recordFilename;
    }


    public String getErrorMessageCode() {
        return errorMessageCode;
    }

    public Class getErrorRecordType() {
        return errorRecordType;
    }

    public String getRecordFilename() {
        return recordFilename;
    }
}