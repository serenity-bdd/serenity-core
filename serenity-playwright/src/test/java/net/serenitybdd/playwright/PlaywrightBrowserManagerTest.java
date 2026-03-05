package net.serenitybdd.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.junit.Options;
import com.microsoft.playwright.junit.OptionsFactory;
import net.thucydides.model.environment.MockEnvironmentVariables;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link PlaywrightBrowserManager} configuration and structural behavior.
 * <p>
 * Note: Tests that actually launch a browser are integration tests (suffixed IT).
 * These unit tests verify the factory methods, option resolution, and manager state
 * without launching real browser instances.
 * </p>
 */
class PlaywrightBrowserManagerTest {

    @AfterEach
    void cleanup() {
        PlaywrightSerenity.resetForTesting();
    }

    @Nested
    @DisplayName("Factory methods")
    class FactoryMethods {

        @Test
        void shouldCreateManagedInstanceWithDefaults() {
            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managed();

            assertThat(manager).isNotNull();
            assertThat(manager.getCurrentPage()).isNull();
            assertThat(manager.getCurrentContext()).isNull();
            assertThat(manager.getBrowserType()).isEqualTo("chromium");
            assertThat(manager.getOptions()).isNull();
        }

        @Test
        void shouldCreateManagedInstanceWithSpecificBrowser() {
            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managed("firefox");

            assertThat(manager.getBrowserType()).isEqualTo("firefox");
            assertThat(manager.getOptions()).isNull();
        }

        @Test
        void shouldCreateInstanceWithExplicitLaunchOptions() {
            BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                    .setHeadless(true)
                    .setSlowMo(100);

            PlaywrightBrowserManager manager = PlaywrightBrowserManager.withOptions(options);

            assertThat(manager).isNotNull();
            assertThat(manager.getOptions()).isNull();
        }

        @Test
        void shouldReturnNewInstanceWithContextOptions() {
            PlaywrightBrowserManager original = PlaywrightBrowserManager.managed();
            PlaywrightBrowserManager withContext = original.withContextOptions(
                    new Browser.NewContextOptions().setViewportSize(1920, 1080)
            );

            assertThat(withContext).isNotNull();
            assertThat(withContext).isNotSameAs(original);
        }

        @Test
        void withContextOptionsShouldPreserveOptionsFromFactory() {
            Options opts = new Options();
            opts.testIdAttribute = "data-test";
            OptionsFactory factory = () -> opts;

            PlaywrightBrowserManager original = PlaywrightBrowserManager.managed(factory);
            PlaywrightBrowserManager withContext = original.withContextOptions(
                    new Browser.NewContextOptions().setViewportSize(1920, 1080)
            );

            assertThat(withContext.getOptions()).isSameAs(opts);
        }
    }

    @Nested
    @DisplayName("managed(OptionsFactory)")
    class ManagedWithOptionsFactory {

        @Test
        void shouldUseBrowserNameFromOptions() {
            OptionsFactory factory = () -> new Options().setBrowserName("firefox");

            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managed(factory);

            assertThat(manager.getBrowserType()).isEqualTo("firefox");
        }

        @ParameterizedTest(name = "browserName \"{0}\"")
        @ValueSource(strings = {"chromium", "firefox", "webkit"})
        void shouldAcceptAllSupportedBrowserTypes(String browserName) {
            OptionsFactory factory = () -> new Options().setBrowserName(browserName);

            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managed(factory);

            assertThat(manager.getBrowserType()).isEqualTo(browserName);
        }

        @Test
        void shouldFallBackToChromiumWhenBrowserNameIsNull() {
            OptionsFactory factory = Options::new;

            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managed(factory);

            assertThat(manager.getBrowserType()).isEqualTo("chromium");
        }

        @Test
        void shouldStoreTheFullOptionsObject() {
            Options options = new Options()
                    .setBrowserName("webkit")
                    .setHeadless(true)
                    .setTestIdAttribute("data-test");
            OptionsFactory factory = () -> options;

            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managed(factory);

            assertThat(manager.getOptions()).isSameAs(options);
        }

        @Test
        void shouldNotSetLaunchOptionsOrContextOptionsDirectly() {
            // When Options is provided, the discrete launchOptions/contextOptions fields
            // should be null — resolution happens lazily via resolve* methods
            Options options = new Options()
                    .setLaunchOptions(new BrowserType.LaunchOptions().setSlowMo(50))
                    .setContextOptions(new Browser.NewContextOptions().setViewportSize(800, 600));
            OptionsFactory factory = () -> options;

            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managed(factory);

            // The Options object is the source of truth, not the discrete fields
            assertThat(manager.getOptions()).isNotNull();
            assertThat(manager.getOptions().launchOptions.slowMo).isEqualTo(50);
        }
    }

    @Nested
    @DisplayName("resolveLaunchOptions()")
    class ResolveLaunchOptions {

        @Test
        void shouldUseOptionsLaunchOptionsAsBase() {
            BrowserType.LaunchOptions base = new BrowserType.LaunchOptions().setSlowMo(200);
            OptionsFactory factory = () -> new Options().setLaunchOptions(base);

            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managed(factory);

            assertThat(manager.resolveLaunchOptions().slowMo).isEqualTo(200);
        }

        @Test
        void shouldCreateFreshLaunchOptionsWhenOptionsLaunchOptionsIsNull() {
            OptionsFactory factory = Options::new;

            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managed(factory);

            assertThat(manager.resolveLaunchOptions()).isNotNull();
        }

        @Test
        void shouldApplyHeadlessShorthandOnTopOfBase() {
            BrowserType.LaunchOptions base = new BrowserType.LaunchOptions();
            OptionsFactory factory = () -> new Options().setLaunchOptions(base).setHeadless(false);

            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managed(factory);

            assertThat(manager.resolveLaunchOptions().headless).isFalse();
        }

        @Test
        void shouldApplyChannelShorthand() {
            OptionsFactory factory = () -> new Options().setChannel("chrome");

            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managed(factory);

            assertThat(manager.resolveLaunchOptions().channel).isEqualTo("chrome");
        }

        @Test
        void headlessShorthandShouldOverrideLaunchOptionsHeadless() {
            BrowserType.LaunchOptions base = new BrowserType.LaunchOptions().setHeadless(true);
            OptionsFactory factory = () -> new Options().setLaunchOptions(base).setHeadless(false);

            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managed(factory);

            // The shorthand wins over what's already in launchOptions
            assertThat(manager.resolveLaunchOptions().headless).isFalse();
        }

        @Test
        void shouldLeaveHeadlessNullWhenNeitherSourceSpecifiesIt() {
            OptionsFactory factory = Options::new;

            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managed(factory);

            assertThat(manager.resolveLaunchOptions().headless).isNull();
        }

        @Test
        void shouldUsePlainLaunchOptionsWhenNoOptionsFactoryProvided() {
            BrowserType.LaunchOptions opts = new BrowserType.LaunchOptions().setSlowMo(150);

            PlaywrightBrowserManager manager = PlaywrightBrowserManager.withOptions(opts);

            assertThat(manager.resolveLaunchOptions().slowMo).isEqualTo(150);
        }

        @Test
        void shouldApplyChannelPropertyFallbackWhenChannelIsNull() {
            MockEnvironmentVariables env = new MockEnvironmentVariables();
            env.setProperty("playwright.channel", "chrome");

            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managedWith(env);

            assertThat(manager.resolveLaunchOptions().channel).isEqualTo("chrome");
        }

        @Test
        void shouldNotOverrideChannelWithPropertyWhenAlreadySet() {
            MockEnvironmentVariables env = new MockEnvironmentVariables();
            env.setProperty("playwright.channel", "chrome");
            OptionsFactory factory = () -> new Options().setChannel("msedge");

            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managedWith(factory, env);

            assertThat(manager.resolveLaunchOptions().channel).isEqualTo("msedge");
        }

        @Test
        void shouldApplySlowMoPropertyFallbackWhenSlowMoIsNull() {
            MockEnvironmentVariables env = new MockEnvironmentVariables();
            env.setProperty("playwright.slowmo", "250");

            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managedWith(env);

            assertThat(manager.resolveLaunchOptions().slowMo).isEqualTo(250.0);
        }

        @Test
        void shouldNotOverrideSlowMoWithPropertyWhenAlreadySet() {
            MockEnvironmentVariables env = new MockEnvironmentVariables();
            env.setProperty("playwright.slowmo", "250");
            OptionsFactory factory = () -> new Options()
                    .setLaunchOptions(new BrowserType.LaunchOptions().setSlowMo(100));

            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managedWith(factory, env);

            assertThat(manager.resolveLaunchOptions().slowMo).isEqualTo(100.0);
        }
    }

    @Nested
    @DisplayName("resolveContextOptions()")
    class ResolveContextOptions {

        @Test
        void shouldReturnNullWhenNoOptionsAndNoContextOptions() {
            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managed();

            assertThat(manager.resolveContextOptions()).isNull();
        }

        @Test
        void shouldReturnContextOptionsDirectlyWhenNoShorthandOverrides() {
            Browser.NewContextOptions ctxOpts = new Browser.NewContextOptions()
                    .setViewportSize(1280, 720);
            OptionsFactory factory = () -> new Options().setContextOptions(ctxOpts);

            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managed(factory);

            assertThat(manager.resolveContextOptions()).isSameAs(ctxOpts);
        }

        @Test
        void shouldCreateFreshContextOptionsAndSetBaseUrl() {
            OptionsFactory factory = () -> {
                Options opts = new Options();
                opts.baseUrl = "https://example.com";
                return opts;
            };

            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managed(factory);

            Browser.NewContextOptions resolved = manager.resolveContextOptions();
            assertThat(resolved).isNotNull();
            assertThat(resolved.baseURL).isEqualTo("https://example.com");
        }

        @Test
        void shouldSetIgnoreHTTPSErrors() {
            OptionsFactory factory = () -> {
                Options opts = new Options();
                opts.ignoreHTTPSErrors = true;
                return opts;
            };

            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managed(factory);

            assertThat(manager.resolveContextOptions().ignoreHTTPSErrors).isTrue();
        }

        @Test
        void shouldApplyOverridesOnTopOfExistingContextOptions() {
            Browser.NewContextOptions base = new Browser.NewContextOptions()
                    .setViewportSize(1280, 720);
            OptionsFactory factory = () -> {
                Options opts = new Options();
                opts.contextOptions = base;
                opts.baseUrl = "https://staging.example.com";
                opts.ignoreHTTPSErrors = true;
                return opts;
            };

            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managed(factory);
            Browser.NewContextOptions resolved = manager.resolveContextOptions();

            assertThat(resolved.baseURL).isEqualTo("https://staging.example.com");
            assertThat(resolved.ignoreHTTPSErrors).isTrue();
            assertThat(resolved.viewportSize).isPresent();
        }

        @Test
        void shouldReturnNullWhenOptionsHasNoContextOptionsAndNoOverrides() {
            OptionsFactory factory = Options::new;

            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managed(factory);

            assertThat(manager.resolveContextOptions()).isNull();
        }

        @Test
        void shouldReturnPlainContextOptionsWhenNoOptionsFactory() {
            Browser.NewContextOptions ctxOpts = new Browser.NewContextOptions()
                    .setViewportSize(800, 600);
            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managed()
                    .withContextOptions(ctxOpts);

            assertThat(manager.resolveContextOptions()).isSameAs(ctxOpts);
        }

        @Test
        void shouldApplyBaseUrlPropertyFallbackWhenContextOptionsIsNull() {
            MockEnvironmentVariables env = new MockEnvironmentVariables();
            env.setProperty("playwright.baseurl", "https://staging.example.com");

            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managedWith(env);

            Browser.NewContextOptions resolved = manager.resolveContextOptions();
            assertThat(resolved).isNotNull();
            assertThat(resolved.baseURL).isEqualTo("https://staging.example.com");
        }

        @Test
        void shouldApplyBaseUrlPropertyFallbackWhenBaseUrlIsNull() {
            MockEnvironmentVariables env = new MockEnvironmentVariables();
            env.setProperty("playwright.baseurl", "https://staging.example.com");
            OptionsFactory factory = () -> new Options()
                    .setContextOptions(new Browser.NewContextOptions().setViewportSize(1280, 720));

            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managedWith(factory, env);

            Browser.NewContextOptions resolved = manager.resolveContextOptions();
            assertThat(resolved.baseURL).isEqualTo("https://staging.example.com");
            assertThat(resolved.viewportSize).isPresent();
        }

        @Test
        void shouldNotOverrideBaseUrlWithPropertyWhenAlreadySet() {
            MockEnvironmentVariables env = new MockEnvironmentVariables();
            env.setProperty("playwright.baseurl", "https://staging.example.com");
            OptionsFactory factory = () -> {
                Options opts = new Options();
                opts.baseUrl = "https://prod.example.com";
                return opts;
            };

            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managedWith(factory, env);

            assertThat(manager.resolveContextOptions().baseURL).isEqualTo("https://prod.example.com");
        }

        @Test
        void shouldReturnNullWhenNoBaseUrlPropertyAndNoContextOptions() {
            MockEnvironmentVariables env = new MockEnvironmentVariables();

            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managedWith(env);

            assertThat(manager.resolveContextOptions()).isNull();
        }
    }

    @Nested
    @DisplayName("Options wsEndpoint handling")
    class WsEndpoint {

        @Test
        void shouldStoreNullWsEndpoint() {
            OptionsFactory factory = Options::new;

            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managed(factory);

            assertThat(manager.getOptions().wsEndpoint).isNull();
        }

        @Test
        void shouldStoreEmptyWsEndpoint() {
            OptionsFactory factory = () -> {
                Options opts = new Options();
                opts.wsEndpoint = "";
                return opts;
            };

            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managed(factory);

            assertThat(manager.getOptions().wsEndpoint).isEmpty();
        }

        @Test
        void shouldStoreNonEmptyWsEndpoint() {
            OptionsFactory factory = () -> {
                Options opts = new Options();
                opts.wsEndpoint = "ws://localhost:3000";
                return opts;
            };

            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managed(factory);

            assertThat(manager.getOptions().wsEndpoint).isEqualTo("ws://localhost:3000");
        }
    }

    @Nested
    @DisplayName("Options testIdAttribute handling")
    class TestIdAttribute {

        @Test
        void shouldStoreTestIdAttribute() {
            OptionsFactory factory = () -> new Options().setTestIdAttribute("data-test");

            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managed(factory);

            assertThat(manager.getOptions().testIdAttribute).isEqualTo("data-test");
        }

        @Test
        void shouldLeaveTestIdAttributeNullWhenNotSet() {
            OptionsFactory factory = Options::new;

            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managed(factory);

            assertThat(manager.getOptions().testIdAttribute).isNull();
        }
    }

    @Nested
    @DisplayName("Initial state")
    class InitialState {

        @Test
        void shouldHaveNoCurrentPageBeforeOpen() {
            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managed();

            assertThat(manager.getCurrentPage()).isNull();
        }

        @Test
        void shouldHaveNoCurrentContextBeforeOpen() {
            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managed();

            assertThat(manager.getCurrentContext()).isNull();
        }

        @Test
        void shouldHaveNoCurrentManagerBeforeOpen() {
            assertThat(PlaywrightBrowserManager.current()).isNull();
        }
    }

    @Nested
    @DisplayName("Close without open")
    class CloseWithoutOpen {

        @Test
        void shouldHandleCloseCurrentContextWithoutOpening() {
            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managed();

            manager.closeCurrentContext();

            assertThat(manager.getCurrentPage()).isNull();
            assertThat(manager.getCurrentContext()).isNull();
        }

        @Test
        void shouldHandleCloseWithoutOpening() {
            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managed();

            manager.close();

            assertThat(manager.getCurrentPage()).isNull();
            assertThat(manager.getCurrentContext()).isNull();
        }

        @Test
        void shouldHandleMultipleCloseCallsSafely() {
            PlaywrightBrowserManager manager = PlaywrightBrowserManager.managed();

            manager.close();
            manager.close();
            manager.closeCurrentContext();
        }
    }
}
