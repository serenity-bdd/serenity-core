package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.ClickStrategy;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.ClickInteraction;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.targets.Target;
import net.thucydides.core.annotations.Step;
import org.openqa.selenium.WebElement;

import static net.serenitybdd.core.pages.ClickStrategy.*;

abstract class ClickOnClickable implements Interaction, Resolvable, ClickInteraction {

    private ClickStrategy clickStrategy = WAIT_UNTIL_PRESENT;

    abstract protected String getName();

    @Step("{0} clicks on #name")
    public <T extends Actor> void performAs(T theUser) {
        resolveFor(theUser).click(clickStrategy);
    }

    @Override
    public ClickInteraction afterWaitingUntilEnabled() {
        clickStrategy = WAIT_UNTIL_ENABLED;
        return this;
    }

    @Override
    public ClickInteraction afterWaitingUntilPresent() {
        clickStrategy = WAIT_UNTIL_PRESENT;
        return this;
    }

    @Override
    public ClickInteraction withNoDelay() {
        clickStrategy = IMMEDIATE;
        return this;
    }
}
