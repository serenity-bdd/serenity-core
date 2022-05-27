package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static net.serenitybdd.screenplay.questions.LabelledQuestion.answer;
import static net.serenitybdd.screenplay.questions.LabelledQuestion.answerEach;

/**
 * Check whether an element is disabled.
 */
public class Disabled {

    public static Question<Boolean> of(Target target) {
        return answer(target.getName() + " is disabled for", actor -> matches(target.resolveAllFor(actor)));
    }

    public static Question<Boolean> of(By byLocator) {
        return answer(byLocator+ " is disabled for", actor -> matches(BrowseTheWeb.as(actor).findAll(byLocator)));
    }

    public static Question<Boolean> of(String locator) {
        return answer(locator + " is disabled for", actor -> matches(BrowseTheWeb.as(actor).findAll(locator)));
    }


    public static Question<List<Boolean>> ofEach(Target target) {
        return answerEach(target.getName() + " are disabled for", actor -> target.resolveAllFor(actor)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList()));
    }

    public static Question<List<Boolean>> ofEach(By byLocator) {
        return answerEach(byLocator+ " are disabled for", actor -> BrowseTheWeb.as(actor).findAll(byLocator)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList()));
    }

    public static Question<List<Boolean>> ofEach(String locator) {
        return answerEach(locator+ " are disabled for", actor -> BrowseTheWeb.as(actor).findAll(locator)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList()));
    }

    private static boolean matches(List<WebElementFacade> elements) {
        return elements.stream()
                .findFirst()
                .map(WebElementFacade::isDisabled)
                .orElse(true);
    }
}
