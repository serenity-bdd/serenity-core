package net.serenitybdd.screenplay.webtests.ui;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.questions.Text;
import net.serenitybdd.screenplay.ui.Button;
import net.serenitybdd.screenplay.ui.Link;
import net.thucydides.core.annotations.Managed;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Working with Anchors
 */
@RunWith(SerenityRunner.class)
public class LinkExamples {

    @Managed(driver = "chrome", options = "--headless")
    WebDriver driver;

    Actor sarah = Actor.named("Sarah");

    @BeforeClass
    public static void setupDriver() {
        WebDriverManager.chromedriver().setup();
    }

    @Before
    public void openBrowser() {
        sarah.can(BrowseTheWeb.with(driver));

        sarah.attemptsTo(
                Open.url("classpath:/sample-web-site/screenplay/ui-elements/elements/links.html")
        );
    }

    @Test
    public void clickingOnALink() {
        sarah.attemptsTo(
                Click.on(Link.called("Link 1"))
        );

        assertThat(Text.of("#result").answeredBy(sarah)).isEqualTo("Link 1");
    }

    @Test
    public void clickingOnALinkWithATitle() {
        sarah.attemptsTo(
                Click.on(Link.withTitle("Link Number 2"))
        );

        assertThat(Text.of("#result").answeredBy(sarah)).isEqualTo("Link 2");
    }

    @Test
    public void clickingOnALinkWithATitleInADifferentCase() {
        sarah.attemptsTo(
                Click.on(Link.withTitle("link number 2"))
        );

        assertThat(Text.of("#result").answeredBy(sarah)).isEqualTo("Link 2");
    }

    @Test
    public void clickingOnAPartialLink() {
        sarah.attemptsTo(
                Click.on(Link.containing("ink 2"))
        );

        assertThat(Text.of("#result").answeredBy(sarah)).isEqualTo("Link 2");
    }
    @Test
    public void clickingOnALinkWithATitleContainingAnApostrophe() {
        sarah.attemptsTo(Click.on(Link.withTitle("The link's title")));

        assertThat(Text.of("#result").answeredBy(sarah)).isEqualTo("Link 4");
    }

    @Test
    public void clickingOnALinkWithATextContainingAnApostrophe() {
        sarah.attemptsTo(Click.on(Link.called("Link's 5")));

        assertThat(Text.of("#result").answeredBy(sarah)).isEqualTo("Link 5");
    }

    @Test
    public void clickingOnALinkWithAnIcon() {
        sarah.attemptsTo(
                Click.on(Link.called("Link 3"))
        );

        assertThat(Text.of("#result").answeredBy(sarah)).isEqualTo("Link 3");
    }

}
