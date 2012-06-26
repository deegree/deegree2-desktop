package org.deegree.igeo.jdbc;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseTestConfig {

    @Bean
    public DataSource oracle10()
                            throws SQLException {
        return null;
    }

    @Bean
    public DataSource oracle11()
                            throws SQLException {
        return null;
    }

    @Bean
    public DataSource postgres82() {
        return null;
    }

    @Bean
    public DataSource postgres83() {
        return null;
    }

    @Bean
    public DataSource postgres84() {
        return null;

    }

    @Bean
    public DataSource sqlserver2008() {
        return null;
    }

}
