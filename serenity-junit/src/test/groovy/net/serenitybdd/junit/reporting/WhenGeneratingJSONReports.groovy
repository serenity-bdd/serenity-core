package net.serenitybdd.junit.reporting

import net.serenitybdd.core.eventbus.Subscribers
import net.serenitybdd.junit.StubbedSerenityRunner
import net.thucydides.core.util.MockEnvironmentVariables
import net.thucydides.core.webdriver.SystemPropertiesConfiguration
import net.thucydides.core.webdriver.WebDriverFactory
import net.thucydides.core.webdriver.WebdriverInstanceFactory
import net.thucydides.samples.SamplePassingScenario
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.junit.runner.JUnitCore
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import spock.lang.Specification

class WhenGeneratingJSONReports extends Specification {

    def firefoxDriver = Mock(FirefoxDriver)
    def htmlUnitDriver = Mock(HtmlUnitDriver)
    def webdriverInstanceFactory = Mock(WebdriverInstanceFactory)
    def environmentVariables = new MockEnvironmentVariables()
    def systemConfiguration = new SystemPropertiesConfiguration(environmentVariables)
    def webDriverFactory = new WebDriverFactory(webdriverInstanceFactory, environmentVariables)
    File temporaryDirectory

    @Rule
    TemporaryFolder temporaryFolder

    def setup() {
        webdriverInstanceFactory.newFirefoxDriver(_) >> firefoxDriver
        webdriverInstanceFactory.newHtmlUnitDriver(_) >> htmlUnitDriver

        temporaryDirectory = temporaryFolder.newFolder()
        systemConfiguration.outputDirectory = temporaryDirectory
        Subscribers.systemConfiguration = systemConfiguration

    }



    def "should generate JSON reports for each scenario"() {
        given:
            def runner = StubbedSerenityRunner.toRun(SamplePassingScenario)
                                              .withWebDriverFactory(webDriverFactory)
                                              .usingTemporaryDirectory(temporaryDirectory)

        when:
            new JUnitCore().run(runner)
        then:
            runner.testOutcomes.size() == 3
        and:
            def jsonTestOutcomes = temporaryDirectory.list().findAll { it.startsWith("outcome_")}
            jsonTestOutcomes.size() == 3
    }

    def "should generate JUnit reports for the overall test result"() {
        given:
            def runner = StubbedSerenityRunner.toRun(SamplePassingScenario)
                    .withWebDriverFactory(webDriverFactory)
                    .usingTemporaryDirectory(temporaryDirectory)

        when:
            new JUnitCore().run(runner)
        then:
            def junitReports = temporaryDirectory.list().findAll { it.startsWith("serenity_junit_")}
            junitReports.size() == 1
    }
}
