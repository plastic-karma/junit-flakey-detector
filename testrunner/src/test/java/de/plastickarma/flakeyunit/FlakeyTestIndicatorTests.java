package de.plastickarma.flakeyunit;


import junit.framework.AssertionFailedError;
import org.junit.Test;
import org.junit.rules.TestRule;

import static de.plastickarma.flakeyunit.UnittestHelper.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Test cases for the core flakey detector mechanism.
 */
public class FlakeyTestIndicatorTests {


    @Test public void passingTestIsNotFlakey() throws Throwable {
        final CountingFlakeyTestcaseListener counter = new CountingFlakeyTestcaseListener();
        final TestRule indicator = FlakeyTestIndicatorBuilder.indicator().listener(counter).build();
        createPassingTest(indicator).evaluate();
        assertThat(counter.getFlakeyTests().size(), is(0));
    }

    @Test public void failingTestIsNotFlakey() throws Throwable {
        CountingFlakeyTestcaseListener counter = new CountingFlakeyTestcaseListener();
        final TestRule indicator = FlakeyTestIndicatorBuilder.indicator().listener(counter).build();
        try {
            createFailingTest(indicator).evaluate();
            fail("Exception expected");
        } catch (AssertionFailedError e) {
            // Expected
        }
        assertThat(counter.getFlakeyTests().size(), is(0));
    }

    @Test public void flakeyTestIsRecognized() throws Throwable {
        final CountingFlakeyTestcaseListener counter = new CountingFlakeyTestcaseListener();
        final TestRule indicator = FlakeyTestIndicatorBuilder.indicator().listener(counter).build();

        try {
            createFlakeyTest(indicator, FlakeyTestIndicatorTests.class, "flakey1").evaluate();
            fail("Exception expected");
        } catch (AssertionFailedError throwable) {
            // Expected
        }
        assertThat(counter.getFlakeyTests().size(), is(1));
        assertThat(counter.getFlakeyTests().get(0), is("flakey1"));
    }

    @Test public void oneIndicatorCanHandleMultipleTestcases() throws Throwable {
        final CountingFlakeyTestcaseListener counter = new CountingFlakeyTestcaseListener();
        final TestRule indicator = FlakeyTestIndicatorBuilder.indicator().listener(counter).build();

        // First test: flakey
        try {
            createFlakeyTest(indicator, FlakeyTestIndicatorTests.class, "flakey1").evaluate();
            fail("Exception expected");
        } catch (AssertionFailedError throwable) {
            // Expected
        }

        // Second test: passing
        createPassingTest(indicator).evaluate();

        // Third test: flakey
        try {
            createFlakeyTest(indicator, FlakeyTestIndicatorTests.class,  "flakey2").evaluate();
            fail("Exception expected");
        } catch (AssertionFailedError e) {
            // Expected
        }

        // Fourth test: failing
        try {
            createFailingTest(indicator).evaluate();
            fail("Exception expected");
        } catch (AssertionFailedError e) {
            // Expected
        }

        assertThat(counter.getFlakeyTests().size(), is(2));
        assertThat(counter.getFlakeyTests().get(0), is("flakey1"));
        assertThat(counter.getFlakeyTests().get(1), is("flakey2"));
    }

    @Test public void flakeyTestIsRecognizedButTestPassesIfNoRethrow() throws Throwable {
        final CountingFlakeyTestcaseListener counter = new CountingFlakeyTestcaseListener();
        final TestRule indicator = FlakeyTestIndicatorBuilder.indicator()
                .rethrowOriginal(false)
                .listener(counter)
                .build();

        createFlakeyTest(indicator, FlakeyTestIndicatorTests.class, "flakey1").evaluate();

        assertThat(counter.getFlakeyTests().size(), is(1));
        assertThat(counter.getFlakeyTests().get(0), is("flakey1"));
    }

    @Test public void failingTestFailsEvenIfNoRethrow() throws Throwable {
        CountingFlakeyTestcaseListener counter = new CountingFlakeyTestcaseListener();
        final TestRule indicator = FlakeyTestIndicatorBuilder.indicator()
                .rethrowOriginal(false)
                .listener(counter)
                .build();
        try {
            createFailingTest(indicator).evaluate();
            fail("Exception expected");
        } catch (AssertionFailedError e) {
            // Expected
        }
        assertThat(counter.getFlakeyTests().size(), is(0));
    }
}
