package ru.bpc.billing.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: Petrov_M
 * Date: 25.02.13
 * Time: 15:44
 * To change this template use File | Settings | File Templates.
 */
public class CountryUtils {
    private static Logger logger = LoggerFactory.getLogger(CountryUtils.class);
    private static ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    public static final String UNKNOWN_COUNTRY_CODE = "UNKNOWN";

    static {
        messageSource.setBasename("i18n/country");
    }

    public static String getCountryName(String countryCode) {
        return getCountryName(countryCode, null);
    }

    public static String getCountryName(String countryCode, String language) {
        if (StringUtils.isBlank(countryCode)) {
            countryCode = UNKNOWN_COUNTRY_CODE;
        }
        try {
            return messageSource.getMessage(countryCode, null, Locale.forLanguageTag(language));
        } catch (NoSuchMessageException e) {
            logger.error("No country code", e);
            return countryCode;
        }
    }
}