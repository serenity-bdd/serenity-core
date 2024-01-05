package net.thucydides.model.requirements;

import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.environment.MockEnvironmentVariables;
import net.thucydides.model.domain.RequirementCache;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestTag;
import net.thucydides.model.reports.TestOutcomeLoader;
import net.thucydides.model.requirements.model.Requirement;
import net.thucydides.model.util.EnvironmentVariables;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The goal here is to load a requirements hierarchy from an arbitrary collection of test outcomes.
 * This can include test outcomes from JUnit 4 or 5 tests, or from Serenity/JS tests.
 * The goal is to reduce the reliance on reading from the classpath, which has proven to be unreliable.
 */
class WhenReadingRequirementsFromACollectionOfTestOutcomes {

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables();

    @BeforeEach
    void clearCaches() {
        ConfiguredEnvironment.reset();
        DefaultCapabilityTypes.instance().clear();
        RequirementCache.getInstance().clear();
    }

    @AfterAll
    static void afterAll() {
        ConfiguredEnvironment.reset();
        DefaultCapabilityTypes.instance().clear();
        RequirementCache.getInstance().clear();
    }

    @Nested
    class WhenTheTestOutcomesAreFromJUnit4Tests {

        @Test
        void testOutcomesShouldBeReadFromTheTargetDirectory() {
            environmentVariables.setProperty("serenity.outputDirectory", localResource("sample-junit4-outcomes"));
            environmentVariables.setProperty("serenity.test.root", "smoketests");
            ConfiguredEnvironment.updateConfiguration(environmentVariables);
            TestOutcomeRequirementsTagProvider tagProvider = new TestOutcomeRequirementsTagProvider(environmentVariables);
            assertThat(tagProvider.getRequirements()).isNotEmpty();
        }

        // Read a test outcome from a JUnit 4 test with a single package level
        @Test
        void shouldReadATestOutcomeFromAJUnit4TestSuite() {
            environmentVariables.setProperty("serenity.outputDirectory", localResource("sample-junit4-outcomes"));
            environmentVariables.setProperty("serenity.test.root", "smoketests");
            ConfiguredEnvironment.updateConfiguration(environmentVariables);
            TestOutcomeRequirementsTagProvider tagProvider = new TestOutcomeRequirementsTagProvider(environmentVariables);
            assertThat(tagProvider.getRequirements()).isNotEmpty();
            assertThat(tagProvider.getRequirements()).hasSize(1);
            assertThat(tagProvider.getRequirements().get(0).hasChildren()).isTrue();
            assertThat(tagProvider.getRequirements().get(0).getChildren()).allMatch(
                    child -> child.getParent().equals("sample")
            );
        }

        // Read a test outcome from a JUnit 4 test with a single package level
        @Test
        void shouldReadATestOutcomeFromNestedJUnit5Tests() {
            environmentVariables.setProperty("serenity.outputDirectory", localResource("sample-nested-junit-outcomes"));
            environmentVariables.setProperty("serenity.test.root", "com.serenitydojo.wordle.integrationtests");
            ConfiguredEnvironment.updateConfiguration(environmentVariables);

            TestOutcomeRequirementsTagProvider tagProvider = new TestOutcomeRequirementsTagProvider(environmentVariables);
            assertThat(tagProvider.getRequirements()).isNotEmpty();
            assertThat(tagProvider.getRequirements()).hasSize(1);

            Requirement rootRequirement = tagProvider.getRequirements().get(0);
            assertThat(rootRequirement.hasChildren()).isTrue();
            assertThat(rootRequirement.getChildren()).allMatch(
                    child -> child.getParent().equals(rootRequirement.getId())
            );
        }

        @Test
        void shouldAssociateTestOutcomesToNestedJUnit5Requirements() throws IOException {

            List<? extends TestOutcome> testOutcomes
                    = TestOutcomeLoader.testOutcomesIn(new File(localResource("sample-nested-junit-outcomes"))).getOutcomes();

            environmentVariables.setProperty("serenity.outputDirectory", localResource("sample-nested-junit-outcomes"));
            environmentVariables.setProperty("serenity.test.root", "com.serenitydojo.wordle.integrationtests");
            ConfiguredEnvironment.updateConfiguration(environmentVariables);

            TestOutcomeRequirementsTagProvider tagProvider = new TestOutcomeRequirementsTagProvider(environmentVariables);

            Optional<Requirement> correspondingRequirement = tagProvider.getParentRequirementOf(testOutcomes.get(0));

            assertThat(correspondingRequirement.isPresent()).isTrue();
            assertThat(correspondingRequirement.get().getName()).isEqualTo("CreatingANewGame");
            assertThat(correspondingRequirement.get().getDisplayName()).isEqualTo("When creating a new game");
        }

        @Test
        void shouldFindTheParentOfARequirement() {
            environmentVariables.setProperty("serenity.outputDirectory", localResource("sample-junit4-outcomes"));
            environmentVariables.setProperty("serenity.test.root", "smoketests.sample");
            ConfiguredEnvironment.updateConfiguration(environmentVariables);

            TestOutcomeRequirementsTagProvider tagProvider = new TestOutcomeRequirementsTagProvider(environmentVariables);

            Requirement parent = tagProvider.getRequirements()
                                            .stream().filter(req -> req.getName().equals("nested"))
                                            .findFirst().get();

            Requirement child = parent.getChildren().get(0);
            assertThat(tagProvider.getParentRequirementOf(child)).isPresent().get().isEqualTo(parent);
        }

        // - Read a test outcome from a JUnit 4 test with nested packages
        @Test
        void shouldReadATestOutcomeFromAJUnit4TestWithNestedPackages() {
            environmentVariables.setProperty("serenity.outputDirectory", localResource("sample-junit4-outcomes"));
            environmentVariables.setProperty("serenity.test.root", "smoketests");
            ConfiguredEnvironment.updateConfiguration(environmentVariables);

            TestOutcomeRequirementsTagProvider tagProvider = new TestOutcomeRequirementsTagProvider(environmentVariables);
            assertThat(tagProvider.getRequirements()).isNotEmpty();
            assertThat(tagProvider.getRequirements()).hasSize(1);
            assertThat(tagProvider.getRequirements().get(0).hasChildren()).isTrue();
            assertThat(tagProvider.getRequirements().get(0).getChildren()).allMatch(
                    child -> child.getParent().equals("sample"));
            assertThat(tagProvider.getRequirements().get(0).getChildren().get(0).getChildren()).allMatch(
                    child -> child.getParent().equals("sample/nested"));
        }

        // Can list the requirement types based on the depth of the requirements hierarchy
        @Test
        void shouldListTheRequirementTypesBasedOnTheDepthOfTheRequirementsHierarchy() {
            environmentVariables.setProperty("serenity.outputDirectory", localResource("sample-junit4-outcomes"));
            environmentVariables.setProperty("serenity.test.root", "smoketests");
            ConfiguredEnvironment.updateConfiguration(environmentVariables);

            TestOutcomeRequirementsTagProvider tagProvider = new TestOutcomeRequirementsTagProvider(environmentVariables);
            assertThat(tagProvider.getActiveRequirementTypes()).containsExactly("theme", "capability", "feature");
        }

        @Test
        void shouldListTheRequirementTypesBasedOnTheDepthOfTheRequirementsHierarchyOfAJavaScriptProject() {
            environmentVariables.setProperty("serenity.outputDirectory", localResource("serenity-js/spec-2-levels/outcomes"));
            environmentVariables.setProperty("serenity.requirements.dir", localResource("serenity-js/spec-2-levels/spec"));
            ConfiguredEnvironment.updateConfiguration(environmentVariables);

            TestOutcomeRequirementsTagProvider tagProvider = new TestOutcomeRequirementsTagProvider(environmentVariables);
            assertThat(tagProvider.getActiveRequirementTypes()).containsExactly("theme", "capability", "feature");
        }

        @Test
        void shouldFindParentRequirementsForTestOutcomes() throws IOException {
            environmentVariables.setProperty("serenity.outputDirectory", localResource("sample-junit4-outcomes"));
            environmentVariables.setProperty("serenity.test.root", "smoketests");
            ConfiguredEnvironment.updateConfiguration(environmentVariables);

            TestOutcomeRequirementsTagProvider tagProvider = new TestOutcomeRequirementsTagProvider(environmentVariables);

            List<? extends TestOutcome> testOutcomes
                    = TestOutcomeLoader.testOutcomesIn(new File(localResource("sample-junit4-outcomes"))).getOutcomes();

            Optional<Requirement> requirement = tagProvider.getParentRequirementOf(testOutcomes.get(0));

            assertThat(requirement).isPresent()
                    .matches(req -> req.get().getName().equals("ATopLevelStoryTest"));
        }

        @Test
        void shouldIdentifyTestTagsForATestOutcome() throws IOException {
            environmentVariables.setProperty("serenity.outputDirectory", localResource("sample-junit4-outcomes"));
            environmentVariables.setProperty("serenity.test.root", "smoketests");
            ConfiguredEnvironment.updateConfiguration(environmentVariables);

            TestOutcomeRequirementsTagProvider tagProvider = new TestOutcomeRequirementsTagProvider(environmentVariables);

            List<? extends TestOutcome> testOutcomes
                    = TestOutcomeLoader.testOutcomesIn(new File(localResource("sample-junit4-outcomes"))).getOutcomes();

            TestOutcome outcome = testOutcomes.get(1);
            Set<TestTag> matchingTags = tagProvider.getTagsFor(outcome);

            assertThat(matchingTags).isNotEmpty();
        }
    }

    @Nested
    class WhenTheTestOutcomesAreFromCucumberTests {
        @Test
        void shouldIgnoreCucumberTestOutcomes() {
            environmentVariables.setProperty("serenity.outputDirectory", localResource("sample-cucumber-and-junit-outcomes"));
            environmentVariables.setProperty("serenity.test.root", "smoketests");
            ConfiguredEnvironment.updateConfiguration(environmentVariables);

            TestOutcomeRequirementsTagProvider tagProvider = new TestOutcomeRequirementsTagProvider(environmentVariables);
            assertThat(tagProvider.getRequirements()).isNotEmpty();
        }
    }

    private String localResource(String directoryName) {
        return getClass().getClassLoader().getResource(directoryName).getPath();
    }

}
