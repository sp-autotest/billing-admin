package ru.bpc.billing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.dao.ReflectionSaltSource;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import ru.bpc.billing.service.UserService;
import ru.bpc.billing.service.security.CustomAuthenticationSuccessHandler;
import ru.bpc.billing.service.security.CustomExceptionMappingAuthenticationFailureHandler;
import ru.bpc.billing.service.security.CustomLogoutHandler;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Krainov
 * Date: 19.09.2014
 * Time: 15:46
 * http://devcolibri.com/3810
 * http://javahash.com/spring-security-hello-world-example/
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private UserService userService;
    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        auth.authenticationProvider(daoAuthenticationProvider);

//        auth.inMemoryAuthentication().withUser("admin").password("pivot43\\very")
//                .credentialsExpired(true)
//                .roles("ADMIN");
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler = new CustomAuthenticationSuccessHandler();
        return customAuthenticationSuccessHandler;
    }

    @Bean
    public ExceptionMappingAuthenticationFailureHandler exceptionMappingAuthenticationFailureHandler() {
        ExceptionMappingAuthenticationFailureHandler handler = new CustomExceptionMappingAuthenticationFailureHandler();
        handler.setDefaultFailureUrl("/mvc/login?error=unknown");
        Map<String,String> exceptionMappings = new HashMap<>();
        exceptionMappings.put(CredentialsExpiredException.class.getName(),"/mvc/login?password_expired");
        exceptionMappings.put(BadCredentialsException.class.getName(),"/mvc/login?error");
        exceptionMappings.put(LockedException.class.getName(),"/mvc/login?locked");
        handler.setExceptionMappings(exceptionMappings);
        return handler;
    }

    @Bean
    public LogoutHandler logoutHandler() {
        CustomLogoutHandler logoutHandler = new CustomLogoutHandler();
        return logoutHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .headers().disable()
                // указываем правила запросов
                // по которым будет определятся доступ к ресурсам и остальным данным
                .authorizeRequests()
                        .regexMatchers("/mvc/login").permitAll().and()
                        .authorizeRequests()
                .antMatchers(
                        "/",
                        "/index.html",
                        "/mvc/billing/**",
                        "/mvc/bo/**",
                        "/mvc/ticket/**",
                        "/mvc/user/**",
                        "/mvc/system/**",
                        "/mvc/carrier/**",
                        "/mvc/currency/**",
                        "/mvc/terminal/**",
                        "/mvc/file/**",
                        "/mvc/automate/**"
                )
                .authenticated()
                .and();

        http.formLogin()
                // указываем страницу с формой логина
                .loginPage("/mvc/login")
                        // указываем action с формы логина
                .loginProcessingUrl("/j_spring_security_check")
                        // указываем URL при неудачном логине
                .failureUrl("/mvc/login?error")
                        // Указываем параметры логина и пароля с формы логина
                .usernameParameter("j_username")
                .passwordParameter("j_password")
                        // даем доступ к форме логина всем
                .permitAll()
                .failureHandler(exceptionMappingAuthenticationFailureHandler())
                .successHandler(authenticationSuccessHandler());

        http.logout()
                // разрешаем делать логаут всем
                .permitAll()
                        // указываем URL логаута
                .logoutUrl("/mvc/logout")
                        // указываем URL при удачном логауте
                .logoutSuccessUrl("/mvc/login?logout")
                        // делаем не валидной текущую сессию
                .deleteCookies("JSESSIONID","SPRING_SECURITY_REMEMBER_ME_COOKIE")
                .invalidateHttpSession(true).addLogoutHandler(logoutHandler());
    }

}
