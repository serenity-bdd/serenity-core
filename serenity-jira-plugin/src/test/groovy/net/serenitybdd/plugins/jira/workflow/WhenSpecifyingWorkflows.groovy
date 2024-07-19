package net.serenitybdd.plugins.jira.workflow

import net.thucydides.model.domain.TestResult
import spock.lang.Specification

class WhenSpecifyingWorkflows extends Specification {
    def "creating an empty builder"() {
        when:
        def builder = new TransitionBuilder()

        then:
        builder != null
    }


    def "reading a non-specified transition"() {
        when:
        def when = new TransitionBuilder()

        when 'Open', {
            'success' should: 'Resolve issue'
        }

        then:
        def transitions = when.transitionSetMap
        transitions.forTestResult(TestResult.FAILURE).whenIssueIs('Open') == []
    }

    def "reading a non-specified outcome result"() {
        when:
        def when = new TransitionBuilder()

        when 'Open', {
            'success' should: 'Resolve issue'
        }

        then:
        def transitions = when.transitionSetMap
        transitions.forTestResult(TestResult.SUCCESS).whenIssueIs('Closed') == []
    }

    def "creating rules with multiple transitions"() {
        when:
        def when = new TransitionBuilder()

        when 'In Progress', {
            'success' should: ['Stop Progress', 'Resolve issue']
        }

        then:
        def transitions = when.transitionSetMap
        transitions.forTestResult(TestResult.SUCCESS).whenIssueIs('In Progress') == ['Stop Progress', 'Resolve issue']
    }

    def "creating rules with multiple transitions using verbose syntax"() {
        when:
        def when = new TransitionBuilder()

        when('In Progress', {
            'success' should: ['Stop Progress', 'Resolve issue']
        })

        then:
        def transitions = when.transitionSetMap
        transitions.forTestResult(TestResult.SUCCESS).whenIssueIs('In Progress') == ['Stop Progress', 'Resolve issue']
    }

    def "creating rules with multiple scenarios"() {
        when:
        def when = new TransitionBuilder()

        when 'Open', {
            'success' should: 'Resolve issue'
        }

        when 'Resolved', {
            'failure' should: 'Reopen issue'
        }

        when 'In Progress', {
            'success' should: ['Stop Progress', 'Resolve issue']
        }

        then:
        def transitions = when.transitionSetMap
        transitions.forTestResult(TestResult.SUCCESS).whenIssueIs('Open') == ['Resolve issue']
    }

}
