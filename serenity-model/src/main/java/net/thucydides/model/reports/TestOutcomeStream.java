package net.thucydides.model.reports;

import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.reports.json.JSONTestOutcomeReporter;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

import static net.thucydides.model.reports.TestOutcomeStream.NextItemIs.*;

public class TestOutcomeStream implements Iterable<TestOutcome>, Closeable {

    private final DirectoryStream<Path> directoryStream;
    private final Iterator<Path> directoryStreamIterator;
    private final AcceptanceTestLoader loader;

    private TestOutcomeStream(Path sourceDirectory) throws IOException {
        this.directoryStream = Files.newDirectoryStream(sourceDirectory);
        this.directoryStreamIterator = Files.newDirectoryStream(sourceDirectory).iterator();
        this.loader = new JSONTestOutcomeReporter();
    }

    public static TestOutcomeStream testOutcomesInDirectory(Path sourceDirectory) throws IOException {
        return new TestOutcomeStream(sourceDirectory);
    }


    enum NextItemIs {
        Unknown, ReadyToRetrieve, Retrieved
    }

    @Override
    public Iterator<TestOutcome> iterator() {
        return new Iterator<TestOutcome>() {

            @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
            Optional<TestOutcome> nextOutcome;

            NextItemIs nextItemIs = Unknown;

            @Override
            public boolean hasNext() {
                fetchNext();
                return nextOutcome.isPresent();
            }

            private void fetchNext() {
                nextOutcome = findNextValidTestOutcomeIn(directoryStreamIterator);
                nextItemIs = ReadyToRetrieve;
            }

            private java.util.Optional<TestOutcome> findNextValidTestOutcomeIn(Iterator<Path> directoryStream) {
                while (directoryStream.hasNext()) {
                    java.util.Optional<TestOutcome> nextOutcomeFromStream = loader.loadReportFrom(directoryStream.next());
                    if (nextOutcomeFromStream.isPresent()) {
                        return nextOutcomeFromStream;
                    }
                }
                return java.util.Optional.empty();
            }

            @Override
            public TestOutcome next() {
                if (nextItemIs == Unknown) {
                    fetchNext();
                }
                nextItemIs = Retrieved;
                return nextOutcome.orElseThrow(NoSuchElementException::new);
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
