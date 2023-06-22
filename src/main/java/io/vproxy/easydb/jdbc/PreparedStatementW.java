package io.vproxy.easydb.jdbc;

import io.vproxy.easydb.SQLWException;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PreparedStatementW {
    private final ConnectionW connAutoClose;
    private final PreparedStatement pstmt;
    private int offset = 0;

    public PreparedStatementW(ConnectionW connAutoClose, PreparedStatement pstmt) {
        this.connAutoClose = connAutoClose;
        this.pstmt = pstmt;
    }

    public PreparedStatementW param(String s) {
        try {
            pstmt.setString(++offset, s);
        } catch (SQLException e) {
            closePStmtIgnoreException();
            throw new SQLWException(e);
        }
        return this;
    }

    public PreparedStatementW param(int n) {
        try {
            pstmt.setInt(++offset, n);
        } catch (SQLException e) {
            closePStmtIgnoreException();
            throw new SQLWException(e);
        }
        return this;
    }

    public PreparedStatementW param(long n) {
        try {
            pstmt.setLong(++offset, n);
        } catch (SQLException e) {
            closePStmtIgnoreException();
            throw new SQLWException(e);
        }
        return this;
    }

    public PreparedStatementW param(double n) {
        try {
            pstmt.setDouble(++offset, n);
        } catch (SQLException e) {
            closePStmtIgnoreException();
            throw new SQLWException(e);
        }
        return this;
    }

    public PreparedStatementW param(boolean b) {
        try {
            pstmt.setBoolean(++offset, b);
        } catch (SQLException e) {
            closePStmtIgnoreException();
            throw new SQLWException(e);
        }
        return this;
    }

    public PreparedStatementW param(byte[] bytes) {
        try {
            pstmt.setBytes(++offset, bytes);
        } catch (SQLException e) {
            closePStmtIgnoreException();
            throw new SQLWException(e);
        }
        return this;
    }

    public int nextOffset() {
        return ++offset;
    }

    public ResultSetW query() {
        try {
            return new ResultSetW(pstmt.executeQuery(), this);
        } catch (SQLException e) {
            closePStmtIgnoreException();
            throw new SQLWException(e);
        }
    }

    public int execute() {
        try {
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLWException(e);
        } finally {
            closePStmtIgnoreException();
        }
    }

    private void closePStmtIgnoreException() {
        try {
            pstmt.close();
        } catch (SQLException ignore) {
        }
        if (connAutoClose != null) {
            try {
                connAutoClose.close();
            } catch (SQLWException ignore) {
            }
        }
    }

    public PreparedStatement getPreparedStatement() {
        return pstmt;
    }
}
