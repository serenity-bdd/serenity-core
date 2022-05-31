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
import static net.serenitybdd.screenplay.questions.LabelledQuestion.answer;
import static net.serenitybdd.screenplay.questions.LabelledQuestion.answerEach;

/**
 * Return the selected value of a SELECT dropdown list.
 */
public class SelectedValue {

    public static Question<String> of(Target target) {
        return Question.about("selected value of " + target.getName()).answeredBy(actor -> matches(target.resolveAllFor(actor)));
    }

    public static Question<String> of(By byLocator) {
        return Question.about("selected value of element located by " + byLocator).answeredBy(actor -> matches(BrowseTheWeb.as(actor).findAll(byLocator)));
    }

    public static Question<String> of(String locator) {
        return Question.about("selected value of " +locator).answeredBy(actor -> matches(BrowseTheWeb.as(actor).findAll(locator)));
    }

    public static Question<Collection<String>> ofEach(Target target) {
        return Question.about("selected values of " + target.getName()).answeredBy(actor -> target.resolveAllFor(actor)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList()));
    }

    public static Question<Collection<String>> ofEach(By byLocator) {
        return Question.about("selected values of element located by " + byLocator).answeredBy(actor -> BrowseTheWeb.as(actor).findAll(byLocator)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList()));
    }

    public static Question<Collection<String>> ofEach(String locator) {
        return Question.about("selected values of " + locator).answeredBy(actor -> BrowseTheWeb.as(actor).findAll(locator)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList()));
    }

    private static String matches(List<WebElementFacade> elements) {
        return elements.stream()
                .findFirst()
                .map(WebElementState::getSelectedValue)
                .orElse("");
    }
}
