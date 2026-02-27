package net.serenitybdd.screenplay.playwright.interactions;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.questions.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for WithinFrame interactions.
 * These tests verify that actors can interact with elements inside iframes.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class WithinFrameInteractionsTest {

    Actor alice;

    @BeforeEach
    void setup() {
        alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());
    }

    @Test
    void should_click_element_within_frame() {
        // Navigate to a page with iframes
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/iframe"),
            WithinFrame.locatedBy("#mce_0_ifr")
                .click("body#tinymce")
        );
        
        // Verify we can interact with frame content
        Target frameContent = Target.the("frame content")
            .inFrame("#mce_0_ifr")
            .locatedBy("body#tinymce");
            
        String text = alice.asksFor(Text.of(frameContent));
        assertThat(text).isNotEmpty();
    }

    @Test
    void should_fill_input_within_frame() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/iframe"),
            // Clear existing content first
            WithinFrame.locatedBy("#mce_0_ifr")
                .clear("body#tinymce"),
            // Fill with new text
            WithinFrame.locatedBy("#mce_0_ifr")
                .fill("body#tinymce", "Hello from Serenity!")
        );

        // Verify the text was entered
        Target frameContent = Target.the("frame content")
            .inFrame("#mce_0_ifr")
            .locatedBy("body#tinymce");
            
        String text = alice.asksFor(Text.of(frameContent));
        assertThat(text).contains("Hello from Serenity!");
    }

    @Test
    void should_type_text_within_frame() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/iframe"),
            // Clear existing content
            WithinFrame.locatedBy("#mce_0_ifr")
                .clear("body#tinymce"),
            // Type text character by character
            WithinFrame.locatedBy("#mce_0_ifr")
                .type("body#tinymce", "Typed text")
        );

        // Verify the text was typed
        Target frameContent = Target.the("frame content")
            .inFrame("#mce_0_ifr")
            .locatedBy("body#tinymce");
            
        String text = alice.asksFor(Text.of(frameContent));
        assertThat(text).contains("Typed text");
    }

    @Test
    void should_perform_custom_actions_within_frame() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/iframe"),
            WithinFrame.locatedBy("#mce_0_ifr")
                .perform(frame -> {
                    // Custom actions using FrameLocator
                    frame.locator("body#tinymce").clear();
                    frame.locator("body#tinymce").fill("Custom action text");
                })
        );

        // Verify the custom action worked
        Target frameContent = Target.the("frame content")
            .inFrame("#mce_0_ifr")
            .locatedBy("body#tinymce");
            
        String text = alice.asksFor(Text.of(frameContent));
        assertThat(text).contains("Custom action text");
    }

    @Test
    void should_interact_with_nested_frames() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/nested_frames")
        );

        // Read text from bottom frame
        Target bottomFrameContent = Target.the("bottom frame content")
            .inFrame("frame[name='frame-bottom']")
            .locatedBy("body");
            
        String bottomText = alice.asksFor(Text.of(bottomFrameContent));
        assertThat(bottomText).containsIgnoringCase("bottom");

        // Read text from left frame (nested inside frame-top)
        Target leftFrameContent = Target.the("left frame content")
            .inFrame("frame[name='frame-top']")
            .inFrame("frame[name='frame-left']")
            .locatedBy("body");
            
        String leftText = alice.asksFor(Text.of(leftFrameContent));
        assertThat(leftText).containsIgnoringCase("left");
    }

    @Test
    void should_handle_multiple_frame_interactions_in_sequence() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/iframe"),
            // First interaction
            WithinFrame.locatedBy("#mce_0_ifr")
                .clear("body#tinymce"),
            // Second interaction  
            WithinFrame.locatedBy("#mce_0_ifr")
                .fill("body#tinymce", "First entry"),
            // Third interaction
            WithinFrame.locatedBy("#mce_0_ifr")
                .clear("body#tinymce"),
            // Fourth interaction
            WithinFrame.locatedBy("#mce_0_ifr")
                .fill("body#tinymce", "Second entry")
        );

        // Verify final state
        Target frameContent = Target.the("frame content")
            .inFrame("#mce_0_ifr")
            .locatedBy("body#tinymce");
            
        String text = alice.asksFor(Text.of(frameContent));
        assertThat(text).contains("Second entry");
        assertThat(text).doesNotContain("First entry");
    }
}
