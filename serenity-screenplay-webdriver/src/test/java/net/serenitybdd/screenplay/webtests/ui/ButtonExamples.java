package net.serenitybdd.screenplay.webtests.ui;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.ui.Button;
import net.thucydides.core.annotations.Managed;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * Working with HTML buttons
 */
@RunWith(SerenityRunner.class)
public class ButtonExamples {

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
                Open.url("classpath:/sample-web-site/screenplay/ui-elements/buttons/multi-button-form.html")
        );
    }

    @Test
    public void clickingOnAButtonElement() {
        sarah.attemptsTo(
                Click.on(Button.called("Cancel"))
        );
    }

    @Test
    public void selectingAnInputFieldButtonByValue() {
        sarah.attemptsTo(
                Click.on(Button.called("Submit"))
        );
    }

    @Test
    public void selectingAnHtmlButtonByName() {
        sarah.attemptsTo(
                Click.on(Button.called("submit-button"))
        );
    }

    @Test
    public void selectingAnHtmlButtonByTestDataAttribute() {
        sarah.attemptsTo(
                Click.on(Button.called("submit-the-button"))
        );
    }
}
