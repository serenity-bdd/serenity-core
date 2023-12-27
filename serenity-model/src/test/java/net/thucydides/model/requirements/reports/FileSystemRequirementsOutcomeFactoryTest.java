package net.thucydides.model.requirements.reports;

import net.serenitybdd.model.di.ModelInfrastructure;
import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.thucydides.model.domain.ReportType;
import net.thucydides.model.domain.RequirementCache;
import net.thucydides.model.reports.OutcomeFormat;
import net.thucydides.model.reports.TestOutcomeLoader;
import net.thucydides.model.reports.TestOutcomes;
import net.thucydides.model.reports.html.ReportNameProvider;
import net.thucydides.model.requirements.model.Requirement;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static net.thucydides.model.reports.html.ReportNameProvider.NO_CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;

class FileSystemRequirementsOutcomeFactoryTest {

    @BeforeEach
    void setUp() {
        RequirementCache.getInstance().clear();
    }

    @AfterAll
    static void afterAll() {
        RequirementCache.getInstance().clear();
    }

    @Nested
    @DisplayName("when interpreting Serenity/JS test outcomes")
    class SerenityJSTestOutcomes {

        @Test
        void should_not_discover_any_additional_requirements_if_the_directory_structure_is_flat() throws IOException {

            List<Requirement> requirements = requirementsFrom(pathTo("serenity-js/spec-flat"));

            assertThat(requirements).hasSize(0);
        }

        @Test
        void should_treat_directories_in_a_single_level_directory_structure_as_representing_capabilities() throws IOException {

            List<Requirement> requirements = requirementsFrom(pathTo("serenity-js/spec-1-level"));

            System.out.println(requirements);

            assertThat(requirements).hasSize(1);

            Requirement capability = requirements.get(0);

            assertThat(capability.getName()).isEqualTo("Payments");
            assertThat(capability.getDisplayName()).isEqualTo("Payments");
            assertThat(capability.getType()).isEqualTo("capability");
        }

        @Test
        void should_treat_directories_in_a_two_level_directory_structure_as_representing_themes_and_capabilities() throws IOException {

            List<Requirement> requirements = requirementsFrom(pathTo("serenity-js/spec-2-levels"));

            System.out.println(requirements);

            assertThat(requirements).hasSize(2);

            Requirement capability = requirements.get(0);
            Requirement theme = requirements.get(1);

            assertThat(capability.getName()).isEqualTo("Payments");
            assertThat(capability.getDisplayName()).isEqualTo("Payments");
            assertThat(capability.getType()).isEqualTo("capability");

            assertThat(theme.getName()).isEqualTo("Ecommerce");
            assertThat(theme.getDisplayName()).isEqualTo("Ecommerce");
            assertThat(theme.getType()).isEqualTo("theme");
        }
    }

    private List<Requirement> requirementsFrom(Path exampleRootDirectory) throws IOException {

        Path requirementsDirectory = exampleRootDirectory.resolve("spec");
        Path jsonOutcomesDirectory = exampleRootDirectory.resolve("outcomes");

        final FileSystemRequirementsOutcomeFactory requirmentsOutcomeFactory = new FileSystemRequirementsOutcomeFactory(
                ConfiguredEnvironment.getEnvironmentVariables(),
                ModelInfrastructure.getIssueTracking(),
                new ReportNameProvider(
                        NO_CONTEXT,
                        ReportType.HTML,
                        null // requirementsService; not needed in this context
                ),
                requirementsDirectory.toString());

        TestOutcomes outcomes = TestOutcomeLoader.loadTestOutcomes()
                .inFormat(OutcomeFormat.JSON)
                .from(jsonOutcomesDirectory.toFile())
                .withRequirementsTags();

        return requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(outcomes).getRequirements();
    }

    private static Path pathTo(String resource) {
        return new File(ClassLoader.getSystemClassLoader().getResource(resource).getFile()).toPath();
    }
}
