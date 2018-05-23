package ru.bpc.billing.repository;

import org.springframework.data.repository.CrudRepository;
import ru.bpc.billing.domain.SystemSetting;

import javax.annotation.Resource;

/**
 * User: Krainov
 * Date: 14.08.14
 * Time: 12:31
 */
@Resource
public interface SystemSettingRepository extends CrudRepository<SystemSetting, Long> {

    public SystemSetting findByName(String name);
}
