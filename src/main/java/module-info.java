module io.vproxy.easydb {
    requires transitive com.zaxxer.hikari;
    requires transitive java.sql;
    requires vjson;
    requires kotlin.stdlib;

    exports io.vproxy.easydb;
    exports io.vproxy.easydb.jdbc;
}
