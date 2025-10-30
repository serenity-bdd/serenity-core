package net.serenitybdd.rest.configuring

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.junit.WireMockRule
import io.restassured.http.ContentType
import io.restassured.specification.FilterableRequestSpecification
import net.serenity.test.utils.rules.TestCase
import net.serenitybdd.rest.decorators.ResponseDecorated
import net.serenitybdd.rest.rules.RestConfigurationAction
import net.serenitybdd.rest.rules.RestConfigurationRule
import net.thucydides.core.steps.BaseStepListener
import org.junit.Rule
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static net.serenitybdd.rest.HeaderNames.CONTENT_TYPE
import static net.serenitybdd.rest.SerenityRest.given
import static net.serenitybdd.rest.SerenityRest.reset

/**
 * User: YamStranger
 * Date: 4/01/16
 * Time: 9:57 AM
 */
class WhenConfiguringHeadersForRequest extends Specification {

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

    def "should be returned wrapped request specification after setting header"() {
        given: "request initialised"
            def request = (FilterableRequestSpecification) given();
        when: "updating content type and getting request specification"
            def requestAfterLog = request.header(CONTENT_TYPE.asString(), ContentType.JSON).request()
        then: "same request should be returned after setting content type"
            requestAfterLog == request
        and: "header should contains correct value"
            request.getHeaders().get(CONTENT_TYPE.asString()).getValue() == ContentType.JSON.toString()
    }

    def "should be returned wrapped request specification after setting header as map"() {
        given: "request initialised"
            def request = (FilterableRequestSpecification) given();
        when: "updating content type and getting request specification"
            def map = new HashMap();
            map.put(CONTENT_TYPE.asString(), ContentType.JSON)
            def requestAfterLog = request.headers(map).request()
        then: "same request should be returned after log operation"
            requestAfterLog == request
        and: "header should contains correct value"
            request.getHeaders().get(CONTENT_TYPE.asString()).getValue() == ContentType.JSON.toString()
    }


    def "should be returned wrapped response after setting header and executing request"() {
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
            def responseAfterExecutingRequest = request.header(CONTENT_TYPE.asString(), ContentType.XML).get(url)
        then: "wrapped response should be returned after executing request"
            responseAfterExecutingRequest instanceof ResponseDecorated
        and: "status code returned as expected"
            responseAfterExecutingRequest.then().statusCode(200)
        and: "header should contains correct value"
            request.getHeaders().get(CONTENT_TYPE.asString()).getValue().contains(ContentType.XML.toString())
    }
}
