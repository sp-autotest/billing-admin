package ru.bpc.billing.util;

/**
 * User: Krainov
 * Date: 14.04.2016
 * Time: 14:37
 */
public class PostingUtils {

    public static String toHexString(String s) {
        if ( null == s ) return null;
        Integer i = s.getBytes().length;
        if (i == null) return null;
        String res = Integer.toHexString(i).toUpperCase();
        return res.length() % 2 == 0 ? res : "0" + res;
    }
}
