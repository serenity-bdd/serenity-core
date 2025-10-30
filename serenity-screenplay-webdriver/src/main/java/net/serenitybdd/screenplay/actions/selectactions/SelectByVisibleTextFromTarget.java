package net.serenitybdd.screenplay.actions.selectactions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.targets.Target;

public class SelectByVisibleTextFromTarget implements Interaction {
    private Target target;
    private String[] options;
    private String selectedOptions;

    public SelectByVisibleTextFromTarget() {}

    public SelectByVisibleTextFromTarget(Target target, String... options) {
        this.target = target;
        this.options = options;
        this.selectedOptions = String.join(",", options);
    }

    @Step("{0} selects #selectedOptions on #target")
    public <T extends Actor> void performAs(T theUser) {
        WebElementFacade dropdown = target.resolveFor(theUser);
        for(String option : options) {
            dropdown.selectByVisibleText(option);
        }
    }


}
