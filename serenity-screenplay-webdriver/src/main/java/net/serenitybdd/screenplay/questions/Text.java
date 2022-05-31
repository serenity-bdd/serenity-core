package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.pages.WebElementState;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import java.util.Collection;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

/**
 * Get the visible (i.e. not hidden by CSS) text of this element, including sub-elements.
 */
public class Text {

    public static Question<String> of(Target target) {
        return Question.about("text of " + target.getName()).answeredBy(actor -> matches(target.resolveAllFor(actor)));
    }

    public static Question<String> of(By byLocator) {
        return Question.about("text of element located by " + byLocator).answeredBy(actor -> matches(BrowseTheWeb.as(actor).findAll(byLocator)));
    }

    public static Question<String> of(String locator) {
        return Question.about("text of element located by " + locator).answeredBy(actor -> matches(BrowseTheWeb.as(actor).findAll(locator)));
    }

    public static Question<Collection<String>> ofEach(Target target) {
        return Question.about("text of each of " + target.getName()).answeredBy(
                actor -> target.resolveAllFor(actor)
                        .stream()
                        .map(element -> matches(singletonList(element)))
                        .collect(Collectors.toList())
        );
    }

    public static Question<Collection<String>> ofEach(By byLocator) {
        return Question.about("text of each of element located by " + byLocator).answeredBy(actor -> BrowseTheWeb.as(actor).findAll(byLocator)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList())
        );
    }

    public static Question<Collection<String>> ofEach(String locator) {
        return Question.about("text of each of " + locator).answeredBy(actor -> BrowseTheWeb.as(actor).findAll(locator)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList())
        );
    }

    private static String matches(Collection<WebElementFacade> elements) {
        return elements.stream()
                .findFirst()
                .map(WebElementState::getText)
                .orElse("");
    }
}
