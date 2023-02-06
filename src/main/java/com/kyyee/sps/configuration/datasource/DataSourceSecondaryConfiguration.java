package com.kyyee.sps.configuration.datasource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@AutoConfigureAfter({PostgresqlInitialConfiguration.class})
@MapperScan(basePackages = "com.kyyee.sps.mapper.secondary", sqlSessionFactoryRef = "sqlSessionFactorySecondary")
public class DataSourceSecondaryConfiguration {

    @Bean(name = "dataSourceSecondary")
    @ConfigurationProperties(prefix = "spring.datasource.secondary")
    public DataSource dataSourceSecondary() {
        return DataSourceBuilder.create().build();

    }

    @Bean(name = "sqlSessionFactorySecondary")
    public SqlSessionFactory sqlSessionFactorySecondary(@Qualifier("dataSourceSecondary") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/secondary/*Mapper.xml"));
        Objects.requireNonNull(bean.getObject()).getConfiguration().setMapUnderscoreToCamelCase(true);
        bean.setTypeAliasesPackage("com.kyyee.sps.model.secondary");
        bean.setTypeHandlersPackage("com.kyyee.sps.mapper.handler");

        return bean.getObject();
    }

    @Bean(name = "transactionManagerSecondary")
    public DataSourceTransactionManager transactionManagerSecondary(@Qualifier("dataSourceSecondary") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);

    }

    @Bean(name = "sqlSessionTemplateSecondary")
    public SqlSessionTemplate sqlSessionTemplateSecondary(@Qualifier("sqlSessionFactorySecondary") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
