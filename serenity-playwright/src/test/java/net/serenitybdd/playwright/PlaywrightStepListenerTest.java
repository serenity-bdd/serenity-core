package net.serenitybdd.playwright;

import com.microsoft.playwright.Page;
import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.model.domain.TestResult;
import net.thucydides.model.environment.TestLocalEnvironmentVariables;
import net.thucydides.model.screenshots.ScreenshotAndHtmlSource;
import net.thucydides.model.steps.ExecutedStepDescription;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

class PlaywrightStepListenerTest {

    private PlaywrightStepListener listener;

    @BeforeEach
    void setup() {
        listener = new PlaywrightStepListener();
    }

    @AfterEach
    void cleanup() {
        PlaywrightPageRegistry.clear();
    }

    @Test
    void shouldCreateListenerWithoutError() {
        assertThat(listener).isNotNull();
    }

    @Nested
    @DisplayName("stepFinished()")
    class StepFinished {

        @Test
        void shouldHandleStepFinishedWhenNoPagesRegistered() {
            assertThatCode(() -> listener.stepFinished()).doesNotThrowAnyException();
        }

        @Test
        void shouldHandleStepFinishedWithClosedPage() {
            Page closedPage = mock(Page.class);
            when(closedPage.isClosed()).thenReturn(true);
            PlaywrightPageRegistry.registerPage(closedPage);

            assertThatCode(() -> listener.stepFinished()).doesNotThrowAnyException();
        }

        @Test
        void shouldHandleStepFinishedWhenOutputDirectoryNotAvailable() {
            Page mockPage = mock(Page.class);
            when(mockPage.isClosed()).thenReturn(false);
            PlaywrightPageRegistry.registerPage(mockPage);

            assertThatCode(() -> listener.stepFinished()).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("stepFailed()")
    class StepFailed {

        @Test
        void shouldHandleStepFailedWhenNoPagesRegistered() {
            assertThatCode(() -> listener.stepFailed(null)).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("takeScreenshots(List)")
    class TakeScreenshots {

        @Test
        void shouldDoNothingWhenNoPagesAreRegistered() {
            List<ScreenshotAndHtmlSource> screenshots = new ArrayList<>();

            listener.takeScreenshots(screenshots);

            assertThat(screenshots).isEmpty();
        }

        @Test
        void shouldDoNothingWhenRegisteredPageIsClosed() {
            Page closedPage = mock(Page.class);
            when(closedPage.isClosed()).thenReturn(true);
            PlaywrightPageRegistry.registerPage(closedPage);
            List<ScreenshotAndHtmlSource> screenshots = new ArrayList<>();

            listener.takeScreenshots(screenshots);

            assertThat(screenshots).isEmpty();
        }

        @Test
        void shouldNotThrowWhenOutputDirectoryIsUnavailableWithRegisteredPages() {
            Page mockPage = mock(Page.class);
            when(mockPage.isClosed()).thenReturn(false);
            PlaywrightPageRegistry.registerPage(mockPage);
            List<ScreenshotAndHtmlSource> screenshots = new ArrayList<>();

            // Without a real StepEventBus context, output directory is null
            assertThatCode(() -> listener.takeScreenshots(screenshots))
                    .doesNotThrowAnyException();
        }

        @Test
        void shouldNotThrowWhenNullListPassedAndNoPagesRegistered() {
            // Guard: hasRegisteredPages() is false → returns before touching the list
            assertThatCode(() -> listener.takeScreenshots((List<ScreenshotAndHtmlSource>) null))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("takeScreenshots(TestResult, List)")
    class TakeScreenshotsWithResult {

        @Test
        void shouldDoNothingWhenNoPagesAreRegistered() {
            List<ScreenshotAndHtmlSource> screenshots = new ArrayList<>();

            listener.takeScreenshots(TestResult.SUCCESS, screenshots);

            assertThat(screenshots).isEmpty();
        }

        @Test
        void shouldDoNothingWhenRegisteredPageIsClosed() {
            Page closedPage = mock(Page.class);
            when(closedPage.isClosed()).thenReturn(true);
            PlaywrightPageRegistry.registerPage(closedPage);
            List<ScreenshotAndHtmlSource> screenshots = new ArrayList<>();

            listener.takeScreenshots(TestResult.FAILURE, screenshots);

            assertThat(screenshots).isEmpty();
        }

        @Test
        void shouldNotThrowWhenNullListPassedAndNoPagesRegistered() {
            assertThatCode(() -> listener.takeScreenshots(TestResult.SUCCESS, null))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("takeScreenshotNow()")
    class TakeScreenshotNow {

        @Test
        void shouldNotThrowWhenNoPagesRegistered() {
            assertThatCode(() -> listener.takeScreenshotNow()).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Screenshot permission checks")
    class ScreenshotPermissionChecks {

        private BaseStepListener baseStepListener;
        private File outputDir;

        @BeforeEach
        void setupEventBus() throws IOException {
            outputDir = Files.createTempDirectory("serenity-test").toFile();
            baseStepListener = new BaseStepListener(outputDir);
            StepEventBus.getParallelEventBus().registerListener(baseStepListener);
            StepEventBus.getParallelEventBus().testStarted("permission_test");
            StepEventBus.getParallelEventBus().stepStarted(ExecutedStepDescription.withTitle("a step"));
        }

        @AfterEach
        void teardownEventBus() {
            StepEventBus.getParallelEventBus().testFinished();
            StepEventBus.getParallelEventBus().dropListener(baseStepListener);
            TestLocalEnvironmentVariables.clear();
            ConfiguredEnvironment.reset();
        }

        private void setScreenshotProperty(String value) {
            TestLocalEnvironmentVariables.setProperty("serenity.take.screenshots", value);
            ConfiguredEnvironment.updateConfiguration(TestLocalEnvironmentVariables.getUpdatedEnvironmentVariables());
        }

        private void setStoreHtmlProperty(String value) {
            TestLocalEnvironmentVariables.setProperty("serenity.store.html", value);
            ConfiguredEnvironment.updateConfiguration(TestLocalEnvironmentVariables.getUpdatedEnvironmentVariables());
        }

        @Test
        @DisplayName("stepFinished should not take screenshot when configured FOR_FAILURES")
        void stepFinished_should_not_take_screenshot_when_configured_for_failures() {
            setScreenshotProperty( "FOR_FAILURES");

            Page mockPage = mock(Page.class);
            when(mockPage.isClosed()).thenReturn(false);
            PlaywrightPageRegistry.registerPage(mockPage);

            listener.stepFinished();

            verify(mockPage, never()).screenshot(any());
        }

        @Test
        @DisplayName("stepFailed should take screenshot when configured FOR_FAILURES")
        void stepFailed_should_take_screenshot_when_configured_for_failures() {
            setScreenshotProperty( "FOR_FAILURES");

            Page mockPage = mock(Page.class);
            when(mockPage.isClosed()).thenReturn(false);
            when(mockPage.screenshot(any(Page.ScreenshotOptions.class))).thenReturn(new byte[]{1, 2, 3});
            PlaywrightPageRegistry.registerPage(mockPage);

            listener.stepFailed(null);

            verify(mockPage).screenshot(any());
        }

        @Test
        @DisplayName("stepFinished should take screenshot when configured AFTER_EACH_STEP")
        void stepFinished_should_take_screenshot_when_configured_after_each_step() {
            setScreenshotProperty( "AFTER_EACH_STEP");

            Page mockPage = mock(Page.class);
            when(mockPage.isClosed()).thenReturn(false);
            when(mockPage.screenshot(any(Page.ScreenshotOptions.class))).thenReturn(new byte[]{1, 2, 3});
            PlaywrightPageRegistry.registerPage(mockPage);

            listener.stepFinished();

            verify(mockPage).screenshot(any());
        }

        @Test
        @DisplayName("stepFinished should not take screenshot when configured DISABLED")
        void stepFinished_should_not_take_screenshot_when_disabled() {
            setScreenshotProperty( "DISABLED");

            Page mockPage = mock(Page.class);
            when(mockPage.isClosed()).thenReturn(false);
            PlaywrightPageRegistry.registerPage(mockPage);

            listener.stepFinished();

            verify(mockPage, never()).screenshot(any());
        }

        @Test
        @DisplayName("page source should not be captured when SERENITY_STORE_HTML is NEVER")
        void page_source_should_not_be_captured_when_store_html_is_never() {
            setScreenshotProperty( "AFTER_EACH_STEP");
            setStoreHtmlProperty( "NEVER");

            Page mockPage = mock(Page.class);
            when(mockPage.isClosed()).thenReturn(false);
            when(mockPage.screenshot(any(Page.ScreenshotOptions.class))).thenReturn(new byte[]{1, 2, 3});
            PlaywrightPageRegistry.registerPage(mockPage);

            listener.stepFinished();

            // page.content() should not be called when STORE_HTML is NEVER
            verify(mockPage, never()).content();
        }
    }

    @Test
    void shouldNotThrowOnAllStepListenerMethods() {
        assertThatCode(() -> {
            listener.testSuiteStarted(Object.class);
            listener.testSuiteFinished();
            listener.testStarted("test");
            listener.testStarted("test", "id");
            listener.testFinished(null);
            listener.testRetried();
            listener.stepStarted(null);
            listener.skippedStepStarted(null);
            listener.lastStepFailed(null);
            listener.stepIgnored();
            listener.stepPending();
            listener.stepPending("message");
            listener.testFailed(null, null);
            listener.testIgnored();
            listener.testSkipped();
            listener.testPending();
            listener.testIsManual();
            listener.notifyScreenChange();
            listener.useExamplesFrom(null);
            listener.addNewExamplesFrom(null);
            listener.exampleStarted(null);
            listener.exampleFinished();
            listener.assumptionViolated("message");
            listener.testRunFinished();
            listener.recordScreenshot("name", new byte[0]);
        }).doesNotThrowAnyException();
    }
}
