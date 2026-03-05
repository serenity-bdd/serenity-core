package net.serenitybdd.playwright;

import com.microsoft.playwright.Page;
import net.thucydides.model.domain.TestResult;
import net.thucydides.model.screenshots.ScreenshotAndHtmlSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
