package net.serenitybdd.screenplay.webtests.integration.questions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.questions.SelectOptionValues;
import net.serenitybdd.screenplay.questions.SelectOptions;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.ui.PageElement;
import net.serenitybdd.screenplay.webtests.integration.ScreenplayInteractionTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class SelectOptionValuesTest extends ScreenplayInteractionTestBase {

    private final static Target COLOR_DROPDOWN = PageElement.locatedBy("#color");

    @Test
    public void checkForTheSelectedValueOfAFieldUsingATarget() {
        assertThat(dina.asksFor(SelectOptionValues.of(COLOR_DROPDOWN))).containsExactly("red","green","blue");
    }

    @Test
    public void checkForTheSelectedValueOfAFieldUsingALocator() {
        assertThat(dina.asksFor(SelectOptionValues.of("#color"))).containsExactly("red","green","blue");
    }

    @Test
    public void checkForTheSelectedValueOfAFieldUsingAByLocator() {
        assertThat(dina.asksFor(SelectOptionValues.of(By.id("color")))).containsExactly("red","green","blue");
    }


}
