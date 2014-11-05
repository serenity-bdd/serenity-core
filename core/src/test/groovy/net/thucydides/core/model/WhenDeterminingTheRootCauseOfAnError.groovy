package net.thucydides.core.model

import net.thucydides.core.util.MockEnvironmentVariables
import sample.steps.FailingStep
import spock.lang.Specification

class WhenDeterminingTheRootCauseOfAnError extends Specification {

    def "Should record exceptions in a simplified form"() {
        given:
            def exception = new FailingStep().failsWithMessage("Oh crap")
        when:
            def rootCause = new RootCauseAnalyzer(exception).getRootCause()
        then:
            rootCause.errorType == "java.lang.IllegalArgumentException"
            rootCause.message == "Oh crap"
            rootCause.stackTrace.size() == 2
    }

    def "Should record full stack trace if configured"() {
        given:
            def exception = new FailingStep().failsWithMessage("Oh crap")
            def environmentVars = new MockEnvironmentVariables();
            environmentVars.setProperty("simplified.stack.traces","false")
        when:
            def rootCause = new RootCauseAnalyzer(exception, environmentVars).getRootCause()
        then:
            rootCause.stackTrace.size() > 2
    }
}