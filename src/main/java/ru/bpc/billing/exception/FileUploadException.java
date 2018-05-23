package ru.bpc.billing.exception;

import java.io.File;

/**
 * User: Krainov
 * Date: 25.09.2014
 * Time: 11:07
 */
public class FileUploadException extends AbstractBillingAdminException {

    private File file;

    public FileUploadException(String errorCode) {
        super(errorCode);
    }
    public FileUploadException(String errorCode, Object[] messageErrorArgs, File file) {
        super(errorCode, messageErrorArgs);
        this.file = file;
    }

    public FileUploadException(String message, String errorCode, Object[] messageErrorArgs, File file) {
        super(message, errorCode, messageErrorArgs);
        this.file = file;
    }

    public FileUploadException(String message, Throwable cause, String errorCode, Object[] messageErrorArgs, File file) {
        super(message, cause, errorCode, messageErrorArgs);
        this.file = file;
    }

    public FileUploadException(Throwable cause, String errorCode, Object[] messageErrorArgs, File file) {
        super(cause, errorCode, messageErrorArgs);
        this.file = file;
    }

    public FileUploadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String errorCode, Object[] messageErrorArgs, File file) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode, messageErrorArgs);
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
