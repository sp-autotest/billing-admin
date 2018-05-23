package ru.bpc.billing.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.bpc.billing.domain.ProcessingFile;

/**
 * User: Krainov
 * Date: 15.08.14
 * Time: 15:18
 */
@Repository
public interface ProcessingFileRepository extends CrudRepository<ProcessingFile, Long>, JpaSpecificationExecutor<ProcessingFile> {

}
