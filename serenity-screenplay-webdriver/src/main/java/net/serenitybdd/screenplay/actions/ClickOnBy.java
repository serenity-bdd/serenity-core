package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.ClickStrategy;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.ClickInteraction;
import net.thucydides.core.annotations.Step;
import org.openqa.selenium.By;

import java.util.List;

import static net.serenitybdd.core.pages.ClickStrategy.*;

public class ClickOnBy extends ByAction implements ClickInteraction {

    private ClickStrategy clickStrategy = WAIT_UNTIL_PRESENT;

    @Step("{0} clicks on #locators")
    public <T extends Actor> void performAs(T theUser) {
        resolveFor(theUser).click(clickStrategy);
    }

    public ClickOnBy(By... locators) {
        super(locators);
    }

    public ClickOnBy(List<By> locators) {
        super(locators.toArray(new By[]{}));
    }

    @Override
    public ClickOnBy afterWaitingUntilEnabled() {
        clickStrategy = WAIT_UNTIL_ENABLED;
        return this;
    }

    @Override
    public ClickOnBy afterWaitingUntilPresent() {
        clickStrategy = WAIT_UNTIL_PRESENT;
        return this;
    }

    @Override
    public ClickInteraction withNoDelay() {
        clickStrategy = IMMEDIATE;
        return this;
    }
}
