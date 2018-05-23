package ru.bpc.billing.service;

import org.hibernate.Hibernate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bpc.billing.domain.Carrier;
import ru.bpc.billing.domain.Carrier_;
import ru.bpc.billing.exception.ElementNotFoundException;
import ru.bpc.billing.repository.CarrierRepository;
import ru.bpc.billing.service.filter.CarrierFilter;

import javax.annotation.Resource;
import javax.persistence.criteria.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Smirnov_Y on 05.04.2016.
 */
@Service
public class CarrierService {

    @Resource
    private CarrierRepository carrierRepository;

    public Carrier get(long id){
        return carrierRepository.findOne(id);
    }

    @Transactional
    public List<Carrier> get(final CarrierFilter filter) {
        final Specification<Carrier> specification = new Specification<Carrier>() {
            @Override
            public Predicate toPredicate(Root<Carrier> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                cq.distinct(true);
                Predicate predicate = cb.conjunction();

                if (filter.getName() != null ) {
                    predicate.getExpressions().add( cb.equal(root.get(Carrier_.name), filter.getName()) );
                }
                if (filter.getIataCode() != null ) {
                    predicate.getExpressions().add( cb.equal(root.get(Carrier_.iataCode), filter.getIataCode()) );
                }
                if (filter.getCreatedAtFrom() != null ) {
                    predicate.getExpressions().add( cb.greaterThanOrEqualTo(root.get(Carrier_.createdAt), filter.getCreatedAtFrom()) );
                }
                if (filter.getCreatedAtTo() != null ) {
                    predicate.getExpressions().add( cb.lessThanOrEqualTo(root.get(Carrier_.createdAt), filter.getCreatedAtTo()) );
                }
                if (filter.getMcc() != null) {
                    predicate.getExpressions().add( cb.equal(root.get(Carrier_.mcc), filter.getMcc()) );
                }
                return predicate;
            }
        };
        List<Carrier> l = carrierRepository.findAll(specification);
//        for (Carrier each : l) {
//            Hibernate.initialize(each.getBsList());
//        }
        return l;
    }

    public Carrier create(Carrier e) {
        e.setCreatedAt(new Date());
        return carrierRepository.save(e);
    }

    public Carrier update(Long id, Carrier source) {
        Carrier target = carrierRepository.findOne(id);
        if (target == null) throw new ElementNotFoundException("Carrier not found, id=" + id);
        if (source.getName() != null){
            target.setName(source.getName());
        }
        if (source.getIataCode() != null){
            target.setIataCode(source.getIataCode());
        }
        if (source.getMcc() != null) {
            target.setMcc(source.getMcc());
        }
        return carrierRepository.save(target);
    }
}
