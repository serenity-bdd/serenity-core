package net.serenitybdd.screenplay.webtests.ui;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.questions.SelectedStatus;
import net.serenitybdd.screenplay.ui.Checkbox;
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
public class CheckBoxExamples {

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
                Open.url("classpath:/sample-web-site/screenplay/ui-elements/forms/checkboxes.html")
        );
    }

    @Test
    public void identifyingACheckBoxById() {
        sarah.attemptsTo(Click.on(Checkbox.called("vehicle1")));
        assertThat(SelectedStatus.of("#vehicle1").answeredBy(sarah)).isTrue();
    }


    @Test
    public void identifyingACheckBoxByLabel() {
        sarah.attemptsTo(Click.on(Checkbox.withLabel("I have a bike")));
        assertThat(SelectedStatus.of("#vehicle1").answeredBy(sarah)).isTrue();
    }

    @Test
    public void identifyingACheckBoxWithANestedLabel() {
        sarah.attemptsTo(Click.on(Checkbox.withLabel("I have a motorbike")));
        assertThat(SelectedStatus.of("#vehicle4").answeredBy(sarah)).isTrue();
    }

    @Test
    public void identifyingACheckBoxByClass() {
        sarah.attemptsTo(Click.on(Checkbox.called("field-style")));
        assertThat(SelectedStatus.of("#vehicle2").answeredBy(sarah)).isTrue();
    }

    @Test
    public void identifyingACheckBoxByValue() {
        sarah.attemptsTo(Click.on(Checkbox.withValue("Car")));
        assertThat(SelectedStatus.of("#vehicle2").answeredBy(sarah)).isTrue();
    }
}
