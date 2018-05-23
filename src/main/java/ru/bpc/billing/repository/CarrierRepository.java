package ru.bpc.billing.repository;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.bpc.billing.domain.Carrier;

@Repository
public interface CarrierRepository extends CrudRepository<Carrier, Long>, JpaSpecificationExecutor<Carrier> {

    public Carrier findByIataCode(String iataCode);
}
