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
            shouldFailWithMessage("""|Expected: a color value that is equal to: <"RED">
                                     |Actual:   <"BLUE">"""
                    .trimMargin())
                    .whenChecking(that("a color value", colorRed()).isEqualTo("RED"))
        }

        @Test
        fun `questions about numbers`() {
            shouldFailWithMessage("""|Expected: an age that is equal to: <20>
                                     |Actual:   <30>"""
                    .trimMargin())
                    .whenChecking(that("an age", age()).isEqualTo(20))
        }

        @Test
        fun `questions about booleans`() {
            shouldFailWithMessage("""|Expected: a flag that is equal to: <true>
                                     |Actual:   <false>"""
                    .trimMargin())
                    .whenChecking(that("a flag", flag()).isEqualTo(true))
        }

        @Test
        fun `questions about floats`() {
            shouldFailWithMessage("""|Expected: a float that is equal to: <1.2>
                                     |Actual:   <1.3>"""
                    .trimMargin())
                    .whenChecking(that("a float", float()).isEqualTo(1.2f))
        }

        @Test
        fun `questions about doubles`() {
            shouldFailWithMessage("""|Expected: a double that is equal to: <1.2>
                                     |Actual:   <1.3>"""
                    .trimMargin())
                    .whenChecking(that("a double", double()).isEqualTo(1.2))
        }

        @Test
        fun `questions about times`() {
            shouldFailWithMessage("""|Expected: a time that is equal to: <13:00>
                                     |Actual:   <12:00>"""
                    .trimMargin())
                    .whenChecking(that("a time", time()).isEqualTo(LocalTime.of(13,0)))
        }

    }
}
