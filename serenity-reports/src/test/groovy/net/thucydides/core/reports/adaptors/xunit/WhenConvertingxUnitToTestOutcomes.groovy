package net.thucydides.core.reports.adaptors.xunit

import net.thucydides.model.domain.TestOutcome
import net.thucydides.model.domain.TestResult
import net.thucydides.model.reports.adaptors.xunit.DefaultXUnitAdaptor
import spock.lang.Specification

import static net.thucydides.model.util.TestResources.fileInClasspathCalled

/**
 * We want to convert xUnit outputs (possibly with some extra custom fields) to TestOutcomes
 * so that they can be used to generate viable Thucydides test reports.
 */
class WhenConvertingxUnitToTestOutcomes extends Specification {

    def "should convert an xunit test result with one test to a single test outcome"() {

        given:
            def xunitFileDirectory = fileInClasspathCalled("/xunit-sample-output")
            def xUnitAdaptor = new DefaultXUnitAdaptor()
        when:
            List<TestOutcome> outcomes = xUnitAdaptor.loadOutcomesFrom(xunitFileDirectory)
            TestOutcome outcome = outcomes[0]
        then:
            outcomes.size() == 7
            outcome.testCount == 1
            outcomes.each {
                outcome.title.startsWith "Should do something"
            }

    }

    def "should set the test result to SUCCESS for successful testcases"() {
        given:
            def xunitFile = fileInClasspathCalled("/xunit-sample-output/singleTestCase.xml")
            def xUnitAdaptor = new DefaultXUnitAdaptor()
        when:
            List<TestOutcome> outcomes = xUnitAdaptor.testOutcomesIn(xunitFile)
            TestOutcome outcome = outcomes[0]
        then:
            outcomes.size() == 1
            outcome.testCount == 1
            outcome.title == "Should do something"
            outcome.result == TestResult.SUCCESS
    }

    def "should convert skipped tests into an outcome with Pending result"() {

        given:
            def xunitFile = fileInClasspathCalled("/xunit-sample-output/skippedTestCase.xml")
            def xUnitAdaptor = new DefaultXUnitAdaptor()
        when:
            List<TestOutcome> outcomes = xUnitAdaptor.testOutcomesIn(xunitFile)
            TestOutcome outcome = outcomes[0]
        then:
            outcomes.size() == 1
            outcome.testCount == 1
            outcome.title == "Should do something"
            outcome.result == TestResult.PENDING
    }

    def "should humanize the title but not assume that it has method argments when there is a colon in the title"() {
        given:
            def xunitFile = fileInClasspathCalled("/xunit-sample-output/singleTestCaseWithColonInName.xml")
            def xUnitAdaptor = new DefaultXUnitAdaptor()
        when:
            List<TestOutcome> outcomes = xUnitAdaptor.testOutcomesIn(xunitFile)
            TestOutcome outcome = outcomes[0]
        then:
            outcomes.size() == 1
            outcome.testCount == 1
            outcome.title == "Should do something: 1 | 2 | 3"
    }
}
