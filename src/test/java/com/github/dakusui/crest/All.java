package com.github.dakusui.crest;

import com.github.dakusui.crest.examples.Example2;
import com.github.dakusui.crest.ut.CrestFunctionsTest;
import com.github.dakusui.crest.ut.CrestPredicatesTest;
import com.github.dakusui.crest.ut.CrestTest;
import com.github.dakusui.crest.ut.FormattableTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    CrestFunctionsTest.class,
    CrestTest.class,
    CrestPredicatesTest.class,
    FormattableTest.class,
    Example2.class
})
public class All {
}
