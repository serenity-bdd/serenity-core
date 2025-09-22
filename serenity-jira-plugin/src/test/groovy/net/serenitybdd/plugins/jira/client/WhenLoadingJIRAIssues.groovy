package net.serenitybdd.plugins.jira.client

import net.serenitybdd.plugins.jira.JiraConnectionSettings
import net.serenitybdd.plugins.jira.domain.IssueSummary
import spock.lang.Ignore
import spock.lang.Specification

@Ignore
class WhenLoadingJIRAIssues extends Specification {


    def globalJiraClient =  new JerseyJiraClient(JiraConnectionSettings.getJIRAWebserviceURL(),JiraConnectionSettings.getJIRAUserName(),
                                                    JiraConnectionSettings.getJIRAUserApiToken(),"DEMO");

    def "should load issues with JQL filters"() {
        given:
            def jiraClient = globalJiraClient
        when:
            List<IssueSummary> issues = jiraClient.findByJQL("project='DEMO'")
        then:
            issues.size() > 10
    }


    def "should load issues complete with custom fields using a JQL filter"() {
        given:
            def jiraClient = globalJiraClient
        when:
            List<IssueSummary> issues = jiraClient.findByJQL("project='DEMO' and type='Epic'")
        then:
            issues.each {
                assert it.customField("RequirementsList") != null
            }
    }

    def "should load issue summary by key"() {
        given:
            def jiraClient = globalJiraClient
        when:
            Optional<IssueSummary> issue = jiraClient.findByKey("DEMO-33")
        then:
            issue.isPresent()
        and:
            issue.get().key == "DEMO-33"
    }

    def "should not load issue by key if the issue is not available"() {
        given:
            def jiraClient = globalJiraClient
        when:
            Optional<IssueSummary> issue = jiraClient.findByKey("DEMO-DOES-NOT-EXIST")
        then:
            !issue.isPresent()
    }

    def "should load the fix versions for an issue "() {
        given:
            def jiraClient = globalJiraClient
        when:
            Optional<IssueSummary> issue = jiraClient.findByKey("DEMO-2")
        then:
            issue.get().fixVersions.size() == 2
        and:
            issue.get().fixVersions.contains("Version 1.0")
        and:
            issue.get().fixVersions.contains("Iteration 1.1")
    }

    def "should not freak out if a JQL query doesn't return any issues"() {
        given:
            def jiraClient = globalJiraClient
        when:
            List<IssueSummary> issue = jiraClient.findByJQL("project='DOES-NOT-EXIST'", LoadingStrategy.LOAD_IN_SINGLE_QUERY)
        then:
            issue.isEmpty()
    }

    def "should not freak out if a JQL count doesn't return any issues"() {
        given:
            def jiraClient = globalJiraClient
        when:
            def total = jiraClient.countByJQL("key=DEMO-DOES-NOT-EXIST")
        then:
            total == 0
    }

    InputStream streamed(String source) { new ByteArrayInputStream(source.bytes) }
}
