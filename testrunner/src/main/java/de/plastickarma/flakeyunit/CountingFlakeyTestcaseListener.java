package de.plastickarma.flakeyunit;

import org.junit.runner.Description;

import java.util.ArrayList;
import java.util.List;


/**
 * Helper class to count flakey testcases.
 */
public final class CountingFlakeyTestcaseListener implements FlakeyTestcaseListener {

    private final List<String> flakeyTests = new ArrayList<>();

    @Override
    public void handlePotentialFlakeyness(
            final Description description,
            final Throwable originalException,
            final int rerunCount,
            final List<Throwable> rerunExceptions) {
        flakeyTests.add(description.getMethodName());
    }

    /**
     * Returns a list of test methods, that were flakey.
     */
    public List<String> getFlakeyTests() {
        return flakeyTests;
    }
}
