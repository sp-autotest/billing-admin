package ru.bpc.billing.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bpc.billing.domain.ProcessingFile;
import ru.bpc.billing.domain.ProcessingFileRecord;
import ru.bpc.billing.domain.ProcessingFileRecordPk;

/**
 * User: Krainov
 * Date: 08.09.2014
 * Time: 10:37
 */
@Repository
public interface ProcessingFileRecordRepository extends CrudRepository<ProcessingFileRecord,ProcessingFileRecordPk> {

    @Modifying
    @Query(value = "delete from ProcessingFileRecord pfr where pfr.pk.record.id = :recordId")
    public void deleteRecordsById(@Param("recordId") Long recordId);
}
