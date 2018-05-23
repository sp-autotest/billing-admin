package ru.bpc.billing.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.bpc.billing.controller.dto.UserDto;
import ru.bpc.billing.domain.User;
import ru.bpc.billing.repository.UserRepository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Krainov
 * Date: 24.09.2014
 * Time: 14:24
 */
@Controller
@RequestMapping(value = "/user")
public class UserController {

    @Resource
    private UserRepository userRepository;

    @RequestMapping(value = "/read")
    public @ResponseBody UserDto read() {
        UserDto userDto = new UserDto();
        userDto.setText(".");
        userDto.setSuccess(true);
        List<UserDto> children = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            children.add(new UserDto(user));
        }
        userDto.setChildren(children);
        return userDto;
    }
}
