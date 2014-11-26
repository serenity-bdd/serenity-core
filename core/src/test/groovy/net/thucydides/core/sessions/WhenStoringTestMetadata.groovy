package net.thucydides.core.sessions

import net.serenity_bdd.core.Serenity
import spock.lang.Specification

class WhenStoringTestMetadata extends Specification {


    def "there should be no metadata stored in the session by default"() {
        when:
            def metadata = Serenity.currentSession.getMetaData()
        then:
            metadata.isEmpty()
    }

    def "you can add metadata to the current session"() {
        given:
            Serenity.currentSession.addMetaData("some","value")
        when:
            def metadata = Serenity.currentSession.getMetaData()
        then:
            metadata.get("some") == "value"
    }

    def "metadata can be cleared"() {
        given:
            Serenity.currentSession.clearMetaData()
        when:
            def metadata = Serenity.currentSession.getMetaData()
        then:
            metadata.isEmpty()
    }

}
