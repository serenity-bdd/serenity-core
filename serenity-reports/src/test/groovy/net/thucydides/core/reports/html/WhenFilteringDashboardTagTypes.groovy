package net.thucydides.core.reports.html

import net.thucydides.model.domain.TestTag
import net.thucydides.model.environment.MockEnvironmentVariables
import net.thucydides.model.reports.html.TagFilter
import spock.lang.Specification

class WhenFilteringDashboardTagTypes extends Specification {

    def environmentVariables = new MockEnvironmentVariables()

    def "should display all tag types by default"() {
        given:
            def tagFilter = new TagFilter(environmentVariables)
        when:
            def filtered = tagFilter.filteredTagTypes(["a","b","c"])
        then:
            filtered == ["a", "b", "c"]
    }

    def "if an inclusive list is provide, only tags in this list should be returned"() {
        given:
            environmentVariables.setProperty("dashboard.tag.list","a,c")
            def tagFilter = new TagFilter(environmentVariables)

        when:
            def filtered = tagFilter.filteredTagTypes(["a","b","c","d"])
        then:
            filtered == ["a", "c"]
    }

    def "if an inclusive list is provide, tags should be case insensitive"() {
        given:
            environmentVariables.setProperty("dashboard.tag.list","A,c")
            def tagFilter = new TagFilter(environmentVariables)
        when:
            def filtered = tagFilter.filteredTagTypes(["a","b","C","d"])
        then:
            filtered == ["a", "C"]
    }


    def "if an exclusive list is provide, tags in this list should not be returned"() {
        given:
            environmentVariables.setProperty("dashboard.excluded.tag.list","c")
            def tagFilter = new TagFilter(environmentVariables)

        when:
            def filtered = tagFilter.filteredTagTypes(["a","b","c","d"])
        then:
            filtered == ["a", "b", "d"]
    }

    def "if an exclusive list is provide, tags in this list should not be returned regardless of case"() {
        given:
            environmentVariables.setProperty("dashboard.excluded.tag.list","C,d")
            def tagFilter = new TagFilter(environmentVariables)
        when:
            def filtered = tagFilter.filteredTagTypes(["a","b","c","D"])
        then:
            filtered == ["a", "b"]
    }

    def "should filter out tags with an unwanted type"() {
        given:
            def tagFilter = new TagFilter(environmentVariables)

            def tags = [TestTag.withName("Search/Display product details").andType("story"),
                        TestTag.withName("Display product details").andType("story"),
                        TestTag.withName("Search").andType("capability"),] as Set
        when:
            def filtered = tagFilter.removeTagsOfType(tags,"story")
        then:
            filtered == [TestTag.withName("Search").andType("capability")] as Set
    }


    def "should filter out tags with an unwanted title"() {
        given:
        def tagFilter = new TagFilter(environmentVariables)
        def tags = [TestTag.withName("web").andType("layer"),
                    TestTag.withName("foo").andType("story"),
                    TestTag.withName("bar/foo").andType("story")] as Set
        when:
        def filtered = tagFilter.removeTagsWithName(tags,"foo")
        then:
        filtered == [TestTag.withName("web").andType("layer")] as Set
    }

}
