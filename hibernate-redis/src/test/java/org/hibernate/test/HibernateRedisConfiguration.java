package org.hibernate.test;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.cache.redis.SingletonRedisRegionFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.test.domain.Account;
import org.hibernate.transaction.JDBCTransactionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.hibernate3.HibernateExceptionTranslator;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * org.hibernate.test.HibernateConfiguration
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 8. 28. 오후 9:33
 */
@Slf4j
@Configuration
public class HibernateRedisConfiguration {

    public String getDatabaseName() {
        return "hibernate";
    }

    public String[] getMappedPackageNames() {
        return new String[] {
                Account.class.getPackage().getName()
        };
    }

    public Properties hibernateProperties() {
        Properties props = new Properties();

        props.put(Environment.FORMAT_SQL, "true");
        props.put(Environment.HBM2DDL_AUTO, "create-drop");
        props.put(Environment.SHOW_SQL, "true");

        props.put(Environment.POOL_SIZE, 30);

        // Secondary Cache
        props.put(Environment.USE_SECOND_LEVEL_CACHE, true);
        props.put(Environment.USE_QUERY_CACHE, true);
        props.put(Environment.CACHE_REGION_FACTORY, SingletonRedisRegionFactory.class.getName());
        props.put(Environment.CACHE_REGION_PREFIX, "");
        props.put(Environment.CACHE_PROVIDER_CONFIG, "hibernate-redis.properties");

        props.setProperty(Environment.GENERATE_STATISTICS, "false");
        props.setProperty(Environment.USE_STRUCTURED_CACHE, "true");
        props.setProperty(Environment.TRANSACTION_STRATEGY, JDBCTransactionFactory.class.getName());

        return props;
    }

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.HSQL)
                .build();
    }

    @Bean
    public SessionFactory sessionFactory() throws Exception {

        AnnotationSessionFactoryBean factoryBean = new AnnotationSessionFactoryBean();
        factoryBean.setPackagesToScan(getMappedPackageNames());
        factoryBean.setDataSource(dataSource());
        factoryBean.setHibernateProperties(hibernateProperties());

        factoryBean.afterPropertiesSet();

        return factoryBean.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager() throws Exception {
        return new HibernateTransactionManager(sessionFactory());
    }

    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator() {
        return new HibernateExceptionTranslator();
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
}
