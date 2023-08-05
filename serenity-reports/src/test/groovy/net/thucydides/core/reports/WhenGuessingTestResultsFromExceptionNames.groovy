package net.thucydides.core.reports

import net.thucydides.model.domain.HeuristicTestResult
import net.thucydides.model.domain.TestResult
import spock.lang.Specification
import spock.lang.Unroll

class WhenGuessingTestResultsFromExceptionNames extends Specification {

    @Unroll
    def "Errors containing the word Assertion should be failures"() {
        expect:
        HeuristicTestResult.from(exceptionName) == expectedResult
        where:
        exceptionName           | expectedResult
        "MyFancyAssertionError" | TestResult.FAILURE
        "ClusterFailure"        | TestResult.FAILURE
        "SoftAssertionError"    | TestResult.FAILURE
        "MyAssertError"         | TestResult.FAILURE
        "SomeOtherError"        | TestResult.ERROR
    }
}
