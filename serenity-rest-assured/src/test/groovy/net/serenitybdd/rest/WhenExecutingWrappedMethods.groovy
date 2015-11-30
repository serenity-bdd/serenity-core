package net.serenitybdd.rest

import net.serenitybdd.core.rest.RestQuery
import net.thucydides.core.steps.BaseStepListener
import net.thucydides.core.steps.StepEventBus
import spock.lang.Ignore
import spock.lang.Specification

import static net.serenitybdd.rest.SerenityRest.rest

/**
 * User: YamStranger
 * Date: 11/29/15
 * Time: 5:58 PM
 */
class WhenExecutingWrappedMethods extends Specification {

    def BaseStepListener listener

    def setup() {
        listener = Mock(BaseStepListener)
        StepEventBus.eventBus.registerListener(listener)
    }

    def cleanup() {
        StepEventBus.eventBus.testFinished()
    }

    def "should work properly after executing log method"() {
        given:
            StepEventBus.eventBus.testStarted("rest")
        and:
            def samplePet = """{"id": 1001, "name": "doggie", "photoUrls": [], "status": "available"}"""
        when:
            rest().given().contentType("application/json").content(samplePet).log().all()
            .post("http://petstore.swagger.io/v2/pet")
        then: "The JSON request should be recorded in the test steps"
            1 * listener.recordRestQuery({ RestQuery query ->
                query.toString() == "POST http://petstore.swagger.io/v2/pet" &&
                    query.content == samplePet &&
                    query.contentType == "application/json"
            })
    }

    def "should work properly after executing auth method"() {
        given:
            StepEventBus.eventBus.testStarted("rest")
        and:
            def samplePet = """{"id": 1001, "name": "doggie", "photoUrls": [], "status": "available"}"""
        when:
            rest().given().contentType("application/json").content(samplePet).auth().none()
                .post("http://petstore.swagger.io/v2/pet")
        then: "The JSON request should be recorded in the test steps"
            1 * listener.recordRestQuery({ RestQuery query ->
                query.toString() == "POST http://petstore.swagger.io/v2/pet" &&
                    query.content == samplePet &&
                    query.contentType == "application/json"
            })
    }

    @Ignore
    def "should work properly after executing redirect method"() {
        given:
            def mockListener = Mock(BaseStepListener)
            StepEventBus.eventBus.registerListener(mockListener)
            StepEventBus.eventBus.testStarted("rest")
        and:
            def samplePet = """{"id": 1001, "name": "doggie", "photoUrls": [], "status": "available"}"""
        when:
            rest().given().contentType("application/json").content(samplePet).redirects().follow(true)
                .post("http://petstore.swagger.io/v2/pet")
            then: "The JSON request should be recorded in the test steps"
            1 * listener.recordRestQuery({ RestQuery query ->
                query.toString() == "POST http://petstore.swagger.io/v2/pet" &&
                    query.content == samplePet &&
                    query.contentType == "application/json"
            })
    }
}