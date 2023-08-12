package net.thucydides.core.tags

import junittestcases.samples.mock.SerenityRunner
import net.serenitybdd.annotations.WithTag
import net.serenitybdd.annotations.WithTagValuesOf
import net.thucydides.model.environment.MockEnvironmentVariables
import net.thucydides.model.tags.TagScanner
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
