package ru.bpc.billing.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bpc.billing.domain.ProcessingFile;

import java.util.List;

/**
 * User: Krainov
 * Date: 15.08.14
 * Time: 15:18
 */
@Repository
public interface ProcessingFileRepository extends CrudRepository<ProcessingFile, Long>, JpaSpecificationExecutor<ProcessingFile> {

    @Query("select p from ProcessingFile p WHERE fileType = 'BILLING' AND originalFileName like '%'||:date||'%' AND NOT EXISTS(select 1 from ProcessingFile pf2 WHERE pf2.parentFile.id = p.id AND pf2.fileType = 'BO')")
    public List<ProcessingFile> getNotProcessedBillings(@Param("date")String date);

}
