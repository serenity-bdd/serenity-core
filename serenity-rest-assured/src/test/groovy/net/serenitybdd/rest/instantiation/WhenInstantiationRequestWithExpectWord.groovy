package net.serenitybdd.rest.instantiation

import net.serenitybdd.rest.decorators.ResponseSpecificationDecorated
import net.serenitybdd.rest.rules.RestConfigurationAction
import net.serenitybdd.rest.rules.RestConfigurationRule
import org.junit.Rule
import spock.lang.Specification

import static net.serenitybdd.rest.SerenityRest.expect
import static net.serenitybdd.rest.SerenityRest.reset

/**
 * User: YamStranger
 * Date: 3/14/16
 * Time: 9:57 AM
 */
class WhenInstantiationRequestWithExpectWord extends Specification {

    @Rule
    def RestConfigurationRule rule = new RestConfigurationRule(new RestConfigurationAction() {
        @Override
        void apply() {
            reset()
        }
    },)

    def "should return wrapped response if used expect method"() {
        given: "provided implementation of Rest Assurance"
        when: "creating new request"
            def response = expect();
        then: "created request should be decorated"
            response instanceof ResponseSpecificationDecorated
    }
}
