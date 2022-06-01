package net.serenitybdd.screenplay.webtests.integration.questions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.questions.SelectedValue;
import net.serenitybdd.screenplay.questions.SelectedValues;
import net.serenitybdd.screenplay.questions.SelectedVisibleTextValue;
import net.serenitybdd.screenplay.questions.SelectedVisibleTextValues;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.ui.PageElement;
import net.serenitybdd.screenplay.webtests.integration.ScreenplayInteractionTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class SelectedMulipleValuesTest extends ScreenplayInteractionTestBase {

    private final static Target FRUIT_DROPDOWN = PageElement.locatedBy("#fruit");

    @Test
    public void checkForTheSelectedValueOfAFieldUsingATarget() {
        assertThat(dina.asksFor(SelectedValues.of(FRUIT_DROPDOWN))).containsExactly("apples","oranges");
        assertThat(dina.asksFor(SelectedVisibleTextValues.of(FRUIT_DROPDOWN))).containsExactly("Apples","Oranges");
    }

    @Test
    public void checkForTheSelectedValueOfAFieldUsingALocator() {
        assertThat(dina.asksFor(SelectedValues.of("#fruit"))).containsExactly("apples","oranges");
        assertThat(dina.asksFor(SelectedVisibleTextValues.of("#fruit"))).containsExactly("Apples","Oranges");
    }

    @Test
    public void checkForTheSelectedValueOfAFieldUsingAByLocator() {
        assertThat(dina.asksFor(SelectedValues.of(By.id("fruit")))).containsExactly("apples","oranges");
        assertThat(dina.asksFor(SelectedVisibleTextValues.of(By.id("fruit")))).containsExactly("Apples","Oranges");
    }


}
