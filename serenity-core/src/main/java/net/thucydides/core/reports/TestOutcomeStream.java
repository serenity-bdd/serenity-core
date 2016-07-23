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
import java.util.NoSuchElementException;

import static net.thucydides.core.reports.TestOutcomeStream.NextItemIs.*;

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


    enum NextItemIs {
        Unknown, ReadyToRetrieve, Retrieved
    }

    @Override
    public Iterator<TestOutcome> iterator() {
        return new Iterator() {

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
                if (nextItemIs == Unknown) {
                    fetchNext();
                }

                if (!nextOutcome.isPresent()) {
                    throw new NoSuchElementException();
                }
                nextItemIs = Retrieved;
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