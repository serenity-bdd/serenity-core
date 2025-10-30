package net.serenitybdd.rest.configuring

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.junit.WireMockRule
import io.restassured.specification.FilterableRequestSpecification
import net.serenity.test.utils.rules.TestCase
import net.serenitybdd.rest.decorators.ResponseDecorated
import net.serenitybdd.rest.decorators.ResponseSpecificationDecorated
import net.serenitybdd.rest.rules.RestConfigurationAction
import net.serenitybdd.rest.rules.RestConfigurationRule
import net.thucydides.core.steps.BaseStepListener
import org.junit.Rule
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static net.serenitybdd.rest.SerenityRest.given
import static net.serenitybdd.rest.SerenityRest.reset

/**
 * User: YamStranger
 * Date: 3/30/16
 * Time: 9:57 AM
 */
class WhenConfiguringRedirectRequestParameters extends Specification {

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

    def "should be returned wrapped request specification after redirects-max configuration"() {
        given: "request initialised"
            def request = (FilterableRequestSpecification) given();
        when: "changing redirect configuration and getting request specification"
            def requestAfterLog = request.redirects().max(10)
        then: "same request should be returned after log operation"
            requestAfterLog == request
    }

    def "should be returned wrapped response specification after redirects-follow configuration"() {
        given: "request initialised"
            def request = (FilterableRequestSpecification) given();
        when: "changing redirect configuration and getting request specification"
            def responseAfterLog = request.redirects().follow(true).response()
        then: "wrapped response specification should be returned after log operation"
            responseAfterLog instanceof ResponseSpecificationDecorated
    }

    def "should be returned wrapped response specification after redirects-allowCircular configuration"() {
        given: "request initialised"
            def request = (FilterableRequestSpecification) given();
        when: "changing redirect configuration and getting request specification"
            def responseAfterLog = request.redirects().allowCircular(true).response()
        then: "wrapped response specification should be returned after log operation"
            responseAfterLog instanceof ResponseSpecificationDecorated
    }

    def "should be returned wrapped response specification after redirects-rejectRelative configuration"() {
        given: "request initialised"
            def request = (FilterableRequestSpecification) given();
        when: "changing redirect configuration and getting request specification"
            def responseAfterLog = request.redirects().rejectRelative(true).response()
        then: "wrapped response specification should be returned after log operation"
            responseAfterLog instanceof ResponseSpecificationDecorated
    }

    def "should be returned wrapped response after redirects configuration and executing request"() {
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
        when: "changing redirect configuration and executing request"
            def responseAfterExecutingRequest = request.redirects().follow(true).get(url)
        then: "wrapped response should be returned after executing request"
            responseAfterExecutingRequest instanceof ResponseDecorated
        and: "status code returned as expected"
            responseAfterExecutingRequest.then().statusCode(200)
    }
}
