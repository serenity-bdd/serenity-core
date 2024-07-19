package net.thucydides.model.domain

import net.thucydides.model.digest.Digest
import net.thucydides.model.reflection.samples.SomeOtherClass
import spock.lang.Shared
import spock.lang.Specification

class WhenDescribingUserStories extends Specification {

    class SomeTestCaseSample {}

    def "should be able obtain a story from a test class"() {
        when:
            def story = Stories.findStoryFrom(SomeTestCaseSample.class)
        then:
            story.name == "SomeTestCaseSample"
    }

    def "should be able obtain a story report from a story"() {
        when:
            def story = Stories.findStoryFrom(SomeTestCaseSample.class)
            def htmlReportName = Stories.reportFor(story, ReportType.HTML)
        then:
            htmlReportName == Digest.ofTextValue("sometestcasesample") + ".html"
    }

    @Shared def story = Stories.findStoryFrom(SomeTestCaseSample.class)
    @Shared def storyForSameClass = Stories.findStoryFrom(SomeTestCaseSample.class)
    @Shared def storyForDifferentClass = Stories.findStoryFrom(SomeOtherClass.class)

    def "stories can be compared with each other"() {
        expect:
            (story1.equals(story2)) == expectedResult
        where:
        story1      | story2                    | expectedResult
        story       | story                     | true
        story       | storyForSameClass         | true
        story       | storyForDifferentClass    | false
        story       | null                      | false
        story       | new SomeOtherClass() | false

    }

    def "story should have a hash code"() {
        expect:
            story.hashCode() == storyForSameClass.hashCode()
        and:
            story.hashCode() != storyForDifferentClass.hashCode()
    }
}
