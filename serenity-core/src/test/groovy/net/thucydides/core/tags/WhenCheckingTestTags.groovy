package net.thucydides.core.tags

import junittestcases.samples.mock.SerenityRunner
import net.thucydides.core.annotations.WithTag
import net.thucydides.core.annotations.WithTagValuesOf
import net.thucydides.core.util.MockEnvironmentVariables
import org.junit.runner.RunWith
import spock.lang.Specification
import spock.lang.Unroll

class WhenCheckingTestTags extends Specification {

    class ClassWithNoTags {}

    @WithTag("color:red")
    class ClassWithTag {}

    @RunWith(SerenityRunner)
    @WithTagValuesOf(["color:red","flavor:strawberry"])
    class ClassWithTags {
        @WithTag("color:blue")
        def blue() {}

        @WithTagValuesOf(["color:orange","flavor:licorice"])
        def orange() {}

        @WithTag("red")
        def red() {}

    }

    def environmentVariables = new MockEnvironmentVariables()

    @Unroll
    def "should find tags on a class"() {
        given:
            environmentVariables.setProperty("tags",tagExpression)
        when:
            def scanner = new TagScanner(environmentVariables)
        then:
            scanner.shouldRunClass(classUnderTest) == expectedMatch
        where:
            classUnderTest  | tagExpression       | expectedMatch
            ClassWithNoTags | "color:red"         | false
            ClassWithNoTags | ""                  | true
            ClassWithTag    | ""                  | true
            ClassWithTags   | ""                  | true
            ClassWithNoTags | "color:blue"        | false
            ClassWithTag    | "flavor:strawberry" | false
            ClassWithTag    | "color:red"         | true
            ClassWithTag    | "color:blue"        | false
            ClassWithTag    | "flavor:strawberry" | false
            ClassWithTags   | "color:red"         | true
            ClassWithTags   | "color:blue"        | false
            ClassWithTags   | "flavor:strawberry" | true
    }

    @Unroll
    def "should find tags on a method"() {
        given:
            environmentVariables.setProperty("tags",tagExpression)
        when:
            def scanner = new TagScanner(environmentVariables)
        then:
            scanner.shouldRunMethod(classUnderTest,methodUnderTest) == expectedMatch
        where:
            classUnderTest  | methodUnderTest   | tagExpression       | expectedMatch
//            ClassWithTags   | "blue"            | "color:blue"        | true
//            ClassWithTags   | "blue"            | "flavor:strawberry" | true
            ClassWithTags   | "blue"            | "color:yellow"      | false
            ClassWithTags   | "blue"            | "not color:yellow"     | true
            ClassWithTags   | "blue"            | "not color:blue"       | false
            ClassWithTags   | "doesnotexist"    | "color:yellow"      | false
            ClassWithTags   | "orange"          | "flavor:licorice"   | true
            ClassWithTags   | "red"             | "tag:red"               | true
    }

    @Unroll
    def "should find tags on a list of tags"() {
        given:
            environmentVariables.setProperty("tags",tagExpression)
        when:
            def scanner = new TagScanner(environmentVariables)
        then:
            scanner.shouldRunForTags(["color:red","flavor:strawberry"]) == expectedMatch
        where:
           tagExpression        | expectedMatch
            "color:red"         | true
            "color=red"         | true
            "@color:red"        | true
            "@color=red"        | true
            ""                  | true
            "color:blue"        | false
            "flavor:strawberry" | true
            "not flavor:strawberry"| false
            "not color:red"        | false
            "not color:red"        | false
            "not color:blue"       | true
            "not @color:red"       | false
            "not @color:blue"      | true
    }


}
