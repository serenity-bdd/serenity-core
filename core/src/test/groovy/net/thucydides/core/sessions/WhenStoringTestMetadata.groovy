package net.thucydides.core.sessions

import net.thucydides.core.Thucydides
import spock.lang.Specification

class WhenStoringTestMetadata extends Specification {


    def "there should be no metadata stored in the session by default"() {
        when:
            def metadata = Thucydides.currentSession.getMetaData()
        then:
            metadata.isEmpty()
    }

    def "you can add metadata to the current session"() {
        given:
            Thucydides.currentSession.addMetaData("some","value")
        when:
            def metadata = Thucydides.currentSession.getMetaData()
        then:
            metadata.get("some") == "value"
    }

    def "metadata can be cleared"() {
        given:
            Thucydides.currentSession.clearMetaData()
        when:
            def metadata = Thucydides.currentSession.getMetaData()
        then:
            metadata.isEmpty()
    }

}
