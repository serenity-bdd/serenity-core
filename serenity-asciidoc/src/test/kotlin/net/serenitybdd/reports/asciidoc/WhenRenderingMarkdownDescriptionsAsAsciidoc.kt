package net.serenitybdd.reports.asciidoc

import net.serenitybdd.reports.asciidoc.configuration.SerenityReport
import net.thucydides.model.util.EnvironmentVariables
import net.thucydides.model.util.MockEnvironmentVariables
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WhenRenderingMarkdownDescriptionsAsAsciidoc {

    @Test
    fun `markdown should be converted to asciidoc`() {
        assertThat(Formatted().asAsciidoc("## A Title")).isEqualTo("== A Title")
    }

    @Test
    fun `asciidoc should be left as asciidoc`() {
        assertThat(Formatted().asAsciidoc("== A Title")).isEqualTo("== A Title")
    }

    @Test
    fun `should capitalise titles`() {
        assertThat(Formatted().asATitle("capability")).isEqualTo("Capability")
    }

    @Test
    fun `should capitalise scenario titles`() {
        assertThat(Formatted().asATitle("some scenario name")).isEqualTo("Some scenario name")
    }
}
