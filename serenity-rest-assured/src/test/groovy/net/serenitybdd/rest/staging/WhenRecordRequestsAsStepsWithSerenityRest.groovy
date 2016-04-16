package net.serenitybdd.rest.staging

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import net.serenity.test.utils.rules.TestCase
import net.thucydides.core.annotations.Step
import net.thucydides.core.model.TestResult
import net.thucydides.core.steps.BaseStepListener
import net.thucydides.core.steps.StepFactory
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching
import static net.serenitybdd.core.rest.RestMethod.DELETE
import static net.serenitybdd.core.rest.RestMethod.GET
import static net.serenitybdd.core.rest.RestMethod.PATCH
import static net.serenitybdd.core.rest.RestMethod.POST
import static net.serenitybdd.core.rest.RestMethod.PUT
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
class WhenRecordRequestsAsStepsWithSerenityRest extends Specification {

    @Rule
    def WireMockRule wire = new WireMockRule(0);

    @Rule
    def TestCase<BaseStepListener> test = new TestCase({
        Mock(BaseStepListener);
    }.call());

    @Rule
    TemporaryFolder temporaryFolder

    def Gson gson = new GsonBuilder().setPrettyPrinting().
        serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();

    def "should record REST queries as steps"() {
        given:
            def listener = new BaseStepListener(temporaryFolder.newFolder())
            test.register(listener)
            def JsonObject json = new JsonObject()
            json.addProperty("Record", "John Lennon")
            json.addProperty("Song", "one")
            def body = gson.toJson(json)

            def base = "http://localhost:${wire.port()}"
            def path = "/test/records/142132"
            def url = "$base$path"

            stubFor(WireMock.get(urlPathMatching(path))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(body)));
        when:
            def result = given().get(url).then()
            test.finish()
        then: "The JSON request should be recorded in the test steps"
            listener.getTestOutcomes().size() > 0
            listener.getTestOutcomes().get(0).getFlattenedTestSteps().size() > 0
            def testSteps = listener.getTestOutcomes().get(0).getFlattenedTestSteps()
            "${testSteps.get(0).getDescription()}" == "GET $url"
        and:
            result.statusCode(200)
    }
}