package net.thucydides.core.reports.html

import net.thucydides.core.util.MockEnvironmentVariables
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
}
