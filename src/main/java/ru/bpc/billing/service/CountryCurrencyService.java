package ru.bpc.billing.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.bpc.billing.domain.CountryCurrency;
import ru.bpc.billing.domain.CountryCurrency_;
import ru.bpc.billing.repository.CountryCurrencyRepository;
import ru.bpc.billing.service.filter.CurrencyFilter;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

/**
 * User: Krainov
 * Date: 12.08.14
 * Time: 17:17
 */
@Service
public class CountryCurrencyService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private CountryCurrencyRepository countryCurrencyRepository;
    private Map<String,CountryCurrency> countryCurrencies = new HashMap<>();

    @PostConstruct
    public void initCache() {
        if ( null == countryCurrencies ) return;
        clearCache();
        for (CountryCurrency countryCurrency : countryCurrencyRepository.findAll()) {
            countryCurrencies.put(countryCurrency.toSimpleString(),countryCurrency);
        }
        logger.debug("CountryCurrency cache was initialized successfully and contains " + countryCurrencies.size() + " records.");
    }

    public CountryCurrency findByCountryAndCurrencyNumericCode(String countryCode,String currencyNumericCode) {
        CountryCurrency countryCurrency = null;
        if ( null != countryCurrencies && null != (countryCurrency = countryCurrencies.get(CountryCurrency.buildSimpleString(countryCode, currencyNumericCode))) )
            return countryCurrency;
        Currency currency = CurrencyService.findByNumericCode(currencyNumericCode.toUpperCase());
        if ( null == currency ) {
            logger.warn("Unknown currency with numberCode: {}",currencyNumericCode.toUpperCase());
            return null;
        }

        int c = currency.getNumericCode();
        String s;
        if (c < 100)
            s = "0" + c;
        else
            s = String.valueOf(c);

        countryCurrency = countryCurrencyRepository.findByCountryCodeAndCurrencyNumericCode(countryCode.toUpperCase(), s);
        if ( null == countryCurrency ) {
            logger.warn("Unknown pair of countryCode: {} and currency numericCode: {}", countryCode.toUpperCase(), currency.getNumericCode());
            return null;
        }
        countryCurrency.setCurrency(currency);
        if ( null != countryCurrency ) addToCache(countryCurrency);
        return countryCurrency;
    }

    public void clearCache() {
        if (null != countryCurrencies) countryCurrencies.clear();
    }

    private void addToCache(CountryCurrency countryCurrency) {
        if ( null == countryCurrencies ) return;
        countryCurrencies.put(CountryCurrency.buildSimpleString(countryCurrency.getCountryCode(), String.valueOf(countryCurrency.getCurrency().getNumericCode())),countryCurrency);
    }

    public List<CountryCurrency> get(final CurrencyFilter filter) {
        final Specification<CountryCurrency> specification = new Specification<CountryCurrency>() {
            @Override
            public Predicate toPredicate(Root<CountryCurrency> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                cq.distinct(true);
                Predicate predicate = cb.conjunction();

                if (!CollectionUtils.isEmpty(filter.getIds())){
                    predicate.getExpressions().add(root.get(CountryCurrency_.id).in(filter.getIds()));
                }

                return predicate;
            }
        };
        return countryCurrencyRepository.findAll(specification);
    }
}
