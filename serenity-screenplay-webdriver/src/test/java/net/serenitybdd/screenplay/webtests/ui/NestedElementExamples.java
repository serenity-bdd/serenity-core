package net.serenitybdd.screenplay.webtests.ui;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.questions.Text;
import net.serenitybdd.screenplay.ui.Button;
import net.serenitybdd.screenplay.ui.PageElement;
import net.thucydides.core.annotations.Managed;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Working with HTML buttons
 */
@RunWith(SerenityRunner.class)
public class NestedElementExamples {

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
                Open.url("classpath:/sample-web-site/screenplay/ui-elements/forms/nested-elements.html")
        );
    }

    @Test
    public void clickingOnAButtonElement() {
        sarah.attemptsTo(
                Click.on(Button.called("Submit").inside("#section1"))
        );
        assertThat(Text.of("#result").answeredBy(sarah)).isEqualTo("Section 1");
    }

    @Test
    public void clickingOnAButtonElementFurtherDownThePage() {
        sarah.attemptsTo(
                Click.on(Button.called("Submit").inside("#section2"))
        );
        assertThat(Text.of("#result").answeredBy(sarah)).isEqualTo("Section 2");
    }

    @Test
    public void findingAnElementContainingText() {
        assertThat(Text.of(PageElement.containingText("Section 1")).answeredBy(sarah)).isEqualTo("Section 1");
    }

    @Test
    public void findingAnElementContainingTextAndMatchingASelector() {
        assertThat(Text.of(PageElement.containingText(".section","Section 1")).answeredBy(sarah)).contains("Section 1");
        assertThat(Text.of(PageElement.containingText(".section","Section 2")).answeredBy(sarah)).contains("Section 2");
    }

    @Test
    public void findingAnElementUsingContainingText() {
        sarah.attemptsTo(
                Click.on(Button.called("Submit")
                        .inside(PageElement.containingText(".section","Section 2")))
        );
        assertThat(Text.of("#result").answeredBy(sarah)).isEqualTo("Section 2");
    }

}
