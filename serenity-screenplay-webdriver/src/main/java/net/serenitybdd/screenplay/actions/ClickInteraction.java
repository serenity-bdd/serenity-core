package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.ClickStrategy;
import net.serenitybdd.screenplay.Performable;

public interface ClickInteraction extends Performable {
    /**
     * Wait until the element is present and enabled before clicking
     */
    ClickInteraction afterWaitingUntilEnabled();

    /**
     * Wait until the element is present before clicking (default behaviour)
     */
    ClickInteraction afterWaitingUntilPresent();

    /**
     * Click immediately, do not check whether the element is present first.
     */
    ClickInteraction withNoDelay();

    /**
     * Specify the click strategy explicitly
     */
    ClickInteraction withStrategy(ClickStrategy clickStrategy);
}
