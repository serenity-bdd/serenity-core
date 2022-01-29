package net.serenitybdd.screenplay.webtests.ui.integration;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.annotations.CastMember;
import net.serenitybdd.screenplay.questions.Text;
import net.serenitybdd.screenplay.ui.Button;
import net.serenitybdd.screenplay.ui.Link;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.SingleBrowser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Working with Anchors
 */
@RunWith(SerenityRunner.class)
@SingleBrowser
public class LinkExamples {

    @Managed(driver = "chrome", options = "--headless")
    WebDriver driver;

    @CastMember(name = "Sarah", browserField = "driver")
    Actor sarah;

    @BeforeClass
    public static void setupDriver() {
        WebDriverManager.chromedriver().setup();
    }

    @Before
    public void openBrowser() {
        sarah.attemptsTo(
                Open.url("classpath:/sample-web-site/screenplay/ui-elements/elements/links.html")
        );
    }

    private String getResult() {
        return driver.findElement(By.id("result")).getText();
    }
    
    @Test
    public void clickingOnALink() {
        sarah.attemptsTo(
                Click.on(Link.called("Link 1"))
        );

        assertThat(getResult()).isEqualTo("Link 1");
    }

    @Test
    public void clickingOnALinkWithATitle() {
        sarah.attemptsTo(
                Click.on(Link.withTitle("Link Number 2"))
        );

        assertThat(getResult()).isEqualTo("Link 2");
    }

    @Test
    public void clickingOnALinkWithATitleInADifferentCase() {
        sarah.attemptsTo(
                Click.on(Link.withTitle("link number 2"))
        );

        assertThat(getResult()).isEqualTo("Link 2");
    }

    @Test
    public void clickingOnAPartialLink() {
        sarah.attemptsTo(
                Click.on(Link.containing("ink 2"))
        );

        assertThat(getResult()).isEqualTo("Link 2");
    }

    @Test
    public void clickingOnAPartialLinkStartingWith() {
        sarah.attemptsTo(
                Click.on(Link.startingWith("Lin"))
        );

        assertThat(getResult()).isEqualTo("Link 1");
    }

    @Test
    public void clickingOnALinkWithATitleContainingAnApostrophe() {
        sarah.attemptsTo(Click.on(Link.withTitle("The link's title")));

        assertThat(getResult()).isEqualTo("Link 4");
    }

    @Test
    public void clickingOnALinkWithATextContainingAnApostrophe() {
        sarah.attemptsTo(Click.on(Link.called("Link's 5")));

        assertThat(getResult()).isEqualTo("Link 5");
    }

    @Test
    public void clickingOnALinkWithAnIcon() {
        sarah.attemptsTo(
                Click.on(Link.withIcon("glyphicon-cloud"))
        );

        assertThat(getResult()).isEqualTo("Link 3");
    }

}
