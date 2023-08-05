package net.thucydides.core.reports.integration

import net.thucydides.model.issues.IssueTracking
import net.thucydides.model.reports.FormatConfiguration
import net.thucydides.core.reports.html.HtmlAggregateStoryReporter
import net.thucydides.model.environment.MockEnvironmentVariables
import org.openqa.selenium.WebDriver
import spock.lang.Specification

import java.nio.file.Files

import static net.thucydides.model.util.TestResources.directoryInClasspathCalled

class WhenGeneratingAggregateHtmlReportsForLargeVolumes extends Specification {

    File temporaryDirectory

    def issueTracking = Mock(IssueTracking)
    def environmentVariables = new MockEnvironmentVariables()
    def reporter = new HtmlAggregateStoryReporter("project", issueTracking)

    File outputDirectory

    WebDriver driver

    def setup() {
        temporaryDirectory = Files.createTempDirectory("tmp").toFile()
        temporaryDirectory.deleteOnExit()
        outputDirectory = new File(temporaryDirectory,"target/site/serenity")
        outputDirectory.mkdirs()
        reporter.outputDirectory = outputDirectory
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
