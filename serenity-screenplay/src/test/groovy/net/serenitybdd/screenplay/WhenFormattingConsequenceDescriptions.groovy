package net.serenitybdd.screenplay

import net.serenitybdd.screenplay.formatting.FormattedTitle
import spock.lang.Specification

class WhenFormattingConsequenceDescriptions extends Specification {

    def consequence = Mock(Consequence)

    def "should not modify a simple text"() {
        when:
            consequence.toString() >> "Foo Bar"
        then:
            FormattedTitle.ofConsequence(consequence) == "Foo Bar"
    }

    def "should make hamcrest expressions HTML-friendly"() {
        when:
        consequence.toString() >> "Then the 'Clear Completed' option should be <Available>"
        then:
        FormattedTitle.ofConsequence(consequence) == "Then the 'Clear Completed' option should be (Available)"
    }

}
