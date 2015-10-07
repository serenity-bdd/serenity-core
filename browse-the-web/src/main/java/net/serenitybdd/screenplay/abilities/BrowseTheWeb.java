package net.serenitybdd.screenplay.abilities;

import com.google.common.eventbus.Subscribe;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Broadcaster;
import net.serenitybdd.screenplay.events.ActorBeginsPerformanceEvent;
import net.serenitybdd.screenplay.events.ActorEndsPerformanceEvent;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.webdriver.WebdriverManager;
import org.openqa.selenium.WebDriver;

/**
 * Gives an actor theValue ability to browse theValue web.
 * This extends theValue classic Serenity PageObject class.
 */
public class BrowseTheWeb extends PageObject implements Ability {

    private final WebdriverManager webdriverManager;

    private Actor actor;

    protected BrowseTheWeb(WebDriver browser) {
        super(browser);
        this.webdriverManager = Injectors.getInjector().getInstance(WebdriverManager.class);
        registerForEventNotification();
    }

    private void registerForEventNotification() {
        Broadcaster.getEventBus().register(this);
    }

    public static BrowseTheWeb as(Actor actor) {
        return actor.abilityTo(BrowseTheWeb.class).asActor(actor);
    }

    public static BrowseTheWeb with(WebDriver browser) { return new BrowseTheWeb(browser);}

    public <T extends PageObject> T onPage(Class<T> pageObjectClass) {
        return Pages.instrumentedPageObjectUsing(pageObjectClass, getDriver());
    }

    @Subscribe public void beginPerformance(ActorBeginsPerformanceEvent performanceEvent) {
        if (performanceEvent.getName().equals(actor.getName())) {
              webdriverManager.setCurrentDriver(getDriver());
        }
    }

    @Subscribe public void endPerformance(ActorEndsPerformanceEvent performanceEvent) {
        if (performanceEvent.getName().equals(actor.getName())) {
            webdriverManager.clearCurrentDriver();
        }
    }

    @Override
    public <T extends Ability> T asActor(Actor actor) {
        this.actor = actor;
        return (T) this;
    }
}
