package net.serenitybdd.reports.model

import net.thucydides.core.model.TestOutcome
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito
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

}