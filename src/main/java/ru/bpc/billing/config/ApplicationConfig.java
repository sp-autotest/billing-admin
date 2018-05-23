package ru.bpc.billing.config;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import ru.bpc.billing.converter.CarrierToDtoConverter;
import ru.bpc.billing.domain.report.accelya.ItemError;
import ru.bpc.billing.domain.report.accelya.ItemsError;
import ru.bpc.billing.domain.report.accelya.Receipt;
import ru.bpc.billing.service.mail.DefaultMailer;
import ru.bpc.billing.service.mail.Mailer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

/**
 * User: Krainov
 * Date: 13.08.14
 * Time: 16:06
 */
@Configuration
@PropertySource(ignoreResourceNotFound = true,
        value = {
                "classpath:config.properties",
                "${" + ApplicationConfig.SYSTEM_VARIABLE_CONFIG_LOCATION + "}"
        }
)
@Import(value = {
        EntityManagerConfig.class,
        SvReportConfig.class,
        SecurityConfig.class,
        ConvertersConfig.class
})
@EnableJpaRepositories(value = {"ru.bpc.billing.repository"})
@ComponentScan(value = {"ru.bpc.billing.service", "ru.bpc.billing.validator"})
public class ApplicationConfig {

    private static Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    /**
     * Если при запуске приложения, переменная с таким именем есть в системных переменных, то будет производится поиск конфига в этом location и настройки будут перезагружены
     */
    public static final String SYSTEM_VARIABLE_CONFIG_LOCATION = "billingAdminConfigLocation";

    @Resource
    private Environment environment;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }


    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n/billing", "i18n/error", "i18n/bo", "i18n/common");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setClassesToBeBound(Receipt.class, ItemsError.class, ItemError.class);
        return jaxb2Marshaller;
    }

    @Bean
    public Mailer mailer() {
        String host = environment.getRequiredProperty("main.mail.host");
        int port = environment.getRequiredProperty("main.mail.port", Integer.class);
        String login = environment.getRequiredProperty("main.mail.login");
        String password = environment.getRequiredProperty("main.mail.password");
        String protocol = environment.getRequiredProperty("main.mail.protocol");
        String auth = environment.getRequiredProperty("main.mail.auth");
        return new DefaultMailer(host, port, login, password, auth, protocol);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        StandardPasswordEncoder standardPasswordEncoder = new StandardPasswordEncoder();
        return standardPasswordEncoder;
    }

    /**
     * Установить системную переменную 'billingAdminConfigLocation' как значение по умолчанию, иначе если её не будет, приложение не запустится
     */
    public final static void setSystemVariableConfigLocation() {
        logger.debug("System variable with name '" + ApplicationConfig.SYSTEM_VARIABLE_CONFIG_LOCATION + "' has value = '" + System.getProperty(ApplicationConfig.SYSTEM_VARIABLE_CONFIG_LOCATION) + "'");
        if (null == System.getProperty(ApplicationConfig.SYSTEM_VARIABLE_CONFIG_LOCATION)) {
            logger.debug("Set system variable with name '" + ApplicationConfig.SYSTEM_VARIABLE_CONFIG_LOCATION + "' as fake value.");
            System.setProperty(ApplicationConfig.SYSTEM_VARIABLE_CONFIG_LOCATION, "fake value");
        }
    }
}
