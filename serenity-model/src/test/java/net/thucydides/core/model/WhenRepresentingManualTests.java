package net.thucydides.core.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenRepresentingManualTests {
    @Test
    void shouldBeConsideredAutomatedByDefault() {
        TestOutcome outcome = TestOutcome.forTestInStory("someTest", Story.withId("1","story"));

        assertThat(outcome.isManual()).isFalse();
    }

    @Test
    void shouldBeAbleToDefineAManualTest() {
        TestOutcome outcome = TestOutcome.forTestInStory("someTest", Story.withId("1","story")).setToManual();

        assertThat(outcome.isManual()).isTrue();
    }

    @Nested
    class AManualTest {


        TestOutcome outcome;

        @BeforeEach
        void setupTestOutcome() {
            outcome = TestOutcome.forTestInStory("someTest", Story.withId("1","story")).setToManual();
        }

        @Test
        void doesNotHaveAStartTime() {
            outcome.clearStartTime();
            assertThat(outcome.isStartTimeNotDefined()).isTrue();
        }

        @Test
        void canHaveADescription() {
            outcome.setDescription("Some description");

            assertThat(outcome.getDescriptionText()).isPresent();
            assertThat(outcome.getDescriptionText().get()).isEqualTo("Some description");
        }

        @Test
        void canHaveIssueKeys() {
            outcome = TestOutcome.forTestInStory("someTest", Story.withId("1","story"))
                    .setToManual()
                    .withIssues(Arrays.asList("MYPROJ-123"));
            assertThat(outcome.getIssues()).containsExactly("MYPROJ-123");
        }

        @Test
        void additionalIssuesComeAfterTheMainIssues() {
            outcome = TestOutcome.forTestInStory("someTest", Story.withId("1","story"))
                    .setToManual()
                    .withIssues(Arrays.asList("MYPROJ-123"));
            outcome.addIssues(Arrays.asList("MYPROJ-012"));
            assertThat(outcome.getIssues()).containsExactly("MYPROJ-012","MYPROJ-123");
        }

    }
}
