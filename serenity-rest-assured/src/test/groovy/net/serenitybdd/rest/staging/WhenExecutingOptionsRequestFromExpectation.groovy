package net.serenitybdd.rest.staging

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.junit.WireMockRule
import net.serenitybdd.rest.staging.decorators.ResponseDecorated
import net.serenitybdd.rest.staging.rules.RestConfigurationAction
import net.serenitybdd.rest.staging.rules.RestConfigurationRule
import org.hamcrest.Matchers
import org.junit.Rule
import spock.lang.Specification

import static net.serenitybdd.rest.staging.SerenityRest.*
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.matching
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching

/**
 * User: YamStranger
 * Date: 3/14/16
 * Time: 9:57 AM
 */
class WhenExecutingOptionsRequestFromExpectation extends Specification {

    @Rule
    def WireMockRule wire = new WireMockRule(0);

    @Rule
    def RestConfigurationRule rule = new RestConfigurationRule(new RestConfigurationAction() {
        @Override
        void apply() {
            reset()
        }
    },)

    def "should return wrapped response during OPTIONS by URL called from expectation"() {
        given: "configured access point"
            def body = "<root>" +
                "<value>7</value>" +
                "</root>"
            def base = "http://localhost:${wire.port()}"
            def path = "/test/options/creature"
            def url = "$base$path"
            stubFor(WireMock.options(urlMatching("$path.*"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(506)
                .withHeader("Content-Type", "application/xml")
                .withBody(body)));
        when: "creating expectation"
            def expectation = expect().
                statusCode(506).
                body(Matchers.equalTo(body))
        and: "executing expectation"
            def response = expectation.when().options(url);
        then: "created response should be decorated"
            response instanceof ResponseDecorated
    }

    def "should return wrapped response during OPTIONS by URL called from expectation with parameters"() {
        given: "configured access point"
            def body = "<root>" +
                "<value>7</value>" +
                "</root>"
            def base = "http://localhost:${wire.port()}"
            def path = "/test/options/creature"
            def url = "$base$path"
            stubFor(WireMock.options(urlMatching("$path.*"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(506)
                .withHeader("Content-Type", "application/xml")
                .withBody(body)));
        when: "executing expectation"
            def response = given().
                param("x", "y").
                expect().
                statusCode(506).
                body(Matchers.equalTo(body)).
                when().
                options(url);
        then: "created response should be decorated"
            response instanceof ResponseDecorated
    }
}