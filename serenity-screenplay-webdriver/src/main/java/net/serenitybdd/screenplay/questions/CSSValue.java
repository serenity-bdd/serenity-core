package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

public class CSSValue {

    private String cssAttributeName;
    private Target target;

    public CSSValue(Target target, String attributeName) {
        this.target = target;
        this.cssAttributeName = attributeName;
    }

    public static Question<String> of(Target target, String attributeName) {
        return actor -> matches(target.resolveAllFor(actor), attributeName);
    }

    public static Question<String> of(By byLocator, String attributeName) {
        return actor -> matches(BrowseTheWeb.as(actor).findAll(byLocator), attributeName);
    }

    public static Question<String> of(String locator, String attributeName) {
        return actor -> matches(BrowseTheWeb.as(actor).findAll(locator), attributeName);
    }

    public static Question<List<String>> ofEach(Target target, String attributeName) {
        return actor -> target.resolveAllFor(actor)
                .stream()
                .map(element -> matches(singletonList(element), attributeName))
                .collect(Collectors.toList());
    }

    public static Question<List<String>> ofEach(By byLocator, String attributeName) {
        return actor -> BrowseTheWeb.as(actor).findAll(byLocator)
                .stream()
                .map(element -> matches(singletonList(element), attributeName))
                .collect(Collectors.toList());
    }

    public static Question<List<String>> ofEach(String locator, String attributeName) {
        return actor -> BrowseTheWeb.as(actor).findAll(locator)
                .stream()
                .map(element -> matches(singletonList(element), attributeName))
                .collect(Collectors.toList());
    }

    public static QuestionForName of(Target target) {
        return name -> CSSValue.of(target, name);
    }

    public static QuestionForName of(By byLocator) {
        return name -> CSSValue.of(byLocator, name);
    }

    public static QuestionForName of(String locator) {
        return name -> CSSValue.of(locator, name);
    }

    public static QuestionForNames ofEach(Target target) {
        return name -> CSSValue.ofEach(target, name);
    }

    public static QuestionForNames ofEach(By byLocator) {
        return name -> CSSValue.ofEach(byLocator, name);
    }

    public static QuestionForNames ofEach(String locator) {
        return name -> CSSValue.ofEach(locator, name);
    }

    private static String matches(List<WebElementFacade> elements, String attributeName) {
        return elements.stream()
                .findFirst()
                .map(element -> element.getCssValue(attributeName))
                .orElse("");
    }
}