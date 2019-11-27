package net.serenitybdd.screenplay.ensure

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WhenUsingFluentAssertionsWithBooleans {


    @Nested
    inner class WeCanCheckFor {

        @Nested
        inner class TrueAndFalse {

            @Test
            fun `when the value is true`() {
                shouldPassWhenChecking(that(true).isTrue())
            }

            @Test
            fun `when the value is false`() {
                shouldPassWhenChecking(that(false).isFalse())
            }

            @Test
            fun `for negative cases`() {
                shouldFailWithMessage("""|Expecting a value that is true
                                     |But got: false"""
                        .trimMargin())
                        .whenChecking(that(false).isTrue())
            }

            @Test
            fun `for false positive cases`() {
                shouldFailWithMessage("""|Expecting a value that is true
                                     |But got: false"""
                        .trimMargin())
                        .whenChecking(that(false).isTrue())
            }
            @Test
            fun `when the value is a double negative`() {
                shouldPassWhenChecking(that(false).not().isTrue())
            }

            @Test
            fun `when the value is null`() {
                val nullFlag : Boolean? = null
                shouldFailWhenChecking(that(nullFlag).isTrue())
                shouldPassWhenChecking(that(nullFlag).isFalse())
            }


            @Test
            fun `when the value is true but we expect false`() {
                shouldFailWhenChecking(that(true).isFalse())
            }

            @Test
            fun `when the value is false but we expect true`() {
                shouldFailWhenChecking(that(false).isTrue())
            }
        }
    }
}