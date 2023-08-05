package net.serenitybdd.cucumber.bootstrap

import net.serenitybdd.core.Serenity
import net.serenitybdd.annotations.Step
import net.thucydides.core.annotations.Steps
import net.thucydides.core.pages.PageObject
import spock.lang.Specification

class WhenInstantiatingStepLibraries extends Specification {


    static class SampleStepDefinitions {

        @Steps
        SampleSteps stepLibrary

    }

    static class SampleSteps {

        public SamplePageObject pageObject;

        @Step
        public void aSimpleStep() { }

        @Step
        public void anotherSimpleStep() {}
    }

    static class SamplePageObject extends PageObject {}

    def "the Thucydides bootstrap classes can be used to instantiate step library variables"() {
        given: "a step definition class containing a step library"
        def sampleStepDefinitions = new SampleStepDefinitions()
        when: "we inject the step libraries"
        Serenity.initialize(sampleStepDefinitions)
        then: "the step library field should be initialized"
        sampleStepDefinitions.stepLibrary != null
    }

    def "the Thucydides bootstrap classes can be used to instantiate nested page objects variables"() {
        given: "a step definition class containing a step library"
        def sampleStepDefinitions = new SampleStepDefinitions()
        when: "we inject the step libraries"
        Serenity.initialize(sampleStepDefinitions)
        then: "the page object field in the step library should be initialized"
        sampleStepDefinitions.stepLibrary.pageObject != null
    }
}
