package net.serenitybdd.core.model
import cucumber.api.PendingException
import net.serenitybdd.core.PendingStepException
import net.serenitybdd.core.exceptions.TestCompromisedException
import net.serenitybdd.core.model.sampleexceptions.MyFailureException
import net.thucydides.core.model.failures.FailureAnalysis
import net.thucydides.core.steps.StepFailureException
import net.thucydides.core.util.MockEnvironmentVariables
import net.thucydides.core.webdriver.WebdriverAssertionError
import org.assertj.core.api.SoftAssertionError
import org.junit.internal.ArrayComparisonFailure
import org.openqa.selenium.WebDriverException
import spock.lang.Specification
import spock.lang.Unroll

import static net.thucydides.core.model.TestResult.*

class WhenReportingExceptions extends Specification {

    def failureAnalysisOf = new FailureAnalysis()

    @Unroll
    def "should be able to report an exception as either a failure or an error depending on its type (#exception -> #expectedResult"() {

        when:
            def result = failureAnalysisOf.resultFor(exception)
        then:
            result == expectedResult

        where:
            exception                                                                  | expectedResult
            new WebdriverAssertionError(new NullPointerException())                    | ERROR
            new WebdriverAssertionError(new NoSuchElementException())                  | ERROR
            new StepFailureException("bother", new NoSuchElementException())           | ERROR
            new AssertionError("test message")                            | FAILURE
            new SoftAssertionError(["test message"])                                     | FAILURE
            new ArrayComparisonFailure("test message",
                                        new AssertionError("wrapped exception"), 1)    | FAILURE
            new WebdriverAssertionError(new AssertionError("wrapped assertion error")) | FAILURE
            new StepFailureException("bother", new AssertionError("test message"))     | FAILURE
            new RuntimeException("message")                                                             | ERROR
            new NullPointerException()                                                                  | ERROR
            new WebDriverException()                                                                    | ERROR
            new PendingStepException("step is pending")                                                 | PENDING
            new PendingException("step is pending")                                                     | PENDING
            new TestCompromisedException("test is compromised")                                         | COMPROMISED
    }

    def "non-assertion exceptions should be reported as Errors by default"() {
        when:
            def failureAnalysisOf = new FailureAnalysis()
            def result = failureAnalysisOf.resultFor(new MyFailureException())
        then:
            result == ERROR
    }

    def "should be able to define what exceptions cause failures using serenity.fail.on"() {

        given:
            def environmentVariables = new MockEnvironmentVariables()
            environmentVariables.setProperty("serenity.fail.on","net.serenitybdd.core.model.sampleexceptions.MyFailureException")
        when:
            def failureAnalysisOf = new FailureAnalysis(environmentVariables)
            def result = failureAnalysisOf.resultFor(new MyFailureException())
        then:
            result == FAILURE
    }

    def "should be able to override failures as errors using serenity.error.on"() {

        given:
            def environmentVariables = new MockEnvironmentVariables()
            environmentVariables.setProperty("serenity.error.on","java.lang.AssertionError")
        when:
            def failureAnalysisOf = new FailureAnalysis(environmentVariables)
            def result = failureAnalysisOf.resultFor(new AssertionError("oh crap"))
        then:
            result == ERROR
    }

    def "should be able to override errors as compromised using serenity.compromised.on"() {

        given:
        def environmentVariables = new MockEnvironmentVariables()
        environmentVariables.setProperty("serenity.compromised.on","java.lang.AssertionError")
        when:
        def failureAnalysisOf = new FailureAnalysis(environmentVariables)
        def result = failureAnalysisOf.resultFor(new AssertionError("oh crap"))
        then:
        result == COMPROMISED
    }

    def "should be able to override errors as skipped using serenity.skip.on"() {

        given:
        def environmentVariables = new MockEnvironmentVariables()
        environmentVariables.setProperty("serenity.skipped.on","java.lang.AssertionError")
        when:
        def failureAnalysisOf = new FailureAnalysis(environmentVariables)
        def result = failureAnalysisOf.resultFor(new AssertionError("oh crap"))
        then:
        result == SKIPPED
    }

    def "should be able to override errors as failures"() {

        given:
            def environmentVariables = new MockEnvironmentVariables()
            environmentVariables.setProperty("serenity.fail.on",
                                             "net.serenitybdd.core.model.sampleexceptions.MyFailureException, java.util.NoSuchElementException")
        when:
            def failureAnalysisOf = new FailureAnalysis(environmentVariables)
            def result = failureAnalysisOf.resultFor(new NoSuchElementException())
        then:
            result == FAILURE
    }

    def "should be able to override errors as pending"() {

        given:
            def environmentVariables = new MockEnvironmentVariables()
            environmentVariables.setProperty("serenity.pending.on", "net.serenitybdd.core.model.sampleexceptions.MyFailureException")
        when:
            def failureAnalysisOf = new FailureAnalysis(environmentVariables)
            def result = failureAnalysisOf.resultFor(new MyFailureException())
        then:
            result == PENDING
    }

    def "should be able to override errors as failures even with nested errors"() {

        given:
            def environmentVariables = new MockEnvironmentVariables()
            environmentVariables.setProperty("serenity.fail.on",
                    "net.serenitybdd.core.model.sampleexceptions.MyFailureException, java.util.NoSuchElementException")
        when:
            def failureAnalysisOf = new FailureAnalysis(environmentVariables)
            def result = failureAnalysisOf.resultFor(new StepFailureException("oh bother!",new NoSuchElementException()))
        then:
            result == FAILURE
    }

}
