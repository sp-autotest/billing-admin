package ru.bpc.billing.service.bo;

/**
 * User: Krainov
 * Date: 15.08.14
 * Time: 12:03
 */
public class BOProcessException extends Exception {
    private ProcessAction action;
    private String errorMessageCode;


    public BOProcessException(ProcessAction action, String errorMessageCode) {
        this.action = action;
        this.errorMessageCode = errorMessageCode;
    }


    public ProcessAction getAction() {
        return action;
    }

    public String getErrorMessageCode() {
        return errorMessageCode;
    }


    public static enum ProcessAction {
        SKIP, ERROR, FRAUD
    }
}