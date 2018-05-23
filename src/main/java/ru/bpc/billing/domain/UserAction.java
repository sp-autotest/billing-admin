package ru.bpc.billing.domain;

/**
 * User: Krainov
 * Date: 22.09.2014
 * Time: 16:08
 */
public enum UserAction {
    CREATE_USER,
    PASSWORD_CHANGE,
    LOGON,
    LOGOUT,
    DOWNLOAD_FILE,
    UPLOAD_BILLING_FILE,
    UPLOAD_BO_FILE,
    CONVERT_BILLING,
    BUILD_REPORT,
    REVERT_POSTING_FILE
}
