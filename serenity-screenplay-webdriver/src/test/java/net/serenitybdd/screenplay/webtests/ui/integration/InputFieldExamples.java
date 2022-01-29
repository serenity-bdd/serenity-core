package net.serenitybdd.screenplay.webtests.ui.integration;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.actions.type.Type;
import net.serenitybdd.screenplay.annotations.CastMember;
import net.serenitybdd.screenplay.ui.InputField;
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
 * Working with Input Fields
 */
@RunWith(SerenityRunner.class)
@SingleBrowser
public class InputFieldExamples {

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
                Open.url("classpath:/sample-web-site/screenplay/ui-elements/forms/input-fields.html")
        );
    }

    private String getResult() {
        return driver.findElement(By.id("result")).getText();
    }
    
    @Test
    public void identifyingATextFieldByName() {
        sarah.attemptsTo(
                Type.theValue("Some value").into(InputField.called("fieldname"))
        );
        assertThat(getResult()).isEqualTo("Some value");
    }

    @Test
    public void identifyingATextFieldByDataTestAttribute() {
        sarah.attemptsTo(
                Type.theValue("Some value").into(InputField.called("field-name"))
        );
        assertThat(getResult()).isEqualTo("Some value");
    }

    @Test
    public void identifyingATextFieldById() {
        sarah.attemptsTo(
                Type.theValue("Some value").into(InputField.called("field"))
        );
        assertThat(getResult()).isEqualTo("Some value");
    }

    @Test
    public void identifyingATextFieldByPlaceholder() {
        sarah.attemptsTo(
                Type.theValue("Some value").into(InputField.withPlaceholder("value goes here"))
        );
        assertThat(getResult()).isEqualTo("Some value");
    }

    @Test
    public void identifyingATextFieldByClassname() {
        sarah.attemptsTo(
                Type.theValue("Some value").into(InputField.called("field-style"))
        );
        assertThat(getResult()).isEqualTo("Some value");
    }

    @Test
    public void identifyingATextFieldByLabel() {
        sarah.attemptsTo(Type.theValue("Some value").into(InputField.withLabel("Customer Name")));
        assertThat(getResult()).isEqualTo("Some value");
    }

    @Test
    public void identifyingATextFieldByAriaLabel() {
        sarah.attemptsTo(Type.theValue("Some value").into(InputField.called("ARIA Label")));
        assertThat(getResult()).isEqualTo("Some value");
    }

}
