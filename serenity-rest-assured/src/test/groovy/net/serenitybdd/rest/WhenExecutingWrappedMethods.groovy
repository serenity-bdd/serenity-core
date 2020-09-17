package net.serenitybdd.rest

import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import net.serenity.test.utils.rules.TestCase
import net.serenitybdd.core.rest.RestQuery
import net.thucydides.core.steps.BaseStepListener
import org.junit.Rule
import spock.lang.Ignore
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.matching
import static com.github.tomakehurst.wiremock.client.WireMock.post
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import static net.serenitybdd.rest.SerenityRest.rest

/**
 * User: YamStranger
 * Date: 11/29/15
 * Time: 5:58 PM
 */
class WhenExecutingWrappedMethods extends Specification {

    @Rule
    def WireMockRule wire = new WireMockRule(0);

    @Rule
    def TestCase<BaseStepListener> test = new TestCase({
        Mock(BaseStepListener)
    }.call());

    def Gson gson = new GsonBuilder().setPrettyPrinting().
        serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();


    def "should work properly after executing log method"() {
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
            def result = rest().given().contentType("application/json").body(body).log().all()
                .post(url).then()
        then: "The JSON request should be recorded in the test steps"
            1 * test.firstListener().recordRestQuery(*_) >> { RestQuery query ->
                assert "$query" == "POST $url"
                assert JsonConverter.formatted(query.content) == JsonConverter.formatted(body)
                assert query.contentType == "application/json"
            }
        and:
            result.statusCode(200)
    }

    def "should work properly after executing auth method"() {
        given:
            def JsonObject json = new JsonObject()
            json.addProperty("Name", "Ivanna")
            json.addProperty("City", "Earth")
            def body = gson.toJson(json)

            def base = "http://localhost:${wire.port()}"
            def path = "/test/resource/auth"
            def url = "$base$path"

            stubFor(post(urlEqualTo(path))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(body)));
        when:
            def result = rest().given().contentType("application/json").body(body).auth().none()
                .post(url).then()
        then: "The JSON request should be recorded in the test steps"
            1 * test.firstListener().recordRestQuery(*_) >> { RestQuery query ->
                assert "$query" == "POST $url"
                assert JsonConverter.formatted(query.content) == JsonConverter.formatted(body)
                assert query.contentType == "application/json"
            }
        and:
            result.statusCode(200)
    }

    @Ignore("Redirect is not implemented")
    def "should work properly after executing redirect method"() {
        given:
            def JsonObject json = new JsonObject()
            json.addProperty("Phone", 1789)
            json.addProperty("Color", "blue")
            def body = gson.toJson(json)

            def base = "http://localhost:${wire.port()}"
            def path = "/test/resource/redirection"
            def url = "$base$path"

            stubFor(post(urlEqualTo(path))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(body)));
        when:
            def result = rest().given().contentType("application/json").content(body)
                .redirects().follow(true)
                .post(url).then()
        then: "The JSON request should be recorded in the test steps"
            1 * test.firstListener().recordRestQuery(*_) >> { RestQuery query ->
                assert "$query" == "POST $url"
                assert JsonConverter.formatted(query.content) == JsonConverter.formatted(body)
                assert query.contentType == "application/json"
            }
        and:
            result.statusCode(200)
    }
}
