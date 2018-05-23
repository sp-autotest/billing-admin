package ru.bpc.billing.service.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import ru.bpc.billing.domain.User;
import ru.bpc.billing.service.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: Krainov
 * Date: 22.09.2014
 * Time: 18:28
 */
public class CustomLogoutHandler extends SecurityContextLogoutHandler{

    @Resource
    private UserService userService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        boolean status;
        User user = null;
        if ( null != authentication && null != authentication.getPrincipal() && authentication.getPrincipal() instanceof User ) {
            user = (User)authentication.getPrincipal();
        }
        try {
            super.logout(request, response, authentication);
            status = true;
        } catch (Exception e) {
            logger.error("Error logout",e);
            throw e;
        }
        userService.logout(user,status);
        return;
    }
}
