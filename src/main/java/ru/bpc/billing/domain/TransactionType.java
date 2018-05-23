package ru.bpc.billing.domain;

/**
 * Created with IntelliJ IDEA.
 * User: Petrov_M
 * Date: 29.08.13
 * Time: 16:51
 * To change this template use File | Settings | File Templates.
 */
public enum TransactionType {
    CR, DR,
    CR_REFUND,
    CR_REVERSE,
    UN; //UNKNOWN

    public static TransactionType valueOfType(String type) {
        if ( null == type ) return null;
        for (TransactionType transactionType : values()) {
            if ( transactionType.name().equalsIgnoreCase(type) ) return transactionType;
        }
        return UN;
    }

    public boolean isCredit() {
        return this.equals(CR) || this.equals(CR_REFUND) || this.equals(CR_REVERSE);
    }

}
