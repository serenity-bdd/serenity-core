package net.serenitybdd.plugins.jira

import net.thucydides.model.domain.TestOutcome
import net.thucydides.model.domain.TestTag
import net.thucydides.model.environment.MockEnvironmentVariables
import net.serenitybdd.plugins.jirarequirements.JIRARequirementsProvider
import net.serenitybdd.plugins.jira.service.JIRAConfiguration
import net.serenitybdd.plugins.jira.service.SystemPropertiesJIRAConfiguration
import spock.lang.Specification

class WhenReadingVersionsFromJira extends Specification {


    JIRAConfiguration configuration
    def environmentVariables = new MockEnvironmentVariables()
    def requirementsProvider

    def setup() {
        environmentVariables.setProperty('jira.url',JiraConnectionSettings.getJIRAWebserviceURL())
        environmentVariables.setProperty('jira.username',JiraConnectionSettings.getJIRAUserName())
        environmentVariables.setProperty('jira.password',JiraConnectionSettings.getJIRAUserApiToken())
        environmentVariables.setProperty('jira.project','DEMO')

        configuration = new SystemPropertiesJIRAConfiguration(environmentVariables)
        requirementsProvider = new JIRARequirementsProvider(configuration)
    }

    def "should find version details for a given issue"() {
        given:
            def requirementsProvider = new JIRARequirementsProvider(configuration)
            def testOutcome = Mock(TestOutcome)
            testOutcome.getIssueKeys() >> ["DEMO-2"]
        when:
            def tags = requirementsProvider.getTagsFor(testOutcome)
        then:
            tags.size() == 3
        and:
            tags.contains(TestTag.withName("Iteration 1.1").andType("Version"))
        and:
            tags.contains(TestTag.withName("Version 1.0").andType("Version"))
    }

}
