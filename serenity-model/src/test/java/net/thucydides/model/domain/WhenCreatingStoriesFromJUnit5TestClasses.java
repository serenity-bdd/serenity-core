package net.thucydides.model.domain;

import net.serenitybdd.annotations.Description;
import net.serenitybdd.annotations.Narrative;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenCreatingStoriesFromJUnit5TestClasses {

    @Test
    void aUserStoryPathIsThePathAboveTheStoryClass() {
        Story story = Story.from(Purchases.class);

        assertThat(story.asTag()).isEqualTo(TestTag.withValue("feature:net/thucydides/model/domain/Purchases"));
    }

    @Test
    void nestedClassesIncludeTheParentClassInThePath() {
        Story story = Story.from(Purchases.FoodPurchases.class);

        assertThat(story.asTag()).isEqualTo(TestTag.withValue("feature:net/thucydides/model/domain/Purchases/FoodPurchases"));
    }

    @Narrative(text = {"As a reader", "I want to collect books in a basket"})
    static class NarratedStory {}

    @Description("A described story")
    @Narrative(text = {"Ignored when a description is present"})
    static class DescribedAndNarratedStory {}

    @Test
    void theNarrativeAnnotationOnATestClassBecomesTheStoryNarrative() {
        assertThat(Story.from(NarratedStory.class).getNarrative()).isEqualTo("As a reader\nI want to collect books in a basket");
    }

    @Test
    void aDescriptionTakesPrecedenceOverANarrative() {
        assertThat(Story.from(DescribedAndNarratedStory.class).getNarrative()).isEqualTo("A described story");
    }

    @Test
    void aStoryWithoutEitherHasNoNarrative() {
        assertThat(Story.from(Purchases.class).getNarrative()).isEmpty();
    }
}
