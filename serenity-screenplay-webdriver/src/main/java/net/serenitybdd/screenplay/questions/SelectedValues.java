package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.pages.WebElementState;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

public class SelectedValues {

    public static Question<List<String>> of(Target target) {
        return actor -> matches(target.resolveAllFor(actor));
    }

    public static Question<List<String>> of(By byLocator) {
        return actor -> matches(BrowseTheWeb.as(actor).findAll(byLocator));
    }

    public static Question<List<String>> of(String locator) {
        return actor -> matches(BrowseTheWeb.as(actor).findAll(locator));
    }

    private static List<String> matches(List<WebElementFacade> elements) {
        return elements.stream()
                .findFirst()
                .map(WebElementState::getSelectedValues)
                .orElse(new ArrayList<>());
    }
}