package net.serenitybdd.screenplay.webtests.integration.questions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.questions.Displayed;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.ui.PageElement;
import net.serenitybdd.screenplay.webtests.integration.ScreenplayInteractionTestBase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class DisplayedTest extends ScreenplayInteractionTestBase {

    private final static Target ENABLED_BUTTON= PageElement.withNameOrId("button");
    private final static Target DISABLED_BUTTON = PageElement.withNameOrId("disabled-button");
    private final static Target HIDDEN_BUTTON = PageElement.withNameOrId("hidden-button");
    private final static Target MISSING_BUTTON = PageElement.withNameOrId("does-not-exist");
    private final static Target DELAYED_BUTTON = PageElement.withNameOrId("delayed-button");
    private final static Target BUTTONS = PageElement.withCSSClass("button");

    @Test
    public void checkIfAnElementIsDisplayedUsingATarget() {
        assertThat(dina.asksFor(Displayed.of(ENABLED_BUTTON))).isTrue();
    }

    @Test
    public void checkIfAnElementIsNotDisplayedUsingATarget() {
        assertThat(dina.asksFor(Displayed.of(DISABLED_BUTTON))).isTrue();
    }

    @Test
    public void checkIfAnElementIsNotYetDisplayedUsingATarget() {
        assertThat(dina.asksFor(Displayed.of(DELAYED_BUTTON))).isTrue();
    }

    @Test
    public void checkIfAnElementIsDisplayedUsingALocator() {
        assertThat(dina.asksFor(Displayed.of("#button"))).isTrue();
    }

    @Test
    public void checkIfAnElementIsDisplayedUsingABy() {
        assertThat(dina.asksFor(Displayed.of(By.id("button")))).isTrue();
    }

    @Test
    public void hiddenElementsAreNotConsideredDisplayed() {
        assertThat(dina.asksFor(Displayed.of(HIDDEN_BUTTON))).isFalse();
    }

    @Test
    public void missingElementsAreNotConsideredDisplayed() {
        assertThat(dina.asksFor(Displayed.of(MISSING_BUTTON))).isFalse();
    }

    @Test
    public void checkIfSeveralElementsAreDisplayed() {
        assertThat(dina.asksFor(Displayed.ofEach(BUTTONS))).containsExactly(true, true, false);

    }

    @Test
    public void checkIfSeveralElementsAreDisplayedByLocator() {
        assertThat(dina.asksFor(Displayed.ofEach(By.cssSelector(".button")))).containsExactly(true, true, false);
    }

    @Test
    public void checkIfSeveralElementsAreDisplayedByString() {
        assertThat(dina.asksFor(Displayed.ofEach(".button"))).containsExactly(true, true, false);
    }

}
