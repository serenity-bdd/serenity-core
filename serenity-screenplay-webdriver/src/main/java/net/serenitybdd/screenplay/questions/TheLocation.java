package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.interactions.Coordinates;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

public class TheLocation {

    public static Question<Point> of(Target target) {
        return actor -> matches(target.resolveAllFor(actor));
    }

    public static Question<Point> of(By byLocator) {
        return actor -> matches(BrowseTheWeb.as(actor).findAll(byLocator));
    }

    public static Question<Point> of(String locator) {
        return actor -> matches(BrowseTheWeb.as(actor).findAll(locator));
    }

    public static Question<List<Point>> ofEach(Target target) {
        return actor -> target.resolveAllFor(actor)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList());
    }

    public static Question<List<Point>> ofEach(By byLocator) {
        return actor -> BrowseTheWeb.as(actor).findAll(byLocator)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList());
    }

    public static Question<List<Point>> ofEach(String locator) {
        return actor -> BrowseTheWeb.as(actor).findAll(locator)
                .stream()
                .map(element -> matches(singletonList(element)))
                .collect(Collectors.toList());
    }

    private static Point matches(List<WebElementFacade> elements) {
        return elements.stream()
                .findFirst()
                .map(WebElementFacade::getLocation)
                .orElse(null);
    }
}