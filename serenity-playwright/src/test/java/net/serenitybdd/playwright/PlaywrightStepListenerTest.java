package net.serenitybdd.playwright;

import com.microsoft.playwright.Page;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    @Test
    void shouldHandleStepFinishedWhenNoPagesRegistered() {
        // Should not throw even when no pages registered
        assertThatCode(() -> listener.stepFinished()).doesNotThrowAnyException();
    }

    @Test
    void shouldHandleStepFailedWhenNoPagesRegistered() {
        // Should not throw even when no pages registered
        assertThatCode(() -> listener.stepFailed(null)).doesNotThrowAnyException();
    }

    @Test
    void shouldHandleTakeScreenshotNowWhenNoPagesRegistered() {
        // Should not throw even when no pages registered
        assertThatCode(() -> listener.takeScreenshotNow()).doesNotThrowAnyException();
    }

    @Test
    void shouldHandleStepFinishedWithClosedPage() {
        Page closedPage = mock(Page.class);
        when(closedPage.isClosed()).thenReturn(true);
        PlaywrightPageRegistry.registerPage(closedPage);

        // Should not throw with closed page
        assertThatCode(() -> listener.stepFinished()).doesNotThrowAnyException();
    }

    @Test
    void shouldHandleStepFinishedWhenOutputDirectoryNotAvailable() {
        // Without a proper test context, output directory will be null
        Page mockPage = mock(Page.class);
        when(mockPage.isClosed()).thenReturn(false);
        PlaywrightPageRegistry.registerPage(mockPage);

        // Should gracefully handle missing output directory
        assertThatCode(() -> listener.stepFinished()).doesNotThrowAnyException();
    }

    @Test
    void shouldNotThrowOnAllStepListenerMethods() {
        // Verify all interface methods are implemented without throwing
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
            listener.takeScreenshots(null);
            listener.takeScreenshots(null, null);
            listener.recordScreenshot("name", new byte[0]);
        }).doesNotThrowAnyException();
    }
}
