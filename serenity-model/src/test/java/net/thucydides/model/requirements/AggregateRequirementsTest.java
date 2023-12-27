package net.thucydides.model.requirements;

import net.thucydides.model.domain.RequirementCache;
import net.thucydides.model.requirements.model.Requirement;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

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

            List<Requirement> requirements = requirementsFrom(pathTo("serenity-js/spec-0-levels"));

            System.out.println(requirements);

            assertThat(requirements).hasSize(1);

            Requirement feature = requirements.get(0);

            assertThat(feature.getChildren()).hasSize(0);

            assertThat(feature.getName()).isEqualTo("card_payment");
            assertThat(feature.getDisplayName()).isEqualTo("Card payment");
            assertThat(feature.getType()).isEqualTo("feature");
        }
    }

    private List<Requirement> requirementsFrom(Path exampleRootDirectory) {

        Path requirementsDirectory = exampleRootDirectory.resolve("spec");
        Path jsonOutcomesDirectory = exampleRootDirectory.resolve("outcomes");

        Requirements requirements = new AggregateRequirements(jsonOutcomesDirectory, requirementsDirectory.toString());

        return requirements.getRequirementsService().getRequirements();
    }

    private static Path pathTo(String resource) {
        return new File(ClassLoader.getSystemClassLoader().getResource(resource).getFile()).toPath();
    }
}
