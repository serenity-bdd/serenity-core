package net.serenitybdd.reports.asciidoc

import net.thucydides.core.reports.OutcomeFormat
import net.thucydides.core.reports.TestOutcomeLoader
import net.thucydides.core.requirements.FileSystemRequirementsTagProvider
import net.thucydides.core.requirements.reports.FileSystemRequirmentsOutcomeFactory
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
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

        assertThat(requirements).hasSize(2)
    }

    @Test
    fun `requirements outcomes can be loaded from a given directory`() {
        val testOutcomes = TestOutcomeLoader.loadTestOutcomes()
                                            .inFormat(OutcomeFormat.JSON)
                                            .from(File("src/test/resources/test_outcomes/with_a_single_test"))

        val outcomesFactory = FileSystemRequirmentsOutcomeFactory(environmentVariables)
        val requirementsOutcomes = outcomesFactory.buildRequirementsOutcomesFrom(testOutcomes)

        assertThat(requirementsOutcomes.requirementCount).isEqualTo(2)
    }
}