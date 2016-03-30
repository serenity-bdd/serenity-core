package net.serenitybdd.rest.staging

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.junit.WireMockRule
import net.serenitybdd.rest.staging.decorators.ResponseDecorated
import net.serenitybdd.rest.staging.rules.RestConfigurationAction
import net.serenitybdd.rest.staging.rules.RestConfigurationRule
import org.hamcrest.Matchers
import org.junit.Rule
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static net.serenitybdd.rest.staging.SerenityRest.reset
import static net.serenitybdd.rest.staging.SerenityRest.given
import static net.serenitybdd.rest.staging.SerenityRest.expect

/**
 * User: YamStranger
 * Date: 3/14/16
 * Time: 9:57 AM
 */
class WhenValidatingResponseFromGetOperation extends Specification {

    @Rule
    def WireMockRule wire = new WireMockRule(0);

    @Rule
    def RestConfigurationRule rule = new RestConfigurationRule(new RestConfigurationAction() {
        @Override
        void apply() {
            reset()
        }
    },)

    def "should be possible to validate status code"() {
        given: "configured access point"
            def body = "<root>" +
                "<value>7</value>" +
                "</root>"
            def base = "http://localhost:${wire.port()}"
            def path = "/test/creature"
            def url = "$base$path"
            stubFor(WireMock.get(urlMatching("$path.*"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(506)
                .withHeader("Content-Type", "application/xml")
                .withBody(body)));
        when: "creating new request and making get request"
            def response = given().get(url)
        then: "created response should be decorated"
            response instanceof ResponseDecorated
        and: "returned status should be correct"
            response.then().statusCode(506)
    }

    def "should be possible to validate response body"() {
        given: "configured access point"
            def body = "<root>" +
                "<value>7</value>" +
                "</root>"
            def base = "http://localhost:${wire.port()}"
            def path = "/test/creature"
            def url = "$base$path"
            stubFor(WireMock.get(urlMatching("$path.*"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(506)
                .withHeader("Content-Type", "application/xml")
                .withBody(body)));
        when: "creating expectation"
            def expectation = expect().
                statusCode(506).
                body(Matchers.equalTo(body))
        then: "validation of expectation should be correct"
            expectation.when().get(url);
    }
}