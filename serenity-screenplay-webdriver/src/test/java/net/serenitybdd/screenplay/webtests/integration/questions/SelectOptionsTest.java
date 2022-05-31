package net.serenitybdd.screenplay.webtests.integration.questions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.questions.SelectOptions;
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
public class SelectOptionsTest extends ScreenplayInteractionTestBase {

    private final static Target COLOR_DROPDOWN = PageElement.locatedBy("#color");

    @Test
    public void checkForTheSelectedValueOfAFieldUsingATarget() {
        assertThat(dina.asksFor(SelectOptions.of(COLOR_DROPDOWN))).containsExactly("Red","Green","Blue");
    }

    @Test
    public void checkForTheSelectedValueOfAFieldUsingALocator() {
        assertThat(dina.asksFor(SelectOptions.of("#color"))).containsExactly("Red","Green","Blue");
    }

    @Test
    public void checkForTheSelectedValueOfAFieldUsingAByLocator() {
        assertThat(dina.asksFor(SelectOptions.of(By.id("color")))).containsExactly("Red","Green","Blue");
    }


}
