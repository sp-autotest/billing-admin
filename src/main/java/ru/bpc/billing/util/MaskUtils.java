package ru.bpc.billing.util;


import org.apache.commons.lang.StringUtils;

import java.util.regex.Pattern;

public class MaskUtils {
    private static final Pattern PAN_PATTERN = Pattern.compile("\\d{13,19}");

    private static String panMask = "**";

    public static String getPanMask() {
        return panMask;
    }

    public static void setPanMask(String panMask) {
        MaskUtils.panMask = panMask;
    }

    public static String mask(String string) {
        if (!StringUtils.isBlank(string)) {
            return StringUtils.rightPad("", string.length(), "*");
        }
        return string;
    }

    public static String getMaskedPan(String pan) {
        if (StringUtils.isBlank(pan)) return pan;

        if (!PAN_PATTERN.matcher(pan).matches()) return pan;

        return pan.substring(0, 6) + panMask + pan.substring(pan.length() - 4);
    }
}
