package net.serenitybdd.screenplay.playwright.questions;

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
 * Tests for collection questions - getting lists of values from multiple elements.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class CollectionQuestionsTest {

    Actor alice;

    @BeforeEach
    void setup() {
        alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());
    }

    @Test
    void should_get_all_text_values() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/checkboxes")
        );

        // Get all link texts from the page
        List<String> texts = alice.asksFor(TextOfAll.of("#checkboxes input"));
        assertThat(texts).hasSize(2);
    }

    @Test
    void should_get_all_attribute_values() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/checkboxes")
        );

        List<String> types = alice.asksFor(AttributeOfAll.of("#checkboxes input").named("type"));
        assertThat(types).hasSize(2);
        assertThat(types).containsOnly("checkbox");
    }

    @Test
    void should_get_selected_option_text() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/dropdown")
        );

        String selectedText = alice.asksFor(SelectedOptionText.of("#dropdown"));
        assertThat(selectedText).isEqualTo("Please select an option");
    }

    @Test
    void should_get_all_option_texts() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/dropdown")
        );

        List<String> options = alice.asksFor(AllOptionTexts.of("#dropdown"));
        assertThat(options).containsExactly("Please select an option", "Option 1", "Option 2");
    }
}
