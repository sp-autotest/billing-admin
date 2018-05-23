package ru.bpc.billing.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.bpc.billing.domain.billing.BillingFile;

import java.util.Date;
import java.util.List;

/**
 * User: Krainov
 * Date: 13.08.14
 * Time: 16:53
 */
@Repository
public interface BillingFileRepository extends CrudRepository<BillingFile, Long>, JpaSpecificationExecutor<BillingFile> {
    List<BillingFile> findAllByBusinessDate(Date businessDate);

}
