package net.serenitybdd.rest.staging

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.jayway.restassured.RestAssured
import com.jayway.restassured.specification.RequestSender
import net.serenitybdd.rest.staging.decorators.RequestSpecificationDecorated
import net.serenitybdd.rest.staging.decorators.ResponseSpecificationDecorated
import net.serenitybdd.rest.staging.rules.RestConfigurationAction
import net.serenitybdd.rest.staging.rules.RestConfigurationRule
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
class WhenExecutingGetRequest extends Specification {

    @Rule
    def WireMockRule wire = new WireMockRule(0);

    @Rule
    def RestConfigurationRule rule = new RestConfigurationRule(new RestConfigurationAction() {
        @Override
        void apply() {
            reset()
        }
    },)

    def "should return wrapped request and response if they initialised separately"() {
        given: "initialised Request and Response and access point"
            def request = Mock(RequestSpecificationDecorated)
            def response = Mock(ResponseSpecificationDecorated)
            def body = "<root>" +
                "<value>1</value>" +
                "</root>"
            def base = "http://localhost:${wire.port()}"
            def path = "/test/levels"
            def url = "$base$path"
            stubFor(WireMock.get(urlMatching(path))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/xml")
                .withBody(body)));
        when: "creating new request and making request"
            def RequestSender sender = given(request, response)
            def generated = sender.get(url)
        then: "created response should be decorated"
            1 * request.get((String) _, (Object) _)
    }
}