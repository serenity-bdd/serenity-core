package net.serenitybdd.screenplay.ensure.web;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.ensure.Ensure;
import net.serenitybdd.screenplay.targets.IFrame;
import net.serenitybdd.screenplay.targets.Target;
import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.annotations.Managed;
import org.junit.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import static net.serenitybdd.screenplay.ensure.web.AnElementThat.containsText;

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
                Ensure.that(ElementLocated.by("#firstName")).isDisplayed().orElseThrow(new Exception()),
                Ensure.thatTheListOf(ElementsLocated.by(".train-line")).hasSizeGreaterThanOrEqualTo(1),
                Ensure.thatTheListOf(By.cssSelector(".train-line")).allMatch(containsText("Line"))
        );
    }

    @Test
    public void weCanTransformCollectionsOfWebElements() {
        Actor aster = Actor.named("Aster").whoCan(BrowseTheWeb.with(driver));

        aster.attemptsTo(
                Open.browserOn(demoPage),
                Ensure.that(ElementsLocated.by(".train-line").mapAll(WebElementFacade::getText))
                        .contains("Jubilee Line","Bakerloo Line","Central Line")
        );
    }

    @Test
    public void weCanTransformASingleWebElemens() {
        Actor aster = Actor.named("Aster").whoCan(BrowseTheWeb.with(driver));

        aster.attemptsTo(
                Open.browserOn(demoPage),
                Ensure.that(ElementsLocated.by(".train-line").mapFirst(WebElementFacade::getText)).isEqualTo("Jubilee Line")
        );
    }

    @Test(expected = SomeCustomException.class)
    public void weCanThrowCustomExceptions() {
        Actor aster = Actor.named("Aster").whoCan(BrowseTheWeb.with(driver));

        aster.attemptsTo(
                Open.browserOn(demoPage),
                Ensure.that(ElementLocated.by("#notDisplayed")).isDisplayed().orElseThrow(new SomeCustomException("Oh crap")),
                Ensure.thatTheListOf(ElementsLocated.by(".train-line")).hasSizeGreaterThanOrEqualTo(1),
                Ensure.thatTheListOf(ElementsLocated.by(".train-line")).allMatch(containsText("Line"))
        );
    }

    @Test
    public void weCanMakeAssertionsAboutCollectionsOfMatchingValues() {
        Actor aster = Actor.named("Aster").whoCan(BrowseTheWeb.with(driver));

        aster.attemptsTo(
                Open.browserOn(demoPage),
                Ensure.that(ElementLocated.by("#colors option")).values().contains("red","blue","green")
        );
    }

    @Test
    public void weCanConvertWebElementValuesToOtherTypes() {
        Actor aster = Actor.named("Aster").whoCan(BrowseTheWeb.with(driver));

        LocalDate expectedDate = LocalDate.of(2019,6,20);

        aster.attemptsTo(
                Open.browserOn(demoPage),
                Ensure.that(ElementLocated.by("#firstName")).value().startsWith("Joe"),
                Ensure.that(ElementLocated.by("#itemCount")).value().asAnInteger().isGreaterThanOrEqualTo(2),
                Ensure.that(ElementLocated.by("#totalCost")).value().asADouble().isCloseTo(99.99d,0.01d),
                Ensure.that(ElementLocated.by("#totalCost")).value().asAFloat().not().isCloseTo(98.99f,0.01f),
                Ensure.that(ElementLocated.by("#totalCost")).value().asABigDecimal().isEqualTo(new BigDecimal("99.99")),
                Ensure.that(ElementLocated.by("#flag")).value().asABoolean().isTrue()
        );
    }

    @Test
    public void weCanConvertWebElementValuesToDateValues() {
        Actor aster = Actor.named("Aster").whoCan(BrowseTheWeb.with(driver));

        LocalDate expectedDate = LocalDate.of(2019,6,20);

        aster.attemptsTo(
                Open.browserOn(demoPage),
                Ensure.that(ElementLocated.by("#currentDate")).value().asADate().isEqualTo(expectedDate),
                Ensure.that(ElementLocated.by("#ddmmyyyyDate")).value().asADate("dd-MM-yyyy").isEqualTo(expectedDate)
        );
    }

    @Test
    public void weCanConvertWebElementValuesToTimeValues() {
        Actor aster = Actor.named("Aster").whoCan(BrowseTheWeb.with(driver));

        LocalTime expectedTime = LocalTime.of(11,42);
        LocalTime laterTime = LocalTime.of(11,43);

        aster.attemptsTo(
                Open.browserOn(demoPage),
                Ensure.that(ElementLocated.by("#currentTime")).value().asATime().isEqualTo(expectedTime),
                Ensure.that(ElementLocated.by("#altFormattedTime")).value().asATime("HH:mm:ss.SSS").isBefore(laterTime)
        );
    }


    @Test
    public void weCanSpecifyADelayForAParticularElement() {
        Actor aster = Actor.named("Aster").whoCan(BrowseTheWeb.with(driver));

        Target CITY = Target.the("City field")
                            .locatedBy("#city")
                            .waitingForNoMoreThan(Duration.ofSeconds(0));

        aster.attemptsTo(
                Open.browserOn(demoPage),
                Ensure.that(CITY.waitingForNoMoreThan(Duration.ofSeconds(4)))
                      .value()
                      .isEqualTo("Marseille")
        );
    }

}
