package net.serenitybdd.ant;

import java.net.URISyntaxException;
import java.nio.file.Paths;

import static java.lang.Thread.currentThread;

public class WhenRunningTheAntTask extends SerenityAntTaskTestBase {
    public void setUp() throws Exception {
        String antFile = Paths.get(currentThread().getContextClassLoader().getResource("build.xml").toURI()).toString();//currentThread().getContextClassLoader().getResource("build.xml").getFile();
        configureProject(antFile);
        cleanReportsIn("test-outcomes");
    }

    public void testShouldExecuteTaskWithDefaultValues() throws URISyntaxException {
        executeTarget("serenity.reports");
        serenityReportsShouldAppearIn("test-outcomes");
        thucydidesResourcesShouldAppearIn("test-outcomes");
    }
}
