package net.serenitybdd.rest

import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import net.serenity.test.utils.rules.TestCase
import net.serenitybdd.model.rest.RestQuery
import net.thucydides.core.steps.BaseStepListener
import net.thucydides.model.util.FileSystemUtils
import org.junit.Rule
import spock.lang.Ignore
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.matching
import static com.github.tomakehurst.wiremock.client.WireMock.post
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import static net.serenitybdd.rest.SerenityRest.rest
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

/**
 * User: YamStranger
 * Date: 11/11/15
 * Time: 2:09 AM
 */
class WhenRunningHttpsRestTestsThroughSerenity extends Specification {

    @Rule
    WireMockRule wire = new WireMockRule(wireMockConfig()
        .httpsPort(0)
        .keystorePath(FileSystemUtils.getResourceAsFile("wiremock/keystore.jks").getAbsolutePath())
        .keystorePassword("serenitybdd"))

    @Rule
    TestCase<BaseStepListener> test = new TestCase({
        Mock(BaseStepListener)
    }.call())

    Gson gson = new GsonBuilder().setPrettyPrinting().
        serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create()

    @Ignore("HTTPS does not work with rest")
    def "Should record RestAssured get() method calls when HTTPS is used"() {
        given:
        JsonObject json = new JsonObject()
            json.addProperty("Size", "Huge")
            json.addProperty("Type", "God")
            def body = gson.toJson(json)

            def base = "https://localhost:${wire.port()}"
            def path = "/secure/resource"
            def url = "$base$path"

            stubFor(post(urlEqualTo(path))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(body)))
        when:
            def result = rest().given().relaxedHTTPSValidation()
                .contentType("application/json").content(body)
                .post(url).then()
        then: "The JSON request should be recorded in the test steps"
            1 * test.firstListener().recordRestQuery(*_) >> { RestQuery query ->
                assert "$query" == "POST $url"
                assert query.content == body
                assert query.statusCode == 200
                assert query.contentType == "application/json"
            }
        and:
            result.statusCode(200)
    }
}
