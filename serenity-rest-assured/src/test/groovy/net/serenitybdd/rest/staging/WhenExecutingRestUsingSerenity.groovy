package net.serenitybdd.rest.staging

import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import net.serenity.test.utils.rules.TestCase
import net.serenitybdd.core.rest.RestQuery
import net.thucydides.core.steps.BaseStepListener
import org.junit.Rule
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.post
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching
import static net.serenitybdd.core.rest.RestMethod.POST
import static net.serenitybdd.rest.staging.SerenityRest.*
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.matching
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor
import static net.serenitybdd.rest.staging.JsonConverter.*;

/**
 * User: YamStranger
 * Date: 11/29/15
 * Time: 5:58 PM
 */
class WhenExecutingRestUsingSerenity extends Specification {

    @Rule
    def WireMockRule wire = new WireMockRule(0);

    @Rule
    def TestCase<BaseStepListener> test = new TestCase({
        Mock(BaseStepListener);
    }.call());

    def Gson gson = new GsonBuilder().setPrettyPrinting().
        serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();

    def "Should record RestAssured post() method calls"() {
        given:
            def JsonObject json = new JsonObject()
            json.addProperty("Transport", "train")
            json.addProperty("arriving", "10min")
            def body = gson.toJson(json)

            def base = "http://localhost:${wire.port()}"
            def path = "/test/transport"
            def url = "$base$path"

            stubFor(post(urlPathMatching("$path.*"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(body)));
        when:
            def result = given().contentType("application/json").content(body).post(url).then()
        then: "The JSON request should be recorded in the test steps"
            1 * test.firstListener().recordRestQuery(*_) >> { RestQuery query ->
                assert "$query" == "POST $url"
                assert query.method == POST
                assert query.statusCode == 200
                assert query.contentType == "application/json"
                assert formatted(query.content) == formatted(body)
            }
        and:
            result.statusCode(200)
    }
}