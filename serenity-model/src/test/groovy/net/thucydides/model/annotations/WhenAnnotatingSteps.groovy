package net.thucydides.model.annotations

import net.serenitybdd.annotations.Step
import net.serenitybdd.annotations.TestAnnotations
import spock.lang.Specification

class WhenAnnotatingSteps extends Specification {


    def setup() {
    }

    class AnnotatedStepLibrary {
        @Step
        public void normalStep() {}

        @Step(callNestedMethods = false)
        public void noNestedCallsStep() {}

    }

    def "should not skip nested methods by default"() {
        when:
            def normalStepMethod = AnnotatedStepLibrary.getMethod("normalStep")
        then:
            !TestAnnotations.shouldSkipNested(normalStepMethod)
    }

    def "should skip nested methods if configured to do so"() {
        when:
            def normalStepMethod = AnnotatedStepLibrary.getMethod("noNestedCallsStep")
        then:
            TestAnnotations.shouldSkipNested(normalStepMethod)
    }

}
