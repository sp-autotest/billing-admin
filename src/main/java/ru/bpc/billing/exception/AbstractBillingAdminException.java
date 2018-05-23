package ru.bpc.billing.exception;

/**
 * User: Krainov
 * Date: 25.09.2014
 * Time: 11:04
 */
public abstract class AbstractBillingAdminException extends Exception {

    private final String errorCode;
    private Object[] messageErrorArgs;

    protected AbstractBillingAdminException(String errorCode) {
        this.errorCode = errorCode;
    }
    protected AbstractBillingAdminException(String errorCode, Object[] messageErrorArgs) {
        this.errorCode = errorCode;
        this.messageErrorArgs = messageErrorArgs;
    }

    protected AbstractBillingAdminException(String message, String errorCode, Object[] messageErrorArgs) {
        super(message);
        this.errorCode = errorCode;
        this.messageErrorArgs = messageErrorArgs;
    }

    protected AbstractBillingAdminException(String message, Throwable cause, String errorCode, Object[] messageErrorArgs) {
        super(message, cause);
        this.errorCode = errorCode;
        this.messageErrorArgs = messageErrorArgs;
    }

    protected AbstractBillingAdminException(Throwable cause, String errorCode, Object[] messageErrorArgs) {
        super(cause);
        this.errorCode = errorCode;
        this.messageErrorArgs = messageErrorArgs;
    }

    protected AbstractBillingAdminException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String errorCode, Object[] messageErrorArgs) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
        this.messageErrorArgs = messageErrorArgs;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Object[] getMessageErrorArgs() {
        return messageErrorArgs;
    }
}
