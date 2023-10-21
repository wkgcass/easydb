package io.vproxy.easydb;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    BeforeAll.class,
    TestQuery.class,
    TestTransaction.class,
    TestResourceLeak.class,
    TestStringProcessor.class,
})
public class Cases {
}
