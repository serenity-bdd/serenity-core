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
                Open.url("classpath:/sample-web-site/screenplay/ui-elements/forms/multi-button-form.html")
        );
    }

    @Test
    public void clickingOnAButtonElement() {
        sarah.attemptsTo(
                Click.on(Button.called("A Button"))
        );
    }

    @Test
    public void clickingOnAButtonElementByARIALabel() {
        sarah.attemptsTo(
                Click.on(Button.called("ARIA Label"))
        );
    }

    @Test
    public void clickingOnAButtonElementByARIALabelByLabel() {
        sarah.attemptsTo(
                Click.on(Button.withLabel("ARIA Label"))
        );
    }

    @Test
    public void selectingAnInputFieldButtonByValue() {
        sarah.attemptsTo(
                Click.on(Button.called("Press Here"))
        );
    }
    @Test
    public void selectingASubmitInputByValue() {
        sarah.attemptsTo(
                Click.on(Button.called("Submit Me!"))
        );
    }

    @Test
    public void selectingAnHtmlButtonByName() {
        sarah.attemptsTo(
                Click.on(Button.called("submit-button"))
        );
    }

    @Test
    public void selectingAMaterialButtonByName() {
        sarah.attemptsTo(
                Click.on(Button.called("Material Button"))
        );
    }

    @Test
    public void selectingAnHtmlButtonByIcon() {
        sarah.attemptsTo(
                Click.on(Button.withIcon("glyphicon-home"))
        );
    }

    @Test
    public void selectingAnHtmlButtonByTestDataAttribute() {
        sarah.attemptsTo(
                Click.on(Button.called("submit-the-button"))
        );
    }
}
