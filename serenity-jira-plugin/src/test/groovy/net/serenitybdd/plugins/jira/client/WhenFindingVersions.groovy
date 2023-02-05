package net.serenitybdd.plugins.jira.client

import net.serenitybdd.plugins.jira.JiraConnectionSettings
import net.serenitybdd.plugins.jira.domain.Version
import spock.lang.Specification

class WhenFindingVersions extends Specification {


    def jiraClient = new JerseyJiraClient(JiraConnectionSettings.getJIRAWebserviceURL(),JiraConnectionSettings.getJIRAUserName(),JiraConnectionSettings.getJIRAUserApiToken(),"DEMO")

    def "should load all known versions for a given project"() {
        when:
            List<Version> versions = jiraClient.findVersionsForProject('DEMO')
        then:
            versions.size() == 2
        and:
            versions.collect {it.name} == ['Iteration 1.1', 'Version 1.0']
    }


}
