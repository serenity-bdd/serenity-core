package net.serenitybdd.screenplay.targets;

import net.serenitybdd.core.pages.*;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.ui.LocatorStrategies;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class Target implements ResolvableElement {

    protected final String targetElementName;
    protected final Optional<IFrame> iFrame;
    protected final Optional<Duration> timeout;

    public Target(String targetElementName, Optional<IFrame> iFrame) {
        this.targetElementName = targetElementName;
        this.iFrame = iFrame;
        this.timeout = Optional.empty();
    }

    public Target(String targetElementName, Optional<IFrame> iFrame, Optional<Duration> timeout) {
        this.targetElementName = targetElementName;
        this.iFrame = iFrame;
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        return targetElementName;
    }

    public static TargetBuilder the(String targetElementName) {
        return new TargetBuilder(targetElementName);
    }

    protected PageObject currentPageVisibleTo(Actor actor) {
        return TargetResolver.create(BrowseTheWeb.as(actor).getDriver(), this);
    }

    public WebElementFacade resolveFor(Actor actor) {
        return resolveFor(currentPageVisibleTo(actor));
    }

    public ListOfWebElementFacades resolveAllFor(Actor actor) {
        return new ListOfWebElementFacades(resolveAllFor(currentPageVisibleTo(actor)));
    }

    public abstract WebElementFacade resolveFor(PageObject page);
    public abstract ListOfWebElementFacades resolveAllFor(PageObject page);

    public abstract SearchableTarget called(String name);

    public abstract SearchableTarget of(String... parameters);

    public abstract String getCssOrXPathSelector();

    public Optional<IFrame> getIFrame() {
        return iFrame;
    }

    public String getName() {
        return targetElementName;
    }

    public abstract Target waitingForNoMoreThan(Duration timeout);

    public SearchableTarget inside(String locator) {
        return inside(Target.the("Containing element").locatedBy(locator));
    }

    public SearchableTarget inside(Target container) {
        return Target.the(getName()).locatedBy(
                LocatorStrategies.findNestedElements(container, this)
        );
    }

    public SearchableTarget thenFind(Target nextElement) {
        return Target.the(getName()).locatedBy(
                LocatorStrategies.findNestedElements(this, nextElement)
        );
    }


    public abstract List<By> selectors(WebDriver driver);

    /**
     * Is the target element currently visible for this actor?
     */
    public boolean isVisibleFor(Actor actor) {
        List<WebElementFacade> matchingElements = resolveAllFor(actor);
        return matchingElements.stream().anyMatch(WebElementState::isCurrentlyVisible);
    }

    public <T> Question<T> mapFirst(Function<WebElementFacade, T> transformation) {
        return actor -> transformation.apply(this.resolveFor(actor));
    }

    public <T> Question<Collection<T>> mapAll(Function<WebElementFacade, T> transformation) {
        return actor -> this.resolveAllFor(actor)
                .stream()
                .map(transformation)
                .collect(Collectors.toList());
    }
}
