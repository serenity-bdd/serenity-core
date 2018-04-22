package net.thucydides.core.reports.integration

import net.thucydides.core.issues.IssueTracking
import net.thucydides.core.reports.FormatConfiguration
import net.thucydides.core.reports.html.HtmlAggregateStoryReporter
import net.thucydides.core.util.MockEnvironmentVariables
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.openqa.selenium.WebDriver
import spock.lang.Specification

import static net.thucydides.core.util.TestResources.directoryInClasspathCalled

public class WhenGeneratingAggregateHtmlReportsForLargeVolumes extends Specification {

    File temporaryDirectory

    @Rule
    TemporaryFolder temporaryFolder

    def issueTracking = Mock(IssueTracking)
    def environmentVariables = new MockEnvironmentVariables()
    def reporter = new HtmlAggregateStoryReporter("project", issueTracking);

    File outputDirectory

    WebDriver driver

    def setup() {
        temporaryDirectory = temporaryFolder.newFolder()
        outputDirectory = new File(temporaryDirectory,"target/site/serenity")
        outputDirectory.mkdirs()
        reporter.outputDirectory = outputDirectory;
        reporter.formatConfiguration = new FormatConfiguration(environmentVariables)

    }

    def cleanup() {
        if (driver) {
            driver.close()
            driver.quit()
        }
    }

//    @Ignore
    def "should cope with large volumes"() {
        when:
            reporter.sourceDirectory = new File("target/site/resources",temporaryDirectory)
            reporter.generateReportsForTestResultsFrom directory("/sample-big-report")
        then:
            true
    }


    def reportHomePageUrl() {
        "file:///${reportHomePage.absolutePath}"
    }

    def getReportHomePage() {
        new File(outputDirectory,"index.html")
    }

    def directory(String path) {
        directoryInClasspathCalled(path)
    }
}
