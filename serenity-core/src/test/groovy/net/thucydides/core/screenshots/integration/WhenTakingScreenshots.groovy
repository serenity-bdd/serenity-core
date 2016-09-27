package net.thucydides.core.screenshots.integration

import net.serenitybdd.core.Serenity
import net.thucydides.core.model.TestStep
import net.thucydides.core.screenshots.ScreenshotAndHtmlSource
import net.thucydides.core.steps.BaseStepListener
import net.thucydides.core.steps.ExecutedStepDescription
import net.thucydides.core.steps.StepEventBus
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class WhenTakingScreenshots extends Specification {
    @Rule
    TemporaryFolder temporaryFolder
    File temporaryDirectory

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables()

    def setup() {
        temporaryDirectory = temporaryFolder.newFolder()
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

    def "should add screenshots to the current test outcome"() {
        given:
            ThucydidesWebDriverSupport.initialize("phantomjs")
            ThucydidesWebDriverSupport.getDriver().get("http://localhost")
        and:
            BaseStepListener stepListener = new BaseStepListener(temporaryDirectory)
            stepListener.testStarted("someTest")
            stepListener.stepStarted(ExecutedStepDescription.withTitle("some step"))
        when:
            stepListener.takeScreenshot()
        then:
            stepListener.getTestOutcomes().get(0).getScreenshots().size() == 1
    }

    def "should not store HTML source by default"() {
        given:
            ThucydidesWebDriverSupport.initialize("phantomjs")
            ThucydidesWebDriverSupport.getDriver().get("http://localhost")
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

    def "identical screenshots should not be duplicated within steps"() {
        given:
            ThucydidesWebDriverSupport.initialize("phantomjs")
            ThucydidesWebDriverSupport.getDriver().get("http://localhost")
        and:
            BaseStepListener stepListener = new BaseStepListener(temporaryDirectory)
            stepListener.testStarted("someTest")
        when:

            stepListener.stepStarted(ExecutedStepDescription.withTitle("step 1"))
            stepListener.takeScreenshot();
            stepListener.stepFinished()

        then:
            TestStep firstStep = stepListener.getTestOutcomes().get(0).getTestSteps().get(0);
            firstStep.getScreenshots().size() == 1
    }


}
