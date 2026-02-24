package net.thucydides.core.reports.integration;

import net.serenitybdd.annotations.Feature;
import net.serenitybdd.annotations.Story;
import net.thucydides.core.reports.html.HtmlAcceptanceTestReporter;
import net.thucydides.core.util.ExtendedTemporaryFolder;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestStep;
import net.thucydides.model.environment.MockEnvironmentVariables;
import net.thucydides.model.issues.IssueTracking;
import net.thucydides.model.issues.SystemPropertiesIssueTracking;
import net.thucydides.model.reports.AcceptanceTestReporter;
import net.thucydides.model.screenshots.ScreenshotAndHtmlSource;
import net.thucydides.model.util.FileSystemUtils;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;

import java.io.File;
import java.io.IOException;

public class AbstractReportGenerationTest {

    @Rule
    public ExtendedTemporaryFolder temporaryDirectory = new ExtendedTemporaryFolder();

    protected AcceptanceTestReporter reporter;

    protected File outputDirectory;

    protected MockEnvironmentVariables environmentVariables;

    public AbstractReportGenerationTest() {
    }

    @Before
    public void setupTestReporter() throws IOException {
        environmentVariables = new MockEnvironmentVariables();
        IssueTracking issueTracking = new SystemPropertiesIssueTracking(environmentVariables);
        reporter = new HtmlAcceptanceTestReporter(environmentVariables, issueTracking);
        outputDirectory = temporaryDirectory.newFolder();
        reporter.setOutputDirectory(outputDirectory);
    }

    protected void recordStepWithScreenshot(final TestOutcome testOutcome,
                                            final String stepName,
                                            final String screenshot) throws IOException {
        String screenshotResource = "/screenshots/" + screenshot;
        File sourceFile = FileSystemUtils.getResourceAsFile(screenshotResource);// new File(sourcePath.getPath());
        FileUtils.copyFileToDirectory(sourceFile, outputDirectory);

        TestStep step = TestStepFactory.successfulTestStepCalled(stepName);

        step.addScreenshot(new ScreenshotAndHtmlSource(new File(outputDirectory, screenshot), null));

        testOutcome.recordStep(step);
    }

    protected class AUserStory {
    }

    @Story(storyClass = WhenGeneratingAnHtmlReport.AUserStory.class)
    protected class SomeTestScenario {
        public void a_simple_test_case() {
        }

        public void should_do_this() {
        }

        public void should_do_that() {
        }

    }

    @Feature
    protected class AFeature {
        class AUserStoryInAFeature {
        }

    }

    @Story(storyClass = WhenGeneratingAnHtmlReport.AFeature.AUserStoryInAFeature.class)
    protected class SomeTestScenarioInAFeature {
        public void should_do_this() {
        }

        public void should_do_that() {
        }

    }
}
