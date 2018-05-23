package ru.bpc.billing.config;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.dialect.PostgreSQL82Dialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * User: Krainov
 * Date: 11.08.14
 * Time: 13:30
 */
@Configuration
@EnableAspectJAutoProxy
@EnableTransactionManagement
@Import(DataSourceConfig.class)
public class EntityManagerConfig {

    @Resource
    protected DataSource dataSource;
    @Resource
    protected Environment environment;

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource);
        entityManagerFactory.setJpaVendorAdapter(vendorAdapter());
        entityManagerFactory.setPackagesToScan(getPackagesToScan());
        entityManagerFactory.setJpaProperties(jpaProperties());
        entityManagerFactory.afterPropertiesSet();
        return entityManagerFactory.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager(entityManagerFactory());
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslator exceptionTranslation() {
        return new HibernateExceptionTranslator();
    }

    /**
     * Массив сущностей, которе будут маппится с помощью хибернэйта
     * @return
     */
    public String[] getPackagesToScan() {
        return new String[]{"ru.bpc.billing.domain"};
    };

    @Bean
    public Properties jpaProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.temp.use_jdbc_metadata_defaults", "false");
        properties.put("hibernate.jdbc.batch_size", 100);
        properties.put("hibernate.hbm2ddl.auto", environment.getProperty("hibernate.hbm2ddl.auto","update"));
        if ( environment.containsProperty("hibernate.hbm2ddl.import_files") )
            properties.put("hibernate.hbm2ddl.import_files",environment.getProperty("hibernate.hbm2ddl.import_files"));
        properties.put("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory");
        properties.put("hibernate.cache.use_second_level_cache", "true");
        properties.put("hibernate.cache.use_query_cache", "true");
        properties.put("hibernate.connection.CharSet","utf8");
        properties.put("hibernate.connection.characterEncoding","utf8");
        properties.put("hibernate.connection.useUnicode","true");
        return properties;
    }

    /**
     * Return default adapter with next parameters: <br/>
     * showSql = general.hibernate.show_sql
     * dialect = general.hibernate.dialect
     * @return
     */
    @Bean
    public JpaVendorAdapter vendorAdapter() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(showSql());
        vendorAdapter.setDatabasePlatform(hibernateDialect());
        return vendorAdapter;
    }

    public boolean showSql() {
        Boolean showSql = environment.getProperty("hibernate.show_sql",Boolean.class);
        if ( null != showSql ) return showSql;
        return false;
    }

    /**
     * Have to return hibernate dialect for concrete database
     * @return
     */
    public String hibernateDialect() {
        return environment.getRequiredProperty("hibernate.dialect");
    }

    @Bean
    public Dialect currentDialect() {
        String dialect = environment.getRequiredProperty("hibernate.dialect");
        if ( dialect.toLowerCase().contains("postgres") ) return new PostgreSQL82Dialect();
        else return new Oracle10gDialect();
    }
}
