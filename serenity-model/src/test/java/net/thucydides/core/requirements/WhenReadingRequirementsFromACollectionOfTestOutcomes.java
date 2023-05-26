package net.thucydides.core.requirements;

import net.thucydides.core.environment.MockEnvironmentVariables;
import net.thucydides.core.util.EnvironmentVariables;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The goal here is to load a requirements hierarchy from an arbitrary collection of test outcomes.
 * This can include test outcomes from JUnit 4 or 5 tests, or from Serenity/JS tests.
 * The goal is to reduce the reliance on reading from the classpath, which has proven to be unreliable.
 */
class WhenReadingRequirementsFromACollectionOfTestOutcomes {

    @Nested
    class WhenTheTestOutcomesAreFromJUnit4Tests {

        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();

        @Test
        void testOutcomesShouldBeReadFromTheTargetDirectory() {
            environmentVariables.setProperty("serenity.outputDirectory", localResource("sample-junit4-outcomes"));
            environmentVariables.setProperty("serenity.test.root","smoketests");
            TestOutcomeRequirementsTagProvider tagProvider = new TestOutcomeRequirementsTagProvider(environmentVariables);

            assertThat(tagProvider.getRequirements()).isNotEmpty();
        }
        // - Read a test outcome from a JUnit 4 test with a single package level
        // - Read a test outcome from a JUnit 4 test with nested packages
        // - Find the parent requirement of a test outcome from a JUnit 4 test
        // - Find the parent requirement of a requirement from a JUnit 4 test
        // - Can list the requirement types based on the depth of the requirements hierarchy
    }

    @Nested
    class WhenTheTestOutcomesAreFromJUnit5Tests {

    }

    private String localResource(String directoryName) {
        return getClass().getClassLoader().getResource(directoryName).getPath();
    }

}
