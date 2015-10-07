package de.plastickarma.flakeyunit;

import org.junit.runner.Description;

import java.io.PrintStream;
import java.util.List;

/**
 * Example listener, that prints a message to the given PrintStream, if a flakey test was discovered.
 */
public final class PrintStreamListener implements FlakeyTestcaseListener {
    private final PrintStream output;

    /**
     * Creates a PrintStreamListener with the given PrintStream.
     */
    public PrintStreamListener(final PrintStream output) {
        this.output = output;
    }

    /**
     * Prints a message to this listener's PrintStream, that a flakey test was discovered.
     */
    @Override
    public void handlePotentialFlakeyness(
            final Description description,
            final Throwable originalException,
            final int rerunCount,
            final List<Throwable> rerunExceptions) {

        this.output.printf(
                "%s failed %d times after %d reruns. Testcase is potentially flakey.\n",
                description.getDisplayName(), rerunExceptions.size(), rerunCount);

    }
}
