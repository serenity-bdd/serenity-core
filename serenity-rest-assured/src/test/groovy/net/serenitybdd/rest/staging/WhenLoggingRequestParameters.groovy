package net.serenitybdd.rest.staging

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.jayway.restassured.specification.FilterableRequestSpecification
import net.serenitybdd.rest.staging.decorators.ResponseDecorated
import net.serenitybdd.rest.staging.decorators.ResponseSpecificationDecorated
import net.serenitybdd.rest.staging.rules.RestConfigurationAction
import net.serenitybdd.rest.staging.rules.RestConfigurationRule
import org.junit.Rule
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.matching
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching

import static net.serenitybdd.rest.staging.SerenityRest.*

/**
 * User: YamStranger
 * Date: 3/30/16
 * Time: 9:57 AM
 */
class WhenLoggingRequestParameters extends Specification {

    @Rule
    def WireMockRule wire = new WireMockRule(0);

    @Rule
    def RestConfigurationRule rule = new RestConfigurationRule(new RestConfigurationAction() {
        @Override
        void apply() {
            reset()
        }
    },)

    def "should be returned wrapped request specification after log operations"() {
        given: "request initialised"
            def request = (FilterableRequestSpecification) given();
        when: "logging from request and getting request specification"
            def requestAfterLog = request.log().body().request()
        then: "same request should be returned after log operation"
            requestAfterLog == request
    }

    def "should be returned wrapped response specification after body log operations"() {
        given: "request initialised"
            def request = (FilterableRequestSpecification) given();
        when: "logging from request and getting response"
            def responseAfterLog = request.log().body().response()
        then: "wrapped response specification should be returned after log operation"
            responseAfterLog instanceof ResponseSpecificationDecorated
    }

    def "should be returned wrapped response specification after all log operations"() {
        given: "request initialised"
            def request = (FilterableRequestSpecification) given();
        when: "logging from request and getting response"
            def responseAfterLog = request.log().all().response()
        then: "wrapped response specification should be returned after log operation"
            responseAfterLog instanceof ResponseSpecificationDecorated
    }

    def "should be returned wrapped response specification after parameters log operations"() {
        given: "request initialised"
            def request = (FilterableRequestSpecification) given();
        when: "logging from request and getting response"
            def responseAfterLog = request.log().parameters().response()
        then: "wrapped response specification should be returned after log operation"
            responseAfterLog instanceof ResponseSpecificationDecorated
    }

    def "should be returned wrapped response after log operations and executing request"() {
        given: "rest with default config updated"
            def body = "<root>" +
                "<value>1</value>" +
                "</root>"

            def base = "http://localhost:${wire.port()}"
            def path = "/test/log/levels"
            def url = "$base$path"

            stubFor(WireMock.get(urlMatching(path))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/xml")
                .withBody(body)));
            def request = (FilterableRequestSpecification) given();
        when: "logging from request and executing request"
            def responseAfterExecutingRequest = request.log().body().request().get(url)
        then: "wrapped response should be returned after executing request"
            responseAfterExecutingRequest instanceof ResponseDecorated
        and: "status code returned as expected"
            responseAfterExecutingRequest.then().statusCode(200)
    }
}