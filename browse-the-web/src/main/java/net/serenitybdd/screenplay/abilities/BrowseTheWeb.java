package net.serenitybdd.screenplay.abilities;

import com.google.common.eventbus.Subscribe;
import net.serenitybdd.core.eventbus.Broadcaster;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.events.ActorAsksQuestion;
import net.serenitybdd.screenplay.events.ActorBeginsPerformanceEvent;
import net.serenitybdd.screenplay.events.ActorEndsPerformanceEvent;
import net.serenitybdd.screenplay.events.ActorPerforms;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.PageObjectDependencyInjector;
import net.thucydides.core.webdriver.WebdriverManager;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gives an actor the ability to browse theValue web.
 * This extends the classic Serenity PageObject class.
 */
public class BrowseTheWeb extends PageObject implements Ability {

    private final WebdriverManager webdriverManager;

    private Actor actor;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

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
        return on(pageObjectClass);
    }

    public <T extends PageObject> T onPageElement(Class<T> pageObjectClass) {
        return on(pageObjectClass);
    }
    public <T extends PageObject> T on(Class<T> pageObjectClass) {
        return Pages.instrumentedPageObjectUsing(pageObjectClass, getDriver());
    }

    @Subscribe public void beginPerformance(ActorBeginsPerformanceEvent performanceEvent) {
        try {
            if (performanceEvent.getName().equals(actor.getName())) {
                webdriverManager.setCurrentDriver(getDriver());
            }
        } catch(Throwable e) {
            logger.warn("Failed to notify begin performance event for actor " + performanceEvent.getName(),e);
        }
    }

    @Subscribe public void perform(ActorPerforms performAction) {
        WebDriver driver = webdriverManager.getWebdriver();
        PageObjectDependencyInjector injector = new PageObjectDependencyInjector(new Pages(driver));
        injector.injectDependenciesInto(performAction.getPerformable());
    }

    @Subscribe public void prepareQuestion(ActorAsksQuestion questionEvent) {
        WebDriver driver = webdriverManager.getWebdriver();
        PageObjectDependencyInjector injector = new PageObjectDependencyInjector(new Pages(driver));
        injector.injectDependenciesInto(questionEvent.getQuestion());
    }

    @Subscribe public void endPerformance(ActorEndsPerformanceEvent performanceEvent) {
        try {
            if (performanceEvent.getName().equals(actor.getName())) {
                webdriverManager.clearCurrentDriver();
            }
        } catch(Throwable e) {
            logger.warn("Failed to notify end performance event for actor " + performanceEvent.getName(),e);
        }
    }

    @Override
    public <T extends Ability> T asActor(Actor actor) {
        this.actor = actor;
        return (T) this;
    }
}
