package net.serenitybdd.reports.asciidoc

import net.thucydides.model.reports.OutcomeFormat
import net.thucydides.model.reports.TestOutcomeLoader
import net.thucydides.model.requirements.FileSystemRequirementsTagProvider
import net.thucydides.model.requirements.reports.FileSystemRequirmentsOutcomeFactory
import net.thucydides.model.util.EnvironmentVariables
import net.thucydides.model.util.MockEnvironmentVariables
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WhenLoadingRequirementsOutcomes {

    private val environmentVariables: EnvironmentVariables = MockEnvironmentVariables()

    init {
        environmentVariables.setProperty("serenity.requirements.dir", "src/test/resources/test_outcomes/with_a_single_test/features")
    }

    @Test
    fun `requirements can be loaded from a specified directory`() {
        val requirementsTagProvider = FileSystemRequirementsTagProvider(environmentVariables)

        val requirements = requirementsTagProvider.requirements

        assertThat(requirements).hasSize(3)
    }

    @Test
    fun `requirements outcomes can be loaded from a given directory`() {
        val testOutcomes = TestOutcomeLoader.loadTestOutcomes()
                                            .inFormat(OutcomeFormat.JSON)
                                            .from(File("src/test/resources/test_outcomes/with_a_single_test"))

        val outcomesFactory = FileSystemRequirmentsOutcomeFactory(environmentVariables)
        val requirementsOutcomes = outcomesFactory.buildRequirementsOutcomesFrom(testOutcomes)
        assertThat(requirementsOutcomes.requirementCount).isEqualTo(3)
    }
}
