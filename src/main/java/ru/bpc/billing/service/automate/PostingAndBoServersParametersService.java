package ru.bpc.billing.service.automate;

import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Service;
import ru.bpc.billing.controller.automate.ServerParametersDto;
import ru.bpc.billing.service.SystemSettingsService;

import javax.annotation.Resource;

import java.io.File;
import java.io.IOException;

import static ru.bpc.billing.service.automate.AutomateConstants.AUTOMATE_ENABLED_GLOBAL;

@Service
@Slf4j
public class PostingAndBoServersParametersService {

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private SystemSettingsService systemSettingsService;

    public ServerParametersDto getParams(String s) {
        String json = systemSettingsService.getString(s);
        try {
            return objectMapper.readValue(json, ServerParametersDto.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ServerParametersDto();
    }

    public void setParams(String s, ServerParametersDto dto) {
        if (dto != null) {
            if (!dto.getPath().endsWith("/"))
                dto.setPath(dto.getPath() + "/");
        }
        try {
            String json = objectMapper.writeValueAsString(dto);
            systemSettingsService.update(s, json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
