package net.serenitybdd.rest

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import io.restassured.response.ValidatableResponse
import net.serenity.test.utils.rules.TestCase
import net.serenitybdd.annotations.Step
import net.thucydides.model.domain.TestResult
import net.thucydides.core.steps.BaseStepListener
import net.thucydides.core.steps.StepFactory
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.nio.file.Files
import java.util.function.Consumer

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static net.serenitybdd.rest.JsonConverter.formatted
import static net.serenitybdd.rest.SerenityRest.*

/**
 * User: YamStranger
 * Date: 11/29/15
 * Time: 5:58 PM
 */
class WhenUsingAssertionsWithSerenityRest extends Specification {

    @Rule
    WireMockRule wire = new WireMockRule(0)

    @Rule
    TestCase<BaseStepListener> test = new TestCase({
        Mock(BaseStepListener)
    }.call())

    File temporaryDirectory

    def setup() {
        temporaryDirectory = Files.createTempDirectory("tmp").toFile()
        temporaryDirectory.deleteOnExit()
    }

    Gson gson = new GsonBuilder().setPrettyPrinting().
        serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create()

    static class RestSteps {
        @Step
        def successfulGet(final String url) {
            rest().get("$url/{id}", 1000).then().body("id", Matchers.equalTo(1000))
            given().get("$url/{id}", 1000).then().body(Matchers.equalTo("/pets/id"))
        }

        @Step
        def failingGet(final String url) {
            given().get("$url/{id}", 1000).then().body("Food", Matchers.equalTo(1001))
        }

        Consumer<ValidatableResponse> foodIsHealthy = new Consumer<ValidatableResponse>() {
            @Override
            void accept(ValidatableResponse response) {
                response.body("Food", Matchers.equalTo(1000))
            }
        }

        Consumer<ValidatableResponse> foodIsTasty = new Consumer<ValidatableResponse>() {
            @Override
            void accept(ValidatableResponse response) {
                response.body("Food", Matchers.equalTo(1001))
            }
        }

        Consumer<ValidatableResponse> foodIsSushi = new Consumer<ValidatableResponse>() {
            @Override
            void accept(ValidatableResponse response) {
                response.body("Food", Matchers.equalTo("sushi"))
            }
        }

        @Step
        def getWithEnsure(final String url) {
            given().get("$url/{id}", 1000)

            Ensure.that("food is 1001", foodIsTasty)
        }

        @Step
        def getWithMultipleEnsures(final String url) {
            given().get("$url/{id}", 1000)

            Ensure.that("food is healthy", foodIsHealthy).andThat("food is sushi", foodIsSushi)
        }

        @Step
        def getWithMultipleFailingEnsures(final String url) {
            given().get("$url/{id}", 1000)

            Ensure.that("food is healthy", foodIsHealthy).andThat("food is tasty", foodIsTasty)
        }


        @Step
        def getWithMultiplePassingEnsures(final String url) {
            given().get("$url/{id}", 1000)

            Ensure.that("food is sushi", foodIsSushi).andThat("food is sushi", foodIsSushi)
        }

        @Step
        def getById(final String url) {
            rest().get("$url/{id}", 1000)
        }

        @Step
        def thenCheckOutcome() {
            then().body("Id", Matchers.anything())
        }

        @Step
        def thenCheckWrongOutcome() {
            then().body("id", Matchers.equalTo(0))
        }
    }

    def "should support failing assertions on response results"() {
        given:
            def listener = new BaseStepListener(temporaryDirectory)
            test.register(listener)
            StepFactory factory = new StepFactory()
        def restSteps = factory.getSharedStepLibraryFor(RestSteps)

        JsonObject json = new JsonObject()
            json.addProperty("Food", "sushi")
            json.addProperty("size", "7")
            def body = gson.toJson(json)

            def base = "http://localhost:${wire.port()}"
            def path = "/test/food/sushi"
            def url = "$base$path"

            stubFor(WireMock.get(urlPathMatching("$path.*"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(body)))
        when:
            restSteps.failingGet(url)
        then:
            def testSteps = listener.testOutcomes[0].testSteps
            testSteps[0].result == TestResult.FAILURE
    }


    def "should support failing assertions with Ensure.that() on response results"() {
        given:
        def listener = new BaseStepListener(temporaryDirectory)
        test.register(listener)
        StepFactory factory = new StepFactory()
        def restSteps = factory.getSharedStepLibraryFor(RestSteps)

        JsonObject json = new JsonObject()
        json.addProperty("Food", "sushi")
        json.addProperty("size", "7")
        def body = gson.toJson(json)

        def base = "http://localhost:${wire.port()}"
        def path = "/test/food/sushi"
        def url = "$base$path"

        stubFor(WireMock.get(urlPathMatching("$path.*"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(body)))
        when:
        restSteps.getWithEnsure(url)
        then:
        def testSteps = listener.testOutcomes[0].testSteps
        testSteps[0].children.size() == 2
        testSteps[0].result == TestResult.FAILURE
    }

    def "should support passing assertions with chained Ensure.that() on response results"() {
        given:
        def listener = new BaseStepListener(temporaryDirectory)
        test.register(listener)
        StepFactory factory = new StepFactory()
        def restSteps = factory.getSharedStepLibraryFor(RestSteps)

        JsonObject json = new JsonObject()
        json.addProperty("Food", "sushi")
        json.addProperty("size", "7")
        def body = gson.toJson(json)

        def base = "http://localhost:${wire.port()}"
        def path = "/test/food/sushi"
        def url = "$base$path"

        stubFor(WireMock.get(urlPathMatching("$path.*"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(body)))
        when:
        restSteps.getWithMultiplePassingEnsures(url)
        then:
        def testSteps = listener.testOutcomes[0].testSteps
        listener.testOutcomes[0].testSteps[0].children.size() == 3
        testSteps[0].children[1].result == TestResult.SUCCESS
        testSteps[0].children[2].result == TestResult.SUCCESS
    }

    def "should support failing assertions with chained Ensure.that() on response results"() {
        given:
        def listener = new BaseStepListener(temporaryDirectory)
        test.register(listener)
        StepFactory factory = new StepFactory()
        def restSteps = factory.getSharedStepLibraryFor(RestSteps)

        JsonObject json = new JsonObject()
        json.addProperty("Food", "sushi")
        json.addProperty("size", "7")
        def body = gson.toJson(json)

        def base = "http://localhost:${wire.port()}"
        def path = "/test/food/sushi"
        def url = "$base$path"

        stubFor(WireMock.get(urlPathMatching("$path.*"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(body)))
        when:
        restSteps.getWithMultipleEnsures(url)
        then:
        def testSteps = listener.testOutcomes[0].testSteps
        listener.testOutcomes[0].testSteps[0].children.size() == 3
        testSteps[0].children[1].result == TestResult.FAILURE
        testSteps[0].children[2].result == TestResult.SUCCESS

    }

    def "should support assertions on response results"() {
        given:
            def listener = new BaseStepListener(temporaryDirectory)
            test.register(listener)
        JsonObject json = new JsonObject()
            json.addProperty("Sky", "Clear")
            json.addProperty("Time", "10:17AM")
            def body = gson.toJson(json)
            json.addProperty("SomeValue","value")
            def requestBody = gson.toJson(json)

            def base = "http://localhost:${wire.port()}"
            def path = "/test/photos/eidk398d"
            def url = "$base$path"

            stubFor(WireMock.get(urlPathMatching(path))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(body)))
        when:
            def result = given().get(url).then().body("Sky", Matchers.equalTo("Clear"))
            test.finish()
        then: "The JSON request should be recorded in the test steps"
            listener.getTestOutcomes().size() > 0
            listener.getTestOutcomes().get(0).getFlattenedTestSteps().size() > 0
            def testSteps = listener.getTestOutcomes().get(0).getFlattenedTestSteps()
            "${testSteps.get(0).getDescription()}" == "GET $url"
            formatted("${testSteps.get(0).getRestQuery().getResponseBody()}") == formatted(body)
        and:
            result.statusCode(200)
    }

    def "should support sequences of operations in different steps"() {
        given:
            def listener = new BaseStepListener(temporaryDirectory)
            test.register(listener)
            StepFactory factory = new StepFactory()
        def restSteps = factory.getSharedStepLibraryFor(RestSteps)
        JsonObject json = new JsonObject()
            json.addProperty("Object", "Groot")
            json.addProperty("id", 7)
            def body = gson.toJson(json)

            def base = "http://localhost:${wire.port()}"
            def path = "/test/objects"
            def url = "$base$path"

            stubFor(get(urlPathMatching("path.*"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(body)))
        when:
            restSteps.getById(url)
            restSteps.thenCheckOutcome()
        then:
            def testSteps = listener.testOutcomes[0].testSteps
            testSteps[0].result == TestResult.SUCCESS
            testSteps[1].result == TestResult.SUCCESS
    }
}
