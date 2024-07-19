package net.thucydides.model.domain

import spock.lang.Specification

class WhenNumberingSteps extends Specification {

    def "a step can have a number"() {
        given:
            def step = new TestStep()
        when:
            int nextCount = step.renumberFrom(1)
        then:
            step.number == 1
        and:
            nextCount == 2
    }

    def "renumbering a step with children will also renumber the children"() {
        given:
            def step = new TestStep()
        and:
            step.addChildStep(new TestStep())
            step.addChildStep(new TestStep())
        when:
            int nextCount = step.renumberFrom(1)
        then:
            step.number == 1
        and:
            step.children[0].number == 2
            step.children[1].number == 3
        and:
            nextCount == 4
    }

    def "renumbering a step with children will also renumber the children of the children"() {
        given:
            def step = new TestStep()
        and:
            step.addChildStep(new TestStep())
            step.children[0].addChildStep(new TestStep())
            step.children[0].addChildStep(new TestStep())
            step.addChildStep(new TestStep())
        when:
            int nextCount = step.renumberFrom(1)
        then:
            step.number == 1
        and:
            step.children[0].number == 2
            step.children[0].children[0].number == 3
            step.children[0].children[1].number == 4
            step.children[1].number == 5
        and:
            nextCount == 6
    }

    def "should renumber test steps automatically when they are added to a test outcome"() {
        given:
            def outcome = new TestOutcome("some_method")
        when:
            outcome.recordStep(new TestStep("some step"))
                   .recordStep(new TestStep("some step"))
                   .recordStep(new TestStep("some step"))
        then:
            outcome.testSteps.collect {it.number} == [1,2,3]
    }

    def "should renumber test steps automatically when they are added to a test outcome in a list"() {
        given:
            def outcome = new TestOutcome("some_method")
        when:
            outcome.recordSteps([new TestStep("some step"), new TestStep("some step"), new TestStep("some step")])
        then:
            outcome.testSteps.collect {it.number} == [1,2,3]
    }

    def "should renumber child test steps automatically when they are added to a test outcome"() {
        given:
            def outcome = new TestOutcome("some_method")
        when:
            outcome.recordStep(new TestStep("some step").addChildStep(new TestStep("child step"))
                                                        .addChildStep(new TestStep("test step")))
                .recordStep(new TestStep("some step"))
                .recordStep(new TestStep("some step"))
        then:
            outcome.testSteps.collect {it.number} == [1,4,5]
        and:
            outcome.testSteps[0].children.collect {it.number} == [2,3]

    }

}
