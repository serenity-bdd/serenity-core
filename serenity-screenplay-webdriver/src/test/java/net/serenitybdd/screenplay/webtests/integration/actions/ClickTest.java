package net.serenitybdd.screenplay.webtests.integration.actions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.questions.SelectedStatus;
import net.serenitybdd.screenplay.questions.Text;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.ui.PageElement;
import net.serenitybdd.screenplay.webtests.integration.ScreenplayInteractionTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class ClickTest extends ScreenplayInteractionTestBase {

    private final static Target BUTTON = PageElement.withNameOrId("button");
    private final static Target BUTTON_STATE = PageElement.withNameOrId("button-state");
    private final static Target DELAYED_BUTTON_STATE = PageElement.withNameOrId("delayed-button-state");
    private final static Target INVISIBLE_BUTTON_STATE = PageElement.withNameOrId("invisible-button-state");
    private final static Target CHECKBOX = PageElement.withNameOrId("checkbox");

    @Test
    public void clickOnAButtonUsingATarget() {

        assertThat(dina.asksFor(Text.of(BUTTON_STATE))).isEqualTo("Unclicked");

        dina.attemptsTo(Click.on(BUTTON));

        assertThat(dina.asksFor(Text.of(BUTTON_STATE))).isEqualTo("Clicked");
    }

    @Test
    public void clickOnAButtonUsingASelector() {

        assertThat(dina.asksFor(Text.of(BUTTON_STATE))).isEqualTo("Unclicked");

        dina.attemptsTo(Click.on("#button"));

        assertThat(dina.asksFor(Text.of(BUTTON_STATE))).isEqualTo("Clicked");
    }

    @Test
    public void clickOnAButtonUsingAByLocator() {

        assertThat(dina.asksFor(Text.of(BUTTON_STATE))).isEqualTo("Unclicked");

        dina.attemptsTo(Click.on(By.id("button")));

        assertThat(dina.asksFor(Text.of(BUTTON_STATE))).isEqualTo("Clicked");
    }

    @Test
    public void clickOnACheckbox() {

        assertThat(dina.asksFor(SelectedStatus.of(CHECKBOX))).isFalse();

        dina.attemptsTo(Click.on(CHECKBOX));

        assertThat(dina.asksFor(SelectedStatus.of(CHECKBOX))).isTrue();
    }

    @Test
    public void clickOnAButtonWaitingForItToBeEnabled() {

        assertThat(dina.asksFor(Text.of(DELAYED_BUTTON_STATE))).isEqualTo("Unclicked");

        dina.attemptsTo(Click.on("#delayed-button").afterWaitingUntilEnabled());

        assertThat(dina.asksFor(Text.of(DELAYED_BUTTON_STATE))).isEqualTo("Clicked");
    }

    @Test
    public void clickOnAButtonWaitingForItToBePresent() {

        assertThat(dina.asksFor(Text.of(INVISIBLE_BUTTON_STATE))).isEqualTo("Unclicked");

        dina.attemptsTo(Click.on("#invisible-button").afterWaitingUntilPresent());

        assertThat(dina.asksFor(Text.of(INVISIBLE_BUTTON_STATE))).isEqualTo("Clicked");
    }

}
