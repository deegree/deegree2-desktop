package org.deegree.igeo.jdbc;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DatabaseTestConfig.class })
public class DatabaseIntegrationTest {

    @Autowired
    @Qualifier("postgres84")
    private DataSource datasource;

    @Test
    public void testDatabaseAccessWithValidConnectionSettings() {
        // TODO: implement
    }

}
