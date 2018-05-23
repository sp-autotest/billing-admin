package ru.bpc.billing.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.bpc.billing.domain.posting.PostingFile;

/**
 * User: Krainov
 * Date: 14.08.14
 * Time: 14:17
 */
@Repository
public interface PostingFileRepository extends CrudRepository<PostingFile, Long> {
}
