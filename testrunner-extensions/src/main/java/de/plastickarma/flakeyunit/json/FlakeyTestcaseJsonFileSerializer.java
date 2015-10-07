package de.plastickarma.flakeyunit.json;

import de.plastickarma.flakeyunit.FlakeyTestcaseListener;
import org.junit.runner.Description;

import java.io.*;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

/**
 * {@link de.plastickarma.flakeyunit.FlakeyTestcaseListener}, that stores flakeyness information in JSON files.
 * The file format is <code><test method><test class><timestamp>.json</code>
 */
public final class FlakeyTestcaseJsonFileSerializer implements FlakeyTestcaseListener {


    private final Path directoryPath;

    /**
     * Creates a FlakeyTestcaseJsonFileSerializer.
     * @param directoryPath path on file system, where the json files shall be stored.
     */
    public FlakeyTestcaseJsonFileSerializer(Path directoryPath) {
        this.directoryPath = directoryPath;
    }

    /**
     * Writes the flakeyness information to a JSON file.
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

        final File directory = this.directoryPath.toFile();
        final boolean dirCreated = directory.mkdirs();
        if (!dirCreated || !directory.exists()) {
            throw new RuntimeException(String.format("directory %s cannot be accessed", directory.getAbsolutePath()));
        }

        final String filename = createFileName(description);
        try(Writer w = new OutputStreamWriter(new FileOutputStream(directoryPath.resolve(filename).toFile()))) {
            new FlakeyTestcaseJsonSerializer(w, true).handlePotentialFlakeyness(
                    description,
                    originalException,
                    rerunCount,
                    rerunExceptions);
        } catch (IOException e) {
            throw new RuntimeException((e));
        }


    }

    private String createFileName(final Description description) {
        return String
                    .format("%s_%s_%d.json", description.getMethodName(), description.getClassName(), new Date().getTime())
                    .replaceAll("\\s", "_");
    }
}
