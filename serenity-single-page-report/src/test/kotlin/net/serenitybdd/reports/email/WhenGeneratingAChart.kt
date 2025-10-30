package net.serenitybdd.reports.asciidoc

import net.serenitybdd.reports.graphs.ResultChart
import net.thucydides.model.domain.TestResult.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WhenGeneratingAChart {

    @Test
    fun `we should be able to save a donut chart as an image file`() {

        val results = mapOf(
                SUCCESS to 10,
                PENDING to 20,
                IGNORED to 30,
                FAILURE to 40,
                ERROR to 50,
                COMPROMISED to 60
        )

        val savedGraph = savedGraph()

        ResultChart(results).saveTo(savedGraph)

        assertThat(savedGraph).exists()
    }

    private fun savedGraph() = java.io.File.createTempFile("graph", ".png")

}
