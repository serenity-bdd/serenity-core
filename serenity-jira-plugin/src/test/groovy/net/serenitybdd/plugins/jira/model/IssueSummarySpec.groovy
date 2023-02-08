package net.serenitybdd.plugins.jira.model

import net.serenitybdd.plugins.jira.domain.IssueSummary
import spock.lang.Specification


class IssueSummarySpec extends Specification{

    def "should store rendered field descriptions"() {
        given:
            def issueSummary = new IssueSummary(new URI("self"),1L,"ISSUE-1","summary","description",
                                                ["description":"<p>description</p>"],"Story","Open")
        when:
            def renderedDescription = issueSummary.rendered.description
        then:
            renderedDescription == "<p>description</p>"
    }

    def "should know when rendered fields are unavailable"() {
        given:
            def issueSummary = new IssueSummary(new URI("self"),1L,"ISSUE-1","summary","description",
                    ["description":"<p>description</p>"],"Story","Open")

        when:
            def fieldPresent = issueSummary.rendered.hasField("not-present")
        then:
            !fieldPresent
    }
}
