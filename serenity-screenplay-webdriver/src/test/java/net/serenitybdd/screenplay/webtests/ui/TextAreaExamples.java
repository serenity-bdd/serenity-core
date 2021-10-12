package net.serenitybdd.screenplay.webtests.ui;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.actions.type.Type;
import net.serenitybdd.screenplay.questions.Value;
import net.serenitybdd.screenplay.ui.InputField;
import net.thucydides.core.annotations.Managed;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Working with Text Areas
 */
@RunWith(SerenityRunner.class)
public class TextAreaExamples {

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
                Open.url("classpath:/sample-web-site/screenplay/ui-elements/forms/textareas.html")
        );
    }

    @Test
    public void identifyingATextAreaByName() {
        sarah.attemptsTo(
                Type.theValue("Some value").into(InputField.called("description"))
        );
        assertThat(Value.of("#description").answeredBy(sarah)).isEqualTo("Some value");
    }

    @Test
    public void identifyingATextFieldByDataTestAttribute() {
        sarah.attemptsTo(
                Type.theValue("Some value").into(InputField.called("description"))
        );
        assertThat(Value.of("#description").answeredBy(sarah)).isEqualTo("Some value");
    }

    @Test
    public void identifyingATextFieldById() {
        sarah.attemptsTo(
                Type.theValue("Some value").into(InputField.called("description"))
        );
        assertThat(Value.of(InputField.withLabel("Description:")).answeredBy(sarah)).isEqualTo("Some value");
    }

    @Test
    public void identifyingATextFieldByLabel() {
        sarah.attemptsTo(Type.theValue("Some value").into(InputField.withLabel("Description:")));
        assertThat(Value.of(InputField.called("description")).answeredBy(sarah)).isEqualTo("Some value");
    }
}
