package io.vproxy.easydb.jdbc;

import io.vproxy.easydb.SQLWException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Function;

public class ConnectionW implements AutoCloseable {
    private final Connection conn;

    public ConnectionW(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            throw new SQLWException(e);
        }
    }

    public PreparedStatementW prepare(String sql) {
        try {
            return new PreparedStatementW(null, conn.prepareStatement(sql));
        } catch (SQLException e) {
            throw new SQLWException(e);
        }
    }

    public <T> T transaction(Function<ConnectionW, T> exec) {
        boolean isAutoCommit;
        try {
            isAutoCommit = conn.getAutoCommit();
        } catch (SQLException e) {
            throw new SQLWException(e);
        }
        try {
            conn.setAutoCommit(false);
            T result;
            try {
                result = exec.apply(this);
            } catch (Throwable t) {
                conn.rollback();
                throw t;
            }
            conn.commit();
            return result;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ignore) {
                // rollback failed ?
                close();
            }
            throw new SQLWException(e);
        } finally {
            if (isAutoCommit) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignore) {
                    // failed setting auto commit back to true
                }
            }
        }
    }

    public void transaction(Consumer<ConnectionW> exec) {
        boolean isAutoCommit;
        try {
            isAutoCommit = conn.getAutoCommit();
        } catch (SQLException e) {
            throw new SQLWException(e);
        }
        try {
            conn.setAutoCommit(false);
            try {
                exec.accept(this);
            } catch (Throwable t) {
                conn.rollback();
                throw t;
            }
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ignore) {
                // rollback failed ?
                close();
            }
            throw new SQLWException(e);
        } finally {
            if (isAutoCommit) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignore) {
                    // failed setting auto commit back to true
                }
            }
        }
    }

    public Connection getConnection() {
        return conn;
    }
}
