package io.vproxy.easydb.jdbc;

import com.zaxxer.hikari.HikariConfig;

import java.util.Properties;

public class HikariConfigW extends HikariConfig {
    public HikariConfigW() {
    }

    public HikariConfigW(Properties properties) {
        super(properties);
    }

    public HikariConfigW(String propertyFileName) {
        super(propertyFileName);
    }

    public void setJdbcUrl(String proto, String host, int port, String db) {
        setJdbcUrl(proto, host, port, db, "");
    }

    public void setJdbcUrl(String proto, String host, int port, String db, String suffix) {
        setJdbcUrl("jdbc:" + proto + "://" + host + ":" + port + "/" + db + suffix);
    }
}
