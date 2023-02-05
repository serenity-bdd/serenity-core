package net.serenitybdd.plugins.jira;

public class JiraConnectionSettings {

    private final static String JIRA_WEBSERVICE_URL = "https://thucydides.atlassian.net/";
    private final static String USER_NAME = "serenity.jira@gmail.com";
    private final static String USER_API_TOKEN = "sZePVVAsoFW7E7bzZuZy43BF";

    public static String getJIRAWebserviceURL() {
        return JIRA_WEBSERVICE_URL;
    }

    public static String getJIRAUserName() {
        return USER_NAME;
    }

    public static String getJIRAUserApiToken() {
        return USER_API_TOKEN;
    }
}
