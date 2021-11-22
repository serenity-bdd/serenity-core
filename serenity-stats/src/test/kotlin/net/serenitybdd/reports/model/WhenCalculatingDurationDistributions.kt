package net.serenitybdd.reports.model

import net.thucydides.core.reports.OutcomeFormat
import net.thucydides.core.reports.TestOutcomeLoader
import net.thucydides.core.reports.TestOutcomes
import net.thucydides.core.util.MockEnvironmentVariables
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.nio.file.Paths

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WhenCalculatingDurationDistributions {

    @Test
    fun `default durations should be defined`() {
        val environmentVariables = MockEnvironmentVariables()
        val testOutcomes = testOutcomesIn("/test_outcomes/with_varied_durations")

        val distribution = DurationDistribution(environmentVariables, testOutcomes)

        assertThat(distribution.getBucketLabels())
            .isEqualTo("['Under 1 second','1 second to 10 seconds','10 seconds to 30 seconds','30 seconds to 1 minute','1 minute to 2 minutes','2 minutes to 5 minutes','5 minutes to 10 minutes','10 minutes or over']")
    }

    @Test
    fun `tests should be distributed acroos the duration buckets`() {
        val environmentVariables = MockEnvironmentVariables()
        environmentVariables.setProperty("serenity.report.durations","1,2,4,8,10,20")
        val testOutcomes = testOutcomesIn("/test_outcomes/with_varied_durations")

        val distribution = DurationDistribution(environmentVariables, testOutcomes)

        assertThat(distribution.getNumberOfTestsPerDuration())
            .isEqualTo("['12','10','2','9','2','3','0']")
    }

    fun testOutcomesIn(directory: String) : TestOutcomes {
        val outcomeDir = Paths.get(javaClass.getResource(directory).toURI()).toFile()
        return TestOutcomeLoader.loadTestOutcomes().inFormat(OutcomeFormat.JSON).from(outcomeDir);
    }
}