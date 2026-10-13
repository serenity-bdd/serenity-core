package net.serenitybdd.screenplay.playwright.questions;

import net.serenitybdd.screenplay.playwright.testsupport.LocalTestSite;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.interactions.Open;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for CSS and style questions.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class StyleQuestionsTest {

    Actor alice;

    @BeforeEach
    void setup() {
        alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());
    }

    @Test
    void should_get_css_value() {
        alice.attemptsTo(
            Open.url(LocalTestSite.url("/"))
        );

        String display = alice.asksFor(CSSValue.of("h1").named("display"));
        assertThat(display).isNotEmpty();
    }

    @Test
    void should_get_computed_style() {
        alice.attemptsTo(
            Open.url(LocalTestSite.url("/"))
        );

        String fontFamily = alice.asksFor(CSSValue.of("h1").named("font-family"));
        assertThat(fontFamily).isNotEmpty();
    }

    @Test
    void should_get_element_classes() {
        alice.attemptsTo(
            Open.url(LocalTestSite.url("/add_remove_elements/"))
        );

        List<String> classes = alice.asksFor(CSSClasses.of("#content .example"));
        assertThat(classes).contains("example");
    }

    @Test
    void should_check_if_element_has_class() {
        alice.attemptsTo(
            Open.url(LocalTestSite.url("/add_remove_elements/"))
        );

        Boolean hasClass = alice.asksFor(CSSClasses.of("#content .example").contains("example"));
        assertThat(hasClass).isTrue();

        Boolean hasOtherClass = alice.asksFor(CSSClasses.of("#content .example").contains("other-class"));
        assertThat(hasOtherClass).isFalse();
    }

    @Test
    void should_get_bounding_box() {
        alice.attemptsTo(
            Open.url(LocalTestSite.url("/"))
        );

        BoundingBox box = alice.asksFor(ElementBounds.of("h1"));
        assertThat(box).isNotNull();
        assertThat(box.getWidth()).isGreaterThan(0);
        assertThat(box.getHeight()).isGreaterThan(0);
    }

    @Test
    void should_get_inner_html() {
        alice.attemptsTo(
            Open.url(LocalTestSite.url("/"))
        );

        String innerHTML = alice.asksFor(InnerHTML.of("h1"));
        assertThat(innerHTML).isEqualTo("Welcome to the-internet");
    }

    @Test
    void should_get_outer_html() {
        alice.attemptsTo(
            Open.url(LocalTestSite.url("/"))
        );

        String outerHTML = alice.asksFor(OuterHTML.of("h1"));
        assertThat(outerHTML).contains("<h1");
        assertThat(outerHTML).contains("Welcome to the-internet");
        assertThat(outerHTML).contains("</h1>");
    }

    @Test
    void should_get_input_value() {
        alice.attemptsTo(
            Open.url(LocalTestSite.url("/inputs"))
        );

        // Input has no initial value
        String value = alice.asksFor(Value.of("input[type='number']"));
        assertThat(value).isEmpty();
    }
}
