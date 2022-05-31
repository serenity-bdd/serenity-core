package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

/**
 * Retrieve the value of a specific attribute of an element.
 */
public class Attribute {

    public static Question<String> of(Target target, String attributeName) {
        return Question.about(attributeName +" attribute of " + target.getName())
                .answeredBy(actor -> matches(target.resolveAllFor(actor), attributeName));
    }

    public static Question<String> of(By byLocator, String attributeName) {
        return Question.about(attributeName + " attribute of " + attributeName)
                .answeredBy(actor -> matches(BrowseTheWeb.as(actor).findAll(byLocator), attributeName));
    }

    public static Question<String> of(String locator, String attributeName) {
        return Question.about(attributeName + " attribute of " + locator)
                .answeredBy(actor -> matches(BrowseTheWeb.as(actor).findAll(locator), attributeName));
    }

    public static Question<Collection<String>> ofEach(Target target, String attributeName) {
        return Question.about(attributeName +" attribute of each " + target.getName())
                .answeredBy(actor -> target.resolveAllFor(actor)
                .stream()
                .map(element -> matches(singletonList(element), attributeName))
                .collect(Collectors.toList()));
    }

    public static Question<Collection<String>> ofEach(By byLocator, String attributeName) {
        return Question.about(attributeName +" attribute of each " + byLocator)
                .answeredBy(actor -> BrowseTheWeb.as(actor).findAll(byLocator)
                .stream()
                .map(element -> matches(singletonList(element), attributeName))
                .collect(Collectors.toList()));
    }

    public static Question<Collection<String>> ofEach(String locator, String attributeName) {
        return Question.about(attributeName +" attribute of each " + locator)
                .answeredBy(actor -> BrowseTheWeb.as(actor).findAll(locator)
                .stream()
                .map(element -> matches(singletonList(element), attributeName))
                .collect(Collectors.toList()));
    }

    public static QuestionForName of(Target target) {
        return name -> Attribute.of(target, name);
    }

    public static QuestionForName of(By byLocator) {
        return name -> Attribute.of(byLocator, name);
    }

    public static QuestionForName of(String locator) {
        return name -> Attribute.of(locator, name);
    }

    public static QuestionForNames ofEach(Target target) {
        return name -> Attribute.ofEach(target, name);
    }

    public static QuestionForNames ofEach(By byLocator) {
        return name -> Attribute.ofEach(byLocator, name);
    }

    public static QuestionForNames ofEach(String locator) {
        return name -> Attribute.ofEach(locator, name);
    }

    private static String matches(List<WebElementFacade> elements, String attributeName) {
        return elements.stream()
                .findFirst()
                .map(element -> element.getAttribute(attributeName))
                .orElse("");
    }
}
