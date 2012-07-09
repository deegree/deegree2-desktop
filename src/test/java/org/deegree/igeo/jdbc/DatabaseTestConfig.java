package org.deegree.igeo.jdbc;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.deegree.igeo.config.JDBCConnection;
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
    public DataSource sqlserver2008DataSource() {
        return null;
    }

    @Bean
    public JDBCConnection sqlserver2008() {
        return new JDBCConnection( "com.microsoft.sqlserver.jdbc.SQLServerDriver",
                                   "jdbc:sqlserver://sqlserver2008:1433;databaseName=deegreedesktop-it;",
                                   "deegreedesktop", "deegreedesktop", false );
    }

}
