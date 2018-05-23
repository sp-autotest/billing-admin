package ru.bpc.billing.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import ru.bpc.billing.domain.SystemSetting;
import ru.bpc.billing.repository.SystemSettingRepository;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * User: Krainov
 * Date: 14.08.14
 * Time: 12:29
 */
@Service
public class SystemSettingsService {

    private static Map<String, SystemSetting> systemSettingsCache = new HashMap<String, SystemSetting>();
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final NumberFormat DEFAULT_DECIMAL_FORMAT = DecimalFormat.getInstance(Locale.US);

    @Resource
    private SystemSettingRepository systemSettingRepository;

    public Integer getInteger(String name, Integer defaultValue) {
        try {
            return Integer.valueOf(get(name));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Integer getInteger(String name) {
        return getInteger(name, null);
    }

    public String getString(String name, String defaultValue) {
        String value = get(name);
        return value != null ? value : defaultValue;
    }

    public String getString(String name) {
        return getString(name, null);
    }

    public Boolean getBoolean(String name, Boolean defaultValue) {
        String value = get(name);
        if ("true".equalsIgnoreCase(value)) return true;
        if ("false".equalsIgnoreCase(value)) return false;
        return defaultValue;
    }

    public Boolean getBoolean(String name) {
        return getBoolean(name, null);
    }

    public Long getLong(String name, Long defaultValue) {
        try {
            return Long.valueOf(get(name));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Long getLong(String name) {
        return getLong(name, null);
    }

    public Float getFloat(String name, Float defaultValue) {
        try {
            return Float.valueOf(get(name));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Float getFloat(String name) {
        return getFloat(name, null);
    }

    public Double getDouble(String name, Double defaultValue) {
        try {
            return Double.valueOf(get(name));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Double getDouble(String name) {
        return getDouble(name, null);
    }

    public BigDecimal getBigDecimal(String name, BigDecimal defaultValue) {
        try {
            return (BigDecimal) DEFAULT_DECIMAL_FORMAT.parse(name);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public BigDecimal getBigDecimal(String name) {
        return getBigDecimal(name, null);
    }

    public Date getDate(String name, String dateFormat, Date defaultValue) {
        try {
            return new SimpleDateFormat(dateFormat).parse(get(name));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Date getDate(String name, Date defaultValue) {
        return getDate(name, DEFAULT_DATE_FORMAT, defaultValue);
    }

    public Date getDate(String name, String dateFormat) {
        return getDate(name, dateFormat, null);
    }

    public Date getDate(String name) {
        return getDate(name, DEFAULT_DATE_FORMAT, null);
    }

    private String get(String name) {
        if (StringUtils.isBlank(name)) return null;

        SystemSetting option = systemSettingsCache.get(name);
        if (option == null) {
            option = systemSettingRepository.findByName(name);
            if (option == null) {
                return null;
            } else {
                systemSettingsCache.put(name, copyOf(option)); // Cache keeps unencrypted data for security purpose.
            }
        }
        return option.getValue();
    }

    private SystemSetting copyOf(SystemSetting option) {
        SystemSetting copy = new SystemSetting(option.getName(), option.getValue());
        copy.setId(option.getId());
        copy.setVisibility(option.getVisibility());
        copy.setEncoder(option.getEncoder());
        return copy;
    }

    public SystemSetting update(SystemSetting systemSetting) {
        systemSettingRepository.save(systemSetting);
        systemSettingsCache.clear();
        return systemSetting;
    }

    public void update(String name, String value) {
        SystemSetting s = systemSettingRepository.findByName(name);
        s.setValue(value);
        systemSettingRepository.save(s);
        systemSettingsCache.clear();
    }

    public SystemSetting findByName(String name) {
        return systemSettingRepository.findByName(name);
    }

    public Iterable<SystemSetting> findAll() {
        return systemSettingRepository.findAll();
    }
}
