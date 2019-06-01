package net.serenitybdd.screenplay.ensure.web;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.ensure.Ensure;
import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.annotations.Managed;
import org.junit.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import net.serenitybdd.screenplay.ensure.Ensure;

import java.time.LocalDate;

import static net.serenitybdd.screenplay.ensure.web.TheMatchingElement.containsText;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@RunWith(SerenityRunner.class)
/**
 * Some high level UI smoke tests
 */
public class WhenUsingFluentAssertionsInJavaAboutWebPages {

    @DefaultUrl("classpath:static-site/index.html")
    public static class DemoPage extends PageObject {}

    @Managed(driver = "chrome", options = "--headless")
    WebDriver driver;

    DemoPage demoPage;

    @Test
    public void weCanMakeAssertionsAboutWebPages() {
        Actor aster = Actor.named("Aster").whoCan(BrowseTheWeb.with(driver));

        aster.attemptsTo(
                Open.browserOn(demoPage),
                Ensure.that(ElementLocated.by("#firstName")).isDisplayed(),
                Ensure.that(ElementLocated.by("#heading")).text().isEqualTo("Heading"),
                Ensure.thatTheSetOf(ElementsLocated.by(".train-line")).allMatch(containsText("Line"))
        );
    }
}
