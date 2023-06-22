# easydb

## How to use

### maven

```xml
<dependency>
  <groupId>io.vproxy</groupId>
  <artifactId>easydb</artifactId>
  <version>1.0.0</version>
</dependency>
```

### gradle

```groovy
implementation 'io.vproxy:easydb:1.0.0'
```

### init

```java
var config = new HikariConfigW();
config.setJdbcUrl("mysql", "127.0.0.1", 3306, "dbname");
config.setUsername("****");
config.setPassword("********");

var jdbc = new JDBC(config);
```

### query and convert

```java
var ls = jdbc.prepare("select * from test_user where name like ?")
             .param("%alice%")
             .query().convert(User.rule);
// use convertFirst to convert only one row (return null if no row returned)
```

### query and get result set

```java
try (var rs = jdbc.prepare("...").query()) {
  var rs0 = rs.getResultSet();
  // ...
}
```

### simple execute

```java
jdbc.prepare("delete from test_user where 1 = 1").execute();
```

### transaction

```java
jdbc.transaction(conn -> {
  conn.prepare("...").execute();
  conn.prepare("...").execute();
});
```

### get connection

```java
try (var conn = jdbc.connection()) {
  conn.transaction(c -> {
    // ...
  });
  conn.transaction(c -> {
    // ...
  });
}
```
