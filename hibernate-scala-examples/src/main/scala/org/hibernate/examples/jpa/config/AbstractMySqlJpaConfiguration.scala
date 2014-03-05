package org.hibernate.examples.jpa.config

import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource
import java.util.Properties
import org.hibernate.cfg.Environment
import org.hibernate.examples._

/**
 * org.hibernate.examples.jpa.config.AbstractMySqlJpaConfiguration 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 9. 오후 4:08
 */
@Configuration
@EnableTransactionManagement
abstract class AbstractMySqlJpaConfiguration extends AbstractJpaConfiguration {

    @Bean
    override def dataSource(): DataSource = {
        buildDataSource(DRIVER_CLASS_MYSQL, "jdbc:mysql://localhost/" + getDatabaseName, "root", "root")
    }

    override def jpaProperties(): Properties = {
        val props: Properties = super.jpaProperties()
        props.put(Environment.DIALECT, DIALECT_MYSQL)
        props
    }
}
