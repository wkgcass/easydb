# easydb

## How to use

### JDK

Requires at least **Java 21**.

### maven

```xml
<dependency>
  <groupId>io.vproxy</groupId>
  <artifactId>easydb</artifactId>
  <version>1.0.1</version>
</dependency>
```

### gradle

```groovy
implementation 'io.vproxy:easydb:1.0.1'
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
var namePattern = "%alice%";
var ls = jdbc.prepare()."select * from test_user where name like \{namePattern}"
             .query().convert(User.rule);
// use .convertFirst(...) to convert only one row (return null if no row returned)
// use .count() to get result of count(*) statements
```

### query and get result set

```java
try (var rs = jdbc.prepare()."...".query()) {
  var rs0 = rs.getResultSet();
  // ...
}
```

### simple execute

```java
jdbc.prepare()."delete from test_user where 1 = 1".execute();
```

### transaction

```java
jdbc.transaction(conn -> {
  conn.prepare()."...".execute();
  conn.prepare()."...".execute();
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
