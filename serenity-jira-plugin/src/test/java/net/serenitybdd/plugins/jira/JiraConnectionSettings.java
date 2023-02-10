package net.serenitybdd.plugins.jira;

public class JiraConnectionSettings {

    public static String getJIRAWebserviceURL() {
        return System.getenv("JIRA_WEBSERVICE_URL");
    }

    public static String getJIRAUserName() {
        return System.getenv("JIRA_USERNAME");
    }

    public static String getJIRAUserApiToken() {
        return System.getenv("JIRA_API_TOKEN");
    }
}
