package ru.bpc.billing.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bpc.billing.domain.PaymentSystem;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.startsWith;


/**
 * User: Krainov
 * Date: 12.08.14
 * Time: 17:09
 */
public class CardService {

    private static final Logger logger = LoggerFactory.getLogger(CardService.class);
    private static final Map<String[], PaymentSystem> iinRanges = new LinkedHashMap<String[], PaymentSystem>();
    private static final Pattern PAN_PATTERN = Pattern.compile("\\d{13,19}");

    static {
        iinRanges.put(new String[]{"4",}, PaymentSystem.VISA);
        iinRanges.put(new String[]{"62"}, PaymentSystem.CUP);
        iinRanges.put(new String[]{"5", "6"}, PaymentSystem.MASTERCARD); // 50,56-59,6 MAESTRO,51-55 MASTERCARD
        iinRanges.put(new String[]{"34", "37"}, PaymentSystem.AMEX);
        iinRanges.put(new String[]{"35"}, PaymentSystem.JCB);
    }

    public static PaymentSystem getPaymentSystem(String pan) {
        for (Map.Entry<String[], PaymentSystem> entry : iinRanges.entrySet()) {
            for (String range : entry.getKey()) {
                if (startsWith(pan, range)) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }
}
