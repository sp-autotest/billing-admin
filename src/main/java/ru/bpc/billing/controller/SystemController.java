package ru.bpc.billing.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import ru.bpc.billing.controller.dto.SystemDto;
import ru.bpc.billing.domain.SystemSetting;
import ru.bpc.billing.service.ISystem;
import ru.bpc.billing.service.SystemSettingsService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * User: Krainov
 * Date: 19.09.2014
 * Time: 17:43
 */
@Controller
@RequestMapping(value = "/system")
public class SystemController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private SystemSettingsService systemSettingsService;
    @Resource
    private MessageSource messageSource;
    private static final String CONSOLE_PREFIX = "console.systemSettings.";
    private Collection<ISystem> systems;

    private String getSystemVariableValue(String name, Locale locale) {
        return messageSource.getMessage(CONSOLE_PREFIX + "variable." + name,new Object[]{},name,locale);
    }

    public void init(ApplicationContext applicationContext) {
        if ( null == systems ) {
            systems = new ArrayList<>();
            for (Map.Entry<String, ISystem> entry : applicationContext.getBeansOfType(ISystem.class).entrySet()) {
                logger.debug("Load {} bean as class {}",entry.getKey(),entry.getValue());
                systems.add(entry.getValue());
            }
        }
    }

    @RequestMapping(value = "/read")
    @ResponseBody
    public SystemDto read(HttpServletRequest request) {
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
        init(webApplicationContext);
        SystemDto systemDto = new SystemDto();
        systemDto.setSuccess(true);
        systemDto.setText(".");
        List<SystemDto> children = new ArrayList<>();
        for (ISystem system : systems) {
            SystemDto dto = new SystemDto();
            dto.setName(system.getSystemName());
            dto.setValue(system.getClass().getName());
            children.add(dto);
        }
        systemDto.setChildren(children);
        return systemDto;
    }

    @RequestMapping(value = "/setting")
    @ResponseBody
    public SystemDto settings(HttpServletRequest request) {
        Locale locale = request.getLocale();
        SystemDto systemDto = new SystemDto();
        systemDto.setSuccess(true);
        systemDto.setText(".");
        List<SystemDto> children = new ArrayList<>();
        for (SystemSetting systemSetting : systemSettingsService.findAll()) {
            SystemDto dto = new SystemDto();
            dto.setName(getSystemVariableValue(systemSetting.getName(),locale));
            if ( null == systemSetting.getVisibility() ) {
                dto.setValue(systemSetting.getValue());
            } else {
                dto.setValue(systemSetting.getVisibility().equals(SystemSetting.Visibility.VISIBLE) ? systemSetting.getValue() : "*****");
            }
            children.add(dto);
        }
        systemDto.setChildren(children);
        return systemDto;
    }

    @RequestMapping(value = "/setting/update")
    @ResponseBody
    public SystemDto update(SystemDto systemDto) {
        SystemSetting systemSetting = systemSettingsService.findByName(systemDto.getName());
        if ( null == systemSetting ) {
            systemDto.setValue(systemDto.getOldValue());
            return systemDto;
        }
        systemSetting.setValue(systemDto.getValue());
        systemSettingsService.update(systemSetting);
        return systemDto;
    }
}
