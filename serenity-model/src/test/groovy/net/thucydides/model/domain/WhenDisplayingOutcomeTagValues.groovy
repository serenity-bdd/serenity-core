package net.thucydides.model.domain

import com.google.common.collect.ImmutableList
import spock.lang.Specification

class WhenDisplayingOutcomeTagValues extends Specification {

    def "should display the value of a tag"() {
        given:
            def outcome = TestOutcome.forTestInStory("someTest", Story.withId("1","story"));
            outcome.addTags(ImmutableList.of(TestTag.withValue("priority:high")))
        when:
            def tagValue = outcome.getTagValue("priority")
        then:
            tagValue.isPresent() && tagValue.get() == "high"
    }

    def "should indicate if a tag is not present"() {
        given:
            def outcome = TestOutcome.forTestInStory("someTest", Story.withId("1","story"));
            outcome.addTags(ImmutableList.of(TestTag.withValue("priority:high")))
        when:
            def tagValue = outcome.getTagValue("severity")
        then:
            !tagValue.isPresent()
    }

    def "should display the value of an issue tag"() {
        given:
            def outcome = TestOutcome.forTestInStory("someTest", Story.withId("1","story"));
            outcome.addIssues(["ISSUE-1"])
        when:
            def tagValue = outcome.getTagValue("issues")
        then:
            tagValue.isPresent() && tagValue.get() == "ISSUE-1"
    }

    def "should display the value of several issue tags"() {
        given:
            def outcome = TestOutcome.forTestInStory("someTest", Story.withId("1","story"));
            outcome.addIssues(["ISSUE-1", "ISSUE-2"])
        when:
            def tagValue = outcome.getTagValue("issues")
        then:
            tagValue.isPresent() && tagValue.get() == "ISSUE-1,ISSUE-2"
    }

    // TODO: Think about how to handle flag-style tags more elegantly
}
