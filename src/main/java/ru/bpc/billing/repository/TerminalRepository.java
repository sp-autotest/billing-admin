package ru.bpc.billing.repository;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.bpc.billing.domain.Terminal;

@Repository
public interface TerminalRepository extends CrudRepository<Terminal, Long>, JpaSpecificationExecutor {

    public Terminal findByAgrn(String agrn);
}
