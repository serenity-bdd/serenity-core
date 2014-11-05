package net.thucydides.core.reports;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.json.JSONTestOutcomeReporter;
import net.thucydides.core.reports.xml.XMLTestOutcomeReporter;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

/**
 * Loads test outcomes from a given directory, and reports on their contents.
 * This class is used for aggregate reporting.
 */
public class TestOutcomeLoader {

    private final EnvironmentVariables environmentVariables;
    private final FormatConfiguration formatConfiguration;

    public TestOutcomeLoader() {
        this(Injectors.getInjector().getProvider(EnvironmentVariables.class).get() );
    }

    @Inject
    public TestOutcomeLoader(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.formatConfiguration = new FormatConfiguration(environmentVariables);
    }

    public TestOutcomeLoader(EnvironmentVariables environmentVariables, FormatConfiguration formatConfiguration) {
        this.environmentVariables = environmentVariables;
        this.formatConfiguration = formatConfiguration;
    }

    public TestOutcomeLoader forFormat(OutcomeFormat format) {

        return new TestOutcomeLoader(environmentVariables, new FormatConfiguration(format));
    }
    /**
     * Load the test outcomes from a given directory.
     *
     * @param reportDirectory An existing directory that contains the test outcomes in XML or JSON format.
     * @return The full list of test outcomes.
     * @throws java.io.IOException Thrown if the specified directory was invalid.
     */
    public List<TestOutcome> loadFrom(final File reportDirectory) throws IOException {

        final AcceptanceTestLoader testOutcomeReporter = getOutcomeReporter();
        List<File> reportFiles = getAllOutcomeFilesFrom(reportDirectory);

        final List<TestOutcome> testOutcomes = Collections.synchronizedList(new ArrayList<TestOutcome>());
        for(File reportFile : reportFiles) {
            testOutcomes.addAll(testOutcomeReporter.loadReportFrom(reportFile).asSet());
        }
        Collections.sort(testOutcomes, byTitle());
        return ImmutableList.copyOf(testOutcomes);
    }

    private Comparator<? super TestOutcome> byTitle() {
        return new Comparator<TestOutcome>() {
            @Override
            public int compare(TestOutcome a, TestOutcome b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        };
    }

    private List<File> getAllOutcomeFilesFrom(final File reportsDirectory) throws IOException{
        File[] matchingFiles = reportsDirectory.listFiles(new SerializedOutcomeFilenameFilter());
        if (matchingFiles == null) {
            throw new IOException("Could not find directory " + reportsDirectory);
        }
        return ImmutableList.copyOf(matchingFiles);
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

    public AcceptanceTestLoader getOutcomeReporter() {
        switch (formatConfiguration.getPreferredFormat()) {
            case XML: return new XMLTestOutcomeReporter();
            case JSON: return new JSONTestOutcomeReporter();
            default: throw new IllegalArgumentException("Unsupported report format: " + formatConfiguration.getPreferredFormat());
        }
    }
    private class SerializedOutcomeFilenameFilter implements FilenameFilter {
        public boolean accept(final File file, final String filename) {
            return filename.toLowerCase(Locale.getDefault()).endsWith(formatConfiguration.getPreferredFormat().getExtension());
        }
    }
}
