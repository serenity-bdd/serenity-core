package net.serenitybdd.plugins.jira

import net.thucydides.model.domain.TestOutcome
import net.thucydides.model.domain.TestTag
import net.thucydides.model.requirements.model.Requirement
import net.thucydides.model.environment.MockEnvironmentVariables
import net.serenitybdd.plugins.jirarequirements.JIRARequirementsProvider
import net.serenitybdd.plugins.jira.service.JIRAConfiguration
import net.serenitybdd.plugins.jira.service.SystemPropertiesJIRAConfiguration
import spock.lang.Specification

class WhenReadingStoriesFromJira extends Specification {


    JIRAConfiguration configuration
    def environmentVariables = new MockEnvironmentVariables()
    def requirementsProvider

    def setup() {
        environmentVariables.setProperty('jira.url',JiraConnectionSettings.getJIRAWebserviceURL())
        environmentVariables.setProperty('jira.username',JiraConnectionSettings.getJIRAUserName())
        environmentVariables.setProperty('jira.password',JiraConnectionSettings.getJIRAUserApiToken())
        environmentVariables.setProperty('jira.project','DEMO')

        configuration = new SystemPropertiesJIRAConfiguration(environmentVariables)
        requirementsProvider = new JIRARequirementsProvider(configuration,environmentVariables)
    }

    def "should read epics as the top level requirements"() {
        when:
            List<Requirement> requirements = requirementsProvider.getRequirements()
        then:
            !requirements.isEmpty()
        and:
            requirements.each { requirement -> requirement.type == 'Epic' }
    }

    def "should read stories beneath the epics"() {
        given:
            def requirementsProvider = new JIRARequirementsProvider(configuration)
        when:
            List<Requirement> requirements = requirementsProvider.getRequirements()
        then:
            requirements.find {epic -> !epic.children.isEmpty() }
    }

    def "should find requirements for an issue name"() {
        given:
            def requirementsProvider = new JIRARequirementsProvider(configuration)
        when:
            def requirement = requirementsProvider.getRequirementFor(TestTag.withName("Epic 1 Test Serenity JIRA Plugin").andType("Epic"))
        then:
            requirement.isPresent() && requirement.get().getCardNumber() == "DEMO-1"
    }

    def "should get the story description from the description field by default"() {
        given:
            def requirementsProvider = new JIRARequirementsProvider(configuration)
            def testOutcome = Mock(TestOutcome)
            testOutcome.getIssueKeys() >> ["DEMO-1"]
        when:
            environmentVariables.setProperty("jira.narrative.field","User Story")
        and:
            def requirement = requirementsProvider.getParentRequirementOf(testOutcome)
        then:
            requirement.isPresent() && requirement.get().narrative.text.contains("The Issue is used for integration tests.")
    }

    /* TODO
       def "should get the story description from a custom field if required"() {
        given:
            def requirementsProvider = new JIRARequirementsProvider(configuration,environmentVariables)
            def testOutcome = Mock(TestOutcome)
            testOutcome.getIssueKeys() >> ["TRAD-5"]
        when:
            environmentVariables.setProperty("jira.narrative.field","User Story")
        and:
            def requirement = requirementsProvider.getParentRequirementOf(testOutcome);
        then:
            requirement.isPresent() && requirement.get().narrative.text.contains("As a seller")
    }*/


    /* TODO
     def "should store custom custom field values in requirements"() {
        given:
            def testOutcome = Mock(TestOutcome)
            testOutcome.getIssueKeys() >> ["TRAD-5"]
        when:
            environmentVariables.setProperty('jira.custom.field.1',"User Story")
            environmentVariables.setProperty('jira.custom.field.2',"Acceptance Criteria")

            configuration = new SystemPropertiesJIRAConfiguration(environmentVariables)
            def requirementsProvider = new JIRARequirementsProvider(configuration, environmentVariables)

        and:
            def requirement = requirementsProvider.getParentRequirementOf(testOutcome);
        then:
            requirement.get().customFields == ["Acceptance Criteria"]
        and:
            def acText = requirement.get().getCustomField("Acceptance Criteria").get().text
            acText.contains("- buyers should be able to see my stuff online") &&
            acText.contains("- buyers should be able to buy my stuff") &&
            acText.contains("- I should be able to get paid")

    }*/


    def "should find the parent requirement from a given issue"() {
        given:
            def requirementsProvider = new JIRARequirementsProvider(configuration,environmentVariables)
            def testOutcome = Mock(TestOutcome)
            testOutcome.getIssueKeys() >> ["DEMO-1"]
        when:
            def parentRequirement = requirementsProvider.getParentRequirementOf(testOutcome)
        then:
            parentRequirement.isPresent() && parentRequirement.get().cardNumber == "DEMO-1"
    }

    def "should return Optional.absent() when no issues are specified"() {
        given:
            def requirementsProvider = new JIRARequirementsProvider(configuration,environmentVariables)
            def testOutcome = Mock(TestOutcome)
            testOutcome.getIssueKeys() >> []
        when:
            def parentRequirement = requirementsProvider.getParentRequirementOf(testOutcome)
        then:
            !parentRequirement.isPresent()
    }

    def "should return Optional.absent() for a non-existant issue"() {
        given:
            def requirementsProvider = new JIRARequirementsProvider(configuration,environmentVariables)
            def testOutcome = Mock(TestOutcome)
            testOutcome.getIssueKeys() >> ["UNKNOWN"]
        when:
            def parentRequirement = requirementsProvider.getParentRequirementOf(testOutcome)
        then:
            !parentRequirement.isPresent()
    }

    def "should find tags for a given issue"() {
        given:
            def requirementsProvider = new JIRARequirementsProvider(configuration, environmentVariables)
            def testOutcome = Mock(TestOutcome)
            testOutcome.getIssueKeys() >> ["DEMO-31"]
        when:
            def tags = requirementsProvider.getTagsFor(testOutcome)
        then:
            tags.contains(TestTag.withName("Epic 2 Story 1").andType("Story")) &&
            tags.contains(TestTag.withName("Epic 2 integration tests").andType("Epic"))
    }

    def "should find tags for a story card"() {
        given:
        def requirementsProvider = new JIRARequirementsProvider(configuration, environmentVariables)
        def testOutcome = Mock(TestOutcome)
        testOutcome.getIssueKeys() >> ["DEMO-31"]
        when:
        def tags = requirementsProvider.getTagsFor(testOutcome)
        then:
        tags.contains(TestTag.withName("Epic 2 Story 1").andType("Story"))
    }

}
