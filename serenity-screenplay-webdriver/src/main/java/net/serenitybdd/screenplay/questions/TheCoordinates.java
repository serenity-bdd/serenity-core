package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.pages.WebElementState;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Coordinates;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

/**
 * Provides coordinates of an element for advanced interactions.
 * Note that some coordinates (such as screen coordinates) are evaluated lazily since the element may have to be scrolled into view.
 */
public class TheCoordinates {

    public static Question<Coordinates> of(Target target) {
        return Question.about("coordinates of " + target.getName()).answeredBy(actor -> matches(target.resolveAllFor(actor)));
    }

    public static Question<Coordinates> of(By byLocator) {
        return Question.about("coordinates of element located by " + byLocator).answeredBy(actor -> matches(BrowseTheWeb.as(actor).findAll(byLocator)));
    }

    public static Question<Coordinates> of(String locator) {
        return Question.about("coordinates of " + locator).answeredBy(actor -> matches(BrowseTheWeb.as(actor).findAll(locator)));
    }

    public static Question<List<Coordinates>> ofEach(Target target) {
        return Question.about("coordinates of each of " + target.getName()).answeredBy(
                actor -> target.resolveAllFor(actor)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList()));
    }

    public static Question<List<Coordinates>> ofEach(By byLocator) {
        return Question.about("coordinates of each of element located by " + byLocator).answeredBy(
                actor -> BrowseTheWeb.as(actor).findAll(byLocator)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList()));
    }

    public static Question<List<Coordinates>> ofEach(String locator) {
        return Question.about("coordinates of each of " + locator)
                .answeredBy(actor -> BrowseTheWeb.as(actor).findAll(locator)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList()));
    }

    private static Coordinates matches(List<WebElementFacade> elements) {
        return elements.stream()
                .findFirst()
                .map(WebElementFacade::getCoordinates)
                .orElse(null);
    }
}
