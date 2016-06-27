package net.serenitybdd.rest.instantiation

import com.jayway.restassured.RestAssured
import com.jayway.restassured.specification.FilterableRequestSpecification
import net.serenitybdd.rest.decorators.request.RequestSpecificationDecorated
import net.serenitybdd.rest.decorators.ResponseSpecificationDecorated
import net.serenitybdd.rest.rules.RestConfigurationAction
import net.serenitybdd.rest.rules.RestConfigurationRule
import org.junit.Rule
import spock.lang.Specification

import static net.serenitybdd.rest.SerenityRest.*


/**
 * User: YamStranger
 * Date: 3/14/16
 * Time: 9:57 AM
 */
class WhenInstantiationRequestWithGivenWord extends Specification {

    @Rule
    def RestConfigurationRule rule = new RestConfigurationRule(new RestConfigurationAction() {
        @Override
        void apply() {
            reset()
        }
    },)

    def "should return wrapped request and response if used given method"() {
        given: "provided implementation of Rest Assurance"
        when: "creating new request"
            def request = given();
            def response = request.response();
        then: "created request and response should be decorated"
            request instanceof RequestSpecificationDecorated
            response instanceof ResponseSpecificationDecorated
    }

    def "should return wrapped request and response if they initialised separately"() {
        given: "initialised Request"
            def request = RestAssured.given()
            request = request.proxy(10)
        when: "creating new request"
            def generated = given(request)
        then: "created response should be decorated"
            generated instanceof RequestSpecificationDecorated
        and: "parameters should be merged"
            ((FilterableRequestSpecification) generated).getProxySpecification().getPort() == 10
    }
}