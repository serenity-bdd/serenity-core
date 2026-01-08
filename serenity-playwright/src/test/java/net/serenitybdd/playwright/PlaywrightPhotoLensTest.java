package net.serenitybdd.playwright;

import com.microsoft.playwright.Page;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlaywrightPhotoLensTest {

    @Test
    void shouldTakeScreenshot() throws IOException {
        Page mockPage = mock(Page.class);
        byte[] expectedScreenshot = "screenshot-data".getBytes();
        when(mockPage.screenshot(any(Page.ScreenshotOptions.class))).thenReturn(expectedScreenshot);

        PlaywrightPhotoLens lens = new PlaywrightPhotoLens(mockPage);
        byte[] result = lens.takeScreenshot();

        assertThat(result).isEqualTo(expectedScreenshot);
    }

    @Test
    void shouldReportCanTakeScreenshotWhenPageOpen() {
        Page mockPage = mock(Page.class);
        when(mockPage.isClosed()).thenReturn(false);

        PlaywrightPhotoLens lens = new PlaywrightPhotoLens(mockPage);

        assertThat(lens.canTakeScreenshot()).isTrue();
    }

    @Test
    void shouldReportCannotTakeScreenshotWhenPageClosed() {
        Page mockPage = mock(Page.class);
        when(mockPage.isClosed()).thenReturn(true);

        PlaywrightPhotoLens lens = new PlaywrightPhotoLens(mockPage);

        assertThat(lens.canTakeScreenshot()).isFalse();
    }

    @Test
    void shouldReportCannotTakeScreenshotWhenPageIsNull() {
        PlaywrightPhotoLens lens = new PlaywrightPhotoLens(null);

        assertThat(lens.canTakeScreenshot()).isFalse();
    }

    @Test
    void shouldReturnEmptyArrayWhenCannotTakeScreenshot() throws IOException {
        PlaywrightPhotoLens lens = new PlaywrightPhotoLens(null);

        byte[] result = lens.takeScreenshot();

        assertThat(result).isEmpty();
    }

    @Test
    void shouldHandleExceptionDuringCanTakeScreenshot() {
        Page mockPage = mock(Page.class);
        when(mockPage.isClosed()).thenThrow(new RuntimeException("Browser disconnected"));

        PlaywrightPhotoLens lens = new PlaywrightPhotoLens(mockPage);

        assertThat(lens.canTakeScreenshot()).isFalse();
    }

    @Test
    void shouldExposeUnderlyingPage() {
        Page mockPage = mock(Page.class);

        PlaywrightPhotoLens lens = new PlaywrightPhotoLens(mockPage);

        assertThat(lens.getPage()).isSameAs(mockPage);
    }

    @Test
    void shouldReturnEmptyArrayWhenScreenshotFails() throws IOException {
        Page mockPage = mock(Page.class);
        when(mockPage.isClosed()).thenReturn(false);
        when(mockPage.screenshot(any(Page.ScreenshotOptions.class)))
                .thenThrow(new RuntimeException("Screenshot failed"));

        PlaywrightPhotoLens lens = new PlaywrightPhotoLens(mockPage);
        byte[] result = lens.takeScreenshot();

        assertThat(result).isEmpty();
    }
}
