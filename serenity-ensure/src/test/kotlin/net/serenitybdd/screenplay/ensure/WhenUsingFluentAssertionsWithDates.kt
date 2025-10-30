package net.serenitybdd.screenplay.ensure

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WhenUsingFluentAssertionsWithDates {


    @Nested
    inner class WeCanCheckThat {

        val firstDayOfTheYear = LocalDate.of(2019,1,1)
        val tuesday = LocalDate.of(2019,1,1)
        val wednesday = LocalDate.of(2019,1,2)
        val thursday = LocalDate.of(2019,1,3)

        @Nested
        inner class ADate {

            @Test
            fun `is equivalent to another date`() {
                shouldPassWhenChecking(that(firstDayOfTheYear).isEqualTo(tuesday))
            }

            @Test
            fun `is different to another date`() {
                shouldPassWhenChecking(that(firstDayOfTheYear).not().isEqualTo(wednesday))
            }

            @Test
            fun `is before another date`() {
                shouldPassWhenChecking(that(tuesday).isBefore(wednesday))
            }

            @Test
            fun `is after another date`() {
                shouldPassWhenChecking(that(thursday).isAfter(wednesday))
            }

            @Test
            fun `matches using a custom comparator`() {

                val byMonth = Comparator.comparingInt<LocalDate> { it.monthValue }

                shouldPassWhenChecking(that(thursday).usingComparator(byMonth).isEqualTo(wednesday))
                shouldFailWhenChecking(that(thursday).usingComparator(byMonth).isNotEqualTo(wednesday))
            }


            @Test
            fun `using custom comparators with the normal comparison methods, not the specific date ones`() {

                val byMonthNumber = Comparator.comparingInt<LocalDate> { it.month.value }

                val firstOfJanuary = LocalDate.of(2019,1,1)
                val secondOfFebruary = LocalDate.of(2018,2,1)

                shouldPassWhenChecking(that(firstOfJanuary).usingComparator(byMonthNumber).isLessThan(secondOfFebruary))
                shouldFailWhenChecking(that(firstOfJanuary).usingComparator(byMonthNumber).isBefore(secondOfFebruary))
            }

            @Test
            fun `when the date does not match`() {
                shouldFailWithMessage("""|Expected: a value that is before: <2019-01-01>
                                         |Actual:   <2019-01-02>"""
                        .trimMargin())
                        .whenChecking(that(wednesday).isBefore(tuesday))
            }
        }

        @Nested
        inner class TheDayOfTheWeek {
            @Test
            fun `is the expected day`() {
                shouldPassWhenChecking(that(thursday).isDayOfWeek(DayOfWeek.THURSDAY))
            }

            @Test
            fun `is not the expected day`() {
                shouldFailWithMessage("""|Expected: a value that is on a: <THURSDAY>
                                         |Actual:   <2019-01-02>"""
                        .trimMargin())
                        .whenChecking(that(wednesday).isDayOfWeek(DayOfWeek.THURSDAY))
            }
        }

        @Nested
        inner class TheDayOfTheMonth {
            @Test
            fun `is the expected day`() {
                shouldPassWhenChecking(that(thursday).isDayOfMonth(3))
            }

            @Test
            fun `is not the expected day`() {
                shouldFailWithMessage("""|Expected: a value that is on day of month: <2>
                                         |Actual:   <2019-01-03>"""
                        .trimMargin())
                        .whenChecking(that(thursday).isDayOfMonth(2))
            }
        }

        @Nested
        inner class TheMonth {
            @Test
            fun `is the expected month`() {
                shouldPassWhenChecking(that(thursday).isInTheMonthOf(Month.JANUARY))
            }

            @Test
            fun `is not the expected month`() {
                shouldFailWithMessage("""|Expected: a value that is in month: <MARCH>
                                         |Actual:   <2019-01-03>"""
                        .trimMargin())
                        .whenChecking(that(thursday).isInTheMonthOf(Month.MARCH))
            }
        }


        @Nested
        inner class TheYear {
            @Test
            fun `is the expected year`() {
                shouldPassWhenChecking(that(thursday).isTheYear(2019))
            }

            @Test
            fun `is not the expected year`() {
                shouldFailWithMessage("""|Expected: a value that is in the year: <2000>
                                         |Actual:   <2019-01-03>"""
                        .trimMargin())
                        .whenChecking(that(thursday).isTheYear(2000))
            }
        }
    }
}
