package serenitycore.net.thucydides.core.screenshots.integration

import serenitycore.net.serenitybdd.core.Serenity
import serenitycore.net.serenitybdd.core.webdriver.servicepools.DriverServicePool
import serenitycore.net.serenitybdd.core.webdriver.servicepools.PhantomJSServicePool
import serenitymodel.net.thucydides.core.model.TestStep
import serenitymodel.net.thucydides.core.screenshots.ScreenshotAndHtmlSource
import serenitycore.net.thucydides.core.steps.BaseStepListener
import serenitymodel.net.thucydides.core.steps.ExecutedStepDescription
import serenitycore.net.thucydides.core.steps.StepEventBus
import serenitymodel.net.thucydides.core.util.EnvironmentVariables
import serenitymodel.net.thucydides.core.util.MockEnvironmentVariables
import serenitycore.net.thucydides.core.webdriver.ThucydidesWebDriverSupport
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.DesiredCapabilities
import spock.lang.Shared
import spock.lang.Specification

import static serenitycore.net.thucydides.core.webdriver.StaticTestSite.fileInClasspathCalled

class WhenTakingScreenshots extends Specification {
    @Rule
    TemporaryFolder temporaryFolder
    File temporaryDirectory

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables()

    @Shared DriverServicePool driverService;
    WebDriver driver

    def setupSpec() {
        driverService = new PhantomJSServicePool()
        driverService.start()
        StepEventBus.eventBus.clear()

    }

    def cleanupSpec() {
        driverService.shutdown()
    }

    def cleanup() {
        if (driver) {
            driver.quit();
        }
    }

    String staticSite;

    def setup() {
        temporaryDirectory = temporaryFolder.newFolder()
        StepEventBus.eventBus.clear()

        def desiredCapabilities = DesiredCapabilities.chrome();
        def chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

        driver = driverService.newDriver(desiredCapabilities);

        ThucydidesWebDriverSupport.useDriver(driver)

        staticSite = "file://" + fileInClasspathCalled("static-site/static-index.html").getAbsolutePath();

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
        TestStep firstStep = stepListener.getTestOutcomes().get(0).getTestSteps().get(0);
        ScreenshotAndHtmlSource screenshot = firstStep.getScreenshots().get(0);
        !screenshot.getHtmlSource().isPresent()
    }



}
