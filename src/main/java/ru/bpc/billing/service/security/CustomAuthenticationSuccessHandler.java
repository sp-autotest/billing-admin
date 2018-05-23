package ru.bpc.billing.service.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import ru.bpc.billing.domain.User;
import ru.bpc.billing.service.UserService;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: Krainov
 * Date: 22.09.2014
 * Time: 13:16
 */
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Resource
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if ( null != authentication.getPrincipal() && authentication.getPrincipal() instanceof User ) {
            User user = (User)authentication.getPrincipal();
            if ( user.isLocked() ) {
                userService.unlockUser((User)authentication.getPrincipal());
            }
            userService.clearInvalidTries(user.getUsername());
        }
        response.sendRedirect(httpServletRequest.getContextPath() + "/");
    }

}
