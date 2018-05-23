package ru.bpc.billing.exception;

/**
 * Created by Smirnov_Y on 05.04.2016.
 */
public class ElementNotFoundException extends RuntimeException {
    public ElementNotFoundException(String s) {
        super(s);
    }
}
