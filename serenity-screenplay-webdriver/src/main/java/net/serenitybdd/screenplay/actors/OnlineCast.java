package net.serenitybdd.screenplay.actors;

import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;
import org.openqa.selenium.WebDriver;

import java.util.function.Consumer;

/**
 * Provide simple support for managing Screenplay actors in Cucumber-JVM or JBehave
 */
public class OnlineCast extends Cast {

    public OnlineCast() {
        this(new Ability[]{});
    }

    public OnlineCast(Ability[] abilities) {
        super(abilities);
    }

    public OnlineCast(Consumer<Actor>... providers) {
        super(providers);
    }

    public static Cast whereEveryoneCan(Ability... abilities) {
        return new OnlineCast(abilities);
    }

    public static Cast whereEveryoneCan(Consumer<Actor>... abilityProviders) {
        return new OnlineCast(abilityProviders);
    }

    @Override
    public Actor actorNamed(String actorName, Ability... abilities) {

        Actor newActor = super.actorNamed(actorName, abilities);
        if (newActor.abilityTo(BrowseTheWeb.class) == null) {
            newActor.can(BrowseTheWeb.with(theDefaulteBrowserFor(actorName)));
        }
        return newActor;
    }

    public BrowsingActorBuilder actorUsingBrowser(String driver) {
        return new BrowsingActorBuilder(this, driver);
    }

    public class BrowsingActorBuilder {

        private final Cast cast;
        private final String driver;

        public BrowsingActorBuilder(Cast cast, String driver) {
            this.cast = cast;
            this.driver = driver;
        }

        public Actor named(String actorName) {
            return cast.actorNamed(actorName, BrowseTheWeb.with(aConfiguredBrowser(actorName)));
        }

        private WebDriver aConfiguredBrowser(String actorName) {
            return ThucydidesWebDriverSupport.getWebdriverManager().getWebdriverByName(actorName, driver);
        }
    }

    private WebDriver theDefaulteBrowserFor(String actorName) {
        return ThucydidesWebDriverSupport.getWebdriverManager().getWebdriverByName(actorName);
    }
}
