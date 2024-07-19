package some.other.place;

import net.serenitybdd.annotations.Step;
import net.thucydides.core.pages.Pages;

public class StepsInSomeOtherPlace extends BaseScenarioInSomeOtherPackage {

    public StepsInSomeOtherPlace(final Pages pages) {
        super(pages);
    }

    @Step
    public void step_one() {}

    @Step
    public void step_two() {}

    @Step
    public void step_that_fails() {
        throw new AssertionError("Step failure");
    }
    
    @Step
    public String step_that_returns_foo() {
        return returnFoo();
    }
}
