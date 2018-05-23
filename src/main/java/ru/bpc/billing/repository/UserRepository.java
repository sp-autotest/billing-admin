package ru.bpc.billing.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.bpc.billing.domain.User;

/**
 * User: Krainov
 * Date: 22.09.2014
 * Time: 13:52
 */
@Repository
public interface UserRepository extends CrudRepository<User,Long>{

    public User findByUsername(String username);
}
