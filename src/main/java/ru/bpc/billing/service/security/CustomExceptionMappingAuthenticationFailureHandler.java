package ru.bpc.billing.service.security;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;
import ru.bpc.billing.service.UserService;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: Krainov
 * Date: 22.09.2014
 * Time: 13:33
 */
public class CustomExceptionMappingAuthenticationFailureHandler extends ExceptionMappingAuthenticationFailureHandler {

    @Resource
    private UserService userService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if ( exception instanceof BadCredentialsException ) {
            BadCredentialsException badCredentialsException = (BadCredentialsException)exception;
            String username = (String)badCredentialsException.getAuthentication().getPrincipal();
            if (StringUtils.isNotBlank(username)) {
                if ( userService.checkForLock(username) ) {
                    logger.debug("User with login '{}' was locked because count of invalid password more than allow times",username);
                    super.onAuthenticationFailure(request,response,new LockedException("Пользователь заблокирован, так как превышено количество неуспешных попыток."));
                    return;
                }
            }
        }
        super.onAuthenticationFailure(request, response, exception);
    }

}
