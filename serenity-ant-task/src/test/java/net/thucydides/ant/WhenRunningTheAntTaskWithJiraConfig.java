package net.thucydides.ant;

import java.net.URISyntaxException;

import static java.lang.Thread.currentThread;

public class WhenRunningTheAntTaskWithJiraConfig extends ThucydidesAntTaskTestBase {
    public void setUp() {
        String antFile = currentThread().getContextClassLoader().getResource("build-with-jira-authentication.xml").getFile();
        configureProject(antFile);
    }

    public void testShouldExecuteTaskWithJIRAConfiguration() throws URISyntaxException {
        executeTarget("thucydides.reports");
        thucydidesReportsShouldAppearIn("test-outcomes");
    }
}
