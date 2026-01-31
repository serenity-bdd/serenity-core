package net.serenitybdd.screenplay.playwright.injectors;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.annotations.PlaywrightCastMember;
import net.serenitybdd.screenplay.playwright.assertions.Ensure;
import net.serenitybdd.screenplay.playwright.interactions.Open;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests that the @PlaywrightCastMember annotation properly injects actors
 * with the BrowseTheWebWithPlaywright ability.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class CastMemberInjectionTest {

    @PlaywrightCastMember(name = "Alice")
    Actor alice;

    @PlaywrightCastMember
    Actor bob; // Should use field name "Bob" as the actor name

    @Test
    void should_inject_actor_with_playwright_ability() {
        assertThat(alice).isNotNull();
        assertThat(alice.getName()).isEqualTo("Alice");
        assertThat(alice.abilityTo(BrowseTheWebWithPlaywright.class))
            .as("Actor should have BrowseTheWebWithPlaywright ability")
            .isNotNull();
    }

    @Test
    void should_use_field_name_when_no_name_specified() {
        assertThat(bob).isNotNull();
        assertThat(bob.getName()).isEqualTo("Bob");
        assertThat(bob.abilityTo(BrowseTheWebWithPlaywright.class))
            .as("Actor should have BrowseTheWebWithPlaywright ability")
            .isNotNull();
    }

    @Test
    void should_be_able_to_browse_with_injected_actor() {
        alice.attemptsTo(
            Open.url("https://example.com"),
            Ensure.that("h1").isVisible()
        );
    }
}