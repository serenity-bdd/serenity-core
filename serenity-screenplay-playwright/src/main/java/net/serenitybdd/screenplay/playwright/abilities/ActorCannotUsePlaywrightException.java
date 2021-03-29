package net.serenitybdd.screenplay.playwright.abilities;

public class ActorCannotUsePlaywrightException extends RuntimeException {
    public ActorCannotUsePlaywrightException(String actorName) {
        super("The actor " + actorName + " does not have the ability to use Playwright." + System.lineSeparator()
                + "You can give them this ability using the BrowseTheWebWithBlaywright class, e.g."  + System.lineSeparator()
                + "   actor.can(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());"
                );
    }
}
