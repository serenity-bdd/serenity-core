package net.serenitybdd.plugins.jira

import net.thucydides.model.requirements.model.Requirement
import net.thucydides.model.environment.MockEnvironmentVariables
import net.serenitybdd.plugins.jirarequirements.JIRARequirementsProvider
import net.serenitybdd.plugins.jira.service.JIRAConfiguration
import net.serenitybdd.plugins.jira.service.SystemPropertiesJIRAConfiguration
import spock.lang.Specification

class WhenReadingRequirementsFromJira extends Specification {


    JIRAConfiguration configuration
    def environmentVariables = new MockEnvironmentVariables()
    def requirementsProvider

    def setup() {
        environmentVariables.setProperty('jira.url',JiraConnectionSettings.getJIRAWebserviceURL())
        environmentVariables.setProperty('jira.username',JiraConnectionSettings.getJIRAUserName())
        environmentVariables.setProperty('jira.password',JiraConnectionSettings.getJIRAUserApiToken())
        environmentVariables.setProperty('jira.project',"DEMO")

        configuration = new SystemPropertiesJIRAConfiguration(environmentVariables)
        requirementsProvider = new JIRARequirementsProvider(configuration)
    }

    def "Requirements can be loaded from the Epic/Story JIRA card structure"() {
        given:
            def requirementsProvider = new JIRARequirementsProvider(configuration)
        when:
            def requirements = requirementsProvider.getRequirements()
        then:
            requirements.size() == 2
        and:
            totalNumberOf(requirements) == 8
    }

    def "Child requirements should have parents"() {
        given:
        def requirementsProvider = new JIRARequirementsProvider(configuration)
        when:
        def requirements = requirementsProvider.getRequirements()
        then:
        def parent = requirements.get(0)
        def firstChild = parent.getChildren().get(0)
        firstChild.parent == parent.name
    }

    def "Requirements can be loaded from a custom JIRA card structure"() {
        given:
        environmentVariables.setProperty("jira.root.issue.type","epic")
        environmentVariables.setProperty("jira.requirement.links","Epic Link, relates to")
        and:
        def requirementsProvider = new JIRARequirementsProvider(configuration, environmentVariables)
        when:
        def requirements = requirementsProvider.getRequirements()
        then:
        totalNumberOf(requirements) == 8
    }

    def "when setting story root issue type"() {
        given:
        environmentVariables.setProperty("jira.root.issue.type","story")
        and:
        def requirementsProvider = new JIRARequirementsProvider(configuration, environmentVariables)
        when:
        def requirements = requirementsProvider.getRequirements()
        then:
        requirementsProvider.rootIssueType.is("story")
        totalNumberOf(requirements) == 4
    }

    def totalNumberOf(List<Requirement> requirements) {
        int total = 0
        requirements.each {
            total++
            if (it.children) {
                total = total + totalNumberOf(it.children)
            }
        }
        return total
    }


}
