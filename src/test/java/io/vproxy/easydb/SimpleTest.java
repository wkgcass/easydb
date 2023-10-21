package io.vproxy.easydb;

import io.vproxy.easydb.jdbc.HikariConfigW;
import io.vproxy.easydb.jdbc.JDBC;
import vjson.deserializer.rule.*;

import java.util.UUID;

public class SimpleTest {
    public static void main(String[] args) {
        var config = new HikariConfigW();
        config.setJdbcUrl("mysql", "127.0.0.1", 3306, "test");
        config.setUsername("root");
        config.setPassword("aaaaaaaa");

        var jdbc = new JDBC(config);

        jdbc.prepare("delete from test_user where 1 = 1").execute();

        var ls = jdbc.prepare("select * from test_user where name like ?")
            .param("%alice%").query().convert(User.rule);
        System.out.println(ls);

        jdbc.transaction(conn -> {
            conn.prepare("insert into test_user (id, name, age) values (?, ?, ?)")
                .param(UUID.randomUUID().toString()).param("alice").param(18)
                .execute();
            conn.prepare("insert into test_user (id, name, age) values (?, ?, ?)")
                .param(UUID.randomUUID().toString()).param("bob").param(21)
                .execute();
            conn.prepare("insert into test_user (id, name, age) values (?, ?, ?)")
                .param(UUID.randomUUID().toString()).param("eve").param(24)
                .execute();
        });

        ls = jdbc.prepare("select * from test_user where name like ?")
            .param("%alice%").query().convert(User.rule);
        System.out.println(ls);
    }

    static class User {
        public String id;
        public String name;
        public int age;
        public static final Rule<User> rule = new ObjectRule<>(User::new)
            .put("id", (o, it) -> o.id = it, StringRule.get())
            .put("name", (o, it) -> o.name = it, NullableStringRule.get()) // use NullableStringRule for tests in another case
            .put("age", (o, it) -> o.age = it, IntRule.get());

        @Override
        public String toString() {
            return "User{" +
                   "id=" + (id == null ? "null" : ("'" + id + "'")) +
                   ", name=" + (name == null ? "null" : ("'" + name + "'")) +
                   ", age=" + age +
                   '}';
        }
    }
}
