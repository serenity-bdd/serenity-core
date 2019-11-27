package net.serenitybdd.screenplay.ensure

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.Comparator

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WhenUsingFluentAssertionsWIthComparableTypes {

    internal enum class Color {
        RED, BLUE
    }

    @Nested
    inner class WeCanUseDifferentTypesSuchAs {
        @Test
        fun integers() {
            shouldPassWhenChecking(that(2).isGreaterThan(1))
        }

        @Test
        fun longs() {
            shouldPassWhenChecking(that(20000000000L).isGreaterThan(10000000000L))
        }

        @Test
        fun floats() {
            shouldPassWhenChecking(that(3.14159).isGreaterThan(3.0))
        }

    }

        @Nested
    inner class WeCanCheckFor {

        @Nested
        inner class GreaterThan {

            @Test
            fun `for positive cases`() {
                shouldPassWhenChecking(that(2).isGreaterThan(1))
            }

            @Test
            fun `for negative cases`() {
                shouldFailWithMessage("""|Expecting a value that is greater than: <2>
                                         |But got...............................: <1>"""
                        .trimMargin())
                        .whenChecking(that(1).isGreaterThan(2))
            }

            @Test
            fun `for longs`() {
                shouldPassWhenChecking(that(2000000000).isGreaterThan(1))

            }

            @Test
            fun `for floats`() {
                shouldPassWhenChecking(that(3.14159).isGreaterThan(3.0))

            }

            @Test
            fun `for shorts`() {
                val age : Short = 30
                shouldPassWhenChecking(that(age).isGreaterThan(18))

            }

            @Test
            fun `with a custom comparator`() {
                shouldPassWhenChecking(that(1).usingComparator(Comparator.reverseOrder()).isGreaterThan(2))

            }

            @Test
            fun `with an enum`() {
                shouldPassWhenChecking(that(Color.BLUE).isGreaterThan(Color.RED))
            }

            @Test
            fun `with an enum and a custom converter`() {
                shouldPassWhenChecking(that(Color.RED).usingComparator(Comparator.reverseOrder()).isGreaterThan(Color.BLUE))
            }
        }

        @Nested
        inner class GreaterThanOrEqualTo {

            @Test
            fun `for positive cases`() {
                shouldPassWhenChecking(that(2).isGreaterThanOrEqualTo(1))
                shouldPassWhenChecking(that(2).isGreaterThanOrEqualTo(2))
            }

            @Test
            fun `for negative cases`() {
                shouldFailWithMessage("""|Expecting a value that is greater than or equal to: <2>
                                         |But got...........................................: <1>"""
                        .trimMargin())
                        .whenChecking(that(1).isGreaterThanOrEqualTo(2))
            }

        }


        @Nested
        inner class LessThan {

            @Test
            fun `for positive cases`() {
                shouldPassWhenChecking(that(1).isLessThan(2))
            }

            @Test
            fun `for negative cases`() {
                shouldFailWithMessage("""|Expecting a value that is less than: <2>
                                         |But got............................: <2>"""
                        .trimMargin())
                        .whenChecking(that(2).isLessThan(2))
            }

        }

        @Nested
        inner class LessThanOrEqualTo {

            @Test
            fun `for positive cases`() {
                shouldPassWhenChecking(that(1).isLessThanOrEqualTo(2))
                shouldPassWhenChecking(that(2).isLessThanOrEqualTo(2))
            }

            @Test
            fun `for negative cases`() {
                shouldFailWithMessage("""|Expecting a value that is less than or equal to: <1>
                                         |But got........................................: <2>"""
                        .trimMargin())
                        .whenChecking(that(2).isLessThanOrEqualTo(1))
            }
        }

        @Nested
        inner class ValuesInRanges {

            @Test
            fun `for positive cases`() {
                shouldPassWhenChecking(that(1).isBetween(0, 2))
            }

            @Test
            fun `for negative cases`() {
                shouldFailWithMessage("""|Expecting a value that is between 1 and 3
                                         |But got: 5"""
                        .trimMargin())
                        .whenChecking(that(5).isBetween(1, 3))
            }
        }

        @Nested
        inner class ValuesStrictlyInRanges {

            @Test
            fun `for positive cases`() {
                shouldPassWhenChecking(that(1).isStrictlyBetween(0, 2))
            }

            @Test
            fun `for negative cases`() {
                shouldFailWithMessage("""|Expecting a value that is strictly between 1 and 3
                                         |But got: 5"""
                        .trimMargin())
                        .whenChecking(that(5).isStrictlyBetween(1, 3))
            }

            @Test
            fun `for lower boundaries`() {
                shouldFailWhenChecking(that(1).isStrictlyBetween(1, 3))
            }

            @Test
            fun `for upper boundaries`() {
                shouldFailWhenChecking(that(3).isStrictlyBetween(3, 3))
            }
        }

        @Nested
        inner class WeCanProvideADifferentComparator {

            @Test
            fun `such as String Length`() {
                val byLength = Comparator.comparingInt<String> { it.length }

                shouldPassWhenChecking(that("aardvark").usingComparator(byLength).isGreaterThan("cat"))
            }

            @Test
            fun `for negative cases`() {
                shouldFailWithMessage("""|Expecting a value that is strictly between 1 and 3
                                         |But got: 5"""
                        .trimMargin())
                        .whenChecking(that(5).isStrictlyBetween(1, 3))
            }
        }
    }
}