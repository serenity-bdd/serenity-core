package net.serenitybdd.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.junit.Options;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link PlaywrightOptionsResolver}.
 * <p>
 * These tests verify the pure Options resolution logic without launching
 * any real Playwright browser instances.
 * </p>
 */
class PlaywrightOptionsResolverTest {

    @Nested
    @DisplayName("resolveBrowserType()")
    class ResolveBrowserType {

        @Test
        void shouldReturnBrowserNameFromOptions() {
            Options options = new Options().setBrowserName("firefox");

            assertThat(PlaywrightOptionsResolver.resolveBrowserType(options, "chromium"))
                    .isEqualTo("firefox");
        }

        @Test
        void shouldReturnFallbackWhenBrowserNameIsNull() {
            Options options = new Options();

            assertThat(PlaywrightOptionsResolver.resolveBrowserType(options, "webkit"))
                    .isEqualTo("webkit");
        }

        @Test
        void shouldReturnFallbackWhenOptionsIsNull() {
            assertThat(PlaywrightOptionsResolver.resolveBrowserType(null, "chromium"))
                    .isEqualTo("chromium");
        }
    }

    @Nested
    @DisplayName("resolveLaunchOptions()")
    class ResolveLaunchOptions {

        @Test
        void shouldUseLaunchOptionsAsBase() {
            BrowserType.LaunchOptions base = new BrowserType.LaunchOptions().setSlowMo(200);
            Options options = new Options().setLaunchOptions(base);

            BrowserType.LaunchOptions resolved = PlaywrightOptionsResolver.resolveLaunchOptions(options);

            assertThat(resolved.slowMo).isEqualTo(200);
        }

        @Test
        void shouldCreateFreshLaunchOptionsWhenBaseIsNull() {
            Options options = new Options();

            BrowserType.LaunchOptions resolved = PlaywrightOptionsResolver.resolveLaunchOptions(options);

            assertThat(resolved).isNotNull();
        }

        @Test
        void shouldApplyHeadlessShorthand() {
            Options options = new Options().setHeadless(false);

            BrowserType.LaunchOptions resolved = PlaywrightOptionsResolver.resolveLaunchOptions(options);

            assertThat(resolved.headless).isFalse();
        }

        @Test
        void shouldApplyChannelShorthand() {
            Options options = new Options().setChannel("chrome");

            BrowserType.LaunchOptions resolved = PlaywrightOptionsResolver.resolveLaunchOptions(options);

            assertThat(resolved.channel).isEqualTo("chrome");
        }

        @Test
        void headlessShorthandShouldOverrideBase() {
            BrowserType.LaunchOptions base = new BrowserType.LaunchOptions().setHeadless(true);
            Options options = new Options().setLaunchOptions(base).setHeadless(false);

            BrowserType.LaunchOptions resolved = PlaywrightOptionsResolver.resolveLaunchOptions(options);

            assertThat(resolved.headless).isFalse();
        }

        @Test
        void shouldLeaveHeadlessNullWhenNeitherSourceSetsIt() {
            Options options = new Options();

            BrowserType.LaunchOptions resolved = PlaywrightOptionsResolver.resolveLaunchOptions(options);

            assertThat(resolved.headless).isNull();
        }

        @Test
        void shouldPreserveBaseFieldsWhenApplyingShorthands() {
            BrowserType.LaunchOptions base = new BrowserType.LaunchOptions().setSlowMo(100);
            Options options = new Options().setLaunchOptions(base).setHeadless(true);

            BrowserType.LaunchOptions resolved = PlaywrightOptionsResolver.resolveLaunchOptions(options);

            assertThat(resolved.slowMo).isEqualTo(100);
            assertThat(resolved.headless).isTrue();
        }
    }

    @Nested
    @DisplayName("resolveContextOptions()")
    class ResolveContextOptions {

        @Test
        void shouldReturnNullWhenNoBaseAndNoOverrides() {
            Options options = new Options();

            assertThat(PlaywrightOptionsResolver.resolveContextOptions(options)).isNull();
        }

        @Test
        void shouldReturnBaseContextOptionsDirectlyWhenNoOverrides() {
            Browser.NewContextOptions ctxOpts = new Browser.NewContextOptions()
                    .setViewportSize(1280, 720);
            Options options = new Options().setContextOptions(ctxOpts);

            assertThat(PlaywrightOptionsResolver.resolveContextOptions(options)).isSameAs(ctxOpts);
        }

        @Test
        void shouldCreateFreshContextOptionsAndSetBaseUrl() {
            Options options = new Options();
            options.baseUrl = "https://example.com";

            Browser.NewContextOptions resolved = PlaywrightOptionsResolver.resolveContextOptions(options);

            assertThat(resolved).isNotNull();
            assertThat(resolved.baseURL).isEqualTo("https://example.com");
        }

        @Test
        void shouldSetIgnoreHTTPSErrors() {
            Options options = new Options();
            options.ignoreHTTPSErrors = true;

            Browser.NewContextOptions resolved = PlaywrightOptionsResolver.resolveContextOptions(options);

            assertThat(resolved.ignoreHTTPSErrors).isTrue();
        }

        @Test
        void shouldApplyOverridesOnTopOfExistingBase() {
            Browser.NewContextOptions base = new Browser.NewContextOptions()
                    .setViewportSize(1280, 720);
            Options options = new Options();
            options.contextOptions = base;
            options.baseUrl = "https://staging.example.com";
            options.ignoreHTTPSErrors = true;

            Browser.NewContextOptions resolved = PlaywrightOptionsResolver.resolveContextOptions(options);

            assertThat(resolved.baseURL).isEqualTo("https://staging.example.com");
            assertThat(resolved.ignoreHTTPSErrors).isTrue();
            assertThat(resolved.viewportSize).isPresent();
        }

        @Test
        void shouldApplyOnlyBaseUrlWhenIgnoreHTTPSErrorsIsNull() {
            Options options = new Options();
            options.baseUrl = "https://example.com";

            Browser.NewContextOptions resolved = PlaywrightOptionsResolver.resolveContextOptions(options);

            assertThat(resolved.baseURL).isEqualTo("https://example.com");
            assertThat(resolved.ignoreHTTPSErrors).isNull();
        }

        @Test
        void shouldApplyOnlyIgnoreHTTPSErrorsWhenBaseUrlIsNull() {
            Options options = new Options();
            options.ignoreHTTPSErrors = false;

            Browser.NewContextOptions resolved = PlaywrightOptionsResolver.resolveContextOptions(options);

            assertThat(resolved.ignoreHTTPSErrors).isFalse();
            assertThat(resolved.baseURL).isNull();
        }
    }
}
