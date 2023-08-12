package net.serenitybdd.rest

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import net.serenity.test.utils.rules.TestCase
import net.serenitybdd.model.rest.RestQuery
import net.serenitybdd.rest.utils.RestExecutionHelper
import net.serenitybdd.annotations.Step
import net.thucydides.model.domain.TestResult
import net.thucydides.core.steps.BaseStepListener
import net.thucydides.core.steps.StepFactory
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.rules.ExternalResource
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.nio.file.Files

import static net.serenitybdd.model.rest.RestMethod.GET
import static net.serenitybdd.rest.JsonConverter.formatted
import static net.serenitybdd.rest.SerenityRest.get
import static net.serenitybdd.rest.SerenityRest.given

/**
 * User: YamStranger
 * Date: 11/29/15
 * Time: 5:58 PM
 */
class WhenEnabledDryRunWithSerenityRest extends Specification {
    @Rule
    TestCase<BaseStepListener> test = new TestCase({
        Mock(BaseStepListener)
    }.call())

    File temporaryDirectory

    def setup() {
        temporaryDirectory = Files.createTempDirectory("tmp").toFile()
        temporaryDirectory.deleteOnExit()
    }

    @Rule
    ExternalResource resource = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            RestExecutionHelper.enableDryRunForClass(WhenEnabledDryRunWithSerenityRest.class)
            RestExecutionHelper.enableDryRunForClass(RestSteps.class)
        }

        @Override
        protected void after() {
            RestExecutionHelper.disableDryRunForClass(WhenEnabledDryRunWithSerenityRest.class)
            RestExecutionHelper.disableDryRunForClass(RestSteps.class)
        }
    }

    Gson gson = new GsonBuilder().setPrettyPrinting().
        serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create()

    def "Should record RestAssured get() method calls when enabled dry run"() {
        given:
        JsonObject json = new JsonObject()
            json.addProperty("Number", "9999")
            json.addProperty("Price", "100")
            def base = "http://localhost:5164"
            def path = "/test/number"
            def url = "$base$path"
        when:
            def result = get(url).then()
        then: "The JSON request should be recorded in the test steps"
            1 * test.firstListener().recordRestQuery(*_) >> { RestQuery query ->
                assert "$query" == "GET $url"
                assert query.method == GET
                assert "${query.path}" == url
                assert query.statusCode == 0
                assert formatted(query.responseBody) == ""
            }
        and:
            result.statusCode(200)
    }
    def "Should record RestAssured get() method calls with parameters when enabled dry run"() {
        given:
        JsonObject json = new JsonObject()
            json.addProperty("Exists", true)
            json.addProperty("label", "UI")
            json.addProperty("SomeValue","value")

            def base = "http://localhost:8654"
            def path = "/test/label"
            def url = "$base$path"
        when:
            def result = get("$url?status={status}", ["status": "available"]).then()
        then: "The JSON request should be recorded in the test steps"
            1 * test.firstListener().recordRestQuery(*_) >> { RestQuery query ->
                assert "$query" == "GET $url?status=available"
                assert query.method == GET
                assert query.statusCode == 0
                assert formatted(query.responseBody) == ""
            }
        and:
            result.statusCode(200)
    }

    def "Should record RestAssured get() method calls with parameter provided as a list when enabled dry run"() {
        given:
        JsonObject json = new JsonObject()
            json.addProperty("Weather", "rain")
            json.addProperty("temperature", "+2")
            json.addProperty("SomeValue","value")

            def base = "http://localhost:7777"
            def path = "/test/weather"
            def url = "$base$path"

        when:
            def result = get("$url?status={status}", "available").then()
        then: "The JSON request should be recorded in the test steps"
            1 * test.firstListener().recordRestQuery(*_) >> { RestQuery query ->
                assert "$query" == "GET $url?status=available"
                assert query.method == GET
                assert query.statusCode == 0
                assert formatted(query.responseBody) == ""
            }
        and:
            result.statusCode(200)
    }

    static class RestSteps {
        @Step
        def successfulGet(final String url) {
            given().get("$url/{id}", 1000).then().body("id", Matchers.equalTo(1000))
            given().get("$url/{id}", 1000).then().body(Matchers.equalTo("/pets/id"))
        }

        @Step
        def failingGet(final String url) {
            given().get("$url/{id}", 1000).then().body("Food", Matchers.equalTo(1001))
        }


        @Step
        def getById(final String url) {
            given().get("$url/{id}", 1000)
        }
    }

    def "should support failing assertions on response results when enabled dry run and return SUCCESS"() {
        given:
            def listener = new BaseStepListener(temporaryDirectory)
            test.register(listener)
            StepFactory factory = new StepFactory()
        def restSteps = factory.getSharedStepLibraryFor(RestSteps)

        JsonObject json = new JsonObject()
            json.addProperty("Food", "sushi")
            json.addProperty("size", "7")
            def base = "http://localhost:23424"
            def path = "/test/food/sushi"
            def url = "$base$path"
        when:
            restSteps.failingGet(url)
        then:
            def testSteps = listener.testOutcomes[0].testSteps
            testSteps[0].result == TestResult.SUCCESS
    }

    def "should support assertions on response results when enabled dry run"() {
        given:
            def listener = new BaseStepListener(temporaryDirectory)
            test.register(listener)
        JsonObject json = new JsonObject()
            json.addProperty("Sky", "Clear")
            json.addProperty("Time", "10:17AM")
            json.addProperty("SomeValue","value")

            def base = "http://localhost:232424"
            def path = "/test/photos/eidk398d"
            def url = "$base$path"
        when:
            def result = given().get(url).then().body("Sky", Matchers.equalTo("Clear"))
            test.finish()
        then: "The JSON request should be recorded in the test steps"
            listener.getTestOutcomes().size() > 0
            listener.getTestOutcomes().get(0).getFlattenedTestSteps().size() > 0
            def testSteps = listener.getTestOutcomes().get(0).getFlattenedTestSteps()
            "${testSteps.get(0).getDescription()}" == "GET $url"
            formatted(testSteps.get(0).getRestQuery().getResponseBody()) == ""
            testSteps.get(0).getRestQuery().getStatusCode() == 0
        and:
            result.statusCode(200)
    }
}
