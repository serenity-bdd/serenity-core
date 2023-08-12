package net.thucydides.core.reports.integration;

import net.thucydides.core.reports.html.HtmlAggregateStoryReporter;
import net.thucydides.model.environment.MockEnvironmentVariables;
import net.thucydides.model.issues.IssueTracking;
import net.thucydides.model.reports.ResultChecker;
import net.thucydides.model.reports.html.ReportNameProvider;
import net.thucydides.model.util.EnvironmentVariables;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;

import static net.thucydides.model.matchers.FileMatchers.exists;
import static net.thucydides.model.util.TestResources.directoryInClasspathCalled;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class WhenGeneratingAnAggregateHtmlReportSetWithContext {

    private static File outputDirectory;

    private static final EnvironmentVariables environmentVariables = new MockEnvironmentVariables();

    @BeforeClass
    public static void generateReports() throws IOException {
        IssueTracking issueTracking = mock(IssueTracking.class);
        environmentVariables.setProperty("output.formats", "json");
        HtmlAggregateStoryReporter reporter = new HtmlAggregateStoryReporter("project", "", issueTracking, environmentVariables);
        outputDirectory = newTemporaryDirectory();
        reporter.setOutputDirectory(outputDirectory);

        File sourceDirectory = directoryInClasspathCalled("/test-outcomes/with-context");
        reporter.generateReportsForTestResultsFrom(sourceDirectory);
    }

    @AfterClass
    public static void deleteReportDirectory() {
        outputDirectory.delete();
    }

    private static File newTemporaryDirectory() throws IOException {
        File createdFolder = File.createTempFile("reports", "");
        createdFolder.delete();
        createdFolder.mkdir();
        return createdFolder;
    }

    @Before
    public void setupTestReporter() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void should_generate_an_aggregate_dashboard() throws Exception {
        assertThat(new File(outputDirectory, "index.html"), exists());
    }
}
