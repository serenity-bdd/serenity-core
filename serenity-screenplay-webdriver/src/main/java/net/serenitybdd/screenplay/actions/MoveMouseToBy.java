package net.serenitybdd.screenplay.actions;

import net.serenitybdd.model.collect.NewList;
import net.serenitybdd.screenplay.Actor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class MoveMouseToBy extends ChainablePerformable {
    private final List<By> locators;

    public MoveMouseToBy(By... locators) {
        this.locators = NewList.copyOf(locators);
    }

    public <T extends Actor> void performAs(T actor) {
        WebElement element = WebElementLocator.forLocators(locators).andActor(actor);
        addActionAtStart(actions -> actions.moveToElement(element));
        performSubsequentActionsAs(actor);
    }
}
