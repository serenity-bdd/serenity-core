package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.pages.WebElementState;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

public class CurrentlyEnabled {

    public static Question<Boolean> of(Target target) {
        return actor -> matches(target.resolveAllFor(actor));
    }

    public static Question<Boolean> of(By byLocator) {
        return actor -> matches(BrowseTheWeb.as(actor).findAll(byLocator));
    }

    public static Question<Boolean> of(String locator) {
        return actor -> matches(BrowseTheWeb.as(actor).findAll(locator));
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
                .findFirst()
                .map(WebElementState::isCurrentlyEnabled)
                .orElse(false);
    }

}
