package net.thucydides.model.reports;


import net.serenitybdd.model.collect.NewList;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.reports.json.JSONTestOutcomeReporter;
import net.thucydides.model.reports.junit.JUnitXMLOutcomeReporter;
import net.thucydides.model.util.EnvironmentVariables;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Loads test outcomes from a given directory, and reports on their contents.
 * This class is used for aggregate reporting.
 */
public class TestOutcomeLoader {

    private final EnvironmentVariables environmentVariables;
    private final FormatConfiguration formatConfiguration;

    public TestOutcomeLoader() {
        this(SystemEnvironmentVariables.currentEnvironmentVariables());
    }

    
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
            final List<Callable<List<TestOutcome>>> partitions = new ArrayList<>();
            final AcceptanceTestLoader testOutcomeReporter = getOutcomeReporter();

            allOutcomeFilesFrom(reportDirectory).forEach(
                    sourceFile -> partitions.add(new TestOutcomeLoaderCallable(testOutcomeReporter,sourceFile.toFile()))
            );

            final ExecutorService executorPool = Executors.newFixedThreadPool(NumberOfThreads.forIOOperations());
            final List<Future<List<TestOutcome>>> loadedTestOutcomes = executorPool.invokeAll(partitions);

            List<TestOutcome> testOutcomes = new ArrayList<>();
            for(Future<List<TestOutcome>> loadedTestOutcome : loadedTestOutcomes) {
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

    class TestOutcomeLoaderCallable implements Callable<List<TestOutcome>> {

        private final File sourceFile;
        private final AcceptanceTestLoader testOutcomeReporter;

        TestOutcomeLoaderCallable(AcceptanceTestLoader testOutcomeReporter, File sourceFile) {
            this.testOutcomeReporter = testOutcomeReporter;
            this.sourceFile = sourceFile;
        }

        @Override
        public List<TestOutcome> call() throws Exception {
            java.util.Optional<TestOutcome> loadedTestOutcome = testOutcomeReporter.loadReportFrom(sourceFile);

            return loadedTestOutcome.map(Collections::singleton).orElse(Collections.emptySet())
                    .stream()
                    .map(this::augmented)
                    .collect(Collectors.toList());
        }

        private TestOutcome augmented(final TestOutcome testOutcome) {

            AUGMENTERS.forEach(
                    augmenter -> augmenter.augment(testOutcome)
            );
            return testOutcome;
        }
    }


    private Stream<Path> allOutcomeFilesFrom(final File reportsDirectory) throws IOException {

        SerializedOutcomeFilenameFilter filter = new SerializedOutcomeFilenameFilter();
        return Files.list(reportsDirectory.toPath())
                .filter(file -> isAJsonTestOutcome(file));
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

    private boolean isAJsonTestOutcome(Path path) {
        String filename = path.toFile().getName();
        return (filename.toLowerCase(Locale.getDefault()).endsWith(formatConfiguration.getPreferredFormat().getExtension())
                && (!filename.endsWith(".features.json"))
                && (!filename.endsWith("manifest.json"))
                && (!filename.startsWith(JUnitXMLOutcomeReporter.FILE_PREFIX))) ;
    }
}
