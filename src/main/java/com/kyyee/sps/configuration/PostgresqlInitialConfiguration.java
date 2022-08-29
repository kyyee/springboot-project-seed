package com.kyyee.sps.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.sql.*;

@Configuration
@AutoConfigureBefore({DataSourceAutoConfiguration.class})
@Slf4j
public class PostgresqlInitialConfiguration {

    @Value("${DATASOURCE_URL:svc-pgsql:5432}")
    private String url;
    @Value("${DATASOURCE_USERNAME:pgsql}")
    private String username;
    @Value("${DATASOURCE_PASSWORD:pgsql}")
    private String password;
    @Value("${DATABASE:cg_imss_cdplatform}")
    private String database;
    @Value("${PGSQLSSLROOT:/opt/app/pgsqlssl}")
    private String pgsqlsslroot;

    @PostConstruct
    public void initDatabase() {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://" + url + "/postgres?sslmode=prefer&sslrootcert=" + pgsqlsslroot + "/root.crt&sslkey=" + pgsqlsslroot + "/pgsql.pk8&sslcert=" + pgsqlsslroot + "/pgsql.crt", username, password);
             PreparedStatement query = connection.prepareStatement("SELECT count(*) FROM pg_catalog.pg_database AS u WHERE u.datname=?");
             Statement create = connection.createStatement()) {
            query.setString(1, database);
            ResultSet resultSet = query.executeQuery();
            while (resultSet.next()) {
                long count = resultSet.getLong("count");
                if (count <= 0) {
                    log.info("database:{} creating...", database);
                    create.execute("CREATE DATABASE " + database);
                } else {
                    log.info("database:{} is exist.", database);
                }
            }
        } catch (SQLException e) {
            // pgsql is not available
            log.error("init pgsql {} failed, message:{}", database, e.getMessage());
            // 如果失败，则不在创建database，初始化失败并退出启动
            System.exit(-1);

        }
    }
}
