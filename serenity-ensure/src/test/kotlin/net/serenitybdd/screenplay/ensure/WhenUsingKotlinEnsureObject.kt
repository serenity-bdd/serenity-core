package net.serenitybdd.screenplay.ensure

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * Tests for the Kotlin-friendly Ensure object syntax.
 * This verifies that Kotlin developers can use `Ensure.that(...)` instead of
 * importing the top-level `that` function directly.
 *
 * See: https://github.com/serenity-bdd/serenity-core/issues/3682
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WhenUsingKotlinEnsureObject {

    @Nested
    inner class WithStrings {

        @Test
        fun `can check string is not empty using Ensure object`() {
            shouldPassWhenChecking(Ensure.that("hello").isNotEmpty())
        }

        @Test
        fun `can check string contains text using Ensure object`() {
            shouldPassWhenChecking(Ensure.that("hello world").contains("world"))
        }

        @Test
        fun `can check null string using Ensure object`() {
            val nullValue: String? = null
            shouldPassWhenChecking(Ensure.that(nullValue).isNullOrEmpty())
        }
    }

    @Nested
    inner class WithBooleans {

        @Test
        fun `can check boolean is true using Ensure object`() {
            shouldPassWhenChecking(Ensure.that(true).isTrue())
        }

        @Test
        fun `can check boolean is false using Ensure object`() {
            shouldPassWhenChecking(Ensure.that(false).isFalse())
        }
    }

    @Nested
    inner class WithNumbers {

        @Test
        fun `can check comparable values using Ensure object`() {
            shouldPassWhenChecking(Ensure.that(42).isGreaterThan(10))
        }

        @Test
        fun `can check double values using Ensure object`() {
            shouldPassWhenChecking(Ensure.that(3.14).isGreaterThan(3.0))
        }
    }

    @Nested
    inner class WithCollections {

        @Test
        fun `can check collection is not empty using Ensure object`() {
            shouldPassWhenChecking(Ensure.that(listOf(1, 2, 3)).isNotEmpty())
        }

        @Test
        fun `can check collection contains element using Ensure object`() {
            shouldPassWhenChecking(Ensure.that(listOf("a", "b", "c")).contains("b"))
        }
    }

    @Nested
    inner class SoftAssertions {

        @Test
        fun `can enable and report soft assertions using Ensure object`() {
            // Just verify these methods are accessible - actual soft assertion testing
            // is done elsewhere
            Ensure.enableSoftAssertions()
            Ensure.reportSoftAssertions()
        }
    }
}