package ru.bpc.billing.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.bpc.billing.domain.ProcessingRecord;
import ru.bpc.billing.domain.TransactionType;

import java.util.List;

/**
 * User: Krainov
 * Date: 12.08.14
 * Time: 17:43
 */
public interface ProcessingRecordRepository extends CrudRepository<ProcessingRecord,Long> , JpaSpecificationExecutor<ProcessingRecord>{

    @Query(value = "from ProcessingRecord bfr where bfr.documentDate = :documentDate and bfr.documentNumber = :documentNumber and bfr.transactionType = :transactionType")
    public ProcessingRecord findByDocumentParams(@Param("documentDate") String documentDate, @Param("documentNumber") String documentNumber, @Param("transactionType") TransactionType transactionType);

    public ProcessingRecord findByRbsId(String rbsId);

    @Query(value = "from ProcessingRecord bfr where bfr.documentNumber = :documentNumber and bfr.amount = :amount and bfr.currency = :currency and bfr.transactionType = 'DR'")
    public List<ProcessingRecord> findOriginalProcessingRecordBy(@Param("documentNumber")String documentNumber, @Param("amount")Integer amount, @Param("currency")String currency);

    @Modifying
    @Query("delete from ProcessingRecord pr where pr.id = :id")
    public void deleteById(@Param("id") Long id);

    @Query(value = "from ProcessingRecord bfr where bfr.documentNumber = :documentNumber")
    public List<ProcessingRecord> findByDocumentNumber(@Param("documentNumber")String documentNumber);
}
