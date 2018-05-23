package ru.bpc.billing.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.bpc.billing.domain.Carrier;
import ru.bpc.billing.domain.Carrier_;
import ru.bpc.billing.domain.Terminal;
import ru.bpc.billing.domain.Terminal_;
import ru.bpc.billing.exception.ElementNotFoundException;
import ru.bpc.billing.repository.TerminalRepository;
import ru.bpc.billing.service.filter.TerminalFilter;

import javax.annotation.Resource;
import javax.persistence.criteria.*;
import java.util.List;

/**
 * Created by Smirnov_Y on 05.04.2016.
 */
@Service
public class TerminalService {
    @Resource
    private TerminalRepository terminalRepository;

    public List<Terminal> get(final TerminalFilter filter) {
        final Specification<Terminal> specification = new Specification<Terminal>() {
            @Override
            public Predicate toPredicate(Root<Terminal> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                cq.distinct(true);
                Predicate predicate = cb.conjunction();

                if (filter.getName() != null ) {
                    predicate.getExpressions().add( cb.equal(root.get(Terminal_.name), filter.getName()) );
                }
                if (filter.getAgrn() != null ) {
                    predicate.getExpressions().add( cb.equal(root.get(Terminal_.agrn), filter.getAgrn()) );
                }
                if (filter.getTerminal() != null ) {
                    predicate.getExpressions().add( cb.equal(root.get(Terminal_.terminal), filter.getTerminal()) );
                }
                if (filter.getCarrierId() != null) {
                    Join<Terminal, Carrier> join = root.join(Terminal_.carrier);
                    predicate.getExpressions().add(join.get(Carrier_.id).in(filter.getCarrierId()));
                }
                return predicate;
            }
        };
        return terminalRepository.findAll(specification);
    }

    public Terminal create(Terminal e) {
        return terminalRepository.save(e);
    }

    public Terminal update(Long id, Terminal source) {
        Terminal target = terminalRepository.findOne(id);
        if (target == null) throw new ElementNotFoundException("Terminal not found, id=" + id);
        if (source.getName() != null) {
            target.setName(source.getName());
        }
        if (source.getAgrn() != null) {
            target.setAgrn(source.getAgrn());
        }
        if (source.getTerminal() != null) {
            target.setTerminal(source.getTerminal());
        }
        if (source.getCarrier() != null){
            target.setCarrier(source.getCarrier());
        }
        if (!CollectionUtils.isEmpty(source.getCurrencies())) {
            target.setCurrencies(source.getCurrencies());
        }
        return terminalRepository.save(target);
    }

    public void delete(Long id) {
        terminalRepository.delete(id);
    }
}
