package net.serenitybdd.screenplay.ensure.dates

import net.serenitybdd.screenplay.ensure.CommonPreconditions.ensureActualAndExpectedNotNull
import net.serenitybdd.screenplay.ensure.CommonPreconditions.ensureActualNotNull
import net.serenitybdd.screenplay.ensure.CommonPreconditions.ensureNoNullElementsIn
import net.serenitybdd.screenplay.ensure.CommonPreconditions.ensureNotEmpty
import net.serenitybdd.screenplay.ensure.expectThatActualIs
import org.assertj.core.internal.InputStreamsException
import java.io.IOException
import java.io.LineNumberReader
import java.io.StringReader
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.util.function.Predicate
import java.util.regex.Pattern

object DateExpectations {
    val BEFORE = expectThatActualIs("before",
            fun(actual: LocalDate?, expected: LocalDate): Boolean {
                ensureActualNotNull(actual)
                return expected.isAfter(actual!!)
            })

    val AFTER = expectThatActualIs("after",
            fun(actual: LocalDate?, expected: LocalDate): Boolean {
                ensureActualNotNull(actual)
                return expected.isBefore(actual!!)
            })

    val DAY_OF_WEEK = expectThatActualIs("on a",
            fun(actual: LocalDate?, expected: DayOfWeek): Boolean {
                ensureActualNotNull(actual)
                return expected == actual!!.dayOfWeek
            })

    val DAY_OF_MONTH = expectThatActualIs("on day of month",
            fun(actual: LocalDate?, expected: Int): Boolean {
                ensureActualNotNull(actual)
                return expected == actual!!.dayOfMonth
            })

    val MONTH = expectThatActualIs("in month",
            fun(actual: LocalDate?, expected: Month): Boolean {
                ensureActualNotNull(actual)
                return expected == actual!!.month
            })

    val YEAR = expectThatActualIs("in the year",
            fun(actual: LocalDate?, expected: Int): Boolean {
                ensureActualNotNull(actual)
                return expected == actual!!.year
            })
}