package net.thucydides.core.reports.integration

import com.github.goldin.spock.extensions.tempdir.TempDir
import net.thucydides.core.ThucydidesSystemProperties
import net.thucydides.core.ThucydidesSystemProperty
import net.thucydides.core.issues.IssueTracking
import net.thucydides.core.reports.FormatConfiguration
import net.thucydides.core.reports.html.HtmlAggregateStoryReporter
import net.thucydides.core.util.MockEnvironmentVariables
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.phantomjs.PhantomJSDriver
import spock.lang.Specification

import static net.thucydides.core.util.TestResources.directoryInClasspathCalled

public class WhenGeneratingAggregateHtmlReports extends Specification {

    @TempDir File temporaryDirectory

    def issueTracking = Mock(IssueTracking)
    def mockSystemProperties = Mock(ThucydidesSystemProperties)
    def environmentVariables = new MockEnvironmentVariables()
    def reporter = new HtmlAggregateStoryReporter("project", issueTracking);

    File outputDirectory
    WebDriver driver;

    def setup() {
        outputDirectory = new File(temporaryDirectory,"target/site/thucydides")
        outputDirectory.mkdirs()
        reporter.outputDirectory = outputDirectory;
        environmentVariables.setProperty("output.formats","xml")
        reporter.formatConfiguration = new FormatConfiguration(environmentVariables)

        driver = new HtmlUnitDriver();
    }

    def cleanup() {
        if (driver) {
            driver.quit()
        }
    }
    def "we can navigate sub reports"() {
        given: "We generate reports from a directory containing features and stories only"
            reporter.generateReportsForTestResultsFrom directory("/test-outcomes/containing-features-and-stories")
        when: "we view the report"
            WebDriver driver = new PhantomJSDriver();
            driver.get reportHomePageUrl();
        then: "we can see all available tags and click on 'Grow New Potatoes' link"
            def anotherDifferentFeatureLink = driver.findElement(By.linkText("Grow New Potatoes"))
            anotherDifferentFeatureLink.click()
            def breadcrumbText = driver.findElement(By.cssSelector(".bluetext")).getText()
            breadcrumbText == "Home > Grow potatoes/Grow new potatoes"
        when: "we click on the Features link"
            def featuresLink = driver.findElement(By.linkText("Requirements"))
            featuresLink.click()
        then: "we see the breadcrumb showing Thucydedes Reports > another different feature"
            def subReportBreadcrumbText = driver.findElement(By.cssSelector(".bluetext")).getText()
            subReportBreadcrumbText == "Home > Requirements"
        and: "a single feature"
            def featureLink = driver.findElement(By.linkText("Grow cucumbers"))
            featureLink.enabled
        and:
            driver.quit()

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
            1 * mockSystemProperties.setValue(ThucydidesSystemProperty.THUCYDIDES_ISSUE_TRACKER_URL,"http://my.issue.tracker")

    }

    def "should generate an overall release report"() {
        given: "We generate reports from a directory containing features and stories only"
            reporter.generateReportsForTestResultsFrom directory("/test-outcomes/containing-features-and-stories")
        when: "we view the report"
            WebDriver driver = new PhantomJSDriver();
            driver.get reportHomePageUrl();
        then: "we should see a Releases tab"
            def releasesLink = driver.findElement(By.linkText("Releases"))
            releasesLink.click();
        and:"a list of releases should be displayed"
            def releases = driver.findElements(By.cssSelector(".jqtree-title")).collect { it.text }
            releases.containsAll(["Release 1.0", "Release 2.0"])
            driver.quit()
    }

    def "should generate a detailed release report for each release"() {
        given: "We generate reports from a directory containing features and stories only"
            reporter.generateReportsForTestResultsFrom directory("/test-outcomes/containing-features-and-stories")
        when: "we view the release report"
            WebDriver driver = new PhantomJSDriver();
            driver.get reportHomePageUrl();
            def releasesLink = driver.findElement(By.linkText("Releases"))
            releasesLink.click();
        then: "we should be able to display a release report for each release"
            driver.findElement(By.className("jqtree-title")).click()
        and: "the release report should contain the requirement type as a title"
            driver.findElement(By.className("requirementTitle"))?.getText() == "Scheduled Requirements"
            driver.quit()

    }


    class CustomHtmlAggregateStoryReporter extends HtmlAggregateStoryReporter {

        public CustomHtmlAggregateStoryReporter(final String projectName) {
            super(projectName);
        }

        @Override
        protected ThucydidesSystemProperties getSystemProperties() {
            return mockSystemProperties;
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