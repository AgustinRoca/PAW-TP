package ar.edu.itba.paw.webapp.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Properties;

@EnableWebMvc
@ComponentScan({
        "ar.edu.itba.paw.webapp.rest",
        "ar.edu.itba.paw.services",
        "ar.edu.itba.paw.persistence",
        "ar.edu.itba.paw.webapp.media_types.parsers",
        "ar.edu.itba.paw.webapp.exceptions"
})
@Configuration
@EnableTransactionManagement
@EnableAsync
@PropertySource("classpath:application-prod.properties")
@PropertySource(value = "classpath:application-local.properties", ignoreResourceNotFound = true) // This will precede previous properties
public class WebConfig {
    @Value("${db.host}")
    private String DB_HOST;
    @Value("${db.port}")
    private String DB_PORT;
    @Value("${db.user}")
    private String DB_USER;
    @Value("${db.pass}")
    private String DB_PASS;
    @Value("${db.db}")
    private String DB_DB;
    @Value("${app.host}")
    private String baseUrl;
    @Value("${app.api.path}")
    private String apiPath;

    //Used in addition of @PropertySource
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public String baseUrl(){
        return "http://" + this.baseUrl;
    }

    @Bean
    public String apiPath(){
        return this.apiPath;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:i18n/message");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.displayName());
        messageSource.setCacheSeconds(5);
        return messageSource;
    }

    @Bean
    public ViewResolver viewResolver() {
        final InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();

        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");
        viewResolver.setContentType("text/html; charset=UTF-8");

        return viewResolver;
    }

    @Bean
    public DataSource dataSource() {
        final SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        dataSource.setDriverClass(org.postgresql.Driver.class);
        dataSource.setUrl(this.buildJDBCUrl());
        dataSource.setUsername(this.DB_USER);
        dataSource.setPassword(this.DB_PASS);

        return dataSource;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);

        return mapper;
    }

    @Bean
    public PlatformTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean(name = "multipartResolver")
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver cmr = new CommonsMultipartResolver();
        return cmr;
    }

    @Bean
    public SimpleMappingExceptionResolver simpleMappingExceptionResolver() {
        SimpleMappingExceptionResolver exceptionResolver = new MappingExceptionResolver();
        Properties properties = new Properties();
        properties.setProperty(Exception.class.getName(), "error/500");

        exceptionResolver.setExceptionMappings(properties);
        exceptionResolver.setDefaultStatusCode(500);
        exceptionResolver.setDefaultErrorView("error/500");
        exceptionResolver.setExceptionAttribute("ex");
        return exceptionResolver;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
        final LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setPackagesToScan("ar.edu.itba.paw.models");
        factoryBean.setDataSource(this.dataSource());

        final JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        factoryBean.setJpaVendorAdapter(vendorAdapter);

        final Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL92Dialect");
        properties.setProperty("hibernate.order_inserts", "true");
        properties.setProperty("hibernate.jdbc.batch_size", "5");
        properties.setProperty("jadira.usertype.autoRegisterUserTypes", "true");
//        properties.setProperty("hibernate.show_sql", "true");
//        properties.setProperty("format_sql", "true");

        factoryBean.setJpaProperties(properties);
        return factoryBean;
    }

    private String buildJDBCUrl() {
        return "jdbc:postgresql://" + this.DB_HOST + ":" + this.DB_PORT + "/" + this.DB_DB + "?useUnicode=true&amp;characterEncoding=utf8";
    }

    private static class MappingExceptionResolver extends SimpleMappingExceptionResolver {
        private static final Logger LOGGER = LoggerFactory.getLogger(MappingExceptionResolver.class);

        @Override
        protected void logException(Exception ex, HttpServletRequest request) {
            LOGGER.error("Request: {} raised: {}", request.getRequestURL(), Arrays.toString(ex.getStackTrace()));
        }
    }
}
