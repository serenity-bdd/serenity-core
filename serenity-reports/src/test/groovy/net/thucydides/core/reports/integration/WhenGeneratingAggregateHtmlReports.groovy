package net.thucydides.core.reports.integration

import net.serenitybdd.model.SerenitySystemProperties
import net.thucydides.model.ThucydidesSystemProperty
import net.thucydides.model.issues.IssueTracking
import net.thucydides.core.reports.html.HtmlAggregateStoryReporter
import net.thucydides.model.environment.MockEnvironmentVariables
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import spock.lang.Ignore
import spock.lang.Specification

import java.nio.file.Files

import static net.thucydides.model.util.TestResources.directoryInClasspathCalled

class WhenGeneratingAggregateHtmlReports extends Specification {

    File temporaryDirectory

    def issueTracking = Mock(IssueTracking)
    def mockSystemProperties = Mock(SerenitySystemProperties)
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
        environmentVariables.setProperty("output.formats","xml")
        //reporter.formatConfiguration = new FormatConfiguration(environmentVariables)

        reporter.generateReportsForTestResultsFrom directory("/test-outcomes/containing-features-and-stories")
    }

    def cleanup() {
        if (driver) {
            driver.close()
            driver.quit()
        }
    }

    def "should pass JIRA URL to reporter"() {
        given:
            def customReport = new CustomHtmlAggregateStoryReporter("project")
        when:
            customReport.jiraUrl = "http://my.jira.url"
        then:
            1 * mockSystemProperties.setValue(ThucydidesSystemProperty.JIRA_URL,"http://my.jira.url")
    }


    def "should pass JIRA project to reporter"() {
        given:
            def customReport = new CustomHtmlAggregateStoryReporter("project")
        when:
            customReport.jiraProject = "MYPROJECT"
        then:
            1 * mockSystemProperties.setValue(ThucydidesSystemProperty.JIRA_PROJECT,"MYPROJECT")
    }


    def "should pass issue tracker to reporter"() {
        given:
            def customReport = new CustomHtmlAggregateStoryReporter("project")
        when:
            customReport.issueTrackerUrl = "http://my.issue.tracker"
        then:
            1 * mockSystemProperties.setValue(ThucydidesSystemProperty.SERENITY_ISSUE_TRACKER_URL,"http://my.issue.tracker")

    }

    @Ignore
    def "should generate an overall release report"() {
//        given: "We generate reports from a directory containing features and stories only"
//            reporter.generateReportsForTestResultsFrom directory("/test-outcomes/containing-features-and-stories")
        when: "we view the report"
            ChromeOptions chromeOptions = new ChromeOptions()
            chromeOptions.addArguments("--headless")
            driver = new ChromeDriver(chromeOptions)
            driver.get reportHomePageUrl()
        then: "we should see a Releases tab"
            def releasesLink = driver.findElement(By.linkText("Releases"))
            releasesLink.click()
        and:"a list of releases should be displayed"
            def releases = driver.findElements(By.cssSelector(".jqtree-title")).collect { it.text }
            releases.containsAll(["Release 1.0", "Release 2.0"])
    }

    @Ignore
    def "should generate a detailed release report for each release"() {
//        given: "We generate reports from a directory containing features and stories only"
//            reporter.generateReportsForTestResultsFrom directory("/test-outcomes/containing-features-and-stories")
        when: "we view the release report"
            ChromeOptions chromeOptions = new ChromeOptions()
            chromeOptions.addArguments("--headless")
            driver = new ChromeDriver(chromeOptions)
            driver.get reportHomePageUrl()
            def releasesLink = driver.findElement(By.linkText("Releases"))
            releasesLink.click()
        then: "we should be able to display a release report for each release"
            driver.findElement(By.className("jqtree-title")).click()
        and: "the release report should contain the requirement type as a title"
            driver.findElement(By.className("requirementTitle"))?.getText() == "Scheduled Requirements"
    }

    class CustomHtmlAggregateStoryReporter extends HtmlAggregateStoryReporter {

        CustomHtmlAggregateStoryReporter(final String projectName) {
            super(projectName)
        }

        @Override
        protected SerenitySystemProperties getSystemProperties() {
            return mockSystemProperties
        }
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
