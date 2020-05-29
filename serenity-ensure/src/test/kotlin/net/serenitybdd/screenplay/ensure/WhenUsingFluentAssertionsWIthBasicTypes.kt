package net.serenitybdd.screenplay.ensure

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.LocalDate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WhenUsingFluentAssertionsWIthBasicTypes {

    @Nested
    inner class WeCanCheckFor {

        @Nested
        inner class Equality {

            @Test
            fun `for positive cases with strings`() {
                shouldPassWhenChecking(that("foo").isEqualTo("foo"))
            }

            @Test
            fun `for negative cases with strings`() {
                shouldFailWithMessage("""|Expecting a value that is equal to: <"bar">
                                         |But got...........................: <"foo">"""
                        .trimMargin())
                        .whenChecking(that("foo").isEqualTo("bar"))
            }

            @Test
            fun `for positive cases with numbers`() {
                shouldPassWhenChecking(that(1).isEqualTo(1))
            }


            @Test
            fun `for negative cases with numbers`() {
                shouldFailWithMessage("""|Expecting a value that is equal to: <2>
                                         |But got...........................: <1>"""
                        .trimMargin())
                        .whenChecking(that(1).isEqualTo(2))
            }

            @Test
            fun `for positive cases with objects`() {
                val someDay = LocalDate.of(2000,1, 1)
                val theSameDay = LocalDate.of(2000,1, 1)
                shouldPassWhenChecking(that(someDay).isEqualTo(theSameDay))
            }

            @Test
            fun `for negative cases with objects`() {
                val someDay = LocalDate.of(2000,1, 1)
                val someOtherDay = LocalDate.of(2000,1, 2)
                shouldFailWithMessage("""|Expecting a value that is equal to: <2000-01-02>
                                         |But got...........................: <2000-01-01>"""
                        .trimMargin())
                        .whenChecking(that(someDay).isEqualTo(someOtherDay))
            }

        }

        @Nested
        inner class Inequality {
            @Test
            fun `for positive cases`() {
                shouldPassWhenChecking(that("foo").isNotEqualTo("bar"))
            }

            @Test
            fun `for negative cases`() {
                shouldFailWithMessage("""|Expecting a value that is not equal to: <"bar">
                                         |But got...............................: <"bar">"""
                        .trimMargin())
                        .whenChecking(that("bar").isNotEqualTo("bar"))
            }
        }

        @Nested
        inner class NullValues {
            val name: String? = null

            @Test
            fun `for positive cases`() {
                shouldPassWhenChecking(that(name).isNull())
            }

            @Test
            fun `for negative cases`() {
                shouldFailWithMessage("""|Expecting a value that is null
                                     |But got: "bar""""
                        .trimMargin())
                        .whenChecking(that("bar").isNull())
            }
        }

        @Nested
        inner class NonNullValues {


            @Test
            fun `for positive cases`() {
                val name: String? = "Bill"
                shouldPassWhenChecking(that(name).isNotNull())
            }

            @Test
            fun `for negative cases`() {
                val nullName: String? = null
                shouldFailWithMessage("""|Expecting a value that is not null
                                     |But got: <null>"""
                        .trimMargin())
                        .whenChecking(that(nullName).isNotNull())
            }
        }

        @Nested
        inner class ValuesInAnArray {

            @Test
            fun `for positive cases`() {
                shouldPassWhenChecking(that("red").isIn("red", "green", "blue"))
            }

            @Test
            fun `for negative cases`() {
                shouldFailWithMessage("""|Expecting a value that is in: <[red, green, blue]>
                                         |But got.....................: <"yellow">"""
                        .trimMargin())
                        .whenChecking(that("yellow").isIn("red", "green", "blue"))
            }
            @Test
            fun `for null values`() {
                val nullValue: String? = null
                shouldFailWhenChecking(that(nullValue).isIn("red", "green", "blue"))
            }
        }

        @Nested
        inner class ValuesNotInAnArray {

            @Test
            fun `for positive cases`() {
                shouldPassWhenChecking(that("yellow").isNotIn("red", "green", "blue"))
            }

            @Test
            fun `for negative cases`() {
                shouldFailWithMessage("""|Expecting a value that is not in: <[red, green, blue]>
                                         |But got.........................: <"red">"""
                        .trimMargin())
                        .whenChecking(that("red").isNotIn("red", "green", "blue"))
            }
            @Test
            fun `for null values`() {
                val nullValue: String? = null
                shouldPassWhenChecking(that(nullValue).isNotIn("red", "green", "blue"))
            }
        }

        @Nested
        inner class ValuesInACollection {

            @Test
            fun `for positive cases`() {
                shouldPassWhenChecking(that("red").isIn(listOf("red", "green", "blue")))
            }

            @Test
            fun `for negative cases`() {
                shouldFailWithMessage("""|Expecting a value that is in: <[red, green, blue]>
                                         |But got.....................: <"yellow">"""
                        .trimMargin())
                        .whenChecking(that("yellow").isIn(listOf("red", "green", "blue")))
            }
            @Test
            fun `for null values`() {
                val nullValue: String? = null
                shouldFailWhenChecking(that(nullValue).isIn(listOf("red", "green", "blue")))
            }
        }

        @Nested
        inner class ValuesNotInACollection {

            @Test
            fun `for positive cases`() {
                shouldPassWhenChecking(that("yellow").isNotIn(setOf("red", "green", "blue")))
            }

            @Test
            fun `for negative cases`() {
                shouldFailWithMessage("""|Expecting a value that is not in: <[red, green, blue]>
                                         |But got.........................: <"red">"""
                        .trimMargin())
                        .whenChecking(that("red").isNotIn(setOf("red", "green", "blue")))
            }

            @Test
            fun `for null values`() {
                val nullValue: String? = null
                shouldPassWhenChecking(that(nullValue).isNotIn(setOf("red", "green", "blue")))
            }
        }

        @Nested
        inner class NegativeConditions {
            @Test
            fun `for positive cases`() {
                shouldPassWhenChecking(that("yellow").not().isIn(setOf("red", "green", "blue")))
            }

            @Test
            fun `for negative cases`() {
                shouldFailWithMessage("""|Expecting a value that is not in: <[red, green, blue]>
                                         |But got.........................: <"red">"""
                        .trimMargin())
                        .whenChecking(that("red").not().isIn(setOf("red", "green", "blue")))
            }
        }

    }
}