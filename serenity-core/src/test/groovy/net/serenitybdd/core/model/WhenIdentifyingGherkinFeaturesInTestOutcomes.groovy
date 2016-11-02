package net.serenitybdd.core.model

import net.thucydides.core.model.Story
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.model.TestTag
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by john on 1/06/2015.
 */
class WhenIdentifyingGherkinFeaturesInTestOutcomes extends Specification {

    @Unroll
    def "should return a feature tag for a test outcome generated from a feature or story file"() {
        given:
        def testOutcome = TestOutcome.forTestInStory("some test", Story.called("my story").withPath(path))
        when:
        def featureTag = testOutcome.getFeatureTag()
        then:
        featureTag.isPresent() == featureIsPresent && featureTag.get() == TestTag.withName(featureName).andType(featureType)

        where:
        path                    | featureIsPresent | featureName | featureType
        "a_feature.feature"     | true             | "A feature/my story" | "feature"
        "a/b/a_feature.feature" | true             | "A feature/my story" | "feature"
        "a_story.story"         | true             | "A story/my story"   | "story"
        "a/b/a_story.story"     | true             | "A story/my story"   | "story"
    }

}
