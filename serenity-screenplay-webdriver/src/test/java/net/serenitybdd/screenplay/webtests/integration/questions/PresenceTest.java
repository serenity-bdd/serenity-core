package net.serenitybdd.screenplay.webtests.integration.questions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.questions.Presence;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.ui.PageElement;
import net.serenitybdd.screenplay.webtests.integration.ScreenplayInteractionTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class PresenceTest extends ScreenplayInteractionTestBase {

    private final static Target ENABLED_BUTTON= PageElement.withNameOrId("button");
    private final static Target DISABLED_BUTTON = PageElement.withNameOrId("disabled-button");
    private final static Target HIDDEN_BUTTON = PageElement.withNameOrId("hidden-button");
    private final static Target MISSING_BUTTON = PageElement.withNameOrId("does-not-exist");
    private final static Target DELAYED_BUTTON = PageElement.withNameOrId("delayed-button");
    private final static Target BUTTONS = PageElement.withCSSClass("button");

    @Test
    public void checkIfAnElementIsPresenceUsingATarget() {
        assertThat(dina.asksFor(Presence.of(ENABLED_BUTTON))).isTrue();
    }

    @Test
    public void checkIfAnElementIsNotPresenceUsingATarget() {
        assertThat(dina.asksFor(Presence.of(DISABLED_BUTTON))).isTrue();
    }

    @Test
    public void checkIfAnElementIsNotYetPresenceUsingATarget() {
        assertThat(dina.asksFor(Presence.of(DELAYED_BUTTON))).isTrue();
    }

    @Test
    public void checkIfAnElementIsPresenceUsingALocator() {
        assertThat(dina.asksFor(Presence.of("#button"))).isTrue();
    }

    @Test
    public void checkIfAnElementIsPresenceUsingABy() {
        assertThat(dina.asksFor(Presence.of(By.id("button")))).isTrue();
    }

    @Test
    public void hiddenElementsAreConsideredPresent() {
        assertThat(dina.asksFor(Presence.of(HIDDEN_BUTTON))).isTrue();
    }

    @Test
    public void missingElementsAreNotConsideredPresence() {
        assertThat(dina.asksFor(Presence.of(MISSING_BUTTON))).isFalse();
    }

    @Test
    public void checkIfSeveralElementsArePresence() {
        assertThat(dina.asksFor(Presence.ofEach(BUTTONS))).containsExactly(true, true, true);

    }

    @Test
    public void checkIfSeveralElementsArePresenceByLocator() {
        assertThat(dina.asksFor(Presence.ofEach(By.cssSelector(".button")))).containsExactly(true, true, true);
    }

    @Test
    public void checkIfSeveralElementsArePresenceByString() {
        assertThat(dina.asksFor(Presence.ofEach(".button"))).containsExactly(true, true, true);
    }

}
