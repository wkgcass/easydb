package io.vproxy.easydb.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.vproxy.easydb.SQLWException;

import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Function;

public class JDBC {
    private final HikariDataSource ds;

    public JDBC(HikariConfig config) {
        this.ds = new HikariDataSource(config);
    }

    public JDBC(HikariDataSource ds) {
        this.ds = ds;
    }

    public ConnectionW connection() {
        return connection(true);
    }

    public ConnectionW connection(boolean autoCommit) {
        try {
            var conn = new ConnectionW(ds.getConnection());
            try {
                conn.getConnection().setAutoCommit(autoCommit);
            } catch (SQLException e) {
                conn.close();
                throw e;
            }
            return conn;
        } catch (SQLException e) {
            throw new SQLWException(e);
        }
    }

    public PreparedStatementW prepare(String sql) {
        //noinspection resource
        var conn = connection();
        return conn.prepare(sql, true);
    }

    public PreparedStatementProcessor prepare() {
        var conn = connection();
        return new PreparedStatementProcessor(conn, true);
    }

    public <T> T transactionWithResult(Function<ConnectionW, T> exec) {
        var conn = connection(false);
        try (conn) {
            return conn.transaction(exec);
        }
    }

    public void transaction(Consumer<ConnectionW> exec) {
        var conn = connection(false);
        try (conn) {
            conn.transaction(exec);
        }
    }

    public HikariDataSource getHikariDataSource() {
        return ds;
    }

    public void close() {
        ds.close();
    }
}
