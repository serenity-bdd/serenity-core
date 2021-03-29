package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import serenitycore.net.serenitybdd.core.pages.ClickStrategy;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.ClickInteraction;

import static serenitycore.net.serenitybdd.core.pages.ClickStrategy.*;

abstract class ClickOnClickable implements ClickInteraction {

    private ClickStrategy clickStrategy = WAIT_UNTIL_PRESENT;

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

    public ClickStrategy getClickStrategy() {
        return clickStrategy;
    }
}
