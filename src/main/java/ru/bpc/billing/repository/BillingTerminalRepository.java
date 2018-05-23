package ru.bpc.billing.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bpc.billing.domain.BillingTerminal;

/**
 * User: Krainov
 * Date: 12.08.14
 * Time: 17:04
 */
@Repository
public interface BillingTerminalRepository extends CrudRepository<BillingTerminal,String> {

    @Query(value = "from BillingTerminal b where b.countryCode = :countryCode")
    public BillingTerminal findByCountryCode(@Param("countryCode") String countryCode);
}
