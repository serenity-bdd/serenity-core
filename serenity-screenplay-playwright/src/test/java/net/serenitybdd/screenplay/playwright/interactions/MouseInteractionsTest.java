package net.serenitybdd.screenplay.playwright.interactions;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.assertions.Ensure;
import net.serenitybdd.screenplay.playwright.questions.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for mouse interactions - hover, right-click, drag and drop.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class MouseInteractionsTest {

    Actor alice;

    @BeforeEach
    void setup() {
        alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());
    }

    @Test
    void should_hover_over_element() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/hovers"),
            Hover.over(".figure >> nth=0")
        );

        // After hovering, the caption should be visible
        alice.attemptsTo(
            Ensure.that(".figure >> nth=0 >> .figcaption").isVisible()
        );
    }

    @Test
    void should_hover_over_target() {
        Target FIRST_FIGURE = Target.the("first figure").locatedBy(".figure >> nth=0");

        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/hovers"),
            Hover.over(FIRST_FIGURE)
        );

        alice.attemptsTo(
            Ensure.that(".figure >> nth=0 >> .figcaption").isVisible()
        );
    }

    @Test
    void should_right_click_element() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/context_menu"),
            RightClick.on("#hot-spot")
        );

        // Right-clicking should trigger an alert on this page
        // The test passes if no error occurs
    }

    @Test
    void should_drag_and_drop() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/drag_and_drop"),
            Drag.from("#column-a").to("#column-b")
        );

        // After drag and drop, column A header should say "B"
        String columnAHeader = alice.asksFor(Text.of("#column-a header"));
        assertThat(columnAHeader).isEqualTo("B");
    }

    @Test
    void should_drag_and_drop_targets() {
        Target COLUMN_A = Target.the("column A").locatedBy("#column-a");
        Target COLUMN_B = Target.the("column B").locatedBy("#column-b");

        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/drag_and_drop"),
            Drag.from(COLUMN_A).to(COLUMN_B)
        );

        String columnAHeader = alice.asksFor(Text.of("#column-a header"));
        assertThat(columnAHeader).isEqualTo("B");
    }
}
