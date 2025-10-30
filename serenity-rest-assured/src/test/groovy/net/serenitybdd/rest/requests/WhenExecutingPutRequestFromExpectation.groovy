package net.serenitybdd.rest.requests

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.junit.WireMockRule
import net.serenity.test.utils.rules.TestCase
import net.serenitybdd.rest.decorators.ResponseDecorated
import net.serenitybdd.rest.rules.RestConfigurationAction
import net.serenitybdd.rest.rules.RestConfigurationRule
import net.thucydides.core.steps.BaseStepListener
import org.hamcrest.Matchers
import org.junit.Rule
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static net.serenitybdd.rest.SerenityRest.*

/**
 * User: YamStranger
 * Date: 3/30/16
 * Time: 9:57 AM
 */
class WhenExecutingPutRequestFromExpectation extends Specification {

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

    def "should return wrapped response during PUT by URL called from expectation"() {
        given: "configured access point"
            def body = "<root>" +
                "<value>7</value>" +
                "</root>"
            def base = "http://localhost:${wire.port()}"
            def path = "/test/put/creature"
            def url = "$base$path"
            stubFor(WireMock.put(urlMatching("$path.*"))
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
            def response = expectation.when().put(url);
        then: "created response should be decorated"
            response instanceof ResponseDecorated
    }

    def "should return wrapped response during PUT by URL called from expectation with parameters"() {
        given: "configured access point"
            def body = "<root>" +
                "<value>7</value>" +
                "</root>"
            def base = "http://localhost:${wire.port()}"
            def path = "/test/put/creature"
            def url = "$base$path"
            stubFor(WireMock.put(urlMatching("$path.*"))
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
                put(url);
        then: "created response should be decorated"
            response instanceof ResponseDecorated
    }
}
