package net.thucydides.core.screenshots.integration

import net.serenitybdd.core.Serenity
import net.serenitybdd.core.webdriver.servicepools.ChromeServicePool
import net.serenitybdd.core.webdriver.servicepools.DriverServicePool
import net.thucydides.core.steps.BaseStepListener
import net.thucydides.core.steps.StepEventBus
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport
import net.thucydides.model.domain.TestStep
import net.thucydides.model.environment.MockEnvironmentVariables
import net.thucydides.model.screenshots.ScreenshotAndHtmlSource
import net.thucydides.model.steps.ExecutedStepDescription
import net.thucydides.model.util.EnvironmentVariables
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.DesiredCapabilities
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files

import static net.thucydides.core.webdriver.StaticTestSite.fileInClasspathCalled

class WhenTakingScreenshots extends Specification {

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables()

    @Shared DriverServicePool driverService
    WebDriver driver

    def setupSpec() {
        driverService = new ChromeServicePool()
        driverService.start()
        StepEventBus.eventBus.clear()

    }

    def cleanupSpec() {
        driverService.shutdown()
    }

    def cleanup() {
        if (driver) {
            driver.quit()
        }
    }

    String staticSite
    File temporaryDirectory

    def setup() {
        temporaryDirectory = Files.createTempDirectory("tmp").toFile()
        temporaryDirectory.deleteOnExit()

        StepEventBus.eventBus.clear()

        def desiredCapabilities = DesiredCapabilities.chrome()
        def chromeOptions = new ChromeOptions()
        chromeOptions.addArguments("--headless")
        desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions)

        driver = driverService.newDriver(desiredCapabilities)

        ThucydidesWebDriverSupport.useDriver(driver)

        staticSite = "file://" + fileInClasspathCalled("static-site/static-index.html").getAbsolutePath()

    }

    def "should take an extra screenshot at any time if requested"() {
        given:
        def baseStepListener = Mock(BaseStepListener)
        StepEventBus.eventBus.registerListener(baseStepListener)
        when: "we ask for a screenshot at an arbitrary point in a step"
        Serenity.takeScreenshot()
        then: "a screenshot should always be recorded"
        1 * baseStepListener.takeScreenshot()
    }

    def "should not store HTML source by default"() {
        given:
        ThucydidesWebDriverSupport.getDriver().get(staticSite)
        and:
        BaseStepListener stepListener = new BaseStepListener(temporaryDirectory)
        stepListener.testStarted("someTest")
        when:
        stepListener.stepStarted(ExecutedStepDescription.withTitle("some step"))
        stepListener.stepFinished()
        then:
        TestStep firstStep = stepListener.getTestOutcomes().get(0).getTestSteps().get(0)
        ScreenshotAndHtmlSource screenshot = firstStep.getScreenshots().get(0)
        !screenshot.getHtmlSource().isPresent()
    }



}
