package net.serenitybdd.screenplay.ensure

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WhenUsingSimpleFluentAssertionsWithDates {


    @Nested
    inner class WeCanCheckFor {

        val firstDayOfTheYear = LocalDate.of(2019,1,1)
        val tuesday = LocalDate.of(2019,1,1)
        val wednesday = LocalDate.of(2019,1,2)
        val thursday = LocalDate.of(2019,1,3)

        @Nested
        inner class DateComparisons {

            @Test
            fun `equality`() {
                shouldPassWhenChecking(that(firstDayOfTheYear).isEqualTo(tuesday))
            }

            @Test
            fun `before`() {
                shouldPassWhenChecking(that(tuesday).isBefore(wednesday))
            }

            @Test
            fun `after`() {
                shouldPassWhenChecking(that(thursday).isAfter(wednesday))
            }

            @Test
            fun `when the date does not match`() {
                shouldFailWithMessage("""|Expecting a value that is before: <2019-01-01>
                                         |But got.........................: <2019-01-02>"""
                        .trimMargin())
                        .whenChecking(that(wednesday).isBefore(tuesday))
            }
        }

        @Nested
        inner class TheDayOfTheWeek {
            @Test
            fun `when it is the expected day`() {
                shouldPassWhenChecking(that(thursday).isDayOfWeek(DayOfWeek.THURSDAY))
            }

            @Test
            fun `when it is not the expected day`() {
                shouldFailWithMessage("""|Expecting a value that is on a: <THURSDAY>
                                         |But got.......................: <2019-01-02>"""
                        .trimMargin())
                        .whenChecking(that(wednesday).isDayOfWeek(DayOfWeek.THURSDAY))
            }
        }

        @Nested
        inner class TheDayOfTheMonth {
            @Test
            fun `when it is the expected day`() {
                shouldPassWhenChecking(that(thursday).isDayOfMonth(3))
            }

            @Test
            fun `when it is not the expected day`() {
                shouldFailWithMessage("""|Expecting a value that is on day of month: <2>
                                         |But got..................................: <2019-01-03>"""
                        .trimMargin())
                        .whenChecking(that(thursday).isDayOfMonth(2))
            }
        }

        @Nested
        inner class TheMonth {
            @Test
            fun `when it is the expected month`() {
                shouldPassWhenChecking(that(thursday).isInTheMonthOf(Month.JANUARY))
            }

            @Test
            fun `when it is not the expected day`() {
                shouldFailWithMessage("""|Expecting a value that is in month: <MARCH>
                                         |But got...........................: <2019-01-03>"""
                        .trimMargin())
                        .whenChecking(that(thursday).isInTheMonthOf(Month.MARCH))
            }
        }


        @Nested
        inner class TheYear {
            @Test
            fun `when it is the expected year`() {
                shouldPassWhenChecking(that(thursday).isTheYear(2019))
            }

            @Test
            fun `when it is not the expected year`() {
                shouldFailWithMessage("""|Expecting a value that is in the year: <2000>
                                         |But got..............................: <2019-01-03>"""
                        .trimMargin())
                        .whenChecking(that(thursday).isTheYear(2000))
            }
        }
    }
}