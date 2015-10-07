package de.plastickarma.flakeyunit;


import org.junit.runner.Description;

import java.util.List;

/**
 * Interface for handling potential flakey test cases.
 */
public interface FlakeyTestcaseListener {

    /**
     * Handles a potentially flakey test case.
     * @param description The description of the testcase as provided by junit.
     * @param originalException The exception that was the initial test case failure.
     * @param rerunCount The number of times, which the failed test case was repeated.
     * @param rerunExceptions The exceptions, that occurred, during the rerun of the test case.
     */
    public void handlePotentialFlakeyness(
            Description description,
            Throwable originalException,
            int rerunCount,
            List<Throwable> rerunExceptions);
}
