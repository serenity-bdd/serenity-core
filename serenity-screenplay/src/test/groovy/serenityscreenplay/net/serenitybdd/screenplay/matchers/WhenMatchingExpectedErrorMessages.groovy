package serenityscreenplay.net.serenitybdd.screenplay.matchers

import spock.lang.Specification
import spock.lang.Unroll
/**
 * Created by john on 8/06/2016.
 */
class WhenMatchingExpectedErrorMessages extends Specification {

    @Unroll
    def "should match an error message in a list"() {
        expect:
        ReportedErrorMessages.reportsErrors(item).matches(items) == expectedResult
        where:
        item                 | items           | expectedResult
        "A"                  | ["A", "B", "C"] | true
        "Z"                  | ["A", "B", "C"] | false
        ["A", "B"]           | ["A", "B", "C"] | true
        ["A", "Z"]           | ["A", "B", "C"] | false
        ["A", "B", "C", "D"] | ["A", "B", "C"] | false
        ""                   | ["A", "B", "C"] | false
    }
}