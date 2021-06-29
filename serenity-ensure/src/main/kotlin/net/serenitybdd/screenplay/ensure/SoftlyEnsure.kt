@file:JvmName("SoftlyEnsure")

package net.serenitybdd.screenplay.ensure

import net.serenitybdd.markers.IsSilent
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Performable

/**
 * Start recording failed Ensure statements as soft assertions
 */
fun start(): Performable = StartSoftAssertions()

/**
 * Throw any cumulated soft assertions in an AssertionError
 */
fun finish(): Performable = FinishSoftAssertions()

class StartSoftAssertions : Performable, IsSilent {
    override fun <T : Actor?> performAs(actor: T) {
        BlackBox.startSoftAssertions()
    }
}

class FinishSoftAssertions : Performable, IsSilent {
    override fun <T : Actor?> performAs(actor: T) {
        BlackBox.reportAnySoftAssertions()
    }
}