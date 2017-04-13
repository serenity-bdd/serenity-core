package net.serenitybdd.rest.instantiation

import net.serenitybdd.rest.decorators.request.RequestSpecificationDecorated
import net.serenitybdd.rest.rules.RestConfigurationAction
import net.serenitybdd.rest.rules.RestConfigurationRule
import org.junit.Rule
import spock.lang.Specification

import static net.serenitybdd.rest.SerenityRest.reset
import static net.serenitybdd.rest.SerenityRest.with

/**
 * User: YamStranger
 * Date: 3/14/16
 * Time: 9:57 AM
 */
class WhenInstantiationRequestWithWithWord extends Specification {

    @Rule
    def RestConfigurationRule rule = new RestConfigurationRule(new RestConfigurationAction() {
        @Override
        void apply() {
            reset()
        }
    },)

    def "should return wrapped request if used with method"() {
        given: "provided implementation of Rest Assurance"
        when: "creating new request"
            def request = with();
        then: "created request should be decorated"
            request instanceof RequestSpecificationDecorated
    }
}
