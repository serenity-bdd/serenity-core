package net.serenitybdd.screenplay.webtests.ui.integration;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.annotations.CastMember;
import net.serenitybdd.screenplay.questions.SelectedStatus;
import net.serenitybdd.screenplay.ui.Checkbox;
import net.thucydides.core.annotations.Managed;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Working with Radio Buttons
 */
@RunWith(SerenityRunner.class)
public class CheckBoxExamples {

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
                Open.url("classpath:/sample-web-site/screenplay/ui-elements/forms/checkboxes.html")
        );
    }

    private boolean isSelected(String checkboxId) {
        return driver.findElement(By.id(checkboxId)).isSelected();
    }

    @Test
    public void identifyingACheckBoxById() {
        sarah.attemptsTo(Click.on(Checkbox.called("vehicle1")));
        assertTrue(isSelected("vehicle1"));
    }


    @Test
    public void identifyingACheckBoxByLabel() {
        sarah.attemptsTo(Click.on(Checkbox.withLabel("I have a bike")));
        assertTrue(isSelected("vehicle1"));
    }

    @Test
    public void identifyingACheckBoxWithANestedLabel() {
        sarah.attemptsTo(Click.on(Checkbox.withLabel("I have a motorbike")));
        assertTrue(isSelected("vehicle4"));
    }

    @Test
    public void identifyingACheckBoxByClass() {
        sarah.attemptsTo(Click.on(Checkbox.called("field-style")));
        assertTrue(isSelected("vehicle2"));
    }

    @Test
    public void identifyingACheckBoxByValue() {
        sarah.attemptsTo(Click.on(Checkbox.withValue("Car")));
        assertTrue(isSelected("vehicle2"));
    }
}
