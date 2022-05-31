package net.serenitybdd.screenplay.webtests.integration.actions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.actions.MoveMouse;
import net.serenitybdd.screenplay.questions.Text;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.ui.PageElement;
import net.serenitybdd.screenplay.webtests.integration.ScreenplayInteractionTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class MoveMouseTest extends ScreenplayInteractionTestBase {

    private final static Target BUTTON = PageElement.withNameOrId("move-button");
    private final static Target BUTTON_STATE = PageElement.withNameOrId("move-button-state");

    @Test
    public void movingToAButtonUsingATarget() {

        assertThat(dina.asksFor(Text.of(BUTTON_STATE))).isEmpty();

        dina.attemptsTo(MoveMouse.to(BUTTON));

        assertThat(dina.asksFor(Text.of(BUTTON_STATE))).isEqualTo("Moved");
    }

    @Test
    public void movingToAButtonUsingALocator() {

        assertThat(dina.asksFor(Text.of(BUTTON_STATE))).isEmpty();

        dina.attemptsTo(MoveMouse.to("#move-button"));

        assertThat(dina.asksFor(Text.of(BUTTON_STATE))).isEqualTo("Moved");
    }

    @Test
    public void movingToAButtonUsingAByLocator() {

        assertThat(dina.asksFor(Text.of(BUTTON_STATE))).isEmpty();

        dina.attemptsTo(MoveMouse.to(By.id("move-button")));

        assertThat(dina.asksFor(Text.of(BUTTON_STATE))).isEqualTo("Moved");
    }

    @Test
    public void movingToAButtonUsingATargetAndThenPerformAnAction() {

        assertThat(dina.asksFor(Text.of(BUTTON_STATE))).isEmpty();

        dina.attemptsTo(MoveMouse.to(BUTTON).andThen(actions -> actions.click()));

        assertThat(dina.asksFor(Text.of(BUTTON_STATE))).isEqualTo("Clicked");
    }
}
