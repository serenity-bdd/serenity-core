package net.serenitybdd.screenplay.webtests.integration.questions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.questions.SelectedValue;
import net.serenitybdd.screenplay.questions.SelectedValue;
import net.serenitybdd.screenplay.questions.SelectedVisibleTextValue;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.ui.PageElement;
import net.serenitybdd.screenplay.webtests.integration.ScreenplayInteractionTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class SelectedValueTest extends ScreenplayInteractionTestBase {

    private final static Target COLOR_DROPDOWN = PageElement.locatedBy("#color");

    @Test
    public void checkForTheSelectedValueOfAFieldUsingATarget() {
        assertThat(dina.asksFor(SelectedValue.of(COLOR_DROPDOWN))).isEqualTo("green");
        assertThat(dina.asksFor(SelectedVisibleTextValue.of(COLOR_DROPDOWN))).isEqualTo("Green");
    }

    @Test
    public void checkForTheSelectedValueOfAFieldUsingALocator() {
        assertThat(dina.asksFor(SelectedValue.of("#color"))).isEqualTo("green");
        assertThat(dina.asksFor(SelectedVisibleTextValue.of("#color"))).isEqualTo("Green");
    }

    @Test
    public void checkForTheSelectedValueOfAFieldUsingAByLocator() {
        assertThat(dina.asksFor(SelectedValue.of(By.id("color")))).isEqualTo("green");
        assertThat(dina.asksFor(SelectedVisibleTextValue.of(By.id("color")))).isEqualTo("Green");
    }


}
