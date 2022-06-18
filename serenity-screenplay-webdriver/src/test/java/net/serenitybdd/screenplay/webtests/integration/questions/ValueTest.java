package net.serenitybdd.screenplay.webtests.integration.questions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.questions.Text;
import net.serenitybdd.screenplay.questions.TextValue;
import net.serenitybdd.screenplay.questions.Value;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.ui.PageElement;
import net.serenitybdd.screenplay.webtests.integration.ScreenplayInteractionTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class ValueTest extends ScreenplayInteractionTestBase {

    private final static Target FIRST_NAME_FIELD = PageElement.withNameOrId("firstName");

    @Test
    public void checkForValueOfAFieldUsingATarget() {
        assertThat(dina.asksFor(Value.of(FIRST_NAME_FIELD))).isEqualTo("Jo Grant");
    }

    @Test
    public void checkForValueOfAFieldUsingALocator() {
        assertThat(dina.asksFor(Value.of("#firstName"))).isEqualTo("Jo Grant");
    }

    @Test
    public void checkForValueOfAFieldUsingAByLocator() {
        assertThat(dina.asksFor(Value.of(By.id("firstName")))).isEqualTo("Jo Grant");
    }

    @Test
    public void checkForValueOfAFieldWithNoValueAttribute() {
        assertThat(dina.asksFor(Value.of(By.id("nickname")))).isEqualTo("");
    }

    @Test
    public void checkForValueOfSeveralFieldsUsingATarget() {
        assertThat(dina.asksFor(Value.ofEach("#firstName"))).containsExactly("Jo Grant");
    }

}
