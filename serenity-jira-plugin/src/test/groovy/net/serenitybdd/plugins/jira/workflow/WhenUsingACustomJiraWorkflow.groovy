package net.serenitybdd.plugins.jira.workflow

import net.thucydides.model.environment.MockEnvironmentVariables
import spock.lang.Shared
import spock.lang.Specification

import static net.thucydides.model.domain.TestResult.FAILURE
import static net.thucydides.model.domain.TestResult.SUCCESS

class WhenUsingACustomJiraWorkflow extends Specification {

    def workflow

    @Shared
    def environmentVariables = new MockEnvironmentVariables()

    def setupSpec() {
        environmentVariables.setProperty(ClasspathWorkflowLoader.WORKFLOW_CONFIGURATION_PROPERTY,'custom-workflow.groovy')
    }

    def setup() {
        workflow = new ClasspathWorkflowLoader("jira-workflow.groovy", environmentVariables).load()
    }

    def "should load a custom workflow defined in the serenity.jira.workflow system property"() {

        expect:
        def transitions = workflow.transitions.forTestResult(result).whenIssueIs(issueStatus)
        transitions == expectedTransitions

        where:
        issueStatus          | result  | expectedTransitions
        'Open'               | SUCCESS | ['Resolve Issue']
        'Pending Validation' | FAILURE | ['Reopen Issue']
    }

}
