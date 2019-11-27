package net.serenitybdd.screenplay.ensure

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WhenUsingFluentAssertionsWithFloatingPointNumbers {


    @Nested
    inner class WeCanCheckFor {

        val floatingPointTen = 10.0f
        val doubleTen = 10.0

        @Nested
        inner class FloatingPointComparisons {

            @Test
            fun equality() {
                shouldPassWhenChecking(that(floatingPointTen).isEqualTo(10.0f))
            }

            @Test
            fun `non-equality`() {
                shouldPassWhenChecking(that(floatingPointTen).not().isEqualTo(11.0f))
            }

            @Test
            fun `close to`() {
                shouldPassWhenChecking(that(floatingPointTen).isCloseTo(9.9f, 0.1f))
            }

            @Test
            fun `not close to`() {
                shouldFailWhenChecking(that(floatingPointTen).isCloseTo(9.9f, 0.05f))
            }
        }


        @Nested
        inner class DoublePointComparisons {

            @Test
            fun equality() {
                shouldPassWhenChecking(that(doubleTen).isEqualTo(10.0))
            }

            @Test
            fun `non-equality`() {
                shouldPassWhenChecking(that(doubleTen).not().isEqualTo(11.0))
            }

            @Test
            fun `close to`() {
                shouldPassWhenChecking(that(doubleTen).isCloseTo(9.9, 0.1))
            }

            @Test
            fun `not close to`() {
                shouldFailWhenChecking(that(doubleTen).isCloseTo(9.9, 0.05))
            }
        }
    }
}