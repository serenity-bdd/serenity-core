package net.serenity_bdd.ant;

import java.net.URISyntaxException;

import static java.lang.Thread.currentThread;

public class WhenRunningTheAntTaskWithJiraConfig extends SerenityAntTaskTestBase {
    public void setUp() {
        String antFile = currentThread().getContextClassLoader().getResource("build-with-jira-authentication.xml").getFile();
        configureProject(antFile);
    }

    public void testShouldExecuteTaskWithJIRAConfiguration() throws URISyntaxException {
        executeTarget("serenity.reports");
        serenityReportsShouldAppearIn("test-outcomes");
    }
}
