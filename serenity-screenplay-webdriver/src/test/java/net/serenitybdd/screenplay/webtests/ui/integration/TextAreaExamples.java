package net.serenitybdd.screenplay.webtests.ui.integration;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.core.steps.WebDriverScenarios;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.actions.type.Type;
import net.serenitybdd.screenplay.annotations.CastMember;
import net.serenitybdd.screenplay.questions.Value;
import net.serenitybdd.screenplay.ui.InputField;
import net.thucydides.core.annotations.Managed;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Working with Text Areas
 */
@RunWith(SerenityRunner.class)
public class TextAreaExamples extends WebDriverScenarios {

    @Managed(driver = "chrome", options = "--headless")
    WebDriver driver;

    @CastMember(name = "Sarah", browserField = "driver")
    Actor sarah;

    @BeforeClass
    public static void setupDriver() {
        WebDriverManager.chromedriver().setup();
    }

    private String getDescription() {
        return $("#description").getValue();
    }

    @Before
    public void openBrowser() {
        sarah.attemptsTo(
                Open.url("classpath:/sample-web-site/screenplay/ui-elements/forms/textareas.html")
        );
    }

    @Test
    public void identifyingATextAreaByName() {
        sarah.attemptsTo(
                Type.theValue("Some value").into(InputField.called("description"))
        );
        assertThat(getDescription()).isEqualTo("Some value");
    }

    @Test
    public void identifyingATextFieldByDataTestAttribute() {
        sarah.attemptsTo(
                Type.theValue("Some value").into(InputField.called("description"))
        );
        assertThat(getDescription()).isEqualTo("Some value");
    }

    @Test
    public void identifyingATextFieldById() {
        sarah.attemptsTo(
                Type.theValue("Some value").into(InputField.called("description"))
        );
        assertThat(getDescription()).isEqualTo("Some value");
    }

    @Test
    public void identifyingATextFieldByLabel() {
        sarah.attemptsTo(Type.theValue("Some value").into(InputField.withLabel("Description:")));
        assertThat(getDescription()).isEqualTo("Some value");
    }
}
