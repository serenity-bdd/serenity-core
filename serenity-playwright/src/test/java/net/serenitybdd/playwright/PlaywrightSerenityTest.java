package net.serenitybdd.playwright;

import com.microsoft.playwright.Page;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class PlaywrightSerenityTest {

    @AfterEach
    void cleanup() {
        PlaywrightPageRegistry.clear();
    }

    @Test
    void shouldRegisterPage() {
        Page mockPage = mock(Page.class);

        PlaywrightSerenity.registerPage(mockPage);

        assertThat(PlaywrightSerenity.getRegisteredPages()).contains(mockPage);
    }

    @Test
    void shouldUnregisterPage() {
        Page mockPage = mock(Page.class);
        PlaywrightSerenity.registerPage(mockPage);

        PlaywrightSerenity.unregisterPage(mockPage);

        assertThat(PlaywrightSerenity.getRegisteredPages()).isEmpty();
    }

    @Test
    void shouldReturnCurrentPageAsLastRegistered() {
        Page page1 = mock(Page.class);
        Page page2 = mock(Page.class);
        PlaywrightSerenity.registerPage(page1);
        PlaywrightSerenity.registerPage(page2);

        Page currentPage = PlaywrightSerenity.getCurrentPage();

        assertThat(currentPage).isSameAs(page2);
    }

    @Test
    void shouldReturnNullWhenNoPagesRegistered() {
        Page currentPage = PlaywrightSerenity.getCurrentPage();

        assertThat(currentPage).isNull();
    }

    @Test
    void shouldClearAllPages() {
        Page page1 = mock(Page.class);
        Page page2 = mock(Page.class);
        PlaywrightSerenity.registerPage(page1);
        PlaywrightSerenity.registerPage(page2);

        PlaywrightSerenity.clear();

        assertThat(PlaywrightSerenity.getRegisteredPages()).isEmpty();
    }

    @Test
    void shouldDelegateToRegistry() {
        Page mockPage = mock(Page.class);
        PlaywrightSerenity.registerPage(mockPage);

        // Verify it's actually in the registry
        assertThat(PlaywrightPageRegistry.getRegisteredPages()).contains(mockPage);
    }
}
