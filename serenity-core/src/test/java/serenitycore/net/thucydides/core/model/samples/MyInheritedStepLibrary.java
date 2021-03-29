package serenitycore.net.thucydides.core.model.samples;

import serenitymodel.net.thucydides.core.annotations.Step;
import serenitycore.net.thucydides.core.pages.Pages;

public class MyInheritedStepLibrary extends MyBaseStepLibrary {


    MyInheritedStepLibrary(Pages pages) {
        super(pages);
    }

    @Step
    public boolean aStepWithAProtectedMethod() {
        return aProtectedMethod();
    }

    @Step
    public boolean anotherStep() {
        return aProtectedStep();
    }
}