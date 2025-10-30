package net.serenitybdd.rest

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import net.serenity.test.utils.rules.TestCase
import net.serenitybdd.model.rest.RestQuery
import net.thucydides.core.steps.BaseStepListener
import org.junit.Rule
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static net.serenitybdd.model.rest.RestMethod.PATCH
import static net.serenitybdd.rest.DecomposedContentType.APPLICATION_JSON
import static net.serenitybdd.rest.JsonConverter.formatted
import static net.serenitybdd.rest.SerenityRest.given


/**
 * User: YamStranger
 * Date: 11/29/15
 * Time: 5:58 PM
 */
class WhenRecordPatchRequestsWithSerenityRest extends Specification {

    @Rule
    WireMockRule wire = new WireMockRule(0)

    @Rule
    TestCase<BaseStepListener> test = new TestCase({
        Mock(BaseStepListener)
    }.call())

    Gson gson = new GsonBuilder().setPrettyPrinting().
        serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create()

    def "Should record RestAssured patch() method calls"() {
        given:
        JsonObject json = new JsonObject()
            json.addProperty("Number", "9999")
            json.addProperty("Price", "100")
            def body = gson.toJson(json)
            json.addProperty("SomeValue","value")
            def requestBody = gson.toJson(json)

            def base = "http://localhost:${wire.port()}"
            def path = "/test/number"
            def url = "$base$path"

            stubFor(WireMock.patch(urlEqualTo(path))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "$APPLICATION_JSON")
                .withBody(body)))
        when:
            def result = given().contentType("$APPLICATION_JSON").body(requestBody).patch(url).then()
        then: "The JSON request should be recorded in the test steps"
            1 * test.firstListener().recordRestQuery(*_) >> { RestQuery query ->
                assert "$query" == "PATCH $url"
                assert query.method == PATCH
                assert "${query.path}" == url
                assert query.statusCode == 200
                assert formatted(query.responseBody) == formatted(body)
                assert formatted(query.content) == formatted(requestBody)
            }
        and:
            result.statusCode(200)
    }

    def "Should record RestAssured patch() method calls with parameters"() {
        given:
        JsonObject json = new JsonObject()
            json.addProperty("Exists", true)
            json.addProperty("label", "UI")
            def body = gson.toJson(json)
            json.addProperty("SomeValue","value")
            def requestBody = gson.toJson(json)

            def base = "http://localhost:${wire.port()}"
            def path = "/test/label"
            def url = "$base$path"

            stubFor(WireMock.patch(urlPathMatching("$path.*"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "$APPLICATION_JSON")
                .withBody(body)))
        when:
            def result = given().contentType("$APPLICATION_JSON").body(requestBody).patch("$url?status={status}", ["status": "available"]).then()
        then: "The JSON request should be recorded in the test steps"
            1 * test.firstListener().recordRestQuery(*_) >> { RestQuery query ->
                assert "$query" == "PATCH $url?status=available"
                assert query.method == PATCH
                assert query.statusCode == 200
                assert formatted(query.responseBody) == formatted(body)
                assert formatted(query.content) == formatted(requestBody)
            }
        and:
            result.statusCode(200)
    }

    def "Should record RestAssured patch() method calls with parameter provided as a list"() {
        given:
        JsonObject json = new JsonObject()
            json.addProperty("Weather", "rain")
            json.addProperty("temperature", "+2")
            def body = gson.toJson(json)
            json.addProperty("SomeValue","value")
            def requestBody = gson.toJson(json)

            def base = "http://localhost:${wire.port()}"
            def path = "/test/weather"
            def url = "$base$path"

            stubFor(WireMock.patch(urlPathMatching("$path.*"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "$APPLICATION_JSON")
                .withBody(body)))
        when:
            def result = given().contentType("$APPLICATION_JSON").body(requestBody).patch("$url?status={status}", "available").then()
        then: "The JSON request should be recorded in the test steps"
            1 * test.firstListener().recordRestQuery(*_) >> { RestQuery query ->
                assert "$query" == "PATCH $url?status=available"
                assert query.method == PATCH
                assert query.statusCode == 200
                assert formatted(query.responseBody) == formatted(body)
                assert formatted(query.content) == formatted(requestBody)
            }
        and:
            result.statusCode(200)
    }
}
