package ru.bpc.billing.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import ru.bpc.billing.domain.CountryCurrency;

/**
 * User: Krainov
 * Date: 12.08.14
 * Time: 17:22
 */
public interface CountryCurrencyRepository extends CrudRepository<CountryCurrency,Long>, JpaSpecificationExecutor {

    public CountryCurrency findByCountryCodeAndCurrencyNumericCode(String countryCode, String currencyNumericCode);
}
