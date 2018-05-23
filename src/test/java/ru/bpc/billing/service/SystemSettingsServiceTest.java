package ru.bpc.billing.service;

import org.junit.Test;
import ru.bpc.billing.AbstractTest;

import javax.annotation.Resource;

/**
 * User: Krainov
 * Date: 25.09.2014
 * Time: 15:28
 */
public class SystemSettingsServiceTest extends AbstractTest {

    @Resource
    private SystemSettingsService systemSettingsService;

    @Test
    public void testGet() {
        Integer i = systemSettingsService.getInteger("billing.converter.count_available_reject_record");
        System.out.println(i);
    }
}
