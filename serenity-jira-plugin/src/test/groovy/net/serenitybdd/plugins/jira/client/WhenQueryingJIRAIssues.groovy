package net.serenitybdd.plugins.jira.client

import net.serenitybdd.plugins.jira.JiraConnectionSettings
import net.serenitybdd.plugins.jira.domain.IssueSummary
import spock.lang.Specification

class WhenQueryingJIRAIssues extends Specification {

    def globalJiraClient =  new JerseyJiraClient(
            JiraConnectionSettings.getJIRAWebserviceURL(),
            JiraConnectionSettings.getJIRAUserName(),
            JiraConnectionSettings.getJIRAUserApiToken(),
            "DEMO");

    def "should be able to read the status of an issue"() {
        given:
            def jiraClient = globalJiraClient
        when:
                Optional<IssueSummary> issue = jiraClient.findByKey("DEMO-33")
        then:
            issue.isPresent()
        and:
            System.out.println(issue.get().status)
            issue.get().status == "Open"
    }


    def "should be able to read the comments of an issue"() {
        given:
            def jiraClient = globalJiraClient
        when:
            Optional<IssueSummary> issue = jiraClient.findByKey("DEMO-33")
        then:
            issue.get().comments
        and:
            issue.get().comments[0].body == "{{Integration test comment}}"
    }

}
