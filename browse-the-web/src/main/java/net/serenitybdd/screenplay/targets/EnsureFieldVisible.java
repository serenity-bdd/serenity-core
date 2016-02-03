package net.serenitybdd.screenplay.targets;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actions.Scroll;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnsureFieldVisible {
    private final Actor actor;
    private final Boolean scrollToTarget;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public static EnsureFieldVisible ensureThat(Actor actor) {
        return new EnsureFieldVisible(actor);
    }

    public EnsureFieldVisible(Actor actor) {
        this.actor = actor;
        EnvironmentVariables environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
        this.scrollToTarget = ThucydidesSystemProperty.SERENITY_SCREENPLAY_SCROLLTOTARGETS.booleanFrom(environmentVariables, true);
    }

    public void canSee(WebElementFacade targetElement) {
        if (scrollToTarget && !targetElement.isCurrentlyVisible()) {
            try {
                actor.attemptsTo(Scroll.to(targetElement));
            } catch (WebDriverException failedToMoveToElement) {
                logger.warn("Failed to move to target", failedToMoveToElement);
            }
        }
    }
}
