package net.serenitybdd.core.model

import io.cucumber.java.PendingException
import net.serenitybdd.core.PendingStepException
import net.serenitybdd.core.exceptions.TestCompromisedException
import net.serenitybdd.core.model.sampleexceptions.MyFailureException
import net.thucydides.core.model.TestResult
import net.thucydides.core.model.failures.FailureAnalysis
import net.thucydides.core.steps.StepFailureException
import net.thucydides.core.util.MockEnvironmentVariables
import net.thucydides.core.webdriver.WebdriverAssertionError
import org.assertj.core.api.SoftAssertionError
import org.junit.internal.ArrayComparisonFailure
import org.openqa.selenium.WebDriverException
import spock.lang.Specification
import spock.lang.Unroll

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
            new WebdriverAssertionError(new NullPointerException())                    | TestResult.ERROR
            new WebdriverAssertionError(new NoSuchElementException())                  | TestResult.ERROR
            new StepFailureException("bother", new NoSuchElementException())           | TestResult.ERROR
            new AssertionError("test message")                                         | TestResult.FAILURE
            new SoftAssertionError(["test message"])                                   | TestResult.FAILURE
            new ArrayComparisonFailure("test message",
                                        new AssertionError("wrapped exception"), 1)    | TestResult.FAILURE
            new WebdriverAssertionError(new AssertionError("wrapped assertion error")) | TestResult.FAILURE
            new StepFailureException("bother", new AssertionError("test message"))     | TestResult.FAILURE
            new RuntimeException("message")                                            | TestResult.ERROR
            new NullPointerException()                                                 | TestResult.ERROR
            new WebDriverException()                                                   | TestResult.ERROR
            new PendingStepException("step is pending")                                | TestResult.PENDING
            new PendingException("step is pending")                                    | TestResult.PENDING
            new TestCompromisedException("test is compromised")                        | TestResult.COMPROMISED
    }

    def "non-assertion exceptions should be reported as Errors by default"() {
        when:
            def failureAnalysisOf = new FailureAnalysis()
            def result = failureAnalysisOf.resultFor(new MyFailureException())
        then:
            result == TestResult.ERROR
    }

    def "should be able to define what exceptions cause failures using serenity.fail.on"() {

        given:
            def environmentVariables = new MockEnvironmentVariables()
            environmentVariables.setProperty("serenity.fail.on","net.serenitybdd.core.model.sampleexceptions.MyFailureException")
        when:
            def failureAnalysisOf = new FailureAnalysis(environmentVariables)
            def result = failureAnalysisOf.resultFor(new MyFailureException())
        then:
            result == TestResult.FAILURE
    }

    def "should be able to override failures as errors using serenity.error.on"() {

        given:
            def environmentVariables = new MockEnvironmentVariables()
            environmentVariables.setProperty("serenity.error.on","java.lang.AssertionError")
        when:
            def failureAnalysisOf = new FailureAnalysis(environmentVariables)
            def result = failureAnalysisOf.resultFor(new AssertionError("oh crap"))
        then:
            result == TestResult.ERROR
    }

    def "should be able to override errors as compromised using serenity.compromised.on"() {

        given:
        def environmentVariables = new MockEnvironmentVariables()
        environmentVariables.setProperty("serenity.compromised.on","java.lang.AssertionError")
        when:
        def failureAnalysisOf = new FailureAnalysis(environmentVariables)
        def result = failureAnalysisOf.resultFor(new AssertionError("oh crap"))
        then:
        result == TestResult.COMPROMISED
    }

    def "should be able to override errors as skipped using serenity.skip.on"() {

        given:
        def environmentVariables = new MockEnvironmentVariables()
        environmentVariables.setProperty("serenity.skipped.on","java.lang.AssertionError")
        when:
        def failureAnalysisOf = new FailureAnalysis(environmentVariables)
        def result = failureAnalysisOf.resultFor(new AssertionError("oh crap"))
        then:
        result == TestResult.SKIPPED
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
            result == TestResult.FAILURE
    }

    def "should be able to override errors as pending"() {

        given:
            def environmentVariables = new MockEnvironmentVariables()
            environmentVariables.setProperty("serenity.pending.on", "net.serenitybdd.core.model.sampleexceptions.MyFailureException")
        when:
            def failureAnalysisOf = new FailureAnalysis(environmentVariables)
            def result = failureAnalysisOf.resultFor(new MyFailureException())
        then:
            result == TestResult.PENDING
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
            result == TestResult.FAILURE
    }

}
