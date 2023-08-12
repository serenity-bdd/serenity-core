package net.thucydides.model.domain

import net.serenitybdd.model.rest.RestMethod
import net.serenitybdd.model.rest.RestQuery
import spock.lang.Specification

class WhenRecordingRestQueriesInTestOutcomes extends Specification {


    def "Test outcomes have no rest data by default"() {
        given:
            def testOutcome = TestOutcome.forTestInStory("aTest", Story.called("a story"))
        when:
            def restStep = TestStep.forStepCalled("rest query").withResult(TestResult.SUCCESS)
        and:
            testOutcome.recordStep(restStep)
        then:
            !testOutcome.hasRestQueries()
    }

    def "REST query data is recorded with a step"() {
        given:
            def testOutcome = TestOutcome.forTestInStory("aTest", Story.called("a story"))
        when:
            def restStep = TestStep.forStepCalled("rest query").withResult(TestResult.SUCCESS)
            restStep.recordRestQuery(RestQuery.withMethod(RestMethod.GET).andPath("/somepath"));
        and:
            testOutcome.recordStep(restStep)
        then:
            testOutcome.hasRestQueries()
    }

    def "REST query data can be in nested steps"() {
        given:
            def testOutcome = TestOutcome.forTestInStory("aTest", Story.called("a story"))
            def parentStep = TestStep.forStepCalled("parent step").withResult(TestResult.SUCCESS)
            def childStep =TestStep.forStepCalled("rest query").withResult(TestResult.SUCCESS)
            parentStep.addChildStep(childStep)
        when:
            childStep.recordRestQuery(RestQuery.withMethod(RestMethod.GET).andPath("/somepath"));
        and:
            testOutcome.recordStep(parentStep)
        then:
            testOutcome.hasRestQueries()
    }


}
