package de.plastickarma.flakeyunit.json;

import com.google.gson.GsonBuilder;
import de.plastickarma.flakeyunit.FlakeyTestcaseListener;
import org.junit.runner.Description;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link de.plastickarma.flakeyunit.FlakeyTestcaseListener}, that serializes information
 * about potential flakeyness to JSON using Gson.
 */
public final class FlakeyTestcaseJsonSerializer implements FlakeyTestcaseListener {

    private final Appendable output;
    private final boolean prettyPrint;

    /**
     * Creates a FlakeyTestcaseJsonSerializer.
     * @param output object to which the JSON is written.
     * @param prettyPrint boolean flag to indicate, if the JSON shall be pretty printed.
     */
    public FlakeyTestcaseJsonSerializer(final Appendable output, boolean prettyPrint) {
        this.output = output;
        this.prettyPrint = prettyPrint;
    }

    /**
     * Creates a FlakeyTestcaseJsonSerializer without JSON pretty print.
     * @param output object to which the JSON is written.
     */
    public FlakeyTestcaseJsonSerializer(final Appendable output) {
        this(output, false);
    }

    /**
     * Writes the flakeyness information to the output object of this object.
     * @param description The description of the testcase as provided by junit.
     * @param originalException The exception that was the initial test case failure.
     * @param rerunCount The number of times, which the failed test case was repeated.
     * @param rerunExceptions The exceptions, that occurred, during the rerun of the test case.
     */
    @Override
    public void handlePotentialFlakeyness(
            final Description description,
            final Throwable originalException,
            final int rerunCount,
            final List<Throwable> rerunExceptions) {

        final GsonBuilder gsonBuilder = new GsonBuilder();
        if (this.prettyPrint) {
            gsonBuilder.setPrettyPrinting();
        }

        try {
            gsonBuilder.create().toJson(
                new GsonBinding(
                    description,
                    originalException,
                    rerunCount,
                    rerunExceptions), this.output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * DTO helper class for flakeyness information, that will be serialized to JSON.
     */
    private static class GsonBinding {
        @SuppressWarnings("unused")
        private final String methodName;

        @SuppressWarnings("unused")
        private final String className;

        @SuppressWarnings("unused")
        private final ExceptionInfo originalException;

        @SuppressWarnings("unused")
        private final int rerunCount;

        private final List<ExceptionInfo> rerunExceptions;

        private GsonBinding(
                final Description description,
                final Throwable originalException,
                final int rerunCount,
                final List<Throwable> rerunExceptions) throws IOException {
            this.methodName = description.getMethodName();
            this.className = description.getTestClass().getName();
            this.originalException = new ExceptionInfo(originalException);

            this.rerunCount = rerunCount;
            this.rerunExceptions = new ArrayList<>(rerunExceptions.size());
            for (Throwable t : rerunExceptions) {
                this.rerunExceptions.add(new ExceptionInfo(t));
            }
        }
    }

    /**
     * DTO helper class for exception information, that will be serialized JSON.
     */
    private static class ExceptionInfo {

        @SuppressWarnings("unused")
        private final String exceptionClass;

        @SuppressWarnings("unused")
        private final String stackTrace;

        public ExceptionInfo(final Throwable t) throws IOException {

            this.exceptionClass = t.getClass().getName();

            try (StringWriter writer = new StringWriter()) {
                try (PrintWriter pw = new PrintWriter(writer)) {
                    t.printStackTrace(pw);
                }
                writer.flush();
                this.stackTrace = writer.toString();
            }
        }
    }
}
