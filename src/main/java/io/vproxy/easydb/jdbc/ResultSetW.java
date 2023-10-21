package io.vproxy.easydb.jdbc;

import io.vproxy.easydb.SQLWException;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import vjson.deserializer.rule.*;
import vjson.ex.JsonParseException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ResultSetW implements AutoCloseable {
    private final ResultSet rs;
    private final PreparedStatementW pstmt;

    public ResultSetW(ResultSet rs, PreparedStatementW pstmt) {
        this.rs = rs;
        this.pstmt = pstmt;
    }

    public long count() {
        try {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new SQLWException(e);
        } finally {
            close();
        }
    }

    public <T> T convertFirst(Rule<T> rule) {
        //noinspection unchecked
        rule = (Rule<T>) rule.real();
        if (!(rule instanceof ObjectRule)) {
            throw new IllegalArgumentException("Rule " + rule + " is not an object rule");
        }
        try {
            if (!rs.next()) {
                return null;
            }
            return serialize(rs, (ObjectRule<T>) rule);
        } catch (SQLException e) {
            throw new SQLWException(e);
        } finally {
            close();
        }
    }

    public <T> Optional<T> convertFirstOptional(Rule<T> rule) {
        return Optional.ofNullable(convertFirst(rule));
    }

    public <T> List<T> convert(Rule<T> rule) {
        //noinspection unchecked
        rule = (Rule<T>) rule.real();
        if (!(rule instanceof ObjectRule)) {
            throw new IllegalArgumentException("Rule " + rule + " is not an object rule");
        }
        var ls = new ArrayList<T>();
        try {
            while (rs.next()) {
                ls.add(serialize(rs, (ObjectRule<T>) rule));
            }
        } catch (SQLException e) {
            throw new SQLWException(e);
        } finally {
            close();
        }
        return ls;
    }

    @SuppressWarnings("unchecked")
    private <T> T serialize(ResultSet rs, ObjectRule<T> rule) {
        try {
            var cons = rule.getConstruct();
            Object constructed = cons.invoke();

            var meta = rs.getMetaData();
            for (int i = 1; i <= meta.getColumnCount(); ++i) {
                var name = meta.getColumnName(i);
                var field = rule.getRule(name);
                setField(constructed, field, rule, rs, name);
            }

            var build = rule.getBuild();
            var result = build.invoke(constructed);
            return (T) result;
        } catch (SQLException e) {
            throw new SQLWException(e);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void setField(Object holder, ObjectField<?, ?> field, ObjectRule<?> rootRule, ResultSet rs, String colname) throws SQLException {
        var value = rs.getObject(colname);

        if (field == null) {
            for (Function3 r : rootRule.getExtraRules()) {
                r.invoke(holder, colname, value);
            }
            return;
        }

        var fieldRule = field.getRule();
        if (value != null) {
            fieldRule = fieldRule.real();
        }
        Function2 set = field.getSet();

        if (value == null) {
            if (fieldRule instanceof NullableRule) {
                set.invoke(holder, ((NullableRule<?>) fieldRule).getOpIfNull().invoke());
                return;
            }
            if (fieldRule instanceof ArrayRule || fieldRule instanceof ObjectRule) {
                set.invoke(holder, null);
                return;
            }
        } else if (fieldRule instanceof BoolRule) {
            var b = rs.getBoolean(colname);
            set.invoke(holder, b);
            return;
        } else if (fieldRule instanceof DoubleRule) {
            var d = rs.getDouble(colname);
            set.invoke(holder, d);
            return;
        } else if (fieldRule instanceof LongRule) {
            var l = rs.getLong(colname);
            set.invoke(holder, l);
            return;
        } else if (fieldRule instanceof IntRule) {
            var i = rs.getInt(colname);
            set.invoke(holder, i);
            return;
        } else if (fieldRule instanceof StringRule) {
            set.invoke(holder, rs.getString(colname));
            return;
        }
        throw new JsonParseException(
            "invalid type: expecting: " + fieldRule + ", value=" + value + "(" + ((value == null) ? "nil"
                : value.getClass()) + ")"
        );
    }

    @Override
    public void close() {
        try {
            rs.close();
        } catch (SQLException e) {
            throw new SQLWException(e);
        } finally {
            pstmt.close();
        }
    }

    public ResultSet getResultSet() {
        return rs;
    }
}
