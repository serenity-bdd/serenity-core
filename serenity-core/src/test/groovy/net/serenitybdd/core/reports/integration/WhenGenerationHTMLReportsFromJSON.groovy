package net.serenitybdd.core.reports.integration

import net.thucydides.core.reports.html.HtmlAcceptanceTestReporter
import net.thucydides.model.domain.TestOutcome
import net.thucydides.model.domain.TestResult
import net.thucydides.model.environment.MockEnvironmentVariables
import net.thucydides.model.issues.IssueTracking
import net.thucydides.model.issues.SystemPropertiesIssueTracking
import net.thucydides.model.reports.AcceptanceTestLoader
import net.thucydides.model.reports.AcceptanceTestReporter
import net.thucydides.model.reports.TestOutcomeStream
import net.thucydides.model.reports.TestOutcomes
import net.thucydides.model.reports.json.JSONTestOutcomeReporter
import org.assertj.core.util.Files
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Path

import static net.thucydides.model.util.TestResources.directoryInClasspathCalled

/**
 * Created by john on 5/07/2016.
 */
class WhenGenerationHTMLReportsFromJSON extends Specification {

    @Shared AcceptanceTestReporter reporter

    def outputDirectory

    def setup() {
        def environmentVariables = new MockEnvironmentVariables()
        IssueTracking issueTracking = new SystemPropertiesIssueTracking(environmentVariables)
        reporter = new HtmlAcceptanceTestReporter(environmentVariables, issueTracking)
        outputDirectory = Files.newTemporaryFolder()
        reporter.setOutputDirectory(outputDirectory)

    }

    def "should generate html from JSON including screenshots"() {
        given:
            Path directory = directoryInClasspathCalled("/serenity-js-outcomes").toPath()
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
            Path report = directoryInClasspathCalled("/json-test-outcomes/test-outcome-with-failing-example.json").toPath()
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
