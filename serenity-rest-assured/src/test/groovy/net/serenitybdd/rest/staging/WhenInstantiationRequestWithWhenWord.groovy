package net.serenitybdd.rest.staging

import net.serenitybdd.rest.staging.decorators.request.RequestSpecificationDecorated
import net.serenitybdd.rest.staging.rules.RestConfigurationAction
import net.serenitybdd.rest.staging.rules.RestConfigurationRule
import org.junit.Rule
import spock.lang.Specification

import static net.serenitybdd.rest.staging.SerenityRest.*

/**
 * User: YamStranger
 * Date: 3/14/16
 * Time: 9:57 AM
 */
class WhenInstantiationRequestWithWhenWord extends Specification {

    @Rule
    def RestConfigurationRule rule = new RestConfigurationRule(new RestConfigurationAction() {
        @Override
        void apply() {
            reset()
        }
    },)

    def "should return wrapped request if used when method"() {
        given: "provided implementation of Rest Assurance"
        when: "creating new request"
            def request = when();
        then: "created request should be decorated"
            request instanceof RequestSpecificationDecorated
    }
}