package net.serenitybdd.rest.staging.configuring

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.jayway.restassured.specification.FilterableRequestSpecification
import com.jayway.restassured.specification.RequestSpecification
import net.serenity.test.utils.rules.TestCase
import net.serenitybdd.rest.staging.decorators.ResponseDecorated
import net.serenitybdd.rest.staging.rules.RestConfigurationAction
import net.serenitybdd.rest.staging.rules.RestConfigurationRule
import net.thucydides.core.steps.BaseStepListener
import org.junit.Rule
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static net.serenitybdd.rest.staging.SerenityRest.given
import static net.serenitybdd.rest.staging.SerenityRest.reset


/**
 * User: YamStranger
 * Date: 4/04/16
 * Time: 9:57 AM
 */
class WhenConfiguringBaseParametersForRequest extends Specification {

    @Rule
    def WireMockRule wire = new WireMockRule(0);

    @Rule
    def RestConfigurationRule rule = new RestConfigurationRule(new RestConfigurationAction() {
        @Override
        void apply() {
            reset()
        }
    },)

    @Rule
    def TestCase<BaseStepListener> test = new TestCase({
        Mock(BaseStepListener)
    }.call());

    def "should be returned wrapped request specification after setting base path"() {
        given: "request initialised"
            def request = (FilterableRequestSpecification) given();
            def path = "some/value"
        when: "getting request specification and updating base path"
            def RequestSpecification requestAfterLog = request.basePath(path)
        then: "same request should be returned after setting base path"
            requestAfterLog == request
        and: "base path should be equal to configured"
            ((FilterableRequestSpecification) requestAfterLog).getBasePath() == path
    }

    def "should be returned wrapped request specification after setting urlEncodingEnabled"() {
        given: "request initialised"
            def request = (FilterableRequestSpecification) given();
        when: "getting request specification and updating base path"
            def RequestSpecification requestAfterLog = request.urlEncodingEnabled(true)
        then: "same request should be returned after setting urlEncodingEnabled"
            requestAfterLog == request
    }

    def "should be returned wrapped response after setting base url and port and executing request"() {
        given: "rest with default config updated"
            def body = "<root>" +
                "<value>1</value>" +
                "</root>"

            def base = "http://localhost"
            def path = "/test/log/levels"
            def url = "$base$path"
            stubFor(WireMock.get(urlMatching("$path.*"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/xml")
                .withBody(body)));
            def request = (FilterableRequestSpecification) given();
        when: "setting base path and executing request"
            def responseAfterExecutingRequest = request.baseUri(base).port(wire.port()).get(path)
        then: "wrapped response should be returned after executing request"
            responseAfterExecutingRequest instanceof ResponseDecorated
        and: "status code returned as expected"
            responseAfterExecutingRequest.then().statusCode(200)
    }

    def "should be returned wrapped response after setting base port and executing request"() {
        given: "rest with default config updated"
            def body = "<root>" +
                "<value>1</value>" +
                "</root>"

            def base = "http://localhost"
            def path = "/test/log/levels"
            def url = "$base$path"
            stubFor(WireMock.get(urlMatching("$path.*"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/xml")
                .withBody(body)));
            def request = (FilterableRequestSpecification) given();
        when: "setting base port and executing request"
            def responseAfterExecutingRequest = request.port(wire.port()).get(url)
        then: "wrapped response should be returned after executing request"
            responseAfterExecutingRequest instanceof ResponseDecorated
        and: "status code returned as expected"
            responseAfterExecutingRequest.then().statusCode(200)
    }

    def "should be returned wrapped response after setting base path and uri and executing request"() {
        given: "rest with default config updated"
            def body = "<root>" +
                "<value>1</value>" +
                "</root>"

            def base = "http://localhost:${wire.port()}"
            def path = "/test/log/levels"
            def url = "$base$path"
            stubFor(WireMock.get(urlMatching("$path.*"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/xml")
                .withBody(body)));
            def request = (FilterableRequestSpecification) given();
        when: "setting base path and uri and executing request"
            def responseAfterExecutingRequest = request.baseUri(base).basePath("/test").get("log/levels")
        then: "wrapped response should be returned after executing request"
            responseAfterExecutingRequest instanceof ResponseDecorated
        and: "status code returned as expected"
            responseAfterExecutingRequest.then().statusCode(200)
    }
}