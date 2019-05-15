package net.serenitybdd.screenplay.ensure

import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Performable
import org.junit.jupiter.api.fail

private val aster : Actor = Actor.named("Aster")

fun shouldPassWhenChecking(assertion: Performable) {
    assertion.performAs(aster)
}

fun shouldFailWhenChecking(assertion: Performable) {
    try {
        assertion.performAs(aster)
    } catch (expectedError: AssertionError) { return }

    fail("Expecting an assertion error")
}

fun shouldFailWithMessage(expectedMessage : String) = WhenCheckingForFailure (expectedMessage)

class WhenCheckingForFailure(val expectedMessage: String) {
    fun whenChecking(assertion: Performable) {
        try {
            assertion.performAs(aster)
        } catch (expectedError: AssertionError) {
            if (expectedError.message!!.trim() != expectedMessage) {
                fail("Expecting an assertion error with message '$expectedMessage' but message was '${expectedError.message}'")
            }
            return
        }

        fail("Expecting an assertion error")
    }
}
