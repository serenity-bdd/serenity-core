package net.serenitybdd.screenplay.webtests.integration.questions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.questions.Attribute;
import net.serenitybdd.screenplay.questions.CheckboxValue;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.ui.PageElement;
import net.serenitybdd.screenplay.webtests.integration.ScreenplayInteractionTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class CheckboxValueTest extends ScreenplayInteractionTestBase {

    private final static Target CHECKBOX = PageElement.locatedBy("#checked-checkbox");
    private final static Target CHECKBOXES = PageElement.locatedBy(".checkbox-field");

    @Test
    public void checkForTheCheckboxValueOfAFieldUsingATarget() {
        assertThat(dina.asksFor(CheckboxValue.of(CHECKBOX))).isTrue();
    }

    @Test
    public void checkForTheCheckboxValueOfAFieldUsingALocator() {
        assertThat(dina.asksFor(CheckboxValue.of("#checked-checkbox"))).isTrue();
    }

    @Test
    public void checkForTheCheckboxValueOfAFieldUsingAByLocator() {
        assertThat(dina.asksFor(CheckboxValue.of(By.id("checked-checkbox")))).isTrue();
    }


    @Test
    public void checkForTheCheckboxValueOfSeveralFieldsUsingATarget() {
        assertThat(dina.asksFor(CheckboxValue.ofEach(CHECKBOXES))).containsExactly(false,true);
    }

    @Test
    public void checkForTheCheckboxValueOfSeveralFieldsUsingALocator() {
        assertThat(dina.asksFor(CheckboxValue.ofEach(".checkbox-field"))).containsExactly(false,true);
    }

    @Test
    public void checkForTheCheckboxValueOfSeveralFieldsUsingAByLocator() {
        assertThat(dina.asksFor(CheckboxValue.ofEach(By.cssSelector(".checkbox-field")))).containsExactly(false,true);
    }


}
