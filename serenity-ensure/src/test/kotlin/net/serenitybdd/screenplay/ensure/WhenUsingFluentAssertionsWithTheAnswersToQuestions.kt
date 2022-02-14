package net.serenitybdd.screenplay.ensure

import net.serenitybdd.screenplay.Question
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.LocalTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WhenUsingFluentAssertionsWithTheAnswersToQuestions {

    internal fun colorRed(): Question<String> {
        return Question.about("the colour").answeredBy { "BLUE" }
    }

    internal fun age(): Question<Int> {
        return Question.about("the age").answeredBy { 30 }
    }

    internal fun flag(): Question<Boolean> {
        return Question.about("the flag").answeredBy { false }
    }

    internal fun float(): Question<Float> {
        return Question.about("the float").answeredBy { 1.3f }
    }

    internal fun double(): Question<Double> {
        return Question.about("the double").answeredBy { 1.3 }
    }

    internal fun time(): Question<LocalTime> {
        return Question.about("the time").answeredBy { LocalTime.of(12,0) }
    }

    @Nested
    inner class YouCanSpecifyADescriptionFor {

        @Test
        fun `questions about strings`() {
            shouldFailWithMessage("""|Expecting a color value that is equal to: <"RED">
                                     |But got.................................: <"BLUE">"""
                    .trimMargin())
                    .whenChecking(that("a color value", colorRed()).isEqualTo("RED"))
        }

        @Test
        fun `questions about numbers`() {
            shouldFailWithMessage("""|Expecting an age that is equal to: <20>
                                     |But got..........................: <30>"""
                    .trimMargin())
                    .whenChecking(that("an age", age()).isEqualTo(20))
        }

        @Test
        fun `questions about booleans`() {
            shouldFailWithMessage("""|Expecting a flag that is equal to: <true>
                                     |But got..........................: <false>"""
                    .trimMargin())
                    .whenChecking(that("a flag", flag()).isEqualTo(true))
        }

        @Test
        fun `questions about floats`() {
            shouldFailWithMessage("""|Expecting a float that is equal to: <1.2>
                                     |But got...........................: <1.3>"""
                    .trimMargin())
                    .whenChecking(that("a float", float()).isEqualTo(1.2f))
        }

        @Test
        fun `questions about doubles`() {
            shouldFailWithMessage("""|Expecting a double that is equal to: <1.2>
                                     |But got............................: <1.3>"""
                    .trimMargin())
                    .whenChecking(that("a double", double()).isEqualTo(1.2))
        }

        @Test
        fun `questions about times`() {
            shouldFailWithMessage("""|Expecting a time that is equal to: <13:00>
                                     |But got..........................: <12:00>"""
                    .trimMargin())
                    .whenChecking(that("a time", time()).isEqualTo(LocalTime.of(13,0)))
        }

    }
}