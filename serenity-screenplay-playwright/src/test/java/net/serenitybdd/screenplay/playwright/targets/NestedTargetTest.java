package net.serenitybdd.screenplay.playwright.targets;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.interactions.Click;
import net.serenitybdd.screenplay.playwright.interactions.Open;
import net.serenitybdd.screenplay.playwright.questions.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for nested targets and locator chaining.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class NestedTargetTest {

    Actor alice;

    static Target FORM = Target.the("login form").locatedBy("#login");
    static Target USERNAME_FIELD = Target.the("username field").locatedBy("#username");
    static Target SUBMIT_BUTTON = Target.the("submit button").locatedBy("button[type='submit']");

    @BeforeEach
    void setup() {
        alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());
    }

    @Test
    void should_find_element_inside_container() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/login")
        );

        // Find submit button inside the form
        Target submitInForm = SUBMIT_BUTTON.inside(FORM);

        alice.attemptsTo(
            Click.on(submitInForm)
        );

        // Should show error message (since we didn't fill credentials)
        String pageText = alice.asksFor(Text.of("#flash"));
        assertThat(pageText).containsIgnoringCase("invalid");
    }

    @Test
    void should_chain_locators_with_find() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/login")
        );

        // Chain: form -> button
        Target formButton = FORM.find(SUBMIT_BUTTON);

        String buttonText = alice.asksFor(Text.of(formButton));
        assertThat(buttonText).containsIgnoringCase("Login");
    }

    @Test
    void inside_should_create_combined_selector() {
        Target nested = SUBMIT_BUTTON.inside(FORM);

        // The selector should combine container and element
        assertThat(nested.asSelector())
            .contains("#login")
            .contains("button[type='submit']");
    }

    @Test
    void find_should_create_combined_selector() {
        Target chained = FORM.find(SUBMIT_BUTTON);

        assertThat(chained.asSelector())
            .contains("#login")
            .contains("button[type='submit']");
    }

    @Test
    void should_preserve_readable_name_for_nested_target() {
        Target nested = SUBMIT_BUTTON.inside(FORM);

        assertThat(nested.toString())
            .contains("submit button")
            .contains("login form");
    }
}
