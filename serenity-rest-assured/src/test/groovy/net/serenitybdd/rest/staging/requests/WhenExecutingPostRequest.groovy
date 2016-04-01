package net.serenitybdd.rest.staging.requests

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.jayway.restassured.RestAssured
import com.jayway.restassured.specification.RequestSender
import com.jayway.restassured.specification.RequestSpecification
import com.jayway.restassured.specification.ResponseSpecification
import net.serenitybdd.core.rest.RestQuery
import net.serenitybdd.rest.staging.decorators.ResponseDecorated
import net.serenitybdd.rest.staging.rules.RestConfigurationAction
import net.serenitybdd.rest.staging.rules.RestConfigurationRule
import org.junit.Rule
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.post
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
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
class WhenExecutingPostRequest extends Specification {

    @Rule
    def WireMockRule wire = new WireMockRule(0);

    @Rule
    def RestConfigurationRule rule = new RestConfigurationRule(new RestConfigurationAction() {
        @Override
        void apply() {
            reset()
        }
    },)

    def Gson gson = new GsonBuilder().setPrettyPrinting().
        serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();

    def "should use wrapped request and response if they initialised separately"() {
        given: "initialised Request and Response and access point"
            def request = (RequestSpecification) RestAssured.given();
            def response = (ResponseSpecification)RestAssured.given().response();
            def body = "<root>" +
                "<value>1</value>" +
                "</root>"
            def base = "http://localhost:${wire.port()}"
            def path = "/test/post/levels"
            def url = "$base$path"
            stubFor(WireMock.post(urlMatching(path))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/xml")
                .withBody(body)));
        when: "creating new request and making request"
            def RequestSender sender = given(request, response)
            def generated = sender.post(url)
        then: "created response should be decorated"
            generated instanceof ResponseDecorated
    }

    def "should return wrapped response during POST by URL called from request"() {
        given: "configured access point"
            def body = "<root>" +
                "<value>2</value>" +
                "</root>"
            def base = "http://localhost:${wire.port()}"
            def path = "/test/post/keyboard"
            def url = "$base$path"
            stubFor(WireMock.post(urlMatching(path))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(256)
                .withHeader("Content-Type", "application/xml")
                .withBody(body)));
        when: "creating new request and making post request"
            def response = given().post(url)
        then: "created response should be decorated"
            response instanceof ResponseDecorated
        and: "returned status should be correct"
            response.then().statusCode(256)
    }

    def "should return wrapped response during POST by URL with MAP parameters called from request"() {
        given: "configured access point"
            def body = "<root>" +
                "<value>3</value>" +
                "</root>"
            def base = "http://localhost:${wire.port()}"
            def path = "/test/post/house"
            def url = "$base$path"
            stubFor(WireMock.post(urlMatching("$path.*"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(700)
                .withHeader("Content-Type", "application/xml")
                .withBody(body)));
        when: "creating new request and making post request"
            def response = given().post("$url?status={status}", ["status": "available"])
        then: "created response should be decorated"
            response instanceof ResponseDecorated
        and: "returned status should be correct"
            response.then().statusCode(700)
    }

    def "should return wrapped response during POST by URL with array parameters called from request"() {
        given: "configured access point"
            def body = "<root>" +
                "<value>4</value>" +
                "</root>"
            def base = "http://localhost:${wire.port()}"
            def path = "/test/post/pet"
            def url = "$base$path"
            stubFor(WireMock.post(urlMatching("$path.*"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(845)
                .withHeader("Content-Type", "application/xml")
                .withBody(body)));
        when: "creating new request and making post request"
            def response = given().post("$url?status={status}", "available")
        then: "created response should be decorated"
            response instanceof ResponseDecorated
        and: "returned status should be correct"
            response.then().statusCode(845)
    }

    def "should return wrapped response during POST by URL called from response"() {
        given: "configured access point"
            def body = "<root>" +
                "<value>5</value>" +
                "</root>"
            def base = "http://localhost:${wire.port()}"
            def path = "/test/post/child"
            def url = "$base$path"
            stubFor(WireMock.post(urlMatching(path))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(945)
                .withHeader("Content-Type", "application/xml")
                .withBody(body)));
        when: "creating new request and making post request"
            def response = expect().post(url)
        then: "created response should be decorated"
            response instanceof ResponseDecorated
        and: "returned status should be correct"
            response.then().statusCode(945)
    }

    def "should return wrapped response during POST by URL with MAP parameters called from response"() {
        given: "configured access point"
            def body = "<root>" +
                "<value>6</value>" +
                "</root>"
            def base = "http://localhost:${wire.port()}"
            def path = "/test/post/book"
            def url = "$base$path"
            stubFor(WireMock.post(urlMatching("$path.*"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(203)
                .withHeader("Content-Type", "application/xml")
                .withBody(body)));
        when: "creating new request and making post request"
            def response = expect().post("$url?status={status}", ["status": "available"])
        then: "created response should be decorated"
            response instanceof ResponseDecorated
        and: "returned status should be correct"
            response.then().statusCode(203)
    }

    def "should return wrapped response during POST by URL with array parameters called from response"() {
        given: "configured access point"
            def body = "<root>" +
                "<value>7</value>" +
                "</root>"
            def base = "http://localhost:${wire.port()}"
            def path = "/test/post/creature"
            def url = "$base$path"
            stubFor(WireMock.post(urlMatching("$path.*"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(506)
                .withHeader("Content-Type", "application/xml")
                .withBody(body)));
        when: "creating new request and making post request"
            def response = expect().post("$url?status={status}", "available")
        then: "created response should be decorated"
            response instanceof ResponseDecorated
        and: "returned status should be correct"
            response.then().statusCode(506)
    }

    def "should work properly after executing setting content type and body"() {
        given:
            def JsonObject json = new JsonObject()
            json.addProperty("Title", "King")
            json.addProperty("Salary", "100")
            def body = gson.toJson(json)

            def base = "http://localhost:${wire.port()}"
            def path = "/test/resource"
            def url = "$base$path"

            stubFor(post(urlEqualTo(path))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(body)));
        when:
            def result = given().contentType("application/json").content(body).post(url)
        then: "created response should be decorated"
            result instanceof ResponseDecorated
        and: "returned status should be correct"
            result.then().statusCode(200)
    }
}