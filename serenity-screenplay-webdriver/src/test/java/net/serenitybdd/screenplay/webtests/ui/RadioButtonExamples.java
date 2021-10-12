package net.serenitybdd.screenplay.webtests.ui;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.actions.type.Type;
import net.serenitybdd.screenplay.questions.SelectedStatus;
import net.serenitybdd.screenplay.questions.Text;
import net.serenitybdd.screenplay.ui.InputField;
import net.serenitybdd.screenplay.ui.RadioButton;
import net.thucydides.core.annotations.Managed;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Working with Radio Buttons
 */
@RunWith(SerenityRunner.class)
public class RadioButtonExamples {

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
                Open.url("classpath:/sample-web-site/screenplay/ui-elements/forms/radio-buttons.html")
        );
    }

    @Test
    public void identifyingARadioButtonById() {
        sarah.attemptsTo(Click.on(RadioButton.called("html")));
        assertThat(SelectedStatus.of("#html").answeredBy(sarah)).isTrue();
    }


    @Test
    public void identifyingARadioButtonByLabel() {
        sarah.attemptsTo(Click.on(RadioButton.withLabel("Choose CSS")));
        assertThat(SelectedStatus.of("#css").answeredBy(sarah)).isTrue();
    }

    @Test
    public void identifyingARadioButtonByValue() {
        sarah.attemptsTo(Click.on(RadioButton.withValue("JavaScript")));
        assertThat(SelectedStatus.of("#javascript").answeredBy(sarah)).isTrue();
    }

}
