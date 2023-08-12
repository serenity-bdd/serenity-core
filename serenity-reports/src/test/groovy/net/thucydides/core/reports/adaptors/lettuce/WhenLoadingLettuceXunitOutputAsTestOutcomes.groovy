package net.thucydides.core.reports.adaptors.lettuce

import net.thucydides.model.domain.TestResult
import net.thucydides.model.reports.adaptors.TestOutcomeAdaptor
import net.thucydides.model.reports.adaptors.lettuce.LettuceXUnitAdaptor
import spock.lang.Ignore
import spock.lang.Specification

import static net.thucydides.model.util.TestResources.fileInClasspathCalled

/**
 * We want to convert xUnit outputs (possibly with some extra custom fields) to TestOutcomes
 * so that they can be used to generate viable Thucydides test reports.
 */
class WhenLoadingLettuceXunitOutputAsTestOutcomes extends Specification {

    def "should convert a lettuce xunit test result to a test containing the given-when-then steps"() {
        given:
            def xunitReport = fileInClasspathCalled("/lettuce-xunit-reports/normal")
            TestOutcomeAdaptor lettuceLoader = new  LettuceXUnitAdaptor()
        when:
            def testOutcomes = lettuceLoader.loadOutcomesFrom(xunitReport)
        then:
            testOutcomes.size() == 4
        and:
            testOutcomes[0].flattenedTestSteps.collect{ it.description } ==
                    ["Given I have the number 0", "When I compute its factorial", "Then I see the number 1"]
            testOutcomes[1].flattenedTestSteps.collect{ it.description } ==
                    ["Given I have the number 1", "When I compute its factorial", "Then I see the number 1"]
            testOutcomes[2].flattenedTestSteps.collect{ it.description } ==
                    ["Given I have the number 2", "When I compute its factorial", "Then I see the number 2"]

    }

    def "should load errors and failing results"() {
        given:
            def xunitReport = fileInClasspathCalled("/lettuce-xunit-reports/with-failures")
            TestOutcomeAdaptor lettuceLoader = new LettuceXUnitAdaptor()
        when:
            def testOutcomes = lettuceLoader.loadOutcomesFrom(xunitReport)
        then:
            testOutcomes[0].result == TestResult.FAILURE
        and:
            testOutcomes[1].result == TestResult.SUCCESS
        and:
            testOutcomes[2].result == TestResult.ERROR

    }

    @Ignore
    def "should convert a table-based lettuce xunit test result to a test outcome containg the table"() {

    }
}
