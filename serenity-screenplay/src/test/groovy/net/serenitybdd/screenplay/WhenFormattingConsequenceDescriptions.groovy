package net.serenitybdd.screenplay

import net.serenitybdd.screenplay.formatting.FormattedTitle
import net.thucydides.model.environment.MockEnvironmentVariables
import spock.lang.Specification

class WhenFormattingConsequenceDescriptions extends Specification {

    def consequence = Mock(Consequence)
    def actor = Actor.named("Archie")
    def environmentVariables = new MockEnvironmentVariables()

    def "should not modify a simple text"() {
        when:
            consequence.toString() >> "Foo Bar"
        then:
            FormattedTitle.ofConsequence(consequence, actor) == "Foo Bar"
    }

    def "should make hamcrest expressions HTML-friendly"() {
        when:
        consequence.toString() >> "Then the 'Clear Completed' option should be <Available>"
        then:
        FormattedTitle.ofConsequence(consequence, actor) == "Then the 'Clear Completed' option should be (Available)"
    }

    def "should add actor name if specified"() {
        given:
        environmentVariables.setProperty("serenity.include.actor.name.in.consequences","true")
        when:
        consequence.toString() >> "Then the 'Clear Completed' option should be <Available>"
        def formattedTitle = new FormattedTitle(environmentVariables, actor)
        then:
        formattedTitle.getFormattedTitleFor(consequence) == "For Archie: Then the 'Clear Completed' option should be (Available)"
    }
}
