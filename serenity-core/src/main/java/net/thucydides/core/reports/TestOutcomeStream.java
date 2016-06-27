package net.thucydides.core.reports;

import com.google.common.base.Optional;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.json.JSONTestOutcomeReporter;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

public class TestOutcomeStream implements Iterable<TestOutcome>, Closeable {

    private final DirectoryStream<Path> directoryStream;
    private final Iterator<Path> directoryStreamIterator;
    private final AcceptanceTestLoader loader;

    protected TestOutcomeStream(Path sourceDirectory) throws IOException {
        this.directoryStream = Files.newDirectoryStream(sourceDirectory);
        this.directoryStreamIterator = Files.newDirectoryStream(sourceDirectory).iterator();
        this.loader = new JSONTestOutcomeReporter();
    }

    public static TestOutcomeStream testOutcomesInDirectory(Path sourceDirectory) throws IOException {
        return new TestOutcomeStream(sourceDirectory);
    }

    @Override
    public Iterator<TestOutcome> iterator() {
        return new Iterator() {

            Optional<TestOutcome> nextOutcome;

            @Override
            public boolean hasNext() {
                nextOutcome = findNextValidTestOutcomeIn(directoryStreamIterator);
                return nextOutcome.isPresent();
            }

            private Optional<TestOutcome> findNextValidTestOutcomeIn(Iterator<Path> directoryStream) {
                while (directoryStream.hasNext()) {
                    Optional<TestOutcome> nextOutcomeFromStream = loader.loadReportFrom(directoryStream.next());
                    if (nextOutcomeFromStream.isPresent()) {
                        return nextOutcomeFromStream;
                    }
                }
                return Optional.absent();
            }

            @Override
            public Object next() {
                return nextOutcome.get();
            }

            @Override
            public void remove() {}
        };
    }

    @Override
    public void close() throws IOException {
        directoryStream.close();
    }
}