package net.thucydides.model.domain

import net.thucydides.model.util.EnvironmentVariables
import net.thucydides.model.environment.MockEnvironmentVariables
import spock.lang.Specification

class WhenRunningTheSameTestInDifferentContexts extends Specification {

    static class MyTest {}

    def "The same test can be run in different contexts and produce different reports"() {
        given:
            EnvironmentVariables environmentVariables1 = new MockEnvironmentVariables()
            environmentVariables1.setProperty("context","chrome")
            environmentVariables1.setProperty("injected.tags","browser:chrome")

            EnvironmentVariables environmentVariables2 = new MockEnvironmentVariables()
            environmentVariables2.setProperty("context","firefox")
            environmentVariables2.setProperty("injected.tags","browser:firefox")
        when:
            TestOutcome testOutcomeOnChrome = TestOutcome.forTest("someTest", MyTest)
            testOutcomeOnChrome.environmentVariables = environmentVariables1

            TestOutcome testOutcomeOnFirefox = TestOutcome.forTest("someTest", MyTest)
            testOutcomeOnFirefox.environmentVariables = environmentVariables2

        then:
            testOutcomeOnChrome.getReportName(ReportType.HTML) != testOutcomeOnFirefox.getReportName(ReportType.HTML)
    }

    def "The same test can be run with and without a context and produce different reports"() {
        given:
            EnvironmentVariables environmentVariables1 = new MockEnvironmentVariables()
            EnvironmentVariables environmentVariables2 = new MockEnvironmentVariables()
            environmentVariables2.setProperty("context","firefox")
            environmentVariables2.setProperty("injected.tags","browser:firefox")
        when:
            TestOutcome testOutcomeOnChrome = TestOutcome.forTest("someTest", MyTest)
            testOutcomeOnChrome.environmentVariables = environmentVariables1

            TestOutcome testOutcomeOnFirefox = TestOutcome.forTest("someTest", MyTest)
            testOutcomeOnFirefox.environmentVariables = environmentVariables2
        then:
            testOutcomeOnChrome.getReportName(ReportType.HTML) != testOutcomeOnFirefox.getReportName(ReportType.HTML)
    }
}
