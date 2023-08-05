package net.thucydides.junit.sampletests.thucydidestests

import net.serenitybdd.annotations.Step
import net.thucydides.core.pages.Pages
import net.thucydides.core.steps.ScenarioSteps


class StepLibrary extends ScenarioSteps {

    StepLibrary(Pages pages) {

        super(pages)

    }

    @Step

    def step1() {

        println "step1"

    }

    @Step

    def step2() {

        println "step2"

    }

    @Step

    def step3() {

        println "step3"

    }

    @Step

    def failingStep() { throw new AssertionError("something didn't work")}

}
