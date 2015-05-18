package net.thucydides.core.tags

import net.thucydides.core.annotations.WithTag
import net.thucydides.core.annotations.WithTagValuesOf
import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification
import spock.lang.Unroll

class WhenCheckingTestTags extends Specification {

    class ClassWithNoTags {}

    @WithTag("color:red")
    class ClassWithTag {}

    @WithTagValuesOf(["color:red","flavor:strawberry"])
    class ClassWithTags {
        @WithTag("color:blue")
        def blue() {}

        @WithTagValuesOf(["color:orange","flavor:licorice"])
        def orange() {}
    }

    def environmentVariables = new MockEnvironmentVariables()

    @Unroll
    def "should find tags on a class"() {
        given:
            def scanner = new TagScanner(environmentVariables)
        when:
            environmentVariables.setProperty("tags",tagExpression)
        then:
            scanner.shouldRunClass(classUnderTest) == expectedMatch
        where:
            classUnderTest  | tagExpression       | expectedMatch
            ClassWithNoTags | ""                  | true
            ClassWithTag    | ""                  | true
            ClassWithTags   | ""                  | true
            ClassWithNoTags | "color:red"         | false
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
            def scanner = new TagScanner(environmentVariables)
        when:
            environmentVariables.setProperty("tags",tagExpression)
        then:
            scanner.shouldRunMethod(classUnderTest,methodUnderTest) == expectedMatch
        where:
            classUnderTest  | methodUnderTest   | tagExpression       | expectedMatch
            ClassWithTags   | "blue"            | "color:blue"        | true
            ClassWithTags   | "blue"            | "flavor:strawberry" | true
            ClassWithTags   | "blue"            | "color:yellow"      | false
            ClassWithTags   | "doesnotexist"    | "color:yellow"      | false
            ClassWithTags   | "orange"          | "flavor:licorice"   | true
    }


}
