package net.serenitybdd.screenplay.webtests.integration.actions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.actions.DoubleClick;
import net.serenitybdd.screenplay.questions.Text;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.ui.PageElement;
import net.serenitybdd.screenplay.webtests.integration.ScreenplayInteractionTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class DoubleClickTest extends ScreenplayInteractionTestBase {

    private final static Target BUTTON = PageElement.withNameOrId("button");
    private final static Target BUTTON_STATE = PageElement.withNameOrId("button-state");

    @Test
    public void clickOnAButtonUsingATarget() {

        assertThat(dina.asksFor(Text.of(BUTTON_STATE))).isEqualTo("Unclicked");

        dina.attemptsTo(DoubleClick.on(BUTTON));

        assertThat(dina.asksFor(Text.of(BUTTON_STATE))).isEqualTo("Clicked");
    }

    @Test
    public void clickOnAButtonUsingASelector() {

        assertThat(dina.asksFor(Text.of(BUTTON_STATE))).isEqualTo("Unclicked");

        dina.attemptsTo(DoubleClick.on("#button"));

        assertThat(dina.asksFor(Text.of(BUTTON_STATE))).isEqualTo("Clicked");
    }

    @Test
    public void clickOnAButtonUsingAByLocator() {

        assertThat(dina.asksFor(Text.of(BUTTON_STATE))).isEqualTo("Unclicked");

        dina.attemptsTo(DoubleClick.on(By.id("button")));

        assertThat(dina.asksFor(Text.of(BUTTON_STATE))).isEqualTo("Clicked");
    }

}
