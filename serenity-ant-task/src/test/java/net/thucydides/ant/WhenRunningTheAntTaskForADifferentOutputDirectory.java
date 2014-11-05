package net.thucydides.ant;

import java.net.URISyntaxException;

import static java.lang.Thread.currentThread;

public class WhenRunningTheAntTaskForADifferentOutputDirectory extends ThucydidesAntTaskTestBase {
    public void setUp() throws Exception {
        String antFile = currentThread().getContextClassLoader().getResource("build-different-output.xml").getFile();
        configureProject(antFile);
        cleanReportsIn("alt-test-outcomes");
    }

    public void testShouldExecuteTaskWithDefaultValues() throws URISyntaxException {
        executeTarget("thucydides.reports");
        thucydidesReportsShouldAppearIn("alt-test-outcomes");
    }
}
