package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.pages.WebElementState;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static net.serenitybdd.screenplay.questions.LabelledQuestion.answer;

/**
 * Determine whether an element is not present on the page
 */
public class Absence {

    public static Question<Boolean> of(Target target) {
        return answer(target.getName() + " is not present for", actor -> matches(target.resolveAllFor(actor)));
    }

    public static Question<Boolean> of(By byLocator) {
        return answer(byLocator + " is not present for", actor -> matches(BrowseTheWeb.as(actor).findAll(byLocator)));
    }

    public static Question<Boolean> of(String locator) {
        return answer(locator + " is not present for", actor -> matches(BrowseTheWeb.as(actor).findAll(locator)));
    }


    public static Question<List<Boolean>> ofEach(Target target) {
        return actor -> target.resolveAllFor(actor)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList());
    }

    public static Question<List<Boolean>> ofEach(By byLocator) {
        return actor -> BrowseTheWeb.as(actor).findAll(byLocator)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList());
    }

    public static Question<List<Boolean>> ofEach(String locator) {
        return actor -> BrowseTheWeb.as(actor).findAll(locator)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList());
    }

    private static boolean matches(List<WebElementFacade> elements) {
        return elements.stream()
                .findAny()
                .map(element -> !element.isDisplayed())
                .orElse(true);
    }
}
