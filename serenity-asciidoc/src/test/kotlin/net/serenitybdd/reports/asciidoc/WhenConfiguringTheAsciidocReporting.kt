package net.serenitybdd.reports.asciidoc

import net.serenitybdd.reports.asciidoc.configuration.SerenityReport
import net.thucydides.model.util.EnvironmentVariables
import net.thucydides.model.util.MockEnvironmentVariables
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WhenConfiguringTheAsciidocReporting {

    @Nested
    inner class TheOutputDirectory {

        private val environmentVariables : EnvironmentVariables = MockEnvironmentVariables()

        @Test
        fun `should be target-site-serenity by default`() {
            assertThat(SerenityReport.outputDirectory().configuredIn(environmentVariables).toString()).isEqualTo("target/site/serenity")
        }

        @Test
        fun `can be overriden using the serenity-dot-outputDirectory property`() {

            environmentVariables.setProperty("serenity.outputDirectory","/my/output/dir")

            assertThat(SerenityReport.outputDirectory().configuredIn(environmentVariables).toString()).isEqualTo("/my/output/dir")
        }

    }
}
