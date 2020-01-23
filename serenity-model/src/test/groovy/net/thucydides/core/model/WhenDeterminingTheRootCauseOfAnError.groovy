package net.thucydides.core.model

import net.serenitybdd.core.exceptions.CausesCompromisedTestFailure
import net.thucydides.core.model.stacktrace.RootCauseAnalyzer
import net.thucydides.core.model.stacktrace.StackTraceSanitizer
import net.thucydides.core.util.MockEnvironmentVariables
import org.openqa.selenium.ElementNotVisibleException
import sample.steps.FailingStep
import spock.lang.Specification
import spock.lang.Unroll

class WhenDeterminingTheRootCauseOfAnError extends Specification {

    def "Should record exceptions in a simplified form"() {
        given:
            def exception = new FailingStep().failsWithMessage("Oh crap")
        when:
            def rootCause = new RootCauseAnalyzer(exception).getRootCause()
        then:
            rootCause.errorType == "java.lang.IllegalArgumentException"
            rootCause.message.contains("Oh crap")
            rootCause.stackTrace.size() == 1
    }

    def "Should report WebdriverAssertionError exceptions directly "() {
        given:
            def exception = new ElementNotVisibleException("Oh crap")
        when:
            def rootCause = new RootCauseAnalyzer(exception).getRootCause()
        then:
            rootCause.errorType == "org.openqa.selenium.ElementNotVisibleException"
    }

    static class AppCompromised extends Error implements CausesCompromisedTestFailure {
        AppCompromised(String message) {
            super(message)
        }

        AppCompromised(String message, Throwable cause) {
            super(message, cause)
        }
    }

    def "Should report Compromised exceptions directly "() {
        given:
            def exception = new AppCompromised("Oh crap", new AssertionError("Oh bugger"))
        when:
            def rootCause = new RootCauseAnalyzer(exception).getRootCause()
        then:
            rootCause.errorType.contains("AppCompromised")
    }

    def "Should record full stack trace if configured"() {
        given:
            def exception = new FailingStep().failsWithMessage("Oh crap")
            def environmentVars = new MockEnvironmentVariables();
            environmentVars.setProperty("simplified.stack.traces","false")
        when:
            def rootCause = new RootCauseAnalyzer(exception).getRootCause()
        then:
            rootCause.stackTrace.size() == 1
    }

    @Unroll
    def "Should filter out test framework steps"() {
        given:
            def environmentVars = new MockEnvironmentVariables();
            environmentVars.setProperty("simplified.stack.traces",simplifiedStackStace)
            def stackTrace = [new StackTraceElement(className, 'someMethod','SomeClass.java', 100)] as StackTraceElement[]
        when:
            def filter = new StackTraceSanitizer(environmentVars,stackTrace)
        then:
            filter.sanitizedStackTrace.size() == filteredStackSize
        where:
            className                                           | simplifiedStackStace | filteredStackSize
            'net.thucydides.showcase.jbehave.pages.ListingPage' | "true"               | 1
            'net.thucydides.core.something.SomeClass'           | "true"               | 0
            'net.serenitybdd.core.pages.WebElementFacadeImpl'   | "true"               | 0
            'net.sf.cglib.proxy.MethodProxy'                    | "true"               | 0
            'sun.reflect.NativeMethodAccessorImpl'              | "true"               | 0
            'net.thucydides.core.something.SomeClass'           | "false"              | 1
            'net.serenitybdd.core.pages.WebElementFacadeImpl'   | "false"              | 1
            'net.sf.cglib.proxy.MethodProxy'                    | "false"              | 1
            'sun.reflect.NativeMethodAccessorImpl'              | "false"              | 1
    }

}