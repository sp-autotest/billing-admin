package ru.bpc.billing.service;

import org.springframework.stereotype.Service;
import ru.bpc.billing.domain.BillingSystem;
import ru.bpc.billing.exception.ElementNotFoundException;
import ru.bpc.billing.repository.BillingSystemRepository;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

@Service
public class BillingSystemService {

    @Resource
    private BillingSystemRepository billingSystemRepository;

    public BillingSystem findOne(long id) {
        return billingSystemRepository.findOne(id);
    }


    public BillingSystem update(Long id, BillingSystem source) {
        BillingSystem target = billingSystemRepository.findOne(id);
        if (target == null) throw new ElementNotFoundException("Carrier not found, id=" + id);

        try {

            for (Field f : source.getClass().getDeclaredFields()) {
                if (f.getName().equals("id")) continue;
                f.setAccessible(true);
                if (f.get(source) != null)
                    f.set(target, f.get(source));
                f.setAccessible(false);
            }
        } catch (IllegalAccessException ignore) {
            System.out.println("ERROR!!!!!!!!!!!!!!!!!");
        }
        return billingSystemRepository.save(target);
    }

    public BillingSystem create(BillingSystem billingSystem) {
        billingSystem.setCreatedDate(new Date());
        return billingSystemRepository.save(billingSystem);
    }

    public List<BillingSystem> findAll() {
        return billingSystemRepository.findAll();
    }

    public List<BillingSystem> findAllAvailable() {
        return billingSystemRepository.findAllByEnabled(true);
    }
}