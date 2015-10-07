package de.plastickarma.flakeyunit;

import junit.framework.AssertionFailedError;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Created by benjaminrogge on 07.10.15.
 */
public class UnittestHelper {
    static Statement createFailingTest(final TestRule flakeyIndicator) {
        return flakeyIndicator.apply(new Statement() {
            @Override
            public void evaluate() throws Throwable {
                throw new AssertionFailedError("");
            }
        }, Description.createTestDescription(UnittestHelper.class, ""));
    }

    static Statement createPassingTest(final TestRule flakeyIndicator) {
        return flakeyIndicator.apply(new Statement() {
            @Override
            public void evaluate() throws Throwable {

            }
        }, Description.createTestDescription(UnittestHelper.class, ""));
    }

    public static Statement createFlakeyTest(
            final TestRule flakeyIndicator,
            Class<?> testClass,
            final String testcaseName) {
        return flakeyIndicator.apply(new Statement() {
            private int state = 1;
            @Override
            public void evaluate() throws Throwable {
                if (state++ % 2 == 1) {
                    throw new AssertionFailedError();
                }
            }
        }, Description.createTestDescription(testClass, testcaseName));
    }
}
