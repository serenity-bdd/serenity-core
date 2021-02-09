package net.thucydides.core.reports

import net.thucydides.core.reports.html.ResultCounts
import spock.lang.Specification

class WhenCountingTestResults extends Specification {


    def "should count the number of automated tests with various statuses"() {
        given:
            def testOutcomes = new TestOutcomesBuilder().defaultResults
        when:
            ResultCounts resultCount = new ResultCounts(testOutcomes)
        then:
            resultCount.getAutomatedTestCount("failure") == 4
            resultCount.getAutomatedTestCount("pending") == 5
            resultCount.getAutomatedTestCount("success") == 2
    }


    def "should include test results for table rows individually"() {
        given:
        def testOutcomes = new TestOutcomesBuilder().dataDrivenResults
        when:
        ResultCounts resultCount = new ResultCounts(testOutcomes)
        then:
        resultCount.getAutomatedTestCount("failure") == 2
        resultCount.getAutomatedTestCount("success") == 6
    }

    def "should calculate percentage labels"() {
        given:
        def testOutcomes = new TestOutcomesBuilder().dataDrivenResults
        when:
        ResultCounts resultCount = new ResultCounts(testOutcomes)
        then:
        resultCount.percentageLabelsByTypeFor("success", "failure") == "['75%', ' ', '25%', ' ']"
    }
}