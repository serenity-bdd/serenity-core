package net.serenitybdd.core.io;

import com.beust.jcommander.internal.Lists;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.TestOutcomeStream;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static net.thucydides.core.util.TestResources.directoryInClasspathCalled;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by john on 23/06/2016.
 */
public class WhenLoadingTestOutcomesViaStreams {

    @Test
    public void should_load_test_outcomes_in_a_test_outcome_stream() throws IOException {

        Path directory = directoryInClasspathCalled("/json-test-outcomes").toPath();

        try(TestOutcomeStream stream = TestOutcomeStream.testOutcomesInDirectory(directory)) {
            for(TestOutcome outcome : stream) {
                assertThat(outcome.getResult()).isNotNull();
            }
        }
    }

    @Test
    public void should_load_the_right_number_of_test_outcomes() throws IOException {

        Path directory = directoryInClasspathCalled("/json-test-outcomes").toPath();

        List<TestOutcome> outcomes = Lists.newArrayList();
        try(TestOutcomeStream stream = TestOutcomeStream.testOutcomesInDirectory(directory)) {
            for(TestOutcome outcome : stream) {
                outcomes.add(outcome);
            }
        }
        assertThat(outcomes).hasSize(12);
    }


    @Test
    public void should_not_fail_with_an_empty_directory() throws IOException {

        Path directory = directoryInClasspathCalled("/features").toPath();

        List<TestOutcome> outcomes = Lists.newArrayList();
        try(TestOutcomeStream stream = TestOutcomeStream.testOutcomesInDirectory(directory)) {
            for(TestOutcome outcome : stream) {
                outcomes.add(outcome);
            }
        }
        assertThat(outcomes).hasSize(0);
    }

    @Test
    public void should_not_crash_if_next_is_called_directly() throws IOException {

        Path directory = directoryInClasspathCalled("/json-test-outcomes").toPath();

        try(TestOutcomeStream stream = TestOutcomeStream.testOutcomesInDirectory(directory)) {
            TestOutcome outcome = stream.iterator().next();
            assertThat(outcome.getResult()).isNotNull();
        }
    }


    @Test
    public void should_skip_test_outcomes_in_a_test_outcome_stream() throws IOException {

        Path directory = directoryInClasspathCalled("/json-reports").toPath();

        try(TestOutcomeStream stream = TestOutcomeStream.testOutcomesInDirectory(directory)) {
            for(TestOutcome outcome : stream) {
                assertThat(outcome.getResult()).isNotNull();
            }
        }
    }

}
