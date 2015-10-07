package de.plastickarma.flakeyunit;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * TestRule, that reruns failed testcases in order to determine, if a testcase if flakey.
 * A testcase is considered potentially flakey, if number of successful testcase executions after
 * the initial failure exceeds a given threshold.
 */
final class FlakeyTestIndicatorRule implements TestRule {

    private final List<FlakeyTestcaseListener> listeners;
    private final int flakeyThreshold;
    private final int noOfRetries;
    private final int waitTime;
    private final TimeUnit waitTimeUnit;
    private final boolean rethrowOriginal;

    /**
     * Constructs a FlakeyTestIndicatorRule. For improved explicitness this TestRule
     * is meant to be created by the {@link de.plastickarma.flakeyunit.FlakeyTestIndicatorBuilder}.
     * @param noOfRetries number of retries to determine flakeyness
     * @param flakeyThreshold number of successful testcase runs after initial failure, which are needed to
     *                        be considered flakey.
     * @param rethrowOriginal Boolean flag to indicate if the original exception should be rethrown. If set
     *                        to <code>false</code> a flakey test case, while being detected and passed to
     *                        the listeners, will appear to Junit as passed.
     * @param waitTime Waiting time between reruns.
     * @param waitTimeUnit Time unit of waiting time.
     * @param listeners {@link de.plastickarma.flakeyunit.FlakeyTestcaseListener listener}, that will be
     *                  notified, if a testcase is considered flakey.
     */
    FlakeyTestIndicatorRule(
            final int noOfRetries,
            final int flakeyThreshold,
            final boolean rethrowOriginal,
            final int waitTime,
            final TimeUnit waitTimeUnit,
            final List<FlakeyTestcaseListener> listeners) {
        this.noOfRetries = noOfRetries;
        this.flakeyThreshold = flakeyThreshold;
        this.rethrowOriginal = rethrowOriginal;
        this.waitTime = waitTime;
        this.waitTimeUnit = waitTimeUnit;
        this.listeners = listeners;
    }


    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {

                // initial run
                Throwable originalException = null;
                try {
                    base.evaluate();
                } catch (final Throwable t) {
                    originalException = t;
                }

                if (originalException != null) {
                    // retries
                    final List<Throwable> exceptions = rerunFailedTestcase(base);

                    if (isPotentiallyFlakey(exceptions)) {
                        fireFlakeyEvent(description, originalException, noOfRetries, exceptions);
                        if (rethrowOriginal) {
                            throw originalException;
                        }
                    } else {
                        throw originalException;
                    }
                }
            }
        };
    }

    private boolean isPotentiallyFlakey(final List<Throwable> exceptions) {
        // TODO check if exceptions are of same time
        return exceptions.size() < (this.noOfRetries - this.flakeyThreshold);
    }

    /**
     * Executes the given Statement for the specified number of retries.
     * @return Exceptions, that occurred during the retries. You can derive the number of successes and
     * failures of the retries by the return value.
     */
    private List<Throwable> rerunFailedTestcase(final Statement base) {
        List<Throwable> exceptions = new ArrayList<>();
        for (int i = 0; i < this.noOfRetries; i++) {
            try {
                Thread.sleep(TimeUnit.MILLISECONDS.convert(this.waitTime, this.waitTimeUnit));
                base.evaluate();
            } catch(Throwable t) {
                exceptions.add(t);
            }
        }
        return exceptions;
    }

    private void fireFlakeyEvent(
            final Description description,
            final Throwable originalException,
            final int retries,
            final List<Throwable> exceptions) {
        for (FlakeyTestcaseListener listener : this.listeners) {
            listener.handlePotentialFlakeyness(
                    description,
                    originalException,
                    retries,
                    exceptions);
        }
    }
}
