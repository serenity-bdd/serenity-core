package net.serenitybdd.playwright;

import com.microsoft.playwright.Page;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.model.domain.DataTable;
import net.thucydides.model.domain.Story;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestResult;
import net.thucydides.model.domain.TestStep;
import net.thucydides.model.screenshots.ScreenshotAndHtmlSource;
import net.thucydides.model.steps.ExecutedStepDescription;
import net.thucydides.model.steps.StepFailure;
import net.thucydides.model.steps.StepListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * Listens to step events and captures Playwright screenshots.
 * <p>
 * This listener integrates with Serenity's step event system to automatically
 * capture screenshots at step boundaries for all registered Playwright pages.
 * </p>
 */
public class PlaywrightStepListener implements StepListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlaywrightStepListener.class);

    public PlaywrightStepListener() {
        // No initialization needed - screenshots are captured directly from Playwright
    }

    /**
     * Manually trigger screenshot capture for all registered pages.
     */
    public void takeScreenshotNow() {
        captureScreenshotsForAllPages(TestResult.SUCCESS);
    }

    @Override
    public void stepFinished() {
        if (PlaywrightPageRegistry.hasRegisteredPages()) {
            captureScreenshotsForAllPages(TestResult.SUCCESS);
        }
    }

    @Override
    public void stepFinished(List<ScreenshotAndHtmlSource> screenshotList, ZonedDateTime time) {
        if (PlaywrightPageRegistry.hasRegisteredPages()) {
            captureScreenshotsForAllPages(TestResult.SUCCESS);
        }
    }

    @Override
    public void stepFailed(StepFailure failure) {
        if (PlaywrightPageRegistry.hasRegisteredPages()) {
            captureScreenshotsForAllPages(TestResult.FAILURE);
        }
    }

    @Override
    public void stepFailed(StepFailure failure, List<ScreenshotAndHtmlSource> screenshotList,
                           boolean isInDataDrivenTest, ZonedDateTime timestamp) {
        if (PlaywrightPageRegistry.hasRegisteredPages()) {
            captureScreenshotsForAllPages(TestResult.FAILURE);
        }
    }

    private void captureScreenshotsForAllPages(TestResult result) {
        try {
            Path outputDir = getOutputDirectory();
            if (outputDir == null) {
                LOGGER.debug("No output directory available for screenshots");
                return;
            }

            for (Page page : PlaywrightPageRegistry.getRegisteredPages()) {
                captureScreenshotForPage(page, outputDir);
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to capture Playwright screenshots", e);
        }
    }

    private void captureScreenshotForPage(Page page, Path outputDir) {
        if (page == null) {
            return;
        }

        PlaywrightPhotoLens lens = new PlaywrightPhotoLens(page);
        if (!lens.canTakeScreenshot()) {
            LOGGER.debug("Cannot take screenshot - page is closed or unavailable");
            return;
        }

        try {
            // Capture screenshot directly from Playwright (bypassing WebDriver-specific ScreenShooterFactory)
            byte[] screenshotData = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));

            if (screenshotData != null && screenshotData.length > 0) {
                // Ensure output directory exists
                Files.createDirectories(outputDir);

                // Write screenshot to file
                Path screenshotPath = Files.createTempFile(outputDir, "screenshot", ".png");
                Files.write(screenshotPath, screenshotData);

                File screenshotFile = screenshotPath.toFile();
                File sourceFile = capturePageSource(page, outputDir);

                ScreenshotAndHtmlSource screenshotAndSource =
                        new ScreenshotAndHtmlSource(screenshotFile, sourceFile);

                addScreenshotToLastStep(screenshotAndSource);
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to capture screenshot for Playwright page", e);
        }
    }

    private File capturePageSource(Page page, Path outputDir) {
        try {
            String content = page.content();
            Path sourceFile = Files.createTempFile(outputDir, "pagesource", ".html");
            Files.writeString(sourceFile, content);
            return sourceFile.toFile();
        } catch (IOException e) {
            LOGGER.debug("Could not capture page source", e);
            return null;
        }
    }

    private void addScreenshotToLastStep(ScreenshotAndHtmlSource screenshot) {
        if (screenshot == null || !screenshot.wasTaken()) {
            return;
        }

        try {
            StepEventBus eventBus = StepEventBus.getParallelEventBus();
            if (eventBus.getBaseStepListener() != null) {
                TestOutcome outcome = eventBus.getBaseStepListener().getCurrentTestOutcome();
                if (outcome != null && !outcome.getTestSteps().isEmpty()) {
                    // Use lastStep() to get the most recently completed step
                    TestStep lastStep = outcome.lastStep();
                    if (lastStep != null) {
                        lastStep.addScreenshot(screenshot);
                        LOGGER.debug("Added Playwright screenshot to step: {}", lastStep.getDescription());
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.debug("Could not add screenshot to step", e);
        }
    }

    private Path getOutputDirectory() {
        try {
            StepEventBus eventBus = StepEventBus.getParallelEventBus();
            if (eventBus.getBaseStepListener() != null) {
                File outputDir = eventBus.getBaseStepListener().getOutputDirectory();
                return outputDir != null ? outputDir.toPath() : null;
            }
        } catch (Exception e) {
            LOGGER.debug("Could not get output directory", e);
        }
        return null;
    }

    // Other StepListener methods - no action needed

    @Override
    public void testSuiteStarted(Class<?> storyClass) {}

    @Override
    public void testSuiteStarted(Story story) {}

    @Override
    public void testSuiteFinished() {}

    @Override
    public void testStarted(String description) {}

    @Override
    public void testStarted(String description, String id) {}

    @Override
    public void testStarted(String description, String id, ZonedDateTime startTime) {}

    @Override
    public void testFinished(TestOutcome result) {}

    @Override
    public void testFinished(TestOutcome result, boolean isInDataDrivenTest, ZonedDateTime finishTime) {}

    @Override
    public void testRetried() {}

    @Override
    public void stepStarted(ExecutedStepDescription description) {}

    @Override
    public void skippedStepStarted(ExecutedStepDescription description) {}

    @Override
    public void lastStepFailed(StepFailure failure) {}

    @Override
    public void stepIgnored() {}

    @Override
    public void stepPending() {}

    @Override
    public void stepPending(String message) {}

    @Override
    public void testFailed(TestOutcome testOutcome, Throwable cause) {}

    @Override
    public void testIgnored() {}

    @Override
    public void testSkipped() {}

    @Override
    public void testPending() {}

    @Override
    public void testIsManual() {}

    @Override
    public void notifyScreenChange() {}

    @Override
    public void useExamplesFrom(DataTable table) {}

    @Override
    public void addNewExamplesFrom(DataTable table) {}

    @Override
    public void exampleStarted(Map<String, String> data) {}

    @Override
    public void exampleFinished() {}

    @Override
    public void assumptionViolated(String message) {}

    @Override
    public void testRunFinished() {}

    @Override
    public void takeScreenshots(List<ScreenshotAndHtmlSource> screenshots) {}

    @Override
    public void takeScreenshots(TestResult testResult, List<ScreenshotAndHtmlSource> screenshots) {}

    @Override
    public void recordScreenshot(String screenshotName, byte[] screenshot) {}
}
