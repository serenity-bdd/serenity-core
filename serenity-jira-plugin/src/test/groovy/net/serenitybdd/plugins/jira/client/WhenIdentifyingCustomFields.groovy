package net.serenitybdd.plugins.jira.client

import net.serenitybdd.plugins.jira.JiraConnectionSettings
import net.serenitybdd.plugins.jira.model.CustomField
import spock.lang.Specification

class WhenIdentifyingCustomFields extends Specification {

    def "Should be able to read a cascading select custom field"() {
        given:
        def jiraClient = new JerseyJiraClient(JiraConnectionSettings.getJIRAWebserviceURL(),JiraConnectionSettings.getJIRAUserName(),JiraConnectionSettings.getJIRAUserApiToken(),"DEMO").
                usingCustomFields(["RequirementsList"])
        when:
            List<CustomField> customFields = jiraClient.customFields
        then:
            customFields.collect { it.name } == ["RequirementsList"]
    }

    def "should be able to read the values of a cascading select field"() {
        given:
        def jiraClient = new JerseyJiraClient(JiraConnectionSettings.getJIRAWebserviceURL(),JiraConnectionSettings.getJIRAUserName(),JiraConnectionSettings.getJIRAUserApiToken(),"DEMO").usingMetadataIssueType("Story");
        when:
            def options = jiraClient.findOptionsForCascadingSelect("RequirementsList");
        then:
            options.collect { it.option }.containsAll("Grow Apples", "Grow Potatoes")
        and:
            def List<List<String>> check = options.collect {it.nestedOptions }.collect { it.option }
            def values = []
            check.each {
                it.each {
                    values << it
                }
            }
            values.containsAll(["Grow red apples","Grow green apples"])
    }

    def "Cascading select options should store parent options"() {

        given:
            def jiraClient = new JerseyJiraClient(JiraConnectionSettings.getJIRAWebserviceURL(),JiraConnectionSettings.getJIRAUserName(),JiraConnectionSettings.getJIRAUserApiToken(),"DEMO").usingMetadataIssueType("Story");
        when:
            def options = jiraClient.findOptionsForCascadingSelect("RequirementsList");
        then:
            options[0].nestedOptions[0].parentOption.get() == options[0]
    }


   /* TODO
   def "should be able to read custom field values as rendered HTML if available"() {
        given:
            def jiraClient = new JerseyJiraClient("https://wakaleo.atlassian.net", "bruce", "batm0bile","DEMO").
                             usingCustomFields(["Acceptance Criteria"])
        when:
            def issue = jiraClient.findByKey("DEMO-8").get()
        then:
            issue.customField("Acceptance Criteria").isPresent()
        and:
            issue.rendered.customField("Acceptance Criteria").get() == "<p>Grow <b>BIG</b> Potatoes</p>"
    }

    def "should be able to read custom field values in non-rendered format"() {
        given:
            def jiraClient = new JerseyJiraClient("https://wakaleo.atlassian.net", "bruce", "batm0bile","DEMO").
                    usingCustomFields(["Acceptance Criteria"])
        when:
            def issue = jiraClient.findByKey("DEMO-8").get()
        then:
            issue.customField("Acceptance Criteria").isPresent()
        and:
            issue.customField("Acceptance Criteria").get().value() == "Grow *BIG* Potatoes"
    }

    def "should be able to read custom field values of a given type"() {
        given:
        def jiraClient = new JerseyJiraClient(JiraConnectionSettings.getJIRAWebserviceURL(),JiraConnectionSettings.getJIRAUserName(),JiraConnectionSettings.getJIRAUserApiToken(),"DEMO").
                                                  usingCustomFields(["capability"])
        when:
            def issue = jiraClient.findByKey("DEMO-8").get()
        then:
            issue.customField("capability").isPresent()
        and:
            issue.customField("capability").get().asString() == "Grow Potatoes"
    }*/

    def "should be able to read field value lists"() {
        given:
            def jiraClient =  new JerseyJiraClient(JiraConnectionSettings.getJIRAWebserviceURL(),JiraConnectionSettings.getJIRAUserName(),JiraConnectionSettings.getJIRAUserApiToken(),"DEMO").
                    usingCustomFields(["RequirementsList"])
        when:
            def issue = jiraClient.findByKey("DEMO-33").get()
        then:
            issue.customField("RequirementsList").isPresent()
        and:
            issue.customField("RequirementsList").get().asListOf(String) == ["Grow Potatoes", "Grow normal potatoes"]

    }

}
