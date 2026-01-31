package net.serenitybdd.screenplay.playwright.targets;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.interactions.Open;
import net.serenitybdd.screenplay.playwright.questions.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for iframe/frame support in Targets.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class FrameTargetTest {

    Actor alice;

    @BeforeEach
    void setup() {
        alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());
    }

    @Test
    void should_interact_with_element_in_iframe() {
        // Target inside an iframe - using the nested frames example page
        Target frameContent = Target.the("frame content")
            .inFrame("frame[name='frame-bottom']")
            .locatedBy("body");

        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/nested_frames")
        );

        String text = alice.asksFor(Text.of(frameContent));
        assertThat(text).containsIgnoringCase("bottom");
    }

    @Test
    void should_support_nested_frames() {
        // Create target in a nested frame context
        Target nestedFrameElement = Target.the("nested element")
            .inFrame("iframe#outer")
            .inFrame("iframe#inner")
            .locatedBy("#content");

        // Verify the frame path is stored
        assertThat(nestedFrameElement.getFramePath())
            .hasSize(2)
            .containsExactly("iframe#outer", "iframe#inner");
    }

    @Test
    void frame_target_should_preserve_label() {
        Target frameTarget = Target.the("my element")
            .inFrame("#myframe")
            .locatedBy(".content");

        assertThat(frameTarget.toString()).isEqualTo("my element");
    }

    @Test
    void should_indicate_if_target_is_in_frame() {
        Target regularTarget = Target.the("regular").locatedBy("#elem");
        Target frameTarget = Target.the("in frame").inFrame("#frame").locatedBy("#elem");

        assertThat(regularTarget.isInFrame()).isFalse();
        assertThat(frameTarget.isInFrame()).isTrue();
    }
}
