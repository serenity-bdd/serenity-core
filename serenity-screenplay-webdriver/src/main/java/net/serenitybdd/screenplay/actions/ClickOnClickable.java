package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.ClickStrategy;

import static net.serenitybdd.core.pages.ClickStrategy.*;

abstract class ClickOnClickable implements ClickInteraction {

    protected ClickStrategy clickStrategy = WAIT_UNTIL_PRESENT;

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

    public ClickInteraction withStrategy(ClickStrategy clickStrategy) {
        this.clickStrategy = clickStrategy;
        return this;
    }

}
