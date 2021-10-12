package net.serenitybdd.screenplay.webtests.ui;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.actions.type.Type;
import net.serenitybdd.screenplay.questions.Text;
import net.serenitybdd.screenplay.ui.Button;
import net.serenitybdd.screenplay.ui.InputField;
import net.thucydides.core.annotations.Managed;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Working with Input Fields
 */
@RunWith(SerenityRunner.class)
public class InputFieldExamples {

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
                Open.url("classpath:/sample-web-site/screenplay/ui-elements/forms/input-fields.html")
        );
    }

    @Test
    public void identifyingATextFieldByName() {
        sarah.attemptsTo(
                Type.theValue("Some value").into(InputField.called("fieldname"))
        );
        assertThat(Text.of("#result").answeredBy(sarah)).isEqualTo("Some value");
    }

    @Test
    public void identifyingATextFieldByDataTestAttribute() {
        sarah.attemptsTo(
                Type.theValue("Some value").into(InputField.called("field-name"))
        );
        assertThat(Text.of("#result").answeredBy(sarah)).isEqualTo("Some value");
    }

    @Test
    public void identifyingATextFieldById() {
        sarah.attemptsTo(
                Type.theValue("Some value").into(InputField.called("field"))
        );
        assertThat(Text.of("#result").answeredBy(sarah)).isEqualTo("Some value");
    }

    @Test
    public void identifyingATextFieldByPlaceholder() {
        sarah.attemptsTo(
                Type.theValue("Some value").into(InputField.withPlaceholder("value goes here"))
        );
        assertThat(Text.of("#result").answeredBy(sarah)).isEqualTo("Some value");
    }

    @Test
    public void identifyingATextFieldByLabel() {
        sarah.attemptsTo(Type.theValue("Some value").into(InputField.withLabel("Customer Name")));
        assertThat(Text.of("#result").answeredBy(sarah)).isEqualTo("Some value");
    }
}
