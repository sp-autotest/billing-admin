package ru.bpc.billing.domain;

/**
 * User: Krainov
 * Date: 15.11.13
 * Time: 14:34
 * AFBRBS-2142
 */
public enum ProcessingStatus {
    SUCCESS,
    REJECT_BILLING,
    REJECT_BO,
    REJECT_BO_HANDLE,
    SUCCESS_AFTER_REJECT,
    REJECT_BO_LOST; //данные, которые были потеряны (есть в биллинге, но нет в БО)
}
