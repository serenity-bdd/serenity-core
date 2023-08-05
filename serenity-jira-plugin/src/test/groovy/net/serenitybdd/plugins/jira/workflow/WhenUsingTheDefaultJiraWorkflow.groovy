package net.serenitybdd.plugins.jira.workflow

import net.serenitybdd.model.di.ModelInfrastructure
import spock.lang.Specification

import static net.thucydides.model.domain.TestResult.*

class WhenUsingTheDefaultJiraWorkflow extends Specification {

    def workflow

    def setupSpec() {
        System.properties[ClasspathWorkflowLoader.ACTIVATE_WORKFLOW_PROPERTY] = 'true'
    }

    def cleanupSpec() {
        System.properties.remove(ClasspathWorkflowLoader.ACTIVATE_WORKFLOW_PROPERTY)
    }

    def setup() {
        def environmentVariables = ModelInfrastructure.environmentVariables
        workflow = new ClasspathWorkflowLoader("jira-workflow.groovy", environmentVariables).load()

    }


    def "should load the default workflow if none is specified"() {
        expect:
        def transitions = workflow.getTransitions().forTestResult(result).whenIssueIs(issueStatus)
        transitions == expectedTransitions

        where:
        issueStatus   | result  | expectedTransitions
        'Open'        | SUCCESS | ['Resolve Issue']
        'Open'        | FAILURE | []
        'Open'        | IGNORED | []
        'Open'        | PENDING | []
        'Open'        | SKIPPED | []

        'Resolved'    | SUCCESS | []
        'Resolved'    | FAILURE | ['Reopen Issue']
        'Resolved'    | IGNORED | []
        'Resolved'    | PENDING | []
        'Resolved'    | SKIPPED | []

        'Closed'      | SUCCESS | []
        'Closed'      | FAILURE | ['Reopen Issue']
        'Closed'      | IGNORED | []
        'Closed'      | PENDING | []
        'Closed'      | SKIPPED | []

        'Reopened'    | SUCCESS | ['Resolve Issue']
        'Reopened'    | FAILURE | []
        'Reopened'    | IGNORED | []
        'Reopened'    | PENDING | []
        'Reopened'    | SKIPPED | []

        'In Progress' | SUCCESS | ['Stop Progress', 'Resolve Issue']
        'In Progress' | FAILURE | []
        'In Progress' | IGNORED | []
        'In Progress' | PENDING | []
        'In Progress' | SKIPPED | []
    }

}
