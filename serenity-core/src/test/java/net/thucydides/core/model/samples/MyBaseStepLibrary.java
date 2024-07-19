package net.thucydides.core.model.samples;

import net.serenitybdd.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

public class MyBaseStepLibrary extends ScenarioSteps {


        MyBaseStepLibrary(Pages pages) {
            super(pages);
        }

        @Step
        public void aStep() {}

        @Step
        protected boolean aProtectedStep() { return true; }

        protected boolean aProtectedMethod() { return true; }
    }
