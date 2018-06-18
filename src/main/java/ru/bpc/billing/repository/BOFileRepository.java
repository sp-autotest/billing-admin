package ru.bpc.billing.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.bpc.billing.domain.bo.BOFile;

import java.util.List;

/**
 * User: Krainov
 * Date: 15.08.14
 * Time: 13:50
 */
@Repository
public interface BOFileRepository extends CrudRepository<BOFile,Long> {
    List<BOFile> findAll();
}
