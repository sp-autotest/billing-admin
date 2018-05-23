package ru.bpc.billing.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.EnumSet;

/**
 * User: krainov
 * Date: 19.08.14
 * Time: 16:01
 */
public class BillingWebApplicationInitializer implements WebApplicationInitializer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    static {
        ApplicationConfig.setSystemVariableConfigLocation();
    }

    @Override
    public void onStartup(ServletContext container) throws ServletException {
        logger.debug("ON START UP Web application [Billing-admin]");
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();

        rootContext.register(ApplicationConfig.class);

        container.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer","false");

        //listerns
        ContextLoaderListener contextLoaderListener = new ContextLoaderListener(rootContext);
        container.addListener(contextLoaderListener);
        container.addListener(new HttpSessionListener() {
            @Override
            public void sessionCreated(HttpSessionEvent httpSessionEvent) {
//                System.out.println("session created");
                httpSessionEvent.getSession().setMaxInactiveInterval(30 * 60);
            }

            @Override
            public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
//                System.out.println("session destroyed");
            }
        });

        //filters
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setForceEncoding(true);
        characterEncodingFilter.setEncoding("UTF-8");

        DelegatingFilterProxy springSecurityFilterChain = new DelegatingFilterProxy("springSecurityFilterChain");

        container.addFilter("characterEncodingFilter",characterEncodingFilter)
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD), true, "/*");
        container.addFilter("springSecurityFilterChain",springSecurityFilterChain)
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

        //appServlet
        AnnotationConfigWebApplicationContext dispatcherContext  = new AnnotationConfigWebApplicationContext();
        dispatcherContext.register(ServletConfig.class);
        DispatcherServlet appServlet = new DispatcherServlet(dispatcherContext);
        ServletRegistration.Dynamic dispatcher = container.addServlet("appServlet", appServlet);
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/mvc/*");

        logger.debug("Finish load web application.");
    }
}
