package com.taisys.ihvWeb.config;

import java.beans.PropertyVetoException;
import java.util.Properties;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mchange.v2.c3p0.ComboPooledDataSource;
 
@Configuration
@EnableTransactionManagement
@ComponentScan({ "com.taisys" })
@PropertySource(value = { "file:/Users/ashish/Documents/ihv.properties" })

public class HibernateConfig{
 
    @Autowired
    private Environment environment;
 
    @Bean
    public LocalSessionFactoryBean sessionFactory() throws IllegalStateException, PropertyVetoException, NamingException{
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan(new String[] { "com.taisys" });
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
     }
     
    @Bean
    public DataSource dataSource() throws IllegalStateException, PropertyVetoException, NamingException{
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName(environment.getRequiredProperty("jdbc.driverClassName"));
//        dataSource.setUrl(environment.getRequiredProperty("jdbc.url"));
//        dataSource.setUsername(environment.getRequiredProperty("jdbc.username"));
//        dataSource.setPassword(environment.getRequiredProperty("jdbc.password"));
//        return dataSource;
        
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
    	//dataSource.setDataSourceName(environment.getRequiredProperty("jdbc.dataSourceName"));
    	dataSource.setDriverClass(environment.getRequiredProperty("jdbc.driverClassName"));
        dataSource.setJdbcUrl(environment.getRequiredProperty("jdbc.url"));
        dataSource.setUser(environment.getRequiredProperty("jdbc.username")); 
        dataSource.setPassword(environment.getRequiredProperty("jdbc.password"));
        dataSource.setMinPoolSize(5);
        dataSource.setMaxPoolSize(15);
        dataSource.setAcquireIncrement(1);
        dataSource.setAcquireRetryAttempts(3);
        dataSource.setAcquireRetryDelay(3000);
        dataSource.setMaxStatements(50);
        dataSource.setUnreturnedConnectionTimeout(1800);
        return dataSource;
    }
     
    private Properties hibernateProperties() {
        Properties properties = new Properties();
       // properties.put("hibernate.connection.datasource", environment.getRequiredProperty("jndi.dataSourceName"));
        properties.put("hibernate.dialect", environment.getRequiredProperty("hibernate.dialect"));
        properties.put("hibernate.show_sql", environment.getRequiredProperty("hibernate.show_sql"));
        properties.put("hibernate.format_sql ", environment.getRequiredProperty("hibernate.format_sql"));
        properties.put("hibernate.hbm2ddl.auto", environment.getRequiredProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.jdbc.batch_size", environment.getRequiredProperty("hibernate.jdbc.batch_size"));
       // properties.put("hibernate.jndi.class", environment.getRequiredProperty("hibernate.jndi.class"));
        return properties;        
    }
     
    @Bean
    @Autowired
    public HibernateTransactionManager transactionManager(SessionFactory s) {
       HibernateTransactionManager txManager = new HibernateTransactionManager();
       txManager.setSessionFactory(s);
       return txManager;
    }
}