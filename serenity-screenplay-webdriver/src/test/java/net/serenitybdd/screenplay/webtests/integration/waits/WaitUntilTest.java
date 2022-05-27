package net.serenitybdd.screenplay.webtests.integration.waits;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.matchers.WebElementStateMatchers;
import net.serenitybdd.screenplay.questions.Absence;
import net.serenitybdd.screenplay.questions.CurrentVisibility;
import net.serenitybdd.screenplay.questions.Text;
import net.serenitybdd.screenplay.questions.Visibility;
import net.serenitybdd.screenplay.targets.EnsureFieldVisible;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.ui.Button;
import net.serenitybdd.screenplay.ui.PageElement;
import net.serenitybdd.screenplay.waits.WaitUntil;
import net.serenitybdd.screenplay.webtests.integration.ScreenplayInteractionTestBase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated;

@RunWith(SerenityRunner.class)
public class WaitUntilTest extends ScreenplayInteractionTestBase {

    private final static Target INVISIBLE_BUTTON = PageElement.locatedBy("#invisible-button");
    private final static Target DELAYED_BUTTON = Button.withText("A Delayed Button");
    private final static Target DISAPPEARING_BUTTON = PageElement.locatedBy("#disappearing-button");

    @Before
    public void setTimeout() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
    }

    @Test
    public void waitForElementToAppearUsingTheDefaultTimeouts() {
        dina.attemptsTo(
                WaitUntil.the(INVISIBLE_BUTTON, isVisible())
        );
        assertThat(dina.asksFor(CurrentVisibility.of(INVISIBLE_BUTTON))).isTrue();
    }

    @Test
    public void waitForElementToAppearUsingTheDefaultTimeoutsWithALocator() {
        dina.attemptsTo(
                WaitUntil.the("#invisible-button", isVisible())
        );
        assertThat(dina.asksFor(CurrentVisibility.of(INVISIBLE_BUTTON))).isTrue();
    }

    @Test
    public void waitForElementToAppearUsingTheDefaultTimeoutsWithAByLocator() {
        dina.attemptsTo(
                WaitUntil.the(By.id("invisible-button"), isVisible())
        );
        assertThat(dina.asksFor(CurrentVisibility.of(INVISIBLE_BUTTON))).isTrue();
    }

    @Test
    public void waitForElementToAppearWithACustomTimeout() {
        dina.attemptsTo(
                WaitUntil.the(INVISIBLE_BUTTON, isVisible()).forNoMoreThan(10).seconds()
        );
        assertThat(dina.asksFor(CurrentVisibility.of(INVISIBLE_BUTTON))).isTrue();
    }

    @Test
    public void waitForElementToAppearWithACustomTimeoutAsADuration() {
        dina.attemptsTo(
                WaitUntil.the(INVISIBLE_BUTTON, isVisible()).forNoMoreThan(Duration.ofSeconds(3))
        );
        assertThat(dina.asksFor(CurrentVisibility.of(INVISIBLE_BUTTON))).isTrue();
    }


    @Test
    public void waitForElementToAppearWithADelayOnTheTarget() {
        dina.attemptsTo(
                Click.on(INVISIBLE_BUTTON.waitingForNoMoreThan(Duration.ofSeconds(3)))
        );
        assertThat(dina.asksFor(CurrentVisibility.of(INVISIBLE_BUTTON))).isTrue();
    }


    @Test
    public void waitForElementToBeEnabledWithADelayOnTheTarget() {
        dina.attemptsTo(
                WaitUntil.the(DELAYED_BUTTON, isEnabled()).forNoMoreThan(Duration.ofSeconds(10)),
                Click.on(DELAYED_BUTTON)
        );
        assertThat(dina.asksFor(Text.of("#delayed-button-state"))).isEqualTo("Clicked");


    }

    @Test
    public void waitForElementToDisappearWithACustomTimeoutAsADuration() {
        dina.attemptsTo(
                WaitUntil.the(DISAPPEARING_BUTTON, isNotVisible())
        );
        assertThat(dina.asksFor(CurrentVisibility.of(DISAPPEARING_BUTTON))).isFalse();
    }

    @Test
    public void waitForAWebdriverCondition() {
        dina.attemptsTo(
                WaitUntil.the(
                        invisibilityOfElementLocated(By.id("disappearing-button")))
        );

    }

}
