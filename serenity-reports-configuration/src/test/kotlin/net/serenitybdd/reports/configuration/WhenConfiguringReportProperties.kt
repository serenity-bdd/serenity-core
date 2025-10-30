package net.serenitybdd.reports.configuration

import net.thucydides.model.environment.MockEnvironmentVariables
import net.thucydides.model.util.EnvironmentVariables
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WhenConfiguringReportProperties {

    private var environmentVariables: EnvironmentVariables =
        MockEnvironmentVariables()


    @Nested
    inner class WithStrings {
        init {
            environmentVariables.setProperty("favorite.color", "Blue")
        }

        @Test
        fun `using string properties`() {
            assertThat(StringReportProperty("favorite.color","").configuredIn(environmentVariables)).isEqualTo("Blue")
        }

        @Test
        fun `providing a default value`() {
            assertThat(StringReportProperty("favorite.city", "Rome").configuredIn(environmentVariables)).isEqualTo("Rome")
        }

        @Test
        fun `the default is an empty string`() {
            assertThat(StringReportProperty("favorite.city","").configuredIn(environmentVariables)).isEqualTo("")
        }
    }

    @Nested
    inner class WithIntegers {
        init {
            environmentVariables.setProperty("favorite.number", "7")
        }

        @Test
        fun `using integer properties`() {
            assertThat(IntReportProperty("favorite.number",0).configuredIn(environmentVariables)).isEqualTo(7)
        }

        @Test
        fun `providing a default value`() {
            assertThat(IntReportProperty("favorite.integer", 7).configuredIn(environmentVariables)).isEqualTo(7)
        }

        @Test
        fun `the default is an empty string`() {
            assertThat(IntReportProperty("favorite.integer",0).configuredIn(environmentVariables)).isEqualTo(0)
        }
    }

    @Nested
    inner class WithListsOfStrings {
        init {
            environmentVariables.setProperty("favorite.colors", "red,blue")
            environmentVariables.setProperty("favorite.colors.with.spaces", " red ,  blue ")
        }

        @Test
        fun `using a comma-separated list of values`() {
            assertThat(StringListReportProperty("favorite.colors").configuredIn(environmentVariables)).isEqualTo(listOf("red", "blue"))
        }

        @Test
        fun `should ignore spaces`() {
            assertThat(StringListReportProperty("favorite.colors.with.spaces").configuredIn(environmentVariables)).isEqualTo(listOf("red", "blue"))
        }

        @Test
        fun `providing a default value`() {
            assertThat(StringListReportProperty("favorite.numbers", listOf("1", "2")).configuredIn(environmentVariables)).isEqualTo(listOf("1", "2"))
        }

        @Test
        fun `the default is an empty list`() {
            assertThat(StringListReportProperty("favorite.things").configuredIn(environmentVariables)).isEqualTo(listOf<String>())
        }
    }

    @Nested
    inner class ForTemplateFiles {

        val absoluteTemplateFile = createTempFile("templates",".ftl")
        val absoluteTemplateDir = createTempDir("templates")
        val relativeTemplateFile = "src/test/resources/sample-template.ftl"

        @BeforeEach
        fun reset() {
            environmentVariables = MockEnvironmentVariables()
        }

//        @Test
//        fun `we can define template files using an absolute path`() {
//
//            environmentVariables.setProperty("reports.templates.email", absoluteTemplateFile.absolutePath)
//
//            assertThat(TemplateFileProperty("default-template.ftl")
//                       .configuredIn(environmentVariables)).isEqualTo(absoluteTemplateFile.absolutePath)
//        }
//
//        @Test
//        fun `we can define template files using a relative path`() {
//
//            environmentVariables.setProperty("reports.templates.email", relativeTemplateFile)
//
//            assertThat(TemplateFileProperty("default-template.ftl","reports.templates.email")
//                    .configuredIn(environmentVariables)).isEqualTo(relativeTemplateFile)
//        }
//
//        @Test
//        fun `we can define template files using a default value path`() {
//
//            assertThat(TemplateFileProperty("default-template.ftl","reports.templates.email")
//                    .configuredIn(environmentVariables)).isEqualTo("default-template.ftl")
//        }

//        @Test
//        fun `we can define template files using a file name and a template directory`() {
//
//            environmentVariables.setProperty("reports.templates.directory", absoluteTemplateDir.absolutePath)
//            environmentVariables.setProperty("reports.templates.email", "default-template.ftl")
//
//            val templatePath = TemplateFileProperty("default-template.ftl",
//                    "reports.templates.email", "reports.templates.directory")
//                    .configuredIn(environmentVariables)
//
//            assertThat(templatePath).startsWith(absoluteTemplateDir.absolutePath)
//                                    .endsWith("default-template.ftl")
//        }

    }
}
