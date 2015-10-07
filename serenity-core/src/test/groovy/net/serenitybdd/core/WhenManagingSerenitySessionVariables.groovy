package net.serenitybdd.core

import spock.lang.Specification

import static net.serenitybdd.core.Serenity.*

/**
 * Session variables can be used to store information between step methods in different classes.
 * They are reinitialized at the start of each session.
 */
class WhenManagingSerenitySessionVariables extends Specification {

    def "storing variables in the Serenity session between steps"() {
        when: "I store a variable in the Serenity Session"
            setSessionVariable("customerName").to("Jim")
        then: "I should be able to retrieve it later"
            sessionVariableCalled("customerName") == "Jim"
    }

    def "session variables are reset at the start of each session"() {
        given:
            setSessionVariable("customerName").to("Jim")
        when:
            initializeTestSession()
        then:
            sessionVariableCalled("customerName") == null
    }
}
