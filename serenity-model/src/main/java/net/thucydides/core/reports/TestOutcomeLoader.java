package net.thucydides.core.reports;

import com.google.inject.Inject;
import net.serenitybdd.core.collect.NewList;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.json.JSONTestOutcomeReporter;
import net.thucydides.core.reports.junit.JUnitXMLOutcomeReporter;
import net.thucydides.core.reports.xml.XMLTestOutcomeReporter;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Loads test outcomes from a given directory, and reports on their contents.
 * This class is used for aggregate reporting.
 */
public class TestOutcomeLoader {

    private final EnvironmentVariables environmentVariables;
    private final FormatConfiguration formatConfiguration;

    public TestOutcomeLoader() {
        this(Injectors.getInjector().getProvider(EnvironmentVariables.class).get());
    }

    @Inject
    public TestOutcomeLoader(EnvironmentVariables environmentVariables) {
        this(environmentVariables, new FormatConfiguration(environmentVariables));
    }

    private TestOutcomeLoader(EnvironmentVariables environmentVariables, FormatConfiguration formatConfiguration) {
        this.environmentVariables = environmentVariables;
        this.formatConfiguration = formatConfiguration;
    }

    public TestOutcomeLoader forFormat(OutcomeFormat format) {
        return new TestOutcomeLoader(environmentVariables, new FormatConfiguration(format));
    }

    /**
     * Load the test outcomes from a given directory, sorted by Title
     *
     * @param reportDirectory An existing directory that contains the test outcomes in XML or JSON format.
     * @return The full list of test outcomes.
     * @throws ReportLoadingFailedError Thrown if the specified directory was invalid or loading finished with error.
     */
    public List<TestOutcome> loadFrom(final File reportDirectory) throws ReportLoadingFailedError {

        try {
            final List<Callable<Set<TestOutcome>>> partitions = new ArrayList<>();
            final AcceptanceTestLoader testOutcomeReporter = getOutcomeReporter();

            for(File sourceFile : getAllOutcomeFilesFrom(reportDirectory)) {
                partitions.add(new TestOutcomeLoaderCallable(testOutcomeReporter,sourceFile));
            }

            final ExecutorService executorPool = Executors.newFixedThreadPool(20);//NumberOfThreads.forIOOperations());
            final List<Future<Set<TestOutcome>>> loadedTestOutcomes = executorPool.invokeAll(partitions);

            List<TestOutcome> testOutcomes = new ArrayList<>();
            for(Future<Set<TestOutcome>> loadedTestOutcome : loadedTestOutcomes) {
                testOutcomes.addAll(loadedTestOutcome.get());
            }
            executorPool.shutdown();
            if (hasAnnotatedOrder(testOutcomes)) {
                return inAnnotatedOrder(testOutcomes);
            } else {
                return inOrderOfTestExecution(testOutcomes);
            }
        } catch (Exception e) {
            throw new ReportLoadingFailedError("Can not load reports for some reason", e);
        }

    }

    private final static List<? extends OutcomeAugmenter> AUGMENTERS = NewList.of(
            new FlagsAugmenter()
    );

    class TestOutcomeLoaderCallable implements Callable<Set<TestOutcome>> {

        private final File sourceFile;
        private final AcceptanceTestLoader testOutcomeReporter;

        TestOutcomeLoaderCallable(AcceptanceTestLoader testOutcomeReporter, File sourceFile) {
            this.testOutcomeReporter = testOutcomeReporter;
            this.sourceFile = sourceFile;
        }

        @Override
        public Set<TestOutcome> call() throws Exception {
            java.util.Optional<TestOutcome> loadedTestOutcome = testOutcomeReporter.loadReportFrom(sourceFile);

            return loadedTestOutcome.map(Collections::singleton).orElse(Collections.emptySet())
                    .stream()
                    .map(this::augmented)
                    .collect(Collectors.toSet());
        }

        private TestOutcome augmented(final TestOutcome testOutcome) {

            AUGMENTERS.forEach(
                    augmenter -> augmenter.augment(testOutcome)
            );
            return testOutcome;
        }
    }

    private List<File> getAllOutcomeFilesFrom(final File reportsDirectory) throws IOException{
        File[] matchingFiles = reportsDirectory.listFiles(new SerializedOutcomeFilenameFilter());
        if (matchingFiles == null) {
            throw new IOException("Could not find directory " + reportsDirectory);
        }
        return NewList.copyOf(matchingFiles);
    }

    public static TestOutcomeLoaderBuilder loadTestOutcomes() {
        return new TestOutcomeLoaderBuilder();
    }

    public static final class TestOutcomeLoaderBuilder {
        OutcomeFormat format;

        public TestOutcomeLoaderBuilder inFormat(OutcomeFormat format) {
            this.format = format;
            return this;
        }

        public TestOutcomes from(final File reportsDirectory) throws IOException {
            TestOutcomeLoader loader = new TestOutcomeLoader().forFormat(format);
            return TestOutcomes.of(loader.loadFrom(reportsDirectory));
        }

    }

    public static TestOutcomes testOutcomesIn(final File reportsDirectory) throws IOException {
        TestOutcomeLoader loader = new TestOutcomeLoader();
        return TestOutcomes.of(loader.loadFrom(reportsDirectory));
    }

    private static List<TestOutcome> inOrderOfTestExecution(List<TestOutcome> testOutcomes) {
            return testOutcomes.stream()
                    .sorted(Comparator.comparing(TestOutcome::getStartTime,
                            Comparator.nullsFirst(Comparator.naturalOrder())))
                    .collect(Collectors.toList());
    }

    private static boolean hasAnnotatedOrder(List<TestOutcome> testOutcomes) {
        return testOutcomes.stream().anyMatch(outcome -> outcome.getOrder() > 0);
    }

    private static List<TestOutcome> inAnnotatedOrder(List<TestOutcome> testOutcomes) {
        return testOutcomes.stream()
                .sorted(Comparator.comparing(TestOutcome::getOrder,
                        Comparator.nullsFirst(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    private AcceptanceTestLoader getOutcomeReporter() {
        switch (formatConfiguration.getPreferredFormat()) {
            case XML:
                return new XMLTestOutcomeReporter();
            case JSON:
                return new JSONTestOutcomeReporter();
            default:
                throw new IllegalArgumentException("Unsupported report format: " + formatConfiguration.getPreferredFormat());
        }
    }

    private class SerializedOutcomeFilenameFilter implements FilenameFilter {
        public boolean accept(final File file, final String filename) {
            return (filename.toLowerCase(Locale.getDefault()).endsWith(formatConfiguration.getPreferredFormat().getExtension())
                    && (!filename.endsWith(".features.json"))
                    && (!filename.endsWith("manifest.json"))
                    && (!filename.startsWith(JUnitXMLOutcomeReporter.FILE_PREFIX))) ;
        }
    }
}
