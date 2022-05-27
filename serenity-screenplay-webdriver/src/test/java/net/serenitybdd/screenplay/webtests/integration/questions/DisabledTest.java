package net.serenitybdd.screenplay.webtests.integration.questions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.questions.Disabled;
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
public class DisabledTest extends ScreenplayInteractionTestBase {

    private final static Target ENABLED_BUTTON= PageElement.withNameOrId("button");
    private final static Target DISABLED_BUTTON = PageElement.withNameOrId("disabled-button");
    private final static Target DELAYED_BUTTON = PageElement.withNameOrId("delayed-button");
    private final static Target BUTTONS = PageElement.withCSSClass("button");

    @Before
    public void setTimeout() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
    }

    @Test
    public void checkIfAnElementIsDisabledUsingATarget() {
        assertThat(dina.asksFor(Disabled.of(ENABLED_BUTTON))).isFalse();
    }

    @Test
    public void checkIfAnElementIsNotDisabledUsingATarget() {
        assertThat(dina.asksFor(Disabled.of(DISABLED_BUTTON))).isTrue();
    }

    @Test
    public void checkIfAnElementIsNotYetDisabledUsingATarget() {
        assertThat(dina.asksFor(Disabled.of(DELAYED_BUTTON))).isTrue();
    }

    @Test
    public void checkIfAnElementIsDisabledUsingALocator() {
        assertThat(dina.asksFor(Disabled.of("#button"))).isFalse();
    }

    @Test
    public void checkIfAnElementIsDisabledUsingABy() {
        assertThat(dina.asksFor(Disabled.of(By.id("button")))).isFalse();
    }

    @Test
    public void checkIfSeveralElementsAreDisabled() {
        assertThat(dina.asksFor(Disabled.ofEach(BUTTONS))).containsExactly(false, true, false);

    }

    @Test
    public void checkIfSeveralElementsAreDisabledByLocator() {
        assertThat(dina.asksFor(Disabled.ofEach(By.cssSelector(".button")))).containsExactly(false, true, false);
    }

    @Test
    public void checkIfSeveralElementsAreDisabledByString() {
        assertThat(dina.asksFor(Disabled.ofEach(".button"))).containsExactly(false, true, false);
    }

}
