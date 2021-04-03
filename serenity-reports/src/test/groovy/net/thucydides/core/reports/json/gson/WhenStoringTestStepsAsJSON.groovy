package net.thucydides.core.reports.json.gson

import net.thucydides.core.model.TestResult
import net.thucydides.core.model.TestStep
import net.thucydides.core.util.MockEnvironmentVariables
import org.skyscreamer.jsonassert.JSONCompare
import org.skyscreamer.jsonassert.JSONCompareMode
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.ZoneId
import java.time.ZonedDateTime

class WhenStoringTestStepsAsJSON extends Specification {

    @Shared
    def environmentVars = new MockEnvironmentVariables();

    private static final ZonedDateTime FIRST_OF_JANUARY = ZonedDateTime.of(2013, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault())

    static TestStep simpleStep = TestStep.forStepCalled("some step").withResult(TestResult.SUCCESS)
            .startingAt(FIRST_OF_JANUARY)

    static String simpleStepJson = """
{
  "number" : 0,
  "description" : "some step",
  "duration" : 0,
  "result" : "SUCCESS",
  "startTime" : "${FIRST_OF_JANUARY.toString()}"
}
"""

    @Shared
    def converter = new GsonJSONConverter(environmentVars)

    @Unroll
    def "should generate an JSON report for a test step"() {
        expect:
        def renderedJson = converter.gson.toJson(testStep)
        JSONCompare.compareJSON(expectedJson, renderedJson, JSONCompareMode.LENIENT).passed();

        where:
        testStep    | expectedJson
        simpleStep  | simpleStepJson
    }


    def "should read a test step from a simple JSON string"() {
        expect:
        def step = converter.gson.fromJson(json,TestStep)
        step == expectedStep

        where:
        json            | expectedStep
        simpleStepJson  | simpleStep
    }

    def "should read and write a test step containing an error"() {
        given:
            TestStep stepWithError = TestStep.forStepCalled("some step").withResult(TestResult.ERROR)
                                           .startingAt(FIRST_OF_JANUARY);
            stepWithError.failedWith(new IllegalStateException("Oh crap!"))

        when:
            def renderedJson = converter.gson.toJson(stepWithError)
        and:
            def step = converter.gson.fromJson(renderedJson,TestStep)
        then:
            step.equals(stepWithError)
        and:
            step.getResult() == TestResult.ERROR
            step.getErrorMessage().contains "Oh crap!"
    }

    def "should read and write a test step containing a failure"() {
        given:
        TestStep failingStep = TestStep.forStepCalled("some step").withResult(TestResult.FAILURE)
                .startingAt(FIRST_OF_JANUARY);
        failingStep.failedWith(new AssertionError("Oh crap!"))

        when:
        def renderedJson = converter.gson.toJson(failingStep)
        and:
        def step = converter.gson.fromJson(renderedJson,TestStep)
        then:
        step.equals(failingStep)
        step.getResult() == TestResult.FAILURE
        step.getErrorMessage().contains "Oh crap!"
    }


    def "should read and write a test step containing a multi-line step"() {
        given:
        def description = """line 1
line 2
line 3
"""
        TestStep aStep = TestStep.forStepCalled(description).withResult(TestResult.FAILURE)
                .startingAt(FIRST_OF_JANUARY);

        when:
        def renderedJson = converter.gson.toJson(aStep)
        and:
        def step = converter.gson.fromJson(renderedJson,TestStep)
        then:
        step.description == description
    }

    def "should read and write a test step containing a Windows multi-line step"() {
        given:
        def description = "line 1\r\nline 2\r\nline 3"
        TestStep aStep = TestStep.forStepCalled(description).withResult(TestResult.FAILURE)
                .startingAt(FIRST_OF_JANUARY);

        when:
        def renderedJson = converter.gson.toJson(aStep)
        and:
        def step = converter.gson.fromJson(renderedJson,TestStep)
        then:
        step.description == description
    }


    def "should read and write a test step with children"() {
        given:
        TestStep parentStep = TestStep.forStepCalled("some step").withResult(TestResult.SUCCESS)
                .startingAt(FIRST_OF_JANUARY)
        parentStep.addChildStep(TestStep.forStepCalled("child step 1").withResult(TestResult.SUCCESS)
                .startingAt(FIRST_OF_JANUARY))
        parentStep.addChildStep(TestStep.forStepCalled("child step 2").withResult(TestResult.SUCCESS)
                .startingAt(FIRST_OF_JANUARY))

        when:
        def renderedJson = converter.gson.toJson(parentStep)
        and:
        def step = converter.gson.fromJson(renderedJson,TestStep)
        then:
        step.equals(parentStep)
        step.getChildren().size() == 2
    }
}