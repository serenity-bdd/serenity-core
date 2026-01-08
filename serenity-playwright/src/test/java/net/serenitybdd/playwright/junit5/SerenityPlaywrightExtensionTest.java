package net.serenitybdd.playwright.junit5;

import com.microsoft.playwright.Page;
import net.serenitybdd.playwright.PlaywrightPageRegistry;
import net.serenitybdd.playwright.PlaywrightSerenity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SerenityPlaywrightExtensionTest {

    @AfterEach
    void cleanup() {
        PlaywrightPageRegistry.clear();
    }

    @Test
    void shouldCreateExtensionWithoutError() {
        SerenityPlaywrightExtension extension = new SerenityPlaywrightExtension();
        assertThat(extension).isNotNull();
    }

    @Test
    void shouldReturnMostRecentPageFromRegistry() {
        Page page1 = mock(Page.class);
        Page page2 = mock(Page.class);

        PlaywrightSerenity.registerPage(page1);
        PlaywrightSerenity.registerPage(page2);

        // The extension would return the most recent page
        Page currentPage = PlaywrightSerenity.getCurrentPage();
        assertThat(currentPage).isSameAs(page2);
    }

    @Test
    void shouldReturnNullWhenNoPageRegistered() {
        // No pages registered
        Page currentPage = PlaywrightSerenity.getCurrentPage();
        assertThat(currentPage).isNull();
    }

    @Test
    void shouldClearPagesOnCleanup() {
        Page mockPage = mock(Page.class);
        PlaywrightSerenity.registerPage(mockPage);

        assertThat(PlaywrightSerenity.getRegisteredPages()).isNotEmpty();

        PlaywrightSerenity.clear();

        assertThat(PlaywrightSerenity.getRegisteredPages()).isEmpty();
    }
}
