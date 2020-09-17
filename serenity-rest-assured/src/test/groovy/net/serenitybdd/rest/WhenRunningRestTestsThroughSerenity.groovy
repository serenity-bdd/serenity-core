package net.serenitybdd.rest

import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import net.serenity.test.utils.rules.TestCase
import net.serenitybdd.core.rest.RestQuery
import net.thucydides.core.annotations.Step
import net.thucydides.core.model.TestResult
import net.thucydides.core.steps.BaseStepListener
import net.thucydides.core.steps.StepFactory
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.delete
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.matching
import static com.github.tomakehurst.wiremock.client.WireMock.patch
import static com.github.tomakehurst.wiremock.client.WireMock.post
import static com.github.tomakehurst.wiremock.client.WireMock.put
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching
import static net.serenitybdd.core.rest.RestMethod.DELETE
import static net.serenitybdd.core.rest.RestMethod.GET
import static net.serenitybdd.core.rest.RestMethod.PATCH
import static net.serenitybdd.core.rest.RestMethod.POST
import static net.serenitybdd.core.rest.RestMethod.PUT
import static net.serenitybdd.rest.SerenityRest.rest
import static net.serenitybdd.rest.SerenityRest.then

class WhenRunningRestTestsThroughSerenity extends Specification {
    @Rule
    def WireMockRule wire = new WireMockRule(0);

    @Rule
    def TestCase<BaseStepListener> test = new TestCase({
        Mock(BaseStepListener)
    }.call());

    def Gson gson = new GsonBuilder().setPrettyPrinting().
        serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();

    @Rule
    TemporaryFolder temporaryFolder

    def "Should record RestAssured get() method calls"() {
        given:
            def JsonObject json = new JsonObject()
            json.addProperty("Number", "9999")
            json.addProperty("Price", "100")
            def body = gson.toJson(json)

            def base = "http://localhost:${wire.port()}"
            def path = "/test/number"
            def url = "$base$path"

            stubFor(get(urlEqualTo(path))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(body)));
        when:
            def result = rest().get(url).then()
        then: "The JSON request should be recorded in the test steps"
            1 * test.firstListener().recordRestQuery(*_) >> { RestQuery query ->
                assert "$query" == "GET $url"
                assert query.method == GET
                assert "${query.path}" == url
                assert query.statusCode == 200
            }
        and:
            result.statusCode(200)
    }

    def "Should record RestAssured get() method calls with parameters"() {
        given:
            def JsonObject json = new JsonObject()
            json.addProperty("Exists", true)
            json.addProperty("label", "UI")
            def body = gson.toJson(json)

            def base = "http://localhost:${wire.port()}"
            def path = "/test/label"
            def url = "$base$path"

            stubFor(get(urlPathMatching("$path.*"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(body)));
        when:
            def result = rest().get("$url?status={status}", ["status": "available"]).then()
        then: "The JSON request should be recorded in the test steps"
            1 * test.firstListener().recordRestQuery(*_) >> { RestQuery query ->
                assert "$query" == "GET $url?status=available"
                assert query.method == GET
                assert query.statusCode == 200
            }
        and:
            result.statusCode(200)
    }

    def "Should record RestAssured get() method calls with parameter provided as a list"() {
        given:
            def JsonObject json = new JsonObject()
            json.addProperty("Weather", "rain")
            json.addProperty("temperature", "+2")
            def body = gson.toJson(json)

            def base = "http://localhost:${wire.port()}"
            def path = "/test/weather"
            def url = "$base$path"

            stubFor(get(urlPathMatching("$path.*"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(body)));
        when:
            def result = rest().get("$url?status={status}", "available").then()
        then: "The JSON request should be recorded in the test steps"
            1 * test.firstListener().recordRestQuery(*_) >> { RestQuery query ->
                assert "$query" == "GET $url?status=available"
                assert query.method == GET
                assert query.statusCode == 200
            }
        and:
            result.statusCode(200)
    }

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
            def result = rest().given().contentType("application/json").body(body).post(url).then()
        then: "The JSON request should be recorded in the test steps"
            1 * test.firstListener().recordRestQuery(*_) >> { RestQuery query ->
                assert "$query" == "POST $url"
                assert query.method == POST
                assert query.statusCode == 200
                assert query.contentType == "application/json"
                assert JsonConverter.formatted(query.content) == JsonConverter.formatted(body)

            }
        and:
            result.statusCode(200)
    }

    def "Should record RestAssured patch() method calls"() {
        given:
            def JsonObject json = new JsonObject()
            json.addProperty("Bird", "calibri")
            json.addProperty("Price", "10")
            def body = gson.toJson(json)

            def base = "http://localhost:${wire.port()}"
            def path = "/test/bird/calibri"
            def url = "$base$path"

            stubFor(patch(urlPathMatching("$path.*"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(body)));
        when:
            def result = rest().given().contentType("application/json").body(body).patch(url).then()
        then: "The JSON request should be recorded in the test steps"
            1 * test.firstListener().recordRestQuery(*_) >> { RestQuery query ->
                assert "$query" == "PATCH $url"
                assert query.method == PATCH
                assert query.statusCode == 200
                assert query.contentType == "application/json"
                assert JsonConverter.formatted(query.content) == JsonConverter.formatted(body)

            }
        and:
            result.statusCode(200)
    }

    def "Should record RestAssured put() method calls"() {
        given:
            def JsonObject json = new JsonObject()
            json.addProperty("Check", "12309-234-7")
            json.addProperty("amount", "15894")
            def body = gson.toJson(json)

            def base = "http://localhost:${wire.port()}"
            def path = "/test/check/17"
            def url = "$base$path"

            stubFor(put(urlPathMatching(path))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(body)));
        when:
            def result = rest().given().contentType("application/json").body(body).put(url).then()
        then: "The JSON request should be recorded in the test steps"
            1 * test.firstListener().recordRestQuery(*_) >> { RestQuery query ->
                assert "$query" == "PUT $url"
                assert query.method == PUT
                assert query.statusCode == 200
                assert query.contentType == "application/json"
                assert JsonConverter.formatted(query.content) == JsonConverter.formatted(body)

            }
        and:
            result.statusCode(200)
    }

    def "Should record RestAssured delete() method calls"() {
        given:
            def JsonObject json = new JsonObject()
            json.addProperty("Memory", "123cidk-3dkdi3")
            json.addProperty("Length", "3 days")
            def body = gson.toJson(json)

            def base = "http://localhost:${wire.port()}"
            def path = "/test/memory/42"
            def url = "$base$path"

            stubFor(delete(urlPathMatching(path))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(body)));
        when:
            def result = rest().given().delete(url).then()
        then: "The JSON request should be recorded in the test steps"
            1 * test.firstListener().recordRestQuery(*_) >> { RestQuery query ->
                assert "$query" == "DELETE $url"
                assert query.method == DELETE
                assert query.statusCode == 200

            }
        and:
            result.statusCode(200)
    }

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

            stubFor(get(urlPathMatching(path))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(body)));
        when:
            def result = rest().given().get(url).then()
            test.finish()
        then: "The JSON request should be recorded in the test steps"
            listener.getTestOutcomes().size() > 0
            listener.getTestOutcomes().get(0).getFlattenedTestSteps().size() > 0
            def testSteps = listener.getTestOutcomes().get(0).getFlattenedTestSteps()
            "${testSteps.get(0).getDescription()}" == "GET $url"
        and:
            result.statusCode(200)
    }

    def "should support assertions on response results"() {
        given:
            def listener = new BaseStepListener(temporaryFolder.newFolder())
            test.register(listener)
            def JsonObject json = new JsonObject()
            json.addProperty("Sky", "Clear")
            json.addProperty("Time", "10:17AM")
            def body = gson.toJson(json)

            def base = "http://localhost:${wire.port()}"
            def path = "/test/photos/eidk398d"
            def url = "$base$path"

            stubFor(get(urlPathMatching(path))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(body)));
        when:
            def result = rest().given().get(url).then().body("Sky", Matchers.equalTo("Clear"))
            test.finish()
        then: "The JSON request should be recorded in the test steps"
            listener.getTestOutcomes().size() > 0
            listener.getTestOutcomes().get(0).getFlattenedTestSteps().size() > 0
            def testSteps = listener.getTestOutcomes().get(0).getFlattenedTestSteps()
            "${testSteps.get(0).getDescription()}" == "GET $url"
        and:
            result.statusCode(200)
    }


    static class RestSteps {
        @Step
        def successfulGet(final String url) {
            rest().get("$url/{id}", 1000).then().body("id", Matchers.equalTo(1000));
            rest().get("$url/{id}", 1000).then().body(Matchers.equalTo("/pets/id"))
        }

        @Step
        def failingGet(final String url) {
            rest().get("$url/{id}", 1000).then().body("Food", Matchers.equalTo(1001));
        }


        @Step
        def getById(final String url) {
            rest().get("$url/{id}", 1000);
        }

        @Step
        def thenCheckOutcome() {
            then().body("Id", Matchers.anything())
        }

        @Step
        def thenCheckWrongOutcome() {
            then().body("id", Matchers.equalTo(0));
        }
    }

    def "should support failing assertions on response results"() {
        given:
            def listener = new BaseStepListener(temporaryFolder.newFolder())
            test.register(listener)
            StepFactory factory = new StepFactory();
            def restSteps = factory.getSharedStepLibraryFor(RestSteps)

            def JsonObject json = new JsonObject()
            json.addProperty("Food", "sushi")
            json.addProperty("size", "7")
            def body = gson.toJson(json)

            def base = "http://localhost:${wire.port()}"
            def path = "/test/food/sushi"
            def url = "$base$path"

            stubFor(get(urlPathMatching("$path.*"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(body)));
        when:
            restSteps.failingGet(url)
        then:
            def testSteps = listener.testOutcomes[0].testSteps
            testSteps[0].result == TestResult.FAILURE
    }

    def "should support sequences of operations in different steps"() {
        given:
            def listener = new BaseStepListener(temporaryFolder.newFolder())
            test.register(listener)
            StepFactory factory = new StepFactory();
            def restSteps = factory.getSharedStepLibraryFor(RestSteps)
            def JsonObject json = new JsonObject()
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
                .withBody(body)));
        when:
            restSteps.getById(url)
            restSteps.thenCheckOutcome()
        then:
            def testSteps = listener.testOutcomes[0].testSteps
            testSteps[0].result == TestResult.SUCCESS
            testSteps[1].result == TestResult.SUCCESS
    }

    def "should report failures in subsequent steps"() {
        given:
            def listener = new BaseStepListener(temporaryFolder.newFolder())
            test.register(listener)
            StepFactory factory = new StepFactory();
            def restSteps = factory.getSharedStepLibraryFor(RestSteps)
            def JsonObject json = new JsonObject()
            json.addProperty("Food", "rice")
            json.addProperty("Id", "88")
            def body = gson.toJson(json)

            def base = "http://localhost:${wire.port()}"
            def path = "/test/food"
            def url = "$base$path"

            stubFor(get(urlPathMatching("path.*"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(body)));
        when:
            restSteps.getById(url)
            restSteps.thenCheckWrongOutcome()
        then:
            def testSteps = listener.testOutcomes[0].testSteps
            testSteps[0].result == TestResult.SUCCESS
            testSteps[1].result == TestResult.FAILURE
    }
}
