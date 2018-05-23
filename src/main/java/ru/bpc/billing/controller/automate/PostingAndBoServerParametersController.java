package ru.bpc.billing.controller.automate;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.bpc.billing.service.automate.PostingAndBoServersParametersService;

import javax.annotation.Resource;

import java.util.Arrays;

import static ru.bpc.billing.service.automate.AutomateConstants.SERVER_PARAMS_BO;
import static ru.bpc.billing.service.automate.AutomateConstants.SERVER_PARAMS_POSTING;


@Controller
@RequestMapping(value = "/automate")
public class PostingAndBoServerParametersController {

    @Resource
    private PostingAndBoServersParametersService parametersService;

    @RequestMapping(value = "/serverParams", method = RequestMethod.GET)
    @ResponseBody
    public ServerParametersDto getParams(@RequestParam String type) {
        if (!Arrays.asList(SERVER_PARAMS_POSTING, SERVER_PARAMS_BO).contains(type))
            return new ServerParametersDto();
        return parametersService.getParams(type);
    }

    @RequestMapping(value = "/serverParams", method = RequestMethod.POST)
    @ResponseBody
    public String setParams(@RequestParam String type, @RequestBody ServerParametersDto dto) throws Exception {
        if (!Arrays.asList(SERVER_PARAMS_POSTING, SERVER_PARAMS_BO).contains(type))
            throw new Exception("unknown params type");
        parametersService.setParams(type, dto);
        return "ok";
    }


}
