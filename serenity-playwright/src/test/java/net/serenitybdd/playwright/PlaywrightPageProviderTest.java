package net.serenitybdd.playwright;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PlaywrightPageProviderTest {

    private final PlaywrightPageProvider provider = new PlaywrightPageProvider();

    @AfterEach
    void cleanup() {
        PlaywrightSerenity.resetForTesting();
    }

    @Test
    void shouldResolvePageType() {
        Optional<Object> result = provider.resolve(Page.class);

        assertThat(result).isPresent();
        assertThat(result.get()).isInstanceOf(Page.class);
    }

    @Test
    void shouldNotResolveUnrelatedTypes() {
        assertThat(provider.resolve(String.class)).isEmpty();
        assertThat(provider.resolve(Object.class)).isEmpty();
    }

    @Nested
    class WhenPageIsAvailable {

        @Test
        void shouldDelegateToRealPage() {
            Page mockPage = mock(Page.class);
            when(mockPage.title()).thenReturn("Test Page");
            PlaywrightSerenity.registerPage(mockPage);

            Page proxy = (Page) provider.resolve(Page.class).get();

            assertThat(proxy.title()).isEqualTo("Test Page");
            verify(mockPage).title();
        }

        @Test
        void shouldDelegateLocatorCalls() {
            Page mockPage = mock(Page.class);
            Locator mockLocator = mock(Locator.class);
            when(mockPage.getByLabel("Email")).thenReturn(mockLocator);
            when(mockLocator.inputValue()).thenReturn("test@example.com");
            PlaywrightSerenity.registerPage(mockPage);

            Page proxy = (Page) provider.resolve(Page.class).get();
            Locator locator = proxy.getByLabel("Email");

            assertThat(locator.inputValue()).isEqualTo("test@example.com");
        }
    }

    @Nested
    class WhenPageIsNotYetAvailable {

        @Test
        void shouldDeferLocatorResolution() {
            // Phase 1: Create proxy and resolve locator BEFORE page is registered
            Page proxy = (Page) provider.resolve(Page.class).get();
            Locator deferredLocator = proxy.getByLabel("First name");

            // The deferred locator should not be null
            assertThat(deferredLocator).isNotNull();

            // Phase 2: Register a real page
            Page mockPage = mock(Page.class);
            Locator mockLocator = mock(Locator.class);
            when(mockPage.getByLabel("First name")).thenReturn(mockLocator);
            when(mockLocator.inputValue()).thenReturn("John");
            PlaywrightSerenity.registerPage(mockPage);

            // Phase 3: Now the deferred locator should resolve through to the real one
            assertThat(deferredLocator.inputValue()).isEqualTo("John");
            verify(mockPage).getByLabel("First name");
            verify(mockLocator).inputValue();
        }

        @Test
        void shouldThrowForTerminalMethodsWhenNoPageAvailable() {
            Page proxy = (Page) provider.resolve(Page.class).get();

            // title() returns String (not an interface) — cannot defer
            assertThatThrownBy(proxy::title)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("No Playwright Page available");
        }

        @Test
        void shouldSupportLocatorStoredInConstructor() {
            // Simulates the ContactForm pattern:
            //   this.firstNameField = page.getByLabel("First name");
            //   this.lastNameField = page.getByLabel("Last name");
            // ... called during construction before any real Page is available
            Page proxy = (Page) provider.resolve(Page.class).get();
            Locator firstNameField = proxy.getByLabel("First name");
            Locator lastNameField = proxy.getByLabel("Last name");

            // Later, register a real page
            Page mockPage = mock(Page.class);
            Locator mockFirstName = mock(Locator.class);
            Locator mockLastName = mock(Locator.class);
            when(mockPage.getByLabel("First name")).thenReturn(mockFirstName);
            when(mockPage.getByLabel("Last name")).thenReturn(mockLastName);
            PlaywrightSerenity.registerPage(mockPage);

            // Both deferred locators should resolve independently
            firstNameField.fill("John");
            lastNameField.fill("Doe");

            verify(mockFirstName).fill("John");
            verify(mockLastName).fill("Doe");
        }
    }
}
