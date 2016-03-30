package net.serenitybdd.rest.staging.requests

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.jayway.restassured.RestAssured
import com.jayway.restassured.specification.RequestSender
import com.jayway.restassured.specification.RequestSpecification
import com.jayway.restassured.specification.ResponseSpecification
import net.serenitybdd.rest.staging.decorators.ResponseDecorated
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
 * Date: 3/30/16
 * Time: 9:57 AM
 */
class WhenExecutingDeleteRequest extends Specification {

    @Rule
    def WireMockRule wire = new WireMockRule(0);

    @Rule
    def RestConfigurationRule rule = new RestConfigurationRule(new RestConfigurationAction() {
        @Override
        void apply() {
            reset()
        }
    },)

    def "should use wrapped request and response if they initialised separately"() {
        given: "initialised Request and Response and access point"
            def request = (RequestSpecification) RestAssured.given();
            def response = (ResponseSpecification)RestAssured.given().response();
            def body = "<root>" +
                "<value>1</value>" +
                "</root>"
            def base = "http://localhost:${wire.port()}"
            def path = "/test/delete/levels"
            def url = "$base$path"
            stubFor(WireMock.delete(urlMatching(path))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/xml")
                .withBody(body)));
        when: "creating new request and making request"
            def RequestSender sender = given(request, response)
            def generated = sender.delete(url)
        then: "created response should be decorated"
            generated instanceof ResponseDecorated
    }

    def "should return wrapped response during DELETE by URL called from request"() {
        given: "configured access point"
            def body = "<root>" +
                "<value>2</value>" +
                "</root>"
            def base = "http://localhost:${wire.port()}"
            def path = "/test/delete/keyboard"
            def url = "$base$path"
            stubFor(WireMock.delete(urlMatching(path))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(256)
                .withHeader("Content-Type", "application/xml")
                .withBody(body)));
        when: "creating new request and making delete request"
            def response = given().delete(url)
        then: "created response should be decorated"
            response instanceof ResponseDecorated
        and: "returned status should be correct"
            response.then().statusCode(256)
    }

    def "should return wrapped response during DELETE by URL with MAP parameters called from request"() {
        given: "configured access point"
            def body = "<root>" +
                "<value>3</value>" +
                "</root>"
            def base = "http://localhost:${wire.port()}"
            def path = "/test/delete/house"
            def url = "$base$path"
            stubFor(WireMock.delete(urlMatching("$path.*"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(700)
                .withHeader("Content-Type", "application/xml")
                .withBody(body)));
        when: "creating new request and making delete request"
            def response = given().delete("$url?status={status}", ["status": "available"])
        then: "created response should be decorated"
            response instanceof ResponseDecorated
        and: "returned status should be correct"
            response.then().statusCode(700)
    }

    def "should return wrapped response during DELETE by URL with array parameters called from request"() {
        given: "configured access point"
            def body = "<root>" +
                "<value>4</value>" +
                "</root>"
            def base = "http://localhost:${wire.port()}"
            def path = "/test/delete/pet"
            def url = "$base$path"
            stubFor(WireMock.delete(urlMatching("$path.*"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(845)
                .withHeader("Content-Type", "application/xml")
                .withBody(body)));
        when: "creating new request and making delete request"
            def response = given().delete("$url?status={status}", "available")
        then: "created response should be decorated"
            response instanceof ResponseDecorated
        and: "returned status should be correct"
            response.then().statusCode(845)
    }

    def "should return wrapped response during DELETE by URL called from response"() {
        given: "configured access point"
            def body = "<root>" +
                "<value>5</value>" +
                "</root>"
            def base = "http://localhost:${wire.port()}"
            def path = "/test/delete/child"
            def url = "$base$path"
            stubFor(WireMock.delete(urlMatching(path))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(945)
                .withHeader("Content-Type", "application/xml")
                .withBody(body)));
        when: "creating new request and making delete request"
            def response = expect().delete(url)
        then: "created response should be decorated"
            response instanceof ResponseDecorated
        and: "returned status should be correct"
            response.then().statusCode(945)
    }

    def "should return wrapped response during DELETE by URL with MAP parameters called from response"() {
        given: "configured access point"
            def body = "<root>" +
                "<value>6</value>" +
                "</root>"
            def base = "http://localhost:${wire.port()}"
            def path = "/test/delete/book"
            def url = "$base$path"
            stubFor(WireMock.delete(urlMatching("$path.*"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(203)
                .withHeader("Content-Type", "application/xml")
                .withBody(body)));
        when: "creating new request and making delete request"
            def response = expect().delete("$url?status={status}", ["status": "available"])
        then: "created response should be decorated"
            response instanceof ResponseDecorated
        and: "returned status should be correct"
            response.then().statusCode(203)
    }

    def "should return wrapped response during DELETE by URL with array parameters called from response"() {
        given: "configured access point"
            def body = "<root>" +
                "<value>7</value>" +
                "</root>"
            def base = "http://localhost:${wire.port()}"
            def path = "/test/delete/creature"
            def url = "$base$path"
            stubFor(WireMock.delete(urlMatching("$path.*"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(506)
                .withHeader("Content-Type", "application/xml")
                .withBody(body)));
        when: "creating new request and making delete request"
            def response = expect().delete("$url?status={status}", "available")
        then: "created response should be decorated"
            response instanceof ResponseDecorated
        and: "returned status should be correct"
            response.then().statusCode(506)
    }
}