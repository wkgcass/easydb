package io.vproxy.easydb.jdbc;

public class PreparedStatementProcessor implements StringTemplate.Processor<PreparedStatementW, RuntimeException> {
    private final ConnectionW conn;
    private final boolean autoClose;

    PreparedStatementProcessor(ConnectionW conn, boolean autoClose) {
        this.conn = conn;
        this.autoClose = autoClose;
    }

    @Override
    public PreparedStatementW process(StringTemplate template) throws RuntimeException {
        var prepSB = new StringBuilder();
        var i = -1;
        for (var s : template.fragments()) {
            if (i != -1) {
                var o = template.values().get(i);
                if (o == null) {
                    prepSB.append("null");
                } else {
                    prepSB.append("?");
                }
            }
            prepSB.append(s);
            ++i;
        }
        var prepStr = prepSB.toString();
        var prep = conn.prepare(prepStr, autoClose);

        for (var o : template.values()) {
            prepare(prep, o);
        }
        return prep;
    }

    private void prepare(PreparedStatementW prep, Object o) {
        switch (o) {
            case Integer i -> prep.param(i);
            case Long l -> prep.param(l);
            case Double d -> prep.param(d);
            case String s -> prep.param(s);
            case Boolean b -> prep.param(b);
            case byte[] ba -> prep.param(ba);
            case null -> {
                // do nothing
            }
            default -> throw new IllegalArgumentException(STR."unsupported type \{o.getClass()}: \{o}");
        }
    }
}
