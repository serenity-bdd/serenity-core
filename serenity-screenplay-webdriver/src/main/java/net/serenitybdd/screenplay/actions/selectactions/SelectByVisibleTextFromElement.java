package net.serenitybdd.screenplay.actions.selectactions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.annotations.Step;

public class SelectByVisibleTextFromElement implements Interaction {
    private WebElementFacade element;
    private String[] options;
    private String selectedOptions;

    public SelectByVisibleTextFromElement() {}

    public SelectByVisibleTextFromElement(WebElementFacade element, String... options) {
        this.element = element;
        this.options = options;
        this.selectedOptions = String.join(",", options);
    }

    @Step("{0} selects #selectedOptions")
    public <T extends Actor> void performAs(T theUser) {
        for(String option : options) {
            element.selectByVisibleText(option);
        }
    }
}
