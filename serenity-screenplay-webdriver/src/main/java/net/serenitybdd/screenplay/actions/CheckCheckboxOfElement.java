package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;

public class CheckCheckboxOfElement extends ClickOnClickable {

    private final WebElementFacade element;
    private final boolean expectedToBeChecked;

    public CheckCheckboxOfElement(WebElementFacade element, boolean expectedToBeChecked) {
        this.element = element;
        this.expectedToBeChecked = expectedToBeChecked;
    }

    @Override
    @Step("{0} sets value of checkbox #element to #expectedToBeChecked")
    public <T extends Actor> void performAs(T actor) {
        boolean isSelected = element.isSelected();
        if(isSelected != expectedToBeChecked) actor.attemptsTo(Click.on(element));
    }
}
