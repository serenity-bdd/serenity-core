package net.thucydides.core.requirements

import net.thucydides.model.requirements.PathStartsWith
import spock.lang.Specification

class WhenAnalysingClassPaths extends Specification {

    def "should find the start of a package path"() {
        when:
            def pathStartsWith = new PathStartsWith(path)
        then:
            pathStartsWith.startsWith(startsWith) == result

        where:
        path            | startsWith        | result
        ["a","b","c"]   | ["a","b"]         | true
        ["a","b","c"]   | ["b","c"]         | false
        ["a","b","c"]   | ["a","b","c","d"] | false

    }

    def "should parse requirements paths correctly"() {
        expect:
            RequirementsPath.stripRootFromPath(root, storyPath) == expectedPath
        where:
        root       | storyPath          | expectedPath
        "a.b"      | ["a","b","c","d"]  | ["c","d"]
        ""         | ["a","b","c","d"]  | ["a","b","c","d"]
        "d.e.f"    | ["a","b","c","d"]  | ["a","b","c","d"]
    }

    def "should extract path elements"() {
        expect:
        RequirementsPath.fileSystemPathElements(storyPath) ==  expectedPath
        where:
        storyPath          | expectedPath
        "c/d"              | ["c","d"]
        "c/d/e"            | ["c","d","e"]
        "c\\d"             | ["c","d"]
        "c"                | ["c"]
        ""                 | []
    }


}
