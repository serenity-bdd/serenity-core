package net.thucydides.model.requirements;

import net.serenitybdd.model.di.ModelInfrastructure;
import net.thucydides.model.domain.RequirementCache;
import net.thucydides.model.requirements.model.Requirement;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AggregateRequirementsTest {

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
        void should_treat_files_in_a_flat_directory_structure_as_representing_features() {

            List<Requirement> requirements = requirementsFrom(pathTo("serenity-js/spec-flat"));

            System.out.println(requirements);

            assertThat(requirements).hasSize(1);

            Requirement feature = requirements.get(0);

            assertThat(feature.getType()).isEqualTo("feature");
            assertThat(feature.getName()).isEqualTo("card_payment");
            assertThat(feature.getDisplayName()).isEqualTo("Card payment");
        }

        @Test
        void should_treat_files_in_a_single_level_directory_structure_as_representing_capabilities_and_features() {

            List<Requirement> requirements = requirementsFrom(pathTo("serenity-js/spec-1-level"));

            System.out.println(requirements);

            assertThat(requirements).hasSize(2);

            Requirement feature = requirements.get(0);
            Requirement capability = requirements.get(1);

            assertThat(feature.getType()).isEqualTo("feature");
            assertThat(feature.getName()).isEqualTo("card_payment");
            assertThat(feature.getDisplayName()).isEqualTo("Card Payment");

            assertThat(capability.getType()).isEqualTo("capability");
            assertThat(capability.getName()).isEqualTo("payments");
            assertThat(capability.getDisplayName()).isEqualTo("Payments");
        }

        @Test
        void should_treat_files_in_a_two_level_directory_structure_as_representing_themes_capabilities_and_features() {

            List<Requirement> requirements = requirementsFrom(pathTo("serenity-js/spec-2-levels"));

            System.out.println(requirements);

            assertThat(requirements).hasSize(3);

            Requirement feature = requirements.get(0);
            Requirement capability = requirements.get(1);
            Requirement theme = requirements.get(2);

            assertThat(feature.getType()).isEqualTo("feature");
            assertThat(feature.getName()).isEqualTo("card_payment");
            assertThat(feature.getDisplayName()).isEqualTo("Card Payment");

            assertThat(capability.getType()).isEqualTo("capability");
            assertThat(capability.getName()).isEqualTo("payments");
            assertThat(capability.getDisplayName()).isEqualTo("Payments");

            assertThat(theme.getType()).isEqualTo("theme");
            assertThat(theme.getName()).isEqualTo("ecommerce");
            assertThat(theme.getDisplayName()).isEqualTo("Ecommerce");
        }
    }

    private List<Requirement> requirementsFrom(Path exampleRootDirectory) {

        Path requirementsDirectory = exampleRootDirectory.resolve("spec");
        Path jsonOutcomesDirectory = exampleRootDirectory.resolve("outcomes");

        final RequirementsService service = new AggregateRequirementsService(
                ModelInfrastructure.getEnvironmentVariables(),
                new FileSystemRequirementsTagProvider(requirementsDirectory.toString()),
                new TestOutcomeRequirementsTagProvider().fromSourceDirectory(jsonOutcomesDirectory)
        );

        return service.getRequirements();
    }

    private static Path pathTo(String resource) {
        return new File(ClassLoader.getSystemClassLoader().getResource(resource).getFile()).toPath();
    }
}
