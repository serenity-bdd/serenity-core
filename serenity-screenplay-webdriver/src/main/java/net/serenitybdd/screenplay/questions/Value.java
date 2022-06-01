package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.pages.WebElementState;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

/**
 * Find the HTML value attribute of a given HTML element.
 */
public class Value {

    public static Question<String> of(Target target) {
        return Question.about("value of " + target.getName()).answeredBy(actor -> matches(target.resolveAllFor(actor)));
    }

    public static Question<String> of(By locator) {
        return Question.about("value of " + locator).answeredBy(actor -> matches(BrowseTheWeb.as(actor).findAll(locator)));
    }

    public static Question<String> of(String locator) {
        return Question.about("value of " + locator).answeredBy(actor -> matches(BrowseTheWeb.as(actor).findAll(locator)));
    }

    public static Question<Collection<String>> ofEach(Target target) {
        return Question.about("values of each " + target.getName()).answeredBy(actor -> target.resolveAllFor(actor)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList()));
    }

    public static Question<Collection<String>> ofEach(By byLocator) {
        return Question.about("values of each " + byLocator).answeredBy(actor -> BrowseTheWeb.as(actor).findAll(byLocator)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList()));
    }

    public static Question<Collection<String>> ofEach(String locator) {
        return Question.about("values of each " + locator).answeredBy(actor -> BrowseTheWeb.as(actor).findAll(locator)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList()));
    }

    private static String matches(List<WebElementFacade> elements) {
        return elements.stream()
                .findFirst()
                .map(WebElementState::getValue)
                .orElse("");
    }
}
