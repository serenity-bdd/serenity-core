package net.serenitybdd.screenplay.webtests.integration.actions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.actions.SelectFromOptions;
import net.serenitybdd.screenplay.questions.SelectedValue;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.ui.PageElement;
import net.serenitybdd.screenplay.webtests.integration.ScreenplayInteractionTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class SelectTest extends ScreenplayInteractionTestBase {

    private final static Target COLOR_DROPDOWN = PageElement.withNameOrId("color");

    @Test
    public void selectFromADropdownUsingATarget() {
        dina.attemptsTo(SelectFromOptions.byVisibleText("Red").from(COLOR_DROPDOWN));
        assertThat(dina.asksFor(SelectedValue.of(COLOR_DROPDOWN))).isEqualTo("red");
    }

    @Test
    public void selectFromADropdownUsingCSS() {
        dina.attemptsTo(SelectFromOptions.byVisibleText("Red").from("#color"));
        assertThat(dina.asksFor(SelectedValue.of(COLOR_DROPDOWN))).isEqualTo("red");
    }

    @Test
    public void selectFromADropdownUsingALocator() {
        dina.attemptsTo(SelectFromOptions.byVisibleText("Red").from(By.id("color")));
        assertThat(dina.asksFor(SelectedValue.of(COLOR_DROPDOWN))).isEqualTo("red");
    }

    @Test
    public void selectFromADropdownByValue() {
        dina.attemptsTo(SelectFromOptions.byValue("red").from(COLOR_DROPDOWN));
        assertThat(dina.asksFor(SelectedValue.of(COLOR_DROPDOWN))).isEqualTo("red");
    }

    @Test
    public void selectFromADropdownByIndex() {
        dina.attemptsTo(SelectFromOptions.byIndex(1).from(COLOR_DROPDOWN));
        assertThat(dina.asksFor(SelectedValue.of(COLOR_DROPDOWN))).isEqualTo("green");
    }

    //
//    @Test
//    public void clickOnAButtonUsingASelector() {
//
//        assertThat(dina.asksFor(Text.of(BUTTON_STATE))).isEqualTo("Unclicked");
//
//        dina.attemptsTo(Click.on("#button"));
//
//        assertThat(dina.asksFor(Text.of(BUTTON_STATE))).isEqualTo("Clicked");
//    }
//
//    @Test
//    public void clickOnAButtonUsingAByLocator() {
//
//        assertThat(dina.asksFor(Text.of(BUTTON_STATE))).isEqualTo("Unclicked");
//
//        dina.attemptsTo(Click.on(By.id("button")));
//
//        assertThat(dina.asksFor(Text.of(BUTTON_STATE))).isEqualTo("Clicked");
//    }
//
//    @Test
//    public void clickOnACheckbox() {
//
//        assertThat(dina.asksFor(SelectedStatus.of(CHECKBOX))).isFalse();
//
//        dina.attemptsTo(Click.on(CHECKBOX));
//
//        assertThat(dina.asksFor(SelectedStatus.of(CHECKBOX))).isTrue();
//    }
//
//    @Test
//    public void clickOnAButtonWaitingForItToBeEnabled() {
//
//        assertThat(dina.asksFor(Text.of(DELAYED_BUTTON_STATE))).isEqualTo("Unclicked");
//
//        dina.attemptsTo(Click.on("#delayed-button").afterWaitingUntilEnabled());
//
//        assertThat(dina.asksFor(Text.of(DELAYED_BUTTON_STATE))).isEqualTo("Clicked");
//    }
//
//    @Test
//    public void clickOnAButtonWaitingForItToBePresent() {
//
//        assertThat(dina.asksFor(Text.of(INVISIBLE_BUTTON_STATE))).isEqualTo("Unclicked");
//
//        dina.attemptsTo(Click.on("#invisible-button").afterWaitingUntilPresent());
//
//        assertThat(dina.asksFor(Text.of(INVISIBLE_BUTTON_STATE))).isEqualTo("Clicked");
//    }

}
