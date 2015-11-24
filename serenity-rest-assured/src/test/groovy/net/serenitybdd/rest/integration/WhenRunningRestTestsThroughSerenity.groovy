package net.serenitybdd.rest.integration

import com.jayway.restassured.http.ContentType
import net.serenitybdd.core.rest.RestQuery
import net.thucydides.core.annotations.Step
import net.thucydides.core.model.TestResult
import net.thucydides.core.steps.BaseStepListener
import net.thucydides.core.steps.StepEventBus
import net.thucydides.core.steps.StepFactory
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static com.jayway.restassured.RestAssured.given
import static net.serenitybdd.core.rest.RestMethod.*
import static net.serenitybdd.rest.SerenityRest.rest
import static net.serenitybdd.rest.SerenityRest.then
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.greaterThan;

class WhenRunningRestTestsThroughSerenity extends Specification {

    @Rule
    TemporaryFolder temporaryFolder

    File outputDirectory
    BaseStepListener stepListener

    def setup() {
        outputDirectory = temporaryFolder.newFolder()
        stepListener = new BaseStepListener(outputDirectory)
        StepEventBus.eventBus.registerListener(stepListener)

        seedTestData();
    }

    private void seedTestData() {
        def samplePet = """{"id": 1000, "name": "doggie", "photoUrls": [], "status": "available"}"""
        given().contentType(ContentType.JSON).body(samplePet).post("http://petstore.swagger.io/v2/pet")
    }

    def cleanup() {
        StepEventBus.eventBus.testFinished()
    }


    def "Should record RestAssured get() method calls"() {
        given:
            def mockListener = Mock(BaseStepListener)
            StepEventBus.eventBus.registerListener(mockListener)
            StepEventBus.eventBus.testStarted("rest")
        when:
            rest().get("http://petstore.swagger.io/v2/store/inventory")
        then: "The JSON request should be recorded in the test steps"
            1 * mockListener.recordRestQuery({ RestQuery query -> query.method == GET  && query.path == "http://petstore.swagger.io/v2/store/inventory"})
    }

    def "Should record RestAssured get() method calls with parameters"() {
        given:
        def mockListener = Mock(BaseStepListener)
        StepEventBus.eventBus.registerListener(mockListener)
        StepEventBus.eventBus.testStarted("rest")
        when:
        rest().get("http://petstore.swagger.io/v2/pet/findByStatus?status={status}",["status":"available"])
        then: "The JSON request should be recorded in the test steps"
        1 * mockListener.recordRestQuery({ RestQuery query -> query.toString() == "GET http://petstore.swagger.io/v2/pet/findByStatus?status=available"})
    }

    def "Should record RestAssured get() method calls with parameter provided as a list"() {
        given:
            def mockListener = Mock(BaseStepListener)
            StepEventBus.eventBus.registerListener(mockListener)
            StepEventBus.eventBus.testStarted("rest")
        when:
            rest().get("http://petstore.swagger.io/v2/pet/findByStatus?status={status}","available")
        then: "The JSON request should be recorded in the test steps"
            1 * mockListener.recordRestQuery({ RestQuery query -> query.toString() == "GET http://petstore.swagger.io/v2/pet/findByStatus?status=available"})
    }

    def "Should record RestAssured post() method calls"() {
        given:
            def mockListener = Mock(BaseStepListener)
            StepEventBus.eventBus.registerListener(mockListener)
            StepEventBus.eventBus.testStarted("rest")
        and:
            def samplePet = """{"id": 1001, "name": "doggie", "photoUrls": [], "status": "available"}"""
        when:
            rest().given().contentType("application/json").content(samplePet).post("http://petstore.swagger.io/v2/pet")
        then: "The JSON request should be recorded in the test steps"
            1 * mockListener.recordRestQuery({ RestQuery query -> query.toString() == "POST http://petstore.swagger.io/v2/pet" &&
                                                                  query.content == samplePet  &&
                                                                  query.contentType == "application/json"})
    }

    def "Should record RestAssured patch() method calls"() {
        given:
        def mockListener = Mock(BaseStepListener)
        StepEventBus.eventBus.registerListener(mockListener)
        StepEventBus.eventBus.testStarted("rest")
        and:
        def updateContent = """{"id": 1007, "name": "doggie", "status": "not_available"}"""
        when:
        rest().given().contentType("application/json").content(updateContent).patch("http://petstore.swagger.io/v2/pet/{id}","1007")

        then: "The JSON request should be recorded in the test steps"
        1 * mockListener.recordRestQuery({ RestQuery query -> query.toString() == "PATCH http://petstore.swagger.io/v2/pet/1007" &&
            query.content == updateContent  &&
            query.contentType == "application/json"})
    }

    def "Should record RestAssured put() method calls"() {
        given:
            def samplePet = """{"id": 1003, "name": "doggie", "photoUrls": [], "status": "available"}"""
            given().body(samplePet).post("http://petstore.swagger.io/v2/pet")
        and:
            def mockListener = Mock(BaseStepListener)
            StepEventBus.eventBus.registerListener(mockListener)
            StepEventBus.eventBus.testStarted("rest")
            def updatedPet = """{"id": 1003, "name": "fido", "photoUrls": [], "status": "available"}"""
        when:
            rest().given().contentType("application/json").content(updatedPet).put("http://petstore.swagger.io/v2/pet")
        then: "The JSON request should be recorded in the test steps"
            1 * mockListener.recordRestQuery({ RestQuery query -> query.toString() == "PUT http://petstore.swagger.io/v2/pet" &&
                query.content == updatedPet  &&
                query.contentType == "application/json"})
    }

    def "Should record RestAssured delete() method calls"() {
        given:
            def samplePet = """{"id": 1002, "name": "doggie", "photoUrls": [], "status": "available"}"""
            given().body(samplePet).post("http://petstore.swagger.io/v2/pet")
        and:
            def mockListener = Mock(BaseStepListener)
            StepEventBus.eventBus.registerListener(mockListener)
            StepEventBus.eventBus.testStarted("rest")
        when:
            rest().delete("http://petstore.swagger.io/v2/pet/{id}","1002")
        then: "The JSON request should be recorded in the test steps"
            1 * mockListener.recordRestQuery({ RestQuery query -> query.toString() == "DELETE http://petstore.swagger.io/v2/pet/1002"})
    }

    def "should record REST queries as steps"() {
        given:
            StepEventBus.eventBus.testStarted("rest")
        when:
            rest().get("http://petstore.swagger.io/v2/store/inventory")
        then: "The JSON request should be recorded in the test steps"
            def testSteps = stepListener.testOutcomes[0].flattenedTestSteps
            testSteps[0].description == "GET http://petstore.swagger.io/v2/store/inventory"
    }

    def "should support assertions on response results"() {
        given:
            StepEventBus.eventBus.testStarted("rest")
        when:
            rest().get("http://petstore.swagger.io/v2/pet/{id}", 1000).then().body("id", equalTo(1000));
        then: "The JSON request should be recorded in the test steps"
            def testSteps = stepListener.testOutcomes[0].flattenedTestSteps
            testSteps[0].description == "GET http://petstore.swagger.io/v2/pet/1000"
    }

    static class RestSteps {
        @Step
        def successfulGet() {
            rest().get("http://petstore.swagger.io/v2/pet/{id}", 1000).then().body("id", equalTo(1000));
            rest().get("http://petstore.swagger.io/v2/pet/{id}", 1000).then().body("/pets/id")
        }

        @Step
        def failingGet() {
            rest().get("http://petstore.swagger.io/v2/pet/{id}", 1000).then().body("id", equalTo(1001));
        }


        @Step
        def getPetById() {
            rest().get("http://petstore.swagger.io/v2/pet/{id}", 1000);
        }

        @Step
        def thenCheckOutcome() {
            then().body("id", equalTo(1000))
            then().body("id", greaterThan(0));
        }

        @Step
        def thenCheckWrongOutcome() {
            then().body("id", equalTo(0));
        }
    }

    def "should support failing assertions on response results"() {
        given:
            StepEventBus.eventBus.testStarted("rest")
            StepFactory factory = new StepFactory();
            def restSteps = factory.getStepLibraryFor(RestSteps)
        when:
            restSteps.failingGet()
        then:
            def testSteps = stepListener.testOutcomes[0].testSteps
            testSteps[0].result == TestResult.FAILURE
    }


    def "should support sequences of operations in different steps"() {
        given:
            StepEventBus.eventBus.testStarted("rest")
            StepFactory factory = new StepFactory();
            def restSteps = factory.getStepLibraryFor(RestSteps)
        when:
            restSteps.getPetById()
            restSteps.thenCheckOutcome()
        then:
            def testSteps = stepListener.testOutcomes[0].testSteps
            testSteps[0].result == TestResult.SUCCESS
            testSteps[1].result == TestResult.SUCCESS
    }


    def "should report failures in subsequent steps"() {
        given:
            StepEventBus.eventBus.testStarted("rest")
            StepFactory factory = new StepFactory();
            def restSteps = factory.getStepLibraryFor(RestSteps)
        when:
            restSteps.getPetById()
            restSteps.thenCheckWrongOutcome()
        then:
            def testSteps = stepListener.testOutcomes[0].testSteps
            testSteps[0].result == TestResult.SUCCESS
            testSteps[1].result == TestResult.FAILURE
    }

}
