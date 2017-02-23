package net.serenitybdd.rest.configuring

import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.jayway.restassured.specification.FilterableRequestSpecification
import net.serenity.test.utils.rules.TestCase
import net.serenitybdd.rest.decorators.ResponseDecorated
import net.serenitybdd.rest.rules.RestConfigurationAction
import net.serenitybdd.rest.rules.RestConfigurationRule
import net.thucydides.core.steps.BaseStepListener
import net.thucydides.core.util.FileSystemUtils
import org.junit.Rule
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import static net.serenitybdd.rest.SerenityRest.given
import static net.serenitybdd.rest.SerenityRest.reset

/**
 * User: YamStranger
 * Date: 3/30/16
 * Time: 9:57 AM
 */
class WhenConfiguringAuthHTTPSRequestParameters extends Specification {

    @Rule
    def WireMockRule wire = new WireMockRule(wireMockConfig()
        .httpsPort(NextAvailablePort.number())
        .keystorePath(FileSystemUtils.getResourceAsFile("wiremock/keystore.jks").getAbsolutePath())
        .keystorePassword("serenitybdd"));

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

    def Gson gson = new GsonBuilder().setPrettyPrinting().
        serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();

    def "should be returned wrapped response after relaxedHTTPSValidation configuration and executing request"() {
        given: "rest with default config updated"
            def JsonObject json = new JsonObject()
            json.addProperty("Size", "Huge")
            json.addProperty("Type", "God")
            def body = gson.toJson(json)

            def base = "https://localhost:${wire.httpsPort()}"
            def path = "/secure/resource"
            def url = "$base$path"

            stubFor(get(urlMatching(path))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(body)));
            def request = (FilterableRequestSpecification) given();
        when: "changing authentication configuration and and executing request"
            def responseAfterExecutingRequest = request.relaxedHTTPSValidation().get(url)
        then: "wrapped response should be returned after executing request"
            responseAfterExecutingRequest instanceof ResponseDecorated
        and: "status code returned as expected"
            responseAfterExecutingRequest.then().statusCode(200)
    }
}