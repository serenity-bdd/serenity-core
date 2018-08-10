package net.serenitybdd.reports.configuration

import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WhenConfiguringReportProperties {

    private val environmentVariables: EnvironmentVariables = MockEnvironmentVariables()


    @Nested
    inner class WithStrings {
        init {
            environmentVariables.setProperty("favorite.color", "Blue")
        }

        @Test
        fun `using string properties`() {
            assertThat(StringReportProperty("favorite.color").configuredIn(environmentVariables)).isEqualTo("Blue")
        }

        @Test
        fun `providing a default value`() {
            assertThat(StringReportProperty("favorite.city","Rome").configuredIn(environmentVariables)).isEqualTo("Rome")
        }

        @Test
        fun `the default is an empty string`() {
            assertThat(StringReportProperty("favorite.city").configuredIn(environmentVariables)).isEqualTo("")
        }
    }

    @Nested
    inner class WithIntegers {
        init {
            environmentVariables.setProperty("favorite.number", "7")
        }

        @Test
        fun `using integer properties`() {
            assertThat(IntReportProperty("favorite.number").configuredIn(environmentVariables)).isEqualTo(7)
        }

        @Test
        fun `providing a default value`() {
            assertThat(IntReportProperty("favorite.integer",7).configuredIn(environmentVariables)).isEqualTo(7)
        }

        @Test
        fun `the default is an empty string`() {
            assertThat(IntReportProperty("favorite.integer").configuredIn(environmentVariables)).isEqualTo(0)
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
            assertThat(StringListReportProperty("favorite.colors").configuredIn(environmentVariables)).isEqualTo(listOf("red","blue"))
        }

        @Test
        fun `should ignore spaces`() {
            assertThat(StringListReportProperty("favorite.colors.with.spaces").configuredIn(environmentVariables)).isEqualTo(listOf("red","blue"))
        }

        @Test
        fun `providing a default value`() {
            assertThat(StringListReportProperty("favorite.numbers",listOf("1","2")).configuredIn(environmentVariables)).isEqualTo(listOf("1","2"))
        }

        @Test
        fun `the default is an empty list`() {
            assertThat(StringListReportProperty("favorite.things").configuredIn(environmentVariables)).isEqualTo(listOf<String>())
        }
    }


}