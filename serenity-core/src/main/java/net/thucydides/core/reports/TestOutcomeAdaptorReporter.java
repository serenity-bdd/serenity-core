package net.thucydides.core.reports;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.adaptors.TestOutcomeAdaptor;
import net.thucydides.core.reports.html.HtmlAcceptanceTestReporter;
import net.thucydides.core.reports.json.JSONTestOutcomeReporter;
import net.thucydides.core.reports.xml.XMLTestOutcomeReporter;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TestOutcomeAdaptorReporter extends ThucydidesReporter {

    private List<TestOutcomeAdaptor> adaptors = Lists.newArrayList();

    private final Optional<File> NO_SOURCE_FILE = Optional.absent();

    public void generateReports() throws IOException {
        generateReports(NO_SOURCE_FILE);
    }

    private final FormatConfiguration formatConfiguration;


    public TestOutcomeAdaptorReporter() {
        formatConfiguration = new FormatConfiguration(Injectors.getInjector().getProvider(EnvironmentVariables.class).get() );
    }

    /**
     * @param sourceDirectory
     * @throws IOException
     */
    public void generateReportsFrom(File sourceDirectory) throws IOException {
        generateReports(Optional.fromNullable(sourceDirectory));
    }

    public void generateReports(Optional<File> sourceDirectory) throws IOException {
        setupOutputDirectoryIfRequired();
        for (TestOutcomeAdaptor adaptor : adaptors) {
            List<TestOutcome> outcomes = sourceDirectory.isPresent() ?
                    adaptor.loadOutcomesFrom(sourceDirectory.get()) : adaptor.loadOutcomes();
            generateReportsFor(outcomes);
        }
    }

    private void setupOutputDirectoryIfRequired() {
        if (getOutputDirectory() != null) {
            getOutputDirectory().mkdirs();
        }
    }

    private void generateReportsFor(List<TestOutcome> outcomes) throws IOException {
        TestOutcomes allOutcomes = TestOutcomes.of(outcomes);
        for (TestOutcome outcome : allOutcomes.getOutcomes()) {
            if (shouldGenerate(OutcomeFormat.XML)) {
                getXMLReporter().generateReportFor(outcome, allOutcomes);
            }
            if (shouldGenerate(OutcomeFormat.JSON)) {
                getJsonReporter().generateReportFor(outcome, allOutcomes);
            }
            getHTMLReporter().generateReportFor(outcome, allOutcomes);
        }
    }

    private boolean shouldGenerate(OutcomeFormat format) {
        return formatConfiguration.getFormats().contains(format);
    }

    private AcceptanceTestReporter getXMLReporter() {
        XMLTestOutcomeReporter reporter = new XMLTestOutcomeReporter();
        reporter.setOutputDirectory(getOutputDirectory());
        return reporter;
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
