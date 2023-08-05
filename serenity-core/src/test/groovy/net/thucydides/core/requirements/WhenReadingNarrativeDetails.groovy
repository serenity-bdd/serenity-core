package net.thucydides.core.requirements

import net.thucydides.model.requirements.classpath.NarrativeTitle
import spock.lang.Specification

class WhenReadingNarrativeDetails extends Specification {

    def "should read narrative details from a narrative.txt file"() {
        when:
            def narrativeText = NarrativeTitle.definedIn("feature_narratives/text", "feature")
        then:
            narrativeText.startsWith("Some feature") && narrativeText.contains("Some description")
    }

    def "should read narrative details from a narrative.mk file"() {
        when:
            def narrativeText = NarrativeTitle.definedIn("feature_narratives/markdown", "feature")
        then:
            narrativeText.startsWith("Some feature") && narrativeText.contains("Some description")
    }

    def "should not read narratives with unrecognised extensions"() {
        when:
        def narrativeText = NarrativeTitle.definedIn("feature_narratives/unknown", "feature")
        then:
        narrativeText == ""
    }

}
