package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

/**
 * What is the width and height of the rendered element?
 */
public class TheSize {

    public static Question<Dimension> of(Target target) {
        return Question.about("size of " + target.getName()).answeredBy(actor -> matches(target.resolveAllFor(actor)));
    }

    public static Question<Dimension> of(By byLocator) {
        return Question.about("size of element located by " + byLocator).answeredBy(actor -> matches(BrowseTheWeb.as(actor).findAll(byLocator)));
    }

    public static Question<Dimension> of(String locator) {
        return Question.about("size of " + locator).answeredBy(actor -> matches(BrowseTheWeb.as(actor).findAll(locator)));
    }

    public static Question<List<Dimension>> ofEach(Target target) {
        return Question.about("size of each of " + target.getName()).answeredBy(actor -> target.resolveAllFor(actor)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList()));
    }

    public static Question<List<Dimension>> ofEach(By byLocator) {
        return Question.about("size of each of element located by " + byLocator).answeredBy(actor -> BrowseTheWeb.as(actor).findAll(byLocator)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList()));
    }

    public static Question<List<Dimension>> ofEach(String locator) {
        return Question.about("size of each of " + locator).answeredBy(actor -> BrowseTheWeb.as(actor).findAll(locator)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList()));
    }

    private static Dimension matches(List<WebElementFacade> elements) {
        return elements.stream()
                .findFirst()
                .map(WebElementFacade::getSize)
                .orElse(null);
    }
}
