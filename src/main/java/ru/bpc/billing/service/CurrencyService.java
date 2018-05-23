package ru.bpc.billing.service;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Currency;

/**
 * User: Krainov
 * Date: 12.08.14
 * Time: 11:20
 */
public class CurrencyService {

    private final static Logger logger = LoggerFactory.getLogger(CurrencyService.class);

    public static final Currency findByAlphaCode(String alfaCode) {
        if (StringUtils.isBlank(alfaCode)) throw new IllegalArgumentException("[alphaCode] must be filled");
        try {
            if (alfaCode.equalsIgnoreCase("RUB")) alfaCode = "RUR";//fast fix(для альфы надо чтобы было 810 валюта)
            return Currency.getInstance(alfaCode);
        } catch (Exception e) {
            logger.warn("Error to find currency by alfaCode: " + alfaCode, e);
        }
        return null;
    }

    public static final Currency findByNumericCode(String numericCode) {
        if (StringUtils.isBlank(numericCode)) throw new IllegalArgumentException("[numericCode] must be filled");
        int iNumericCode = Integer.parseInt(numericCode);
        return findByNumberCode(iNumericCode);
    }

    public static final Currency findByNumberCode(Integer numericCode) {
        if (null == numericCode) throw new IllegalArgumentException("[numericCode] must be filled");
        for (Currency currency : Currency.getAvailableCurrencies()) {
            if (numericCode.equals(currency.getNumericCode())) return currency;
        }
        return null;
    }

}
