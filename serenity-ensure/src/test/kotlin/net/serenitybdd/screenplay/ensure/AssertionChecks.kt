package net.serenitybdd.screenplay.ensure

import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Performable
import org.junit.jupiter.api.fail

private val aster : Actor = Actor.named("Aster")

fun shouldPassWhenChecking(assertion: Performable, actor : Actor? = aster) {
    assertion.performAs(actor)
}

fun shouldFailWhenChecking(assertion: Performable, actor: Actor? = aster) {

    try {
        assertion.performAs(actor)
    } catch (webError: Throwable) { return }

    fail("Expecting an error")
}

fun shouldFailWithMessage(expectedMessage : String) = WhenCheckingForFailure (expectedMessage)

class WhenCheckingForFailure(val expectedMessage: String) {
    fun whenChecking(assertion: Performable, actor: Actor? = aster) {
        try {
            assertion.performAs(actor)
        } catch (expectedError: AssertionError) {
            if (expectedError.message!!.trim() != expectedMessage) {
                fail("Expecting an assertion error with message '$expectedMessage' but message was '${expectedError.message}'")
            }
            return
        }

        fail("Expecting an assertion error")
    }
}
