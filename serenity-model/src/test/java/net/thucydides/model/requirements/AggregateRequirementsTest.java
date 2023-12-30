package net.thucydides.model.requirements;

import net.serenitybdd.model.di.ModelInfrastructure;
import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.domain.RequirementCache;
import net.thucydides.model.environment.MockEnvironmentVariables;
import net.thucydides.model.requirements.model.Requirement;
import net.thucydides.model.util.EnvironmentVariables;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class AggregateRequirementsTest {

    @AfterAll
    static void afterAll() {
        ConfiguredEnvironment.reset();
        DefaultCapabilityTypes.instance().clear();
        RequirementCache.getInstance().clear();
    }

    @Nested
    @DisplayName("when interpreting Serenity/JS test outcomes")
    class SerenityJSTestOutcomes {

        @BeforeEach
        void setUp() {
            ConfiguredEnvironment.reset();
            DefaultCapabilityTypes.instance().clear();
            RequirementCache.getInstance().clear();
        }

        @Test
        void should_treat_files_in_a_flat_directory_structure_as_representing_features() {

            List<Requirement> requirements = requirementsFrom(pathTo("serenity-js/spec-0-levels"));

            assertThat(requirements).hasSize(1);

            Requirement feature = requirements.get(0);

            assertThat(feature.getChildren()).hasSize(0);

            assertThat(feature.getName()).isEqualTo("card_payment");
            assertThat(feature.getDisplayName()).isEqualTo("Card payment");
            assertThat(feature.getType()).isEqualTo("feature");
        }

        @Test
        void should_treat_files_in_a_single_level_directory_structure_as_representing_capabilities_and_features() {

            List<Requirement> requirements = requirementsFrom(pathTo("serenity-js/spec-1-level"));

            assertThat(requirements).hasSize(1);

            Requirement capability = requirements.get(0);
            assertThat(capability.getName()).isEqualTo("payments");
            assertThat(capability.getDisplayName()).isEqualTo("Payments");
            assertThat(capability.getType()).isEqualTo("capability");

            assertThat(capability.getChildren()).hasSize(1);

            Requirement feature = capability.getChildren().get(0);
            assertThat(feature.getName()).isEqualTo("card_payment");
            assertThat(feature.getDisplayName()).isEqualTo("Card payment");
            assertThat(feature.getType()).isEqualTo("feature");
        }

        @Test
        void should_treat_files_in_a_two_level_directory_structure_as_representing_themes_capabilities_and_features() {

            List<Requirement> requirements = requirementsFrom(pathTo("serenity-js/spec-2-levels"));

            assertThat(requirements).hasSize(1);

            Requirement theme = requirements.get(0);
            assertThat(theme.getName()).isEqualTo("ecommerce");
            assertThat(theme.getDisplayName()).isEqualTo("Ecommerce");
            assertThat(theme.getType()).isEqualTo("theme");

            assertThat(theme.getChildren()).hasSize(1);

            Requirement capability = theme.getChildren().get(0);
            assertThat(capability.getName()).isEqualTo("payments");
            assertThat(capability.getDisplayName()).isEqualTo("Payments");
            assertThat(capability.getType()).isEqualTo("capability");

            assertThat(capability.getChildren()).hasSize(1);

            Requirement feature = capability.getChildren().get(0);
            assertThat(feature.getName()).isEqualTo("card_payment");
            assertThat(feature.getDisplayName()).isEqualTo("Card payment");
            assertThat(feature.getType()).isEqualTo("feature");
        }
    }

    private List<Requirement> requirementsFrom(Path exampleRootDirectory) {

        Path requirementsDirectory = exampleRootDirectory.resolve("spec");
        Path jsonOutcomesDirectory = exampleRootDirectory.resolve("outcomes");

        EnvironmentVariables env = new MockEnvironmentVariables();

        Requirements requirements = new AggregateRequirements(
                jsonOutcomesDirectory,
                requirementsDirectory.toString(),
                env
        );

        RequirementsService requirementsService = requirements.getRequirementsService();

        return requirementsService.getRequirements();
    }

    private static Path pathTo(String resource) {
        return new File(ClassLoader.getSystemClassLoader().getResource(resource).getFile()).toPath();
    }
}
