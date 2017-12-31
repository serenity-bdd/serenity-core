package net.thucydides.core.requirements.reports

import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification

class WhenIncludingARequirementsOverviewSection extends Specification {

    def "should use the overview.md file for the requirements overview text"() {
        given:
            def environmentVariables = new MockEnvironmentVariables();
        when:
            def requirementsOverviewText = RequirementsOverview.withEnvironmentVariables(environmentVariables).asText()
        then:
            requirementsOverviewText == "## My overview"
    }

    def "should render the overview text as markdown"() {
        given:
        def environmentVariables = new MockEnvironmentVariables();
        when:
        def requirementsOverviewText = RequirementsOverview.withEnvironmentVariables(environmentVariables).asRenderedHtml()
        then:
        requirementsOverviewText == "<h2>My overview</h2>" + System.lineSeparator()
    }

}