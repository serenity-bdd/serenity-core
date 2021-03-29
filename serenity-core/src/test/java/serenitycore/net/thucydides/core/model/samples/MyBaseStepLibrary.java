package serenitycore.net.thucydides.core.model.samples;

import serenitymodel.net.thucydides.core.annotations.Step;
import serenitycore.net.thucydides.core.pages.Pages;
import serenitycore.net.thucydides.core.steps.ScenarioSteps;

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
