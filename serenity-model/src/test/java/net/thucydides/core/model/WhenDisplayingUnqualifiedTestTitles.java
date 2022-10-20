package net.thucydides.core.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenDisplayingUnqualifiedTestTitles {

    @Test
    void shouldDisplayTestTitlesWithoutTheQualifier() {
        TestOutcome outcome = TestOutcome.forTestInStory("Should be able to do some thing", Story.withId("1","story"));
        outcome = outcome.withQualifier("QUALIFIER");

        String title = outcome.getUnqualified().getTitleWithLinks();

        assertThat(title).isEqualTo("Should be able to do some thing");
    }

    @Test
    void shouldDisplayTestTitleWithLinksWithoutTheQualifier() {
        TestOutcome outcome = TestOutcome.forTestInStory("Should be able to do some thing", Story.withId("1","story"));
        outcome = outcome.withQualifier("QUALIFIER");

        String title = outcome.getUnqualified().getTitle();

        assertThat(title).isEqualTo("Should be able to do some thing");
    }
}
