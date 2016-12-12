package net.serenitybdd.core.model

import net.thucydides.core.model.LastElement
import spock.lang.Specification
import spock.lang.Unroll

class WhenFindingTheParentOfAStoryOrFeature extends Specification {

    @Unroll
    def "should find last element of path for a test case or a feature file"() {
        expect:
        LastElement.of(path) == expectedLastElement
        where:
        path                       | expectedLastElement
        "com.acme.widgets"         | "widgets"
        "widgets"                  | ""
        "com.acme.widgets.feature" | "acme"
        "com.acme.widgets.story"   | "acme"

    }
}
