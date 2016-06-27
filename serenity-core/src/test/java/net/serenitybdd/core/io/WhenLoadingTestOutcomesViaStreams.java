package net.serenitybdd.core.io;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.TestOutcomeStream;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;

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
    public void should_skip_test_outcomes_in_a_test_outcome_stream() throws IOException {

        Path directory = directoryInClasspathCalled("/json-reports").toPath();

        try(TestOutcomeStream stream = TestOutcomeStream.testOutcomesInDirectory(directory)) {
            for(TestOutcome outcome : stream) {
                assertThat(outcome.getResult()).isNotNull();
            }
        }
    }

}
