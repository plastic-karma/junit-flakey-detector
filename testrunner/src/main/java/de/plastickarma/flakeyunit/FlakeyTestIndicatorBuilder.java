package de.plastickarma.flakeyunit;


import org.junit.rules.TestRule;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Builder for the TestRule, that indicates a flakey test cases.
 * Usage:
 * <code>
 *     FlakeyTestIndicatorBuilder
 *          .indicator()
 *          .retries(15)  // defaults to 10
 *          .threshold(5) // defaults to 1
 *          .listener(...listener1...)
 *          .listener(...listener2...)
 *          .build();
 * </code>
 */
public class FlakeyTestIndicatorBuilder {

    private final List<FlakeyTestcaseListener> listeners = new ArrayList<>();
    private int noOfRetries = 10;
    private int flakeyThreshold = 1;
    private int waitTime = 0;
    private TimeUnit waitTimeUnit = TimeUnit.MILLISECONDS;
    private boolean rethrowOriginal = true;

    private FlakeyTestIndicatorBuilder() { }


    /**
     * Creates a new Builder Object.
     */
    public static FlakeyTestIndicatorBuilder indicator() {
        return new FlakeyTestIndicatorBuilder();
    }

    /**
     * Adds a FlakeyTestcaseListener to the FlakeyTestIndicator.
     */
    public FlakeyTestIndicatorBuilder listener(FlakeyTestcaseListener listener) {
        this.listeners.add(listener);
        return this;
    }

    /**
     * Sets the number of retries the test case is executed again to check if it is flakey.
     * Defaults to 10.
     */
    public FlakeyTestIndicatorBuilder retries(final int retries) {
        this.noOfRetries = retries;
        return this;
    }

    /**
     * Boolean flag to indicate, if the original exception should be rethrown. If set to <code>false</code>
     * a flakey test case, while being detected and passed to the listeners, will appear to Junit as passed.
     */
    public FlakeyTestIndicatorBuilder rethrowOriginal(final boolean rethrow) {
        this.rethrowOriginal = rethrow;
        return this;
    }

    /**
     * Sets the threshold of a test for being flakey, i.e. how many times must does the test case pass after
     * initial failure, in order to be considered flakey.
     * Defaults to 1.
     */
    public FlakeyTestIndicatorBuilder threshold(final int threshold) {
        this.flakeyThreshold = threshold;
        return this;
    }

    /**
     * Sets the waiting time between test reruns.
     * @param waitTime Time, that will be waited before rerunning a test case again
     * @param waitTimeUnit Time unit for the waiting time.
     */
    public FlakeyTestIndicatorBuilder waitTime(final int waitTime, final TimeUnit waitTimeUnit) {
        this.waitTime = waitTime;
        this.waitTimeUnit = waitTimeUnit;
        return this;
    }

    /**
     *  Adds a listener, that logs the discovery of flakey tests to System.out.
     */
    public FlakeyTestIndicatorBuilder logToStdout() {
        return this.listener(new PrintStreamListener(System.out));
    }

    /**
     * Builds the actual TestRule, that indicates flakey test cases.
     */
    public TestRule build() {
        return new FlakeyTestIndicatorRule(
                this.noOfRetries,
                this.flakeyThreshold,
                this.rethrowOriginal,
                this.waitTime,
                this.waitTimeUnit,
                this.listeners);
    }
}
