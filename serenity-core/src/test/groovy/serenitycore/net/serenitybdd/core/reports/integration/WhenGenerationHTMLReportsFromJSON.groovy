package serenitycore.net.serenitybdd.core.reports.integration

import serenitymodel.net.thucydides.core.issues.IssueTracking
import serenitymodel.net.thucydides.core.issues.SystemPropertiesIssueTracking
import serenitymodel.net.thucydides.core.model.TestOutcome
import serenitymodel.net.thucydides.core.model.TestResult
import serenitymodel.net.thucydides.core.reports.AcceptanceTestLoader
import serenitymodel.net.thucydides.core.reports.AcceptanceTestReporter
import serenitymodel.net.thucydides.core.reports.TestOutcomeStream
import serenitymodel.net.thucydides.core.reports.TestOutcomes
import net.thucydides.core.reports.html.HtmlAcceptanceTestReporter
import serenitymodel.net.thucydides.core.reports.json.JSONTestOutcomeReporter
import serenitymodel.net.thucydides.core.util.MockEnvironmentVariables
import org.assertj.core.util.Files
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Path

import static serenitycore.net.thucydides.core.util.TestResources.directoryInClasspathCalled
/**
 * Created by john on 5/07/2016.
 */
class WhenGenerationHTMLReportsFromJSON extends Specification {

    @Shared AcceptanceTestReporter reporter

    def outputDirectory

    def setup() {
        def environmentVariables = new MockEnvironmentVariables();
        IssueTracking issueTracking = new SystemPropertiesIssueTracking(environmentVariables);
        reporter = new HtmlAcceptanceTestReporter(environmentVariables, issueTracking);
        outputDirectory = Files.newTemporaryFolder()
        reporter.setOutputDirectory(outputDirectory)

    }

    def "should generate html from JSON including screenshots"() {
        given:
            Path directory = directoryInClasspathCalled("/serenity-js-outcomes").toPath();
        when:
            TestOutcomeStream stream = TestOutcomeStream.testOutcomesInDirectory(directory)
            def outcomes = []
            def outcomeReports = []
            for(TestOutcome outcome : stream.iterator()) {
                outcomes.add(outcome)
                outcomeReports.add(reporter.generateReportFor(outcome).text)
            }
        then:
            outcomeReports.findAll { !it.contains(">Screenshots<") }.isEmpty()
    }

    def "failures in example tables show be reflected individually in the overall report"() {
        given:
            Path report = directoryInClasspathCalled("/json-test-outcomes/test-outcome-with-failing-example.json").toPath();
        when:
            AcceptanceTestLoader loader = new JSONTestOutcomeReporter()
            TestOutcome outcome = loader.loadReportFrom(report).get()
            TestOutcomes outcomes = TestOutcomes.of([outcome])
        then:
            outcomes.totalTests.withResult(TestResult.SUCCESS) == 1
        and:
            outcomes.totalTests.withFailureOrError() == 1
    }

}