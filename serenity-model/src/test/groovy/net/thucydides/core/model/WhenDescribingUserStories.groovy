package net.thucydides.core.model

import net.thucydides.core.digest.Digest
import net.thucydides.core.reflection.samples.SomeOtherClass
import spock.lang.Shared
import spock.lang.Specification

class WhenDescribingUserStories extends Specification {

    class SomeTestCaseSample {}

    def "should be able obtain a story from a test class"() {
        when:
            def story = Stories.findStoryFrom(SomeTestCaseSample.class)
        then:
            story.name == "Some test case sample"
    }

    def "should be able obtain a story report from a story"() {
        when:
            def story = Stories.findStoryFrom(SomeTestCaseSample.class)
            def htmlReportName = Stories.reportFor(story, ReportType.HTML)
        then:
            htmlReportName == Digest.ofTextValue("some_test_case_sample") + ".html"
    }

    def "should be able obtain an XML story report name from a test class"() {
        when:
        def story = Stories.findStoryFrom(SomeTestCaseSample.class)
        def htmlReportName = story.getReportName(ReportType.XML)
        then:
        htmlReportName == Digest.ofTextValue("some_test_case_sample") + ".xml"
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
        story       | new SomeOtherClass()      | false

    }

    def "story should have a hash code"() {
        expect:
            story.hashCode() == storyForSameClass.hashCode()
        and:
            story.hashCode() != storyForDifferentClass.hashCode()
    }
}
