package serenity_bdd.core

import net.serenity_bdd.core.Serenity
import spock.lang.Specification

/**
 * Session variables can be used to store information between step methods in different classes.
 * They are reinitialized at the start of each session.
 */
class WhenManagingSerenitySessionVariables extends Specification {

    def "storing variables in the Serenity session between steps"() {
        when: "I store a variable in the Serenity Session"
            Serenity.setSessionVariable("customerName").to("Jim")
        then: "I should be able to retrieve it later"
            Serenity.sessionVariableCalled("customerName") == "Jim"
    }

    def "session variables are reset at the start of each session"() {
        given:
            Serenity.setSessionVariable("customerName").to("Jim")
        when:
            Serenity.initializeTestSession()
        then:
            Serenity.sessionVariableCalled("customerName") == null
    }
}
