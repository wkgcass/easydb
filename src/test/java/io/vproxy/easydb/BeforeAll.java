package io.vproxy.easydb;

import io.vproxy.easydb.jdbc.HikariConfigW;
import io.vproxy.easydb.jdbc.JDBC;
import org.junit.BeforeClass;
import org.junit.Test;

public class BeforeAll {
    public static JDBC jdbc;

    @BeforeClass
    public static void beforeClass() throws Exception {
        if (jdbc != null)
            return;
        Class.forName("org.hsqldb.jdbc.JDBCDriver");

        var config = new HikariConfigW();
        config.setJdbcUrl("jdbc:hsqldb:mem:mymemdb");
        config.setUsername("SA");
        config.setPassword("");
        jdbc = new JDBC(config);
    }

    @Test
    public void dummy() {
    }
}
