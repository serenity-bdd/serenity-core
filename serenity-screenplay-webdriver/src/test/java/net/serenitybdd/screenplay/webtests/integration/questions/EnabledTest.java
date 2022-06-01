package net.serenitybdd.screenplay.webtests.integration.questions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.questions.Enabled;
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
public class EnabledTest extends ScreenplayInteractionTestBase {

    private final static Target ENABLED_BUTTON= PageElement.withNameOrId("button");
    private final static Target DISABLED_BUTTON = PageElement.withNameOrId("disabled-button");
    private final static Target DELAYED_BUTTON = PageElement.withNameOrId("delayed-button");
    private final static Target BUTTONS = PageElement.withCSSClass("button");

    @Before
    public void setTimeout() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
    }

    @Test
    public void checkIfAnElementIsEnabledUsingATarget() {
        assertThat(dina.asksFor(Enabled.of(ENABLED_BUTTON))).isTrue();
    }

    @Test
    public void checkIfAnElementIsNotEnabledUsingATarget() {
        assertThat(dina.asksFor(Enabled.of(DISABLED_BUTTON))).isFalse();
    }

    @Test
    public void checkIfAnElementIsNotYetEnabledUsingATarget() {
        assertThat(dina.asksFor(Enabled.of(DELAYED_BUTTON))).isTrue();
    }

    @Test
    public void checkIfAnElementIsEnabledUsingALocator() {
        assertThat(dina.asksFor(Enabled.of("#button"))).isTrue();
    }

    @Test
    public void checkIfAnElementIsEnabledUsingABy() {
        assertThat(dina.asksFor(Enabled.of(By.id("button")))).isTrue();
    }

    @Test
    public void checkIfSeveralElementsAreEnabled() {
        assertThat(dina.asksFor(Enabled.ofEach(BUTTONS))).containsExactly(true, false, true);

    }

    @Test
    public void checkIfSeveralElementsAreEnabledByLocator() {
        assertThat(dina.asksFor(Enabled.ofEach(By.cssSelector(".button")))).containsExactly(true, false, true);
    }

    @Test
    public void checkIfSeveralElementsAreEnabledByString() {
        assertThat(dina.asksFor(Enabled.ofEach(".button"))).containsExactly(true, false, true);
    }

}
