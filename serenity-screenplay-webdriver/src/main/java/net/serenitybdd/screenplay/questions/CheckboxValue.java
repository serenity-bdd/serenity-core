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
 * Determine whether a checkbox has been checked or not.
 */
public class CheckboxValue {

    public static Question<Boolean> of(Target target) {
        return Question.about("checkbox state of " + target.getName()).answeredBy(actor -> matches(target.resolveAllFor(actor)));
    }

    public static Question<Boolean> of(By byLocator) {
        return Question.about("checkbox state of element located by " + byLocator).answeredBy(actor -> matches(BrowseTheWeb.as(actor).findAll(byLocator)));
    }

    public static Question<Boolean> of(String locator) {
        return Question.about("checkbox state of " + locator).answeredBy(actor -> matches(BrowseTheWeb.as(actor).findAll(locator)));
    }

    public static Question<Collection<Boolean>> ofEach(Target target) {
        return Question.about("checkbox state of each of " + target.getName()).answeredBy(
                actor -> target.resolveAllFor(actor)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList()));
    }

    public static Question<Collection<Boolean>> ofEach(By byLocator) {
        return Question.about("checkbox state of each of element located by " + byLocator)
                .answeredBy(actor -> BrowseTheWeb.as(actor).findAll(byLocator)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList()));
    }

    public static Question<Collection<Boolean>> ofEach(String locator) {
        return Question.about("checkbox state of each " + locator)
                .answeredBy(actor -> BrowseTheWeb.as(actor).findAll(locator)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList()));
    }

    private static Boolean matches(List<WebElementFacade> elements) {
        return elements.stream()
                .findFirst()
                .map(WebElementState::isSelected)
                .orElse(false);
    }
}
