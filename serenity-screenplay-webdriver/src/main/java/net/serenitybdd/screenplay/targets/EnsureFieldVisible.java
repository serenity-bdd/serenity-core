package net.serenitybdd.screenplay.targets;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actions.Scroll;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnsureFieldVisible {
    private final Actor actor;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public static EnsureFieldVisible ensureThat(Actor actor) {
        return new EnsureFieldVisible(actor);
    }

    private EnsureFieldVisible(Actor actor) {
        this.actor = actor;
    }

    public void canSee(WebElementFacade targetElement) {
        if ((targetElement != null) && (!targetElement.isCurrentlyVisible())) {
            try {
                actor.attemptsTo(Scroll.to(targetElement));
            } catch (WebDriverException failedToMoveToElement) {
                logger.warn("Failed to move to target", failedToMoveToElement);
            }
        }
    }
}
