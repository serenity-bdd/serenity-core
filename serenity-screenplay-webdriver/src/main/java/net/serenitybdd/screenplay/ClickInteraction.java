package net.serenitybdd.screenplay;

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
}
