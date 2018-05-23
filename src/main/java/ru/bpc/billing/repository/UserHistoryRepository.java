package ru.bpc.billing.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.bpc.billing.domain.UserHistory;

/**
 * User: Krainov
 * Date: 22.09.2014
 * Time: 16:15
 */
@Repository
public interface UserHistoryRepository extends CrudRepository<UserHistory,Long> {
}
