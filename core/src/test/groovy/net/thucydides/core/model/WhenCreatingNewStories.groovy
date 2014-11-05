package net.thucydides.core.model

import spock.lang.Specification


/**
 * Created by john on 4/08/2014.
 */
class WhenCreatingNewStories extends Specification {

    def "a story object normally represents a story"() {
        when:
        def story = Story.called("some story")
        then:
        story.type == Story.RequirementType.story
    }

    def "a story object can also represent a feature"() {
        when:
            def feature = Story.called("some feature").asFeature()
        then:
            feature.type == Story.RequirementType.feature
    }

    def "a story object can also represent a feature with a narrative"() {
        when:
        def feature = Story.called("some feature").asFeature().withNarrative("a narrative")
        then:
        feature.type == Story.RequirementType.feature
        feature.narrative == "a narrative"
    }
}