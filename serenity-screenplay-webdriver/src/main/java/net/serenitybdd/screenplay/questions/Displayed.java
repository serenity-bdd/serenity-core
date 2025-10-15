package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.pages.WebElementState;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static net.serenitybdd.screenplay.questions.LabelledQuestion.answer;
import static net.serenitybdd.screenplay.questions.LabelledQuestion.answerEach;

/**
 * Check whether an element is displayed.
 */
public class Displayed {

    public static Question<Boolean> of(Target target) {
        return answer(target.getName() + " is displayed for", actor -> matches(target.resolveAllFor(actor)));
    }

    public static Question<Boolean> of(By byLocator) {
        return answer(byLocator + " is displayed for", actor -> matches(BrowseTheWeb.as(actor).findAll(byLocator)));
    }

    public static Question<Boolean> of(String locator) {
        return answer(locator + " is displayed for", actor -> matches(BrowseTheWeb.as(actor).findAll(locator)));
    }


    public static Question<List<Boolean>> ofEach(Target target) {
        return answerEach(target.getName() + " is displayed for each", actor -> target.resolveAllFor(actor)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList()));
    }

    public static Question<List<Boolean>> ofEach(By byLocator) {
        return answerEach(byLocator + " is displayed for each", actor -> BrowseTheWeb.as(actor).findAll(byLocator)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList()));
    }

    public static Question<List<Boolean>> ofEach(String locator) {
        return answerEach(locator + " is displayed for each", actor -> BrowseTheWeb.as(actor).findAll(locator)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList()));
    }

    private static boolean matches(List<WebElementFacade> elements) {
        return elements.stream()
                .findFirst()
                .map(WebElement::isDisplayed)
                .orElse(false);
    }
}
