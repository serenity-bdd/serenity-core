package net.serenity_bdd.ant;

import java.net.URISyntaxException;

import static java.lang.Thread.currentThread;

public class WhenRunningTheAntTaskForADifferentOutputDirectory extends SerenityAntTaskTestBase {
    public void setUp() throws Exception {
        String antFile = currentThread().getContextClassLoader().getResource("build-different-output.xml").getFile();
        configureProject(antFile);
        cleanReportsIn("alt-test-outcomes");
    }

    public void testShouldExecuteTaskWithDefaultValues() throws URISyntaxException {
        executeTarget("serenity.reports");
        serenityReportsShouldAppearIn("alt-test-outcomes");
    }
}
