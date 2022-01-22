package net.serenitybdd.screenplay.webtests.ui.integration;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.annotations.CastMember;
import net.serenitybdd.screenplay.questions.Text;
import net.serenitybdd.screenplay.ui.Dropdown;
import net.serenitybdd.screenplay.ui.Link;
import net.thucydides.core.annotations.Managed;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Working with Anchors
 */
@RunWith(SerenityRunner.class)
public class DropdownExamples {

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
                Open.url("classpath:/sample-web-site/screenplay/ui-elements/forms/selects.html")
        );
    }

    @Test
    public void findADropdownValueByName() {
        sarah.attemptsTo(
                Click.on(Dropdown.called("cars"))
        );
    }

    @Test
    public void findADropdownValueByLabel() {
        sarah.attemptsTo(
                Click.on(Dropdown.withLabel("Choose a car:"))
        );
    }


    @Test
    public void findADropdownValueByDefaultOption() {
        sarah.attemptsTo(
                Click.on(Dropdown.withDefaultOption("---Pick Your Car---"))
        );
    }
}
