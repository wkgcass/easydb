package io.vproxy.easydb;

import java.sql.SQLException;

public class SQLWException extends RuntimeException {
    public SQLWException(SQLException cause) {
        super(cause);
    }
}
