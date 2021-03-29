package some.other.place;

import serenitycore.net.thucydides.core.pages.Pages;
import serenitycore.net.thucydides.core.steps.ScenarioSteps;

public class BaseScenarioInSomeOtherPackage extends ScenarioSteps{

    public BaseScenarioInSomeOtherPackage(Pages pages) {
        super(pages);
    }

    public String returnFoo() {
        return "proportionOf";

    }

}
