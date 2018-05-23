package ru.bpc.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ru.bpc.billing.domain.BillingSystem;
import ru.bpc.billing.domain.BillingSystem_;

import java.util.List;

public interface BillingSystemRepository extends JpaRepository<BillingSystem, Long> {

    List<BillingSystem> findAllByEnabled(Boolean enabled);
}
