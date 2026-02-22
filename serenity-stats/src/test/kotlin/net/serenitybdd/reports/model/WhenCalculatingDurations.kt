package net.serenitybdd.reports.model

import net.thucydides.model.domain.TestOutcome
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito
import java.time.Duration
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WhenCalculatingDurations {

    @Test
    fun `maxDurationOf does not raise KotlinNullPointerException when called with no test steps`() {

        val outcome = Mockito.mock(TestOutcome::class.java)
        Mockito.`when`(outcome.isDataDriven).thenReturn(true)
        Mockito.`when`(outcome.testSteps).thenReturn(Collections.emptyList())

        assertThat(maxDurationOf(outcome)).isEqualTo(0)
    }

    @Test
    fun `formattedDuration should format sub-second durations in milliseconds`() {
        assertThat(formattedDuration(Duration.ofMillis(500))).isEqualTo("500ms")
    }

    @Test
    fun `formattedDuration should format seconds correctly`() {
        assertThat(formattedDuration(Duration.ofSeconds(45))).isEqualTo("45s")
    }

    @Test
    fun `formattedDuration should format minutes and seconds`() {
        val result = formattedDuration(Duration.ofMinutes(5).plusSeconds(30))
        assertThat(result).contains("5m").contains("30s")
    }

    @Test
    fun `formattedDuration should format hours minutes and seconds`() {
        val result = formattedDuration(Duration.ofHours(2).plusMinutes(15).plusSeconds(30))
        assertThat(result).contains("2h").contains("15m").contains("30s")
    }

    @Test
    fun `formattedDuration should correctly format durations over 10 hours (issue 3723)`() {
        // 13 hours 10 minutes 56 seconds — reported as "4d 23h 10m 56s"
        val duration = Duration.ofHours(13).plusMinutes(10).plusSeconds(56)
        val result = formattedDuration(duration)
        assertThat(result).contains("13h").contains("10m").contains("56s")
        assertThat(result).doesNotContain("d")
    }

    @Test
    fun `formattedDuration should correctly format durations with days`() {
        val duration = Duration.ofDays(1).plusHours(5).plusMinutes(30).plusSeconds(15)
        val result = formattedDuration(duration)
        assertThat(result).contains("1d").contains("5h").contains("30m").contains("15s")
    }

    @Test
    fun `formattedDuration should correctly format exactly 24 hours`() {
        val result = formattedDuration(Duration.ofHours(24))
        assertThat(result).contains("1d")
        assertThat(result).doesNotContain("h")
    }

    @Test
    fun `totalDurationOf sums individual test durations not wall clock time`() {
        // If tests run in parallel, totalDurationOf can be much larger than wall clock time.
        // With 10 outcomes each lasting 5000ms, total = 50000ms regardless of parallelism.
        val outcomes = (1..10).map { _ ->
            val outcome = Mockito.mock(TestOutcome::class.java)
            Mockito.`when`(outcome.duration).thenReturn(5000L)
            outcome
        }
        val total = totalDurationOf(outcomes)
        assertThat(total).isEqualTo(Duration.ofMillis(50000))
    }

}
