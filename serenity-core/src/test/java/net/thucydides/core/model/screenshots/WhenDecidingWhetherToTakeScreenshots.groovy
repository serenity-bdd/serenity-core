package net.thucydides.core.model.screenshots

import net.serenitybdd.annotations.Screenshots
import net.serenitybdd.annotations.Step
import net.thucydides.core.steps.BaseStepListener
import net.thucydides.core.steps.StepEventBus
import net.thucydides.model.configuration.SystemPropertiesConfiguration
import net.thucydides.model.domain.TakeScreenshots
import net.thucydides.model.environment.MockEnvironmentVariables
import net.thucydides.model.util.EnvironmentVariables
import net.thucydides.model.webdriver.Configuration
import spock.lang.Specification
import spock.lang.Unroll

class WhenDecidingWhetherToTakeScreenshots extends Specification {

    def configuration = Mock(Configuration)

    def setup() {
        configuration.screenshotLevel >> Optional.empty()
        StepDefinitionAnnotations.clear()
    }

    @Unroll
    def "should be able to configure screenshots using the legacy system properties"() {

        when:
        configuration.takeVerboseScreenshots() >> takeVerboseScreenshots
        configuration.onlySaveFailingScreenshots() >> onlyTakeFailingverboseScreenshots
        configuration.screenshotLevel >> Optional.empty()
        ScreenshotPermission permissions = new ScreenshotPermission(configuration)

        then:
        permissions.areAllowed(screenshotLevel) == shouldBeAllowed

        where:
        onlyTakeFailingverboseScreenshots | takeVerboseScreenshots | screenshotLevel                            | shouldBeAllowed
        true                              | true                   | TakeScreenshots.FOR_EACH_ACTION            | false
        true                              | true                   | TakeScreenshots.BEFORE_AND_AFTER_EACH_STEP | false
        true                              | true                   | TakeScreenshots.AFTER_EACH_STEP            | false
        true                              | true                   | TakeScreenshots.FOR_FAILURES               | true

        false                             | false                  | TakeScreenshots.FOR_EACH_ACTION            | false
        false                             | false                  | TakeScreenshots.BEFORE_AND_AFTER_EACH_STEP | true
        false                             | false                  | TakeScreenshots.AFTER_EACH_STEP            | true
        false                             | false                  | TakeScreenshots.FOR_FAILURES               | true

        false                             | true                   | TakeScreenshots.FOR_EACH_ACTION            | true
        false                             | true                   | TakeScreenshots.BEFORE_AND_AFTER_EACH_STEP | true
        false                             | true                   | TakeScreenshots.AFTER_EACH_STEP            | true
        false                             | true                   | TakeScreenshots.FOR_FAILURES               | true
    }


    def environmentVariables = new MockEnvironmentVariables()
    def systemPropConfiguration = new SystemPropertiesConfiguration(environmentVariables)

    @Unroll
    def "should be able to configure screenshot level via the new thucydides.take.screenshots system properties"() {

        when:
        environmentVariables.setProperty("thucydides.take.screenshots", takeScreenshotsConfiguration)
        ScreenshotPermission permissions = new ScreenshotPermission(systemPropConfiguration)

        then:
        permissions.areAllowed(screenshotLevel) == shouldBeAllowed

        where:
        takeScreenshotsConfiguration | screenshotLevel                            | shouldBeAllowed
        "FOR_EACH_ACTION"            | TakeScreenshots.FOR_EACH_ACTION            | true
        "FOR_EACH_ACTION"            | TakeScreenshots.FOR_FAILURES               | true
        "FOR_EACH_ACTION"            | TakeScreenshots.BEFORE_AND_AFTER_EACH_STEP | true
        "FOR_EACH_ACTION"            | TakeScreenshots.AFTER_EACH_STEP            | true

        "BEFORE_AND_AFTER_EACH_STEP" | TakeScreenshots.FOR_EACH_ACTION            | false
        "BEFORE_AND_AFTER_EACH_STEP" | TakeScreenshots.FOR_FAILURES               | true
        "BEFORE_AND_AFTER_EACH_STEP" | TakeScreenshots.BEFORE_AND_AFTER_EACH_STEP | true
        "BEFORE_AND_AFTER_EACH_STEP" | TakeScreenshots.AFTER_EACH_STEP            | true

        "AFTER_EACH_STEP"            | TakeScreenshots.FOR_EACH_ACTION            | false
        "AFTER_EACH_STEP"            | TakeScreenshots.FOR_FAILURES               | true
        "AFTER_EACH_STEP"            | TakeScreenshots.BEFORE_AND_AFTER_EACH_STEP | false
        "AFTER_EACH_STEP"            | TakeScreenshots.AFTER_EACH_STEP            | true

        "FOR_FAILURES"               | TakeScreenshots.FOR_EACH_ACTION            | false
        "FOR_FAILURES"               | TakeScreenshots.FOR_FAILURES               | true
        "FOR_FAILURES"               | TakeScreenshots.BEFORE_AND_AFTER_EACH_STEP | false
        "FOR_FAILURES"               | TakeScreenshots.AFTER_EACH_STEP            | false
    }

    def "overriding screenshot configuration using method annotations"() {
        when:
        configuration.takeVerboseScreenshots() >> true
        then:
        checkOnlyTakeOnFailures()
        checkOnEachStep()
        checkOnBeforeAndAfterEachStep()
        checkDefaultScreenshotPermissions()
    }

    def "overriding only-on-failure screenshot configuration using method annotations"() {
        when:
        configuration.onlySaveFailingScreenshots() >> true
        then:
        checkOverrideOnlyOnFailure()
        checkOverrideOnlyOnFailureWithPerStepMode()
        checkOverrideOnlyOnFailureWithVerboseMode()
    }

    def "onlyOnFailures in annotations should override AFTER_EACH_STEP in system properties"() {
        when:
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables()
        environmentVariables.setProperty("thucydides.take.screenshots", "AFTER_EACH_STEP")

        Configuration configuration = new SystemPropertiesConfiguration(environmentVariables)
        then:
        shouldTakeScreenshotsOnlyOnFailures(configuration)
    }

    def "allow screenshot configuration using annotations over test step methods"() {
        given:
            def listener = Mock(BaseStepListener)
            def testMethod =this.class.getMethod("testStepWithScreenshotConfiguration")
            listener.getCurrentStepMethod() >> Optional.of(testMethod)
            StepEventBus.getEventBus().registerListener(listener)
        when:
            configuration.takeVerboseScreenshots() >> true
        then:
            ScreenshotPermission permissions = new ScreenshotPermission(configuration)
            assert !permissions.areAllowed(TakeScreenshots.FOR_EACH_ACTION)
            assert !permissions.areAllowed(TakeScreenshots.BEFORE_AND_AFTER_EACH_STEP)
            assert !permissions.areAllowed(TakeScreenshots.AFTER_EACH_STEP)
            assert !permissions.areAllowed(TakeScreenshots.FOR_FAILURES)
    }

    @Screenshots(onlyOnFailures = true)
    void checkOnlyTakeOnFailures() {
        ScreenshotPermission permissions = new ScreenshotPermission(configuration)

        assert !permissions.areAllowed(TakeScreenshots.FOR_EACH_ACTION)
        assert !permissions.areAllowed(TakeScreenshots.BEFORE_AND_AFTER_EACH_STEP)
        assert !permissions.areAllowed(TakeScreenshots.AFTER_EACH_STEP)
        assert permissions.areAllowed(TakeScreenshots.FOR_FAILURES)
    }

    @Screenshots(afterEachStep = true)
    void checkOnEachStep() {
        ScreenshotPermission permissions = new ScreenshotPermission(configuration)

        assert !permissions.areAllowed(TakeScreenshots.FOR_EACH_ACTION)
        assert !permissions.areAllowed(TakeScreenshots.BEFORE_AND_AFTER_EACH_STEP)
        assert permissions.areAllowed(TakeScreenshots.AFTER_EACH_STEP)
        assert permissions.areAllowed(TakeScreenshots.FOR_FAILURES)
    }

    @Screenshots(beforeAndAfterEachStep = true)
    void checkOnBeforeAndAfterEachStep() {
        ScreenshotPermission permissions = new ScreenshotPermission(configuration)

        assert !permissions.areAllowed(TakeScreenshots.FOR_EACH_ACTION)
        assert permissions.areAllowed(TakeScreenshots.AFTER_EACH_STEP)
        assert permissions.areAllowed(TakeScreenshots.BEFORE_AND_AFTER_EACH_STEP)
        assert permissions.areAllowed(TakeScreenshots.FOR_FAILURES)
    }


    @Screenshots()
    void checkDefaultScreenshotPermissions() {
        ScreenshotPermission permissions = new ScreenshotPermission(configuration)

        assert !permissions.areAllowed(TakeScreenshots.FOR_EACH_ACTION)
        assert permissions.areAllowed(TakeScreenshots.BEFORE_AND_AFTER_EACH_STEP)
        assert permissions.areAllowed(TakeScreenshots.AFTER_EACH_STEP)
        assert permissions.areAllowed(TakeScreenshots.FOR_FAILURES)
    }

    @Screenshots()
    void checkOverrideOnlyOnFailure() {
        ScreenshotPermission permissions = new ScreenshotPermission(configuration)

        assert !permissions.areAllowed(TakeScreenshots.FOR_EACH_ACTION)
        assert permissions.areAllowed(TakeScreenshots.AFTER_EACH_STEP)
        assert permissions.areAllowed(TakeScreenshots.FOR_FAILURES)
    }

    @Screenshots(afterEachStep = true)
    void checkOverrideOnlyOnFailureWithPerStepMode() {
        ScreenshotPermission permissions = new ScreenshotPermission(configuration)

        assert !permissions.areAllowed(TakeScreenshots.FOR_EACH_ACTION)
        assert permissions.areAllowed(TakeScreenshots.AFTER_EACH_STEP)
        assert permissions.areAllowed(TakeScreenshots.FOR_FAILURES)
    }

    @Screenshots(forEachAction = true)
    void checkOverrideOnlyOnFailureWithVerboseMode() {
        ScreenshotPermission permissions = new ScreenshotPermission(configuration)

        assert permissions.areAllowed(TakeScreenshots.FOR_EACH_ACTION)
        assert permissions.areAllowed(TakeScreenshots.AFTER_EACH_STEP)
        assert permissions.areAllowed(TakeScreenshots.FOR_FAILURES)
    }

    @Screenshots(onlyOnFailures = true)
    void shouldTakeScreenshotsOnlyOnFailures(Configuration configuration) {
        ScreenshotPermission permissions = new ScreenshotPermission(configuration)
        assert permissions.areAllowed(TakeScreenshots.FOR_FAILURES)
        assert !permissions.areAllowed(TakeScreenshots.FOR_EACH_ACTION)
    }

    @Screenshots(disabled = true)
    void shouldNotTakeScreenshots(Configuration configuration) {
        ScreenshotPermission permissions = new ScreenshotPermission(configuration)
        assert !permissions.areAllowed(TakeScreenshots.FOR_EACH_ACTION)
        assert !permissions.areAllowed(TakeScreenshots.BEFORE_AND_AFTER_EACH_STEP)
        assert !permissions.areAllowed(TakeScreenshots.AFTER_EACH_STEP)
        assert !permissions.areAllowed(TakeScreenshots.FOR_FAILURES)
    }
    @Screenshots(disabled = true)
    @Step
    void testStepWithScreenshotConfiguration(){}
}
