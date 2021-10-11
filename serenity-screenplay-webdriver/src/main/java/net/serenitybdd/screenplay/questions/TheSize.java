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

public class TheSize {

    public static Question<Dimension> of(Target target) {
        return actor -> matches(target.resolveAllFor(actor));
    }

    public static Question<Dimension> of(By byLocator) {
        return actor -> matches(BrowseTheWeb.as(actor).findAll(byLocator));
    }

    public static Question<Dimension> of(String locator) {
        return actor -> matches(BrowseTheWeb.as(actor).findAll(locator));
    }

    public static Question<List<Dimension>> ofEach(Target target) {
        return actor -> target.resolveAllFor(actor)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList());
    }

    public static Question<List<Dimension>> ofEach(By byLocator) {
        return actor -> BrowseTheWeb.as(actor).findAll(byLocator)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList());
    }

    public static Question<List<Dimension>> ofEach(String locator) {
        return actor -> BrowseTheWeb.as(actor).findAll(locator)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList());
    }

    private static Dimension matches(List<WebElementFacade> elements) {
        return elements.stream()
                .findFirst()
                .map(WebElementFacade::getSize)
                .orElse(null);
    }
}