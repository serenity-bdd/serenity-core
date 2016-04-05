package net.serenitybdd.rest.staging

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.jayway.restassured.specification.FilterableRequestSpecification
import net.serenitybdd.rest.staging.decorators.ResponseDecorated
import net.serenitybdd.rest.staging.decorators.ResponseSpecificationDecorated
import net.serenitybdd.rest.staging.decorators.request.RequestSpecificationDecorated
import net.serenitybdd.rest.staging.rules.RestConfigurationAction
import net.serenitybdd.rest.staging.rules.RestConfigurationRule
import org.junit.Rule
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static net.serenitybdd.rest.staging.SerenityRest.given
import static net.serenitybdd.rest.staging.SerenityRest.reset

/**
 * User: YamStranger
 * Date: 3/30/16
 * Time: 9:57 AM
 */
class WhenUseChainOfRequestInitialisations extends Specification {

    @Rule
    def RestConfigurationRule rule = new RestConfigurationRule(new RestConfigurationAction() {
        @Override
        void apply() {
            reset()
        }
    },)

    def "should be returned wrapped request specification after given operation"() {
        given: "request initialised"
            def request = (FilterableRequestSpecification) given();
        when: "executing given from request specification"
            def requestAfterLog = request.given()
        then: "same request should be returned after given operation"
            requestAfterLog == request
        and: "request should be wrapped"
            requestAfterLog instanceof RequestSpecificationDecorated
    }

    def "should be returned wrapped request specification after when operation"() {
        given: "request initialised"
            def request = (FilterableRequestSpecification) given();
        when: "executing when from request specification"
            def requestAfterLog = request.when()
        then: "same request should be returned after when operation"
            requestAfterLog == request
        and: "request should be wrapped"
            requestAfterLog instanceof RequestSpecificationDecorated
    }

    def "should be returned wrapped request specification after AND operation"() {
        given: "request initialised"
            def request = (FilterableRequestSpecification) given();
        when: "executing when from request specification"
            def requestAfterLog = request.and()
        then: "same request should be returned after and operation"
            requestAfterLog == request
        and: "request should be wrapped"
            requestAfterLog instanceof RequestSpecificationDecorated
    }

    def "should be returned wrapped request specification after with operation"() {
        given: "request initialised"
            def request = (FilterableRequestSpecification) given();
        when: "executing when from request specification"
            def requestAfterLog = request.with()
        then: "same request should be returned after with operation"
            requestAfterLog == request
        and: "request should be wrapped"
            requestAfterLog instanceof RequestSpecificationDecorated
    }
}