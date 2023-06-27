package net.thucydides.core.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenCreatingStoriesFromJUnit5TestClasses {

    @Test
    void aUserStoryPathIsThePathAboveTheStoryClass() {
        Story story = Story.from(Purchases.class);

        assertThat(story.asTag()).isEqualTo(TestTag.withValue("story:Model/Purchases"));
    }

    @Test
    void nestedClassesIncludeTheParentClassInThePath() {
        Story story = Story.from(Purchases.FoodPurchases.class);

        assertThat(story.asTag()).isEqualTo(TestTag.withValue("story:Purchases/Food purchases"));
    }

}
