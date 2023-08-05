package net.thucydides.model.matchers

import spock.lang.Specification

import static org.hamcrest.CoreMatchers.equalTo
import static org.hamcrest.CoreMatchers.is
import static org.hamcrest.Matchers.greaterThan

class BeanMatcherSpecification extends Specification {

    def "should match strings"() {
        expect:
            BeanMatchers.checkThat(value, is(comparedWith)).matches() == expectedResult
        where:
        value       | comparedWith  | expectedResult
            "Smith"     | "Smith"       | true
            "Smith"     | "Jones"       | false
    }

    def "should match booleans"() {
        expect:
         BeanMatchers.checkThat(value, org.hamcrest.Matchers.is(comparedWith)).matches() == expectedResult
        where:
        value     | comparedWith  | expectedResult
        true      | true          | true
        true      | false         | false
        false     | true          | false
        false     | false         | true
    }

    def "should match numbers"() {
        expect:
        BeanMatchers.checkThat(value, equalTo(comparedWith)).matches() == expectedResult
        where:
        value     | comparedWith             | expectedResult
        100       | 100.0                    | true
        100.0     | 100                      | true
        100.00    | 100.0                    | true
        100       | 100                      | true
        100       | 0                        | false
        0         | 100                      | false
    }

    def "should match big decimals"() {
        when:
            def matcher = new BigDecimalValueMatcher(1.0, is(1.0))
        then:
            matcher.matches()
    }

    def "should match big decimals with comparisons"() {
        when:
        def matcher = new BigDecimalValueMatcher(1.0, greaterThan(0.5))
        then:
        matcher.matches()
    }

    def "should match numbers with 'is'"() {
        expect:
        BeanMatchers.checkThat(value, org.hamcrest.Matchers.is(comparedWith)).matches() == expectedResult
        where:
        value     | comparedWith             | expectedResult
        100       | 100.0                    | true
        100.0     | 100                      | true
        100.00    | 100.0                    | true
        100       | 100                      | true
        100       | 0                        | false
        0         | 100                      | false
    }
}
