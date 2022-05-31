package net.serenitybdd.screenplay.webtests.integration.actions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.actions.Clear;
import net.serenitybdd.screenplay.questions.Value;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.ui.PageElement;
import net.serenitybdd.screenplay.webtests.integration.ScreenplayInteractionTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class ClearTest extends ScreenplayInteractionTestBase {

    private final static Target POPULATED_INPUT = PageElement.withNameOrId("populated-input");
    private final static Target POPULATED_CHECKBOX = PageElement.withNameOrId("populated-checkbox");

    @Test
    public void clearAnInputFieldUsingATarget() {

        assertThat(dina.asksFor(Value.of(POPULATED_INPUT))).isEqualTo("Populated");

        dina.attemptsTo(Clear.field(POPULATED_INPUT));

        assertThat(dina.asksFor(Value.of(POPULATED_INPUT))).isEmpty();
    }

    @Test
    public void clearAnInputFieldUsingASelector() {

        assertThat(dina.asksFor(Value.of(POPULATED_INPUT))).isEqualTo("Populated");

        dina.attemptsTo(Clear.field("#populated-input"));

        assertThat(dina.asksFor(Value.of(POPULATED_INPUT))).isEmpty();
    }

    @Test
    public void clearAnInputFieldUsingAByLocator() {

        assertThat(dina.asksFor(Value.of(POPULATED_INPUT))).isEqualTo("Populated");

        dina.attemptsTo(Clear.field(By.id("populated-input")));

        assertThat(dina.asksFor(Value.of(POPULATED_INPUT))).isEmpty();
    }
}
