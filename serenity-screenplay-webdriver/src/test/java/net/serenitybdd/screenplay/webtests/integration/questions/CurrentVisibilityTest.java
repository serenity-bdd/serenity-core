package net.serenitybdd.screenplay.webtests.integration.questions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.questions.CurrentVisibility;
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
public class CurrentVisibilityTest extends ScreenplayInteractionTestBase {

    private final static Target VISIBLE_BUTTON = PageElement.withNameOrId("button");
    private final static Target INVISIBLE_BUTTON = PageElement.withNameOrId("hidden-button");
    private final static Target DELAYED_BUTTON = PageElement.withNameOrId("invisible-button");
    private final static Target MISSING_BUTTON = PageElement.withNameOrId("does-not-exist");
    private final static Target BUTTONS = PageElement.withCSSClass("button");

    @Before
    public void setTimeout() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
    }

    @Test
    public void checkIfAnElementIsVisibleUsingATarget() {
        assertThat(dina.asksFor(CurrentVisibility.of(VISIBLE_BUTTON))).isTrue();
    }

    @Test
    public void checkIfAnElementIsNotVisibledUsingATarget() {
        assertThat(dina.asksFor(CurrentVisibility.of(INVISIBLE_BUTTON))).isFalse();
    }

    @Test
    public void checkIfAMissingElementIsNotVisibledUsingATarget() {
        assertThat(dina.asksFor(CurrentVisibility.of(MISSING_BUTTON))).isFalse();
    }

    @Test
    public void checkIfAnElementIsNotYetVisibleUsingATarget() {
        assertThat(dina.asksFor(CurrentVisibility.of(DELAYED_BUTTON))).isFalse();
    }

    @Test
    public void checkIfAnElementIsVisibleUsingALocator() {
        assertThat(dina.asksFor(CurrentVisibility.of("#button"))).isTrue();
    }

    @Test
    public void checkIfAnElementIsVisibleUsingABy() {
        assertThat(dina.asksFor(CurrentVisibility.of(By.id("button")))).isTrue();
    }

    @Test
    public void checkIfSeveralElementsAreEnabled() {
        assertThat(dina.asksFor(CurrentVisibility.ofEach(BUTTONS))).containsExactly(true, true, false);

    }

    @Test
    public void checkIfSeveralElementsAreEnabledByLocator() {
        assertThat(dina.asksFor(CurrentVisibility.ofEach(By.cssSelector(".button")))).containsExactly(true, true, false);
    }

    @Test
    public void checkIfSeveralElementsAreEnabledByString() {
        assertThat(dina.asksFor(CurrentVisibility.ofEach(".button"))).containsExactly(true, true, false);
    }

}
