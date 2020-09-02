package net.thucydides.core.model


import spock.lang.Specification

class WhenDisplayingUnqualifiedTestTitles extends Specification {

    def "should display the test title without the qualifier"() {
        given:
            def outcome = TestOutcome.forTestInStory("Should be able to do some thing", Story.withId("1","story"))
            outcome = outcome.withQualifier("QUALIFIER")
        when:
            def title = outcome.unqualified.title
        then:
            title == "Should be able to do some thing"
    }

    def "should display the test title with links without the qualifier"() {
        given:
        def outcome = TestOutcome.forTestInStory("Should be able to do some thing", Story.withId("1","story"))
        outcome = outcome.withQualifier("QUALIFIER")
        when:
        def title = outcome.unqualified.titleWithLinks
        then:
        title == "Should be able to do some thing"
    }

}