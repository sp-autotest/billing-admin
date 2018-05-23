package ru.bpc.billing.controller;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.bpc.billing.service.UserService;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: krainov
 * Date: 19.08.14
 * Time: 15:40
 */
@Controller
@RequestMapping(value = "/login")
public class LoginController {

    @Resource
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "password_expired", required = false) String passwordExpired,
            @RequestParam(value = "locked", required = false) String locked) {

        ModelAndView model = new ModelAndView();
        if (error != null) {
            model.addObject("error", "Неверные данные авторизации!");
        }

        if (logout != null) {
            model.addObject("msg", "Вы успешно разлогированы.");
        }

        if ( null != passwordExpired ) {
            model.addObject("error","Пароль просрочен");
            model.setViewName("changePassword");
            return model;
        }

        if ( null != locked ) {
            model.addObject("error","Пользователь заблокирован, так как превышено количество неуспешных попыток.");
        }
        model.setViewName("login");

        return model;

    }

    @RequestMapping(method = RequestMethod.GET,value = "/changePassword")
    public String changePasswordPage() {
        return "changePassword";
    }

    @RequestMapping(method = RequestMethod.POST,value = "/changePassword")
    public ModelAndView changePassword(HttpServletRequest request,
                               HttpServletResponse response,
                               @RequestParam("username") String username,
                               @RequestParam("password") String password,
                               @RequestParam("repeatNewPassword") String repeatNewPassword,
                               @RequestParam("newPassword") String newPassword) throws IOException, ServletException {

        ModelAndView model = new ModelAndView();
        if ( StringUtils.isBlank(newPassword) || StringUtils.isBlank(password) || StringUtils.isBlank(repeatNewPassword) ) {
            model.addObject("error","Поля не заполнены");
            model.setViewName("changePassword");
            return model;
        }
        if ( !repeatNewPassword.equals(newPassword) ) {
            model.addObject("error","Пароли не совпадают");
            model.setViewName("changePassword");
            return model;
        }
        else {
            try {
                userService.changePassword(username,password,newPassword);
            } catch (AuthenticationException e) {
                model.addObject("error",e.getMessage());
                model.setViewName("changePassword");
                return model;
            }
            model.setViewName("login");
            model.addObject("msg","Пароль был изменён");
            return model;
        }
    }

}
