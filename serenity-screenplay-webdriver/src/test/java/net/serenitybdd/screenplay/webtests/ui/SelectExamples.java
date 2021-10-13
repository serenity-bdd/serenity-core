package net.serenitybdd.screenplay.webtests.ui;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Clear;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.actions.SelectFromOptions;
import net.serenitybdd.screenplay.questions.SelectedStatus;
import net.serenitybdd.screenplay.questions.SelectedValue;
import net.serenitybdd.screenplay.questions.SelectedValues;
import net.serenitybdd.screenplay.questions.SelectedVisibleTextValues;
import net.serenitybdd.screenplay.ui.Deselect;
import net.serenitybdd.screenplay.ui.Dropdown;
import net.serenitybdd.screenplay.ui.RadioButton;
import net.serenitybdd.screenplay.ui.Select;
import net.thucydides.core.annotations.Managed;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Working with Select dropdown fields
 */
@RunWith(SerenityRunner.class)
public class SelectExamples {

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
                Open.url("classpath:/sample-web-site/screenplay/ui-elements/forms/selects.html")
        );
    }

    @Test
    public void identifyingADropdownByID() {
        sarah.attemptsTo(
                Select.value("volvo").from(Dropdown.called("cars"))
        );
        assertThat(SelectedValue.of("#cars").answeredBy(sarah)).isEqualTo("volvo");
    }

    @Test
    public void identifyingADropdownByLabel() {
        sarah.attemptsTo(
                Select.value("volvo").from(Dropdown.withLabel("Choose a car:"))
        );
        assertThat(SelectedValue.of("#cars").answeredBy(sarah)).isEqualTo("volvo");
    }

    @Test
    public void identifyingADropdownByDefaultOption() {
        sarah.attemptsTo(
                Select.value("volvo").from(Dropdown.withDefaultOption("---Pick Your Car---"))
        );
        assertThat(SelectedValue.of("#cars").answeredBy(sarah)).isEqualTo("volvo");
    }

    @Test
    public void selectingAnOptionByVisibleText() {
        sarah.attemptsTo(
                Select.option("Volvo").from(Dropdown.called("cars"))
        );
        assertThat(SelectedValue.of("#cars").answeredBy(sarah)).isEqualTo("volvo");
    }

    @Test
    public void selectingMultipleOptionsByVisibleText() {
        sarah.attemptsTo(
                Select.options("Spitfire","Hurricane").from(Dropdown.called("planes"))
        );
        assertThat(SelectedValues.of("#planes").answeredBy(sarah)).contains("spitfire","hurricane");
    }

    @Test
    public void clearSelectedOptions() {
        sarah.attemptsTo(
                Select.options("Spitfire","Hurricane").from(Dropdown.called("planes")),
                Deselect.allOptionsFrom(Dropdown.called("planes"))
        );
        assertThat(SelectedValues.of("#planes").answeredBy(sarah)).isEmpty();
    }


    @Test
    public void identifyingASelectOptionByValue() {
        sarah.attemptsTo(
                Select.value("volvo").from(Dropdown.called("cars"))
        );
        assertThat(SelectedValue.of("#cars").answeredBy(sarah)).isEqualTo("volvo");
    }

    @Test
    public void selectingMultipleOptionsByValue() {
        sarah.attemptsTo(
                Select.values("spitfire","hurricane").from(Dropdown.called("planes"))
        );
        assertThat(SelectedVisibleTextValues.of("#planes").answeredBy(sarah)).contains("Spitfire","Hurricane");
    }

    @Test
    public void identifyingASelectOptionByIndex() {
        sarah.attemptsTo(
                Select.optionNumber(1).from(Dropdown.called("cars"))
        );
        assertThat(SelectedValue.of("#cars").answeredBy(sarah)).isEqualTo("volvo");
    }

    @Test
    public void selectingMultipleOptionsByIndex() {
        sarah.attemptsTo(
                Select.optionNumbers(0,1).from(Dropdown.called("planes"))
        );
        assertThat(SelectedVisibleTextValues.of("#planes").answeredBy(sarah)).contains("Spitfire","Hurricane");
    }

}
