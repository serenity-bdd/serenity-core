package net.thucydides.core.reports;

import net.thucydides.core.reports.html.HtmlAcceptanceTestReporter;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.reports.*;
import net.thucydides.model.reports.adaptors.TestOutcomeAdaptor;
import net.thucydides.model.reports.json.JSONTestOutcomeReporter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TestOutcomeAdaptorReporter extends ThucydidesReporter {

    private final List<TestOutcomeAdaptor> adaptors = new ArrayList<>();

    private final Optional<File> NO_SOURCE_FILE = Optional.empty();

    public void generateReports() throws Exception {
        generateReports(NO_SOURCE_FILE);
    }

    private final FormatConfiguration formatConfiguration;


    public TestOutcomeAdaptorReporter() {
        formatConfiguration = new FormatConfiguration(SystemEnvironmentVariables.currentEnvironmentVariables() );
    }

    /**
     * @param sourceDirectory
     * @throws IOException
     */
    public void generateReportsFrom(File sourceDirectory) throws Exception {
        generateReports(Optional.ofNullable(sourceDirectory));
    }

    public void generateReports(Optional<File> sourceDirectory) throws Exception {
        setupOutputDirectoryIfRequired();
        for (TestOutcomeAdaptor adaptor : adaptors) {
            List<TestOutcome> outcomes = sourceDirectory.isPresent() ? adaptor.loadOutcomesFrom(sourceDirectory.get()) : adaptor.loadOutcomes();
            generateReportsFor(outcomes);
        }
    }

    private void setupOutputDirectoryIfRequired() {
        if (getOutputDirectory() != null) {
            getOutputDirectory().mkdirs();
        }
    }

    private void generateReportsFor(List<TestOutcome> outcomes) throws Exception {
        TestOutcomes allOutcomes = TestOutcomes.of(outcomes);
        for (TestOutcome outcome : allOutcomes.getOutcomes()) {
            getJsonReporter().generateReportFor(outcome);
            if (shouldGenerate(OutcomeFormat.HTML)) {
                getHTMLReporter().generateReportFor(outcome);
            }
        }
    }

    private boolean shouldGenerate(OutcomeFormat format) {
        return formatConfiguration.getFormats().contains(format);
    }

    private AcceptanceTestReporter getJsonReporter() {
        JSONTestOutcomeReporter reporter = new JSONTestOutcomeReporter();
        reporter.setOutputDirectory(getOutputDirectory());
        return reporter;
    }

    private AcceptanceTestReporter getHTMLReporter() {
        HtmlAcceptanceTestReporter reporter = new HtmlAcceptanceTestReporter();
        reporter.setOutputDirectory(getOutputDirectory());
        return reporter;
    }

    public void registerAdaptor(TestOutcomeAdaptor adaptor) {
        adaptors.add(adaptor);
    }
}
