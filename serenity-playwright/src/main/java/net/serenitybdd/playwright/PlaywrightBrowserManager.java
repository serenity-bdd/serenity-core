package net.serenitybdd.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.junit.Options;
import com.microsoft.playwright.junit.OptionsFactory;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Manages the Playwright browser lifecycle for use with any test framework.
 * <p>
 * This class handles the creation and cleanup of Playwright, Browser, BrowserContext,
 * and Page instances using thread-local storage for parallel test execution.
 * Pages are automatically registered with {@link PlaywrightSerenity} for screenshot
 * capture, and constructor injection via {@code @Steps} works automatically when
 * {@code serenity-playwright} is on the classpath.
 * </p>
 * <p>
 * The browser type is read from {@code playwright.browsertype} and headless mode from
 * {@code playwright.headless}. For additional launch options, use {@link #withOptions(BrowserType.LaunchOptions)}.
 * For full property-driven configuration, use the Screenplay {@code BrowseTheWebWithPlaywright} ability instead.
 * </p>
 *
 * <h3>Usage with Cucumber:</h3>
 * <pre>
 * public class PlaywrightHooks {
 *     private static final PlaywrightBrowserManager browser = PlaywrightBrowserManager.managed();
 *
 *     &#64;Before(order = 100)
 *     public void setUp() { browser.openNewPage(); }
 *
 *     &#64;After(order = 100)
 *     public void tearDown() { browser.closeCurrentContext(); }
 *
 *     &#64;AfterAll
 *     public static void shutdown() { browser.close(); }
 * }
 *
 * // In other hook classes, use current() to access the active manager:
 * public class TracingHooks {
 *     &#64;Before(order = 200)
 *     public void startTracing() {
 *         PlaywrightBrowserManager.current().getCurrentContext().tracing().start(...);
 *     }
 * }
 * </pre>
 *
 * <h3>Usage with JUnit 5 (without &#64;UsePlaywright):</h3>
 * <pre>
 * &#64;ExtendWith(SerenityJUnit5Extension.class)
 * class MyTest {
 *     private static final PlaywrightBrowserManager browser = PlaywrightBrowserManager.managed();
 *
 *     &#64;Steps SearchComponent search;
 *
 *     &#64;BeforeEach void setUp() { browser.openNewPage(); }
 *     &#64;AfterEach  void tearDown() { browser.closeCurrentContext(); }
 *     &#64;AfterAll   static void shutdown() { browser.close(); }
 * }
 * </pre>
 *
 * @see PlaywrightSerenity
 * @see PlaywrightPageProvider
 */
public class PlaywrightBrowserManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlaywrightBrowserManager.class);
    private static final String DEFAULT_BROWSER_TYPE = "chromium";

    private static final ThreadLocal<PlaywrightBrowserManager> CURRENT = new ThreadLocal<>();

    private static final Map<String, Function<Playwright, BrowserType>> BROWSER_TYPES = new HashMap<>();

    static {
        BROWSER_TYPES.put("chromium", Playwright::chromium);
        BROWSER_TYPES.put("firefox", Playwright::firefox);
        BROWSER_TYPES.put("webkit", Playwright::webkit);
    }

    private final ThreadLocal<Playwright> playwright = new ThreadLocal<>();
    private final ThreadLocal<Browser> browser = new ThreadLocal<>();
    private final ThreadLocal<BrowserContext> browserContext = new ThreadLocal<>();
    private final ThreadLocal<Page> page = new ThreadLocal<>();

    private final String browserType;
    private final BrowserType.LaunchOptions launchOptions;
    private final Browser.NewContextOptions contextOptions;
    private final EnvironmentVariables environmentVariables;

    /**
     * The full Playwright Options from an OptionsFactory, or null when using
     * the simpler factory methods. When present, this is the source of truth
     * for Playwright, Browser, and BrowserContext configuration.
     */
    private final Options options;

    private PlaywrightBrowserManager(String browserType,
                                     BrowserType.LaunchOptions launchOptions,
                                     Browser.NewContextOptions contextOptions,
                                     EnvironmentVariables environmentVariables) {
        this(browserType, launchOptions, contextOptions, environmentVariables, null);
    }

    private PlaywrightBrowserManager(String browserType,
                                     BrowserType.LaunchOptions launchOptions,
                                     Browser.NewContextOptions contextOptions,
                                     EnvironmentVariables environmentVariables,
                                     Options options) {
        this.browserType = browserType;
        this.launchOptions = launchOptions;
        this.contextOptions = contextOptions;
        this.environmentVariables = environmentVariables;
        this.options = options;
    }

    /**
     * Creates a new browser manager using configuration from Serenity properties.
     * <p>
     * Reads {@code playwright.browsertype} and {@code playwright.headless} from
     * Serenity properties. This is a factory method — each call creates a new instance.
     * Store it as a field and use {@link #current()} from other classes that need access.
     * </p>
     */
    public static PlaywrightBrowserManager managed() {
        EnvironmentVariables env = SystemEnvironmentVariables.currentEnvironmentVariables();
        String browserType = env.optionalProperty("playwright.browsertype").orElse(DEFAULT_BROWSER_TYPE);
        return new PlaywrightBrowserManager(browserType, new BrowserType.LaunchOptions(), null, env);
    }

    /**
     * Creates a browser manager using configuration from a Playwright {@link OptionsFactory}.
     * <p>
     * This applies the full set of options — browser type, launch options, context options,
     * {@code testIdAttribute}, {@code wsEndpoint}, etc. — just as {@code @UsePlaywright} does.
     * Serenity environment properties ({@code playwright.headless}, {@code playwright.browsertype})
     * are used as fallbacks when the corresponding option is not set in the factory.
     * </p>
     */
    public static PlaywrightBrowserManager managed(OptionsFactory optionsFactory) {
        Options options = optionsFactory.getOptions();
        EnvironmentVariables env = SystemEnvironmentVariables.currentEnvironmentVariables();

        String browserType = (options.browserName != null)
                ? options.browserName
                : env.optionalProperty("playwright.browsertype").orElse(DEFAULT_BROWSER_TYPE);

        return new PlaywrightBrowserManager(browserType, null, null, env, options);
    }

    /**
     * Creates a browser manager for a specific browser type.
     * {@code playwright.headless} is still read from Serenity properties.
     */
    public static PlaywrightBrowserManager managed(String browserType) {
        EnvironmentVariables env = SystemEnvironmentVariables.currentEnvironmentVariables();
        return new PlaywrightBrowserManager(browserType, new BrowserType.LaunchOptions(), null, env);
    }

    /**
     * Creates a browser manager with explicit launch options.
     * {@code playwright.headless} from Serenity properties is applied if not already set.
     */
    public static PlaywrightBrowserManager withOptions(BrowserType.LaunchOptions launchOptions) {
        EnvironmentVariables env = SystemEnvironmentVariables.currentEnvironmentVariables();
        String browserType = env.optionalProperty("playwright.browsertype").orElse(DEFAULT_BROWSER_TYPE);
        return new PlaywrightBrowserManager(browserType, launchOptions, null, env);
    }

    /**
     * Returns the active browser manager for the current thread.
     * <p>
     * The current manager is set automatically when {@link #openNewPage()} is called.
     * Use this in auxiliary hook classes (e.g. tracing fixtures) that need to access
     * the browser context without holding a direct reference to the manager instance.
     * </p>
     *
     * @return the active PlaywrightBrowserManager, or null if no page has been opened on this thread
     */
    public static PlaywrightBrowserManager current() {
        return CURRENT.get();
    }

    /**
     * Returns a copy of this manager with the specified context options applied to
     * new browser contexts.
     */
    public PlaywrightBrowserManager withContextOptions(Browser.NewContextOptions contextOptions) {
        return new PlaywrightBrowserManager(this.browserType, this.launchOptions, contextOptions, this.environmentVariables, this.options);
    }

    /**
     * Opens a new browser context and page, registering the page with
     * {@link PlaywrightSerenity} for screenshot capture and {@code @Steps} injection.
     * <p>
     * Call this in your {@code @Before} / {@code @BeforeEach} hook.
     * </p>
     *
     * @return the new Page instance
     */
    public Page openNewPage() {
        Browser currentBrowser = getOrCreateBrowser();

        Browser.NewContextOptions ctxOptions = resolveContextOptions();
        BrowserContext context = (ctxOptions != null) ? currentBrowser.newContext(ctxOptions) : currentBrowser.newContext();
        browserContext.set(context);

        Page newPage = context.newPage();
        page.set(newPage);
        PlaywrightSerenity.registerPage(newPage);
        CURRENT.set(this);
        return newPage;
    }

    /**
     * Returns the current page for this thread, or null if none is open.
     */
    public Page getCurrentPage() {
        return page.get();
    }

    /**
     * Returns the current browser context for this thread, or null if none is open.
     */
    public BrowserContext getCurrentContext() {
        return browserContext.get();
    }

    /**
     * Closes the current browser context and unregisters pages.
     * <p>
     * Call this in your {@code @After} / {@code @AfterEach} hook.
     * </p>
     */
    public void closeCurrentContext() {
        Page currentPage = page.get();
        if (currentPage != null) {
            PlaywrightSerenity.unregisterPage(currentPage);
            page.remove();
        }

        BrowserContext context = browserContext.get();
        if (context != null) {
            try {
                context.close();
            } catch (Exception e) {
                LOGGER.warn("Failed to close browser context", e);
            }
            browserContext.remove();
        }
    }

    /**
     * Closes the browser, Playwright instance, and all thread-local resources.
     * <p>
     * Call this in your {@code @AfterAll} hook or test suite teardown.
     * </p>
     */
    public void close() {
        closeCurrentContext();

        Browser currentBrowser = browser.get();
        if (currentBrowser != null) {
            try {
                currentBrowser.close();
            } catch (Exception e) {
                LOGGER.warn("Failed to close browser", e);
            }
            browser.remove();
        }

        Playwright currentPlaywright = playwright.get();
        if (currentPlaywright != null) {
            try {
                currentPlaywright.close();
            } catch (Exception e) {
                LOGGER.warn("Failed to close Playwright", e);
            }
            playwright.remove();
        }

        CURRENT.remove();
    }

    // ── Package-visible accessors for testing ────────────────────────────

    String getBrowserType() {
        return browserType;
    }

    Options getOptions() {
        return options;
    }

    // ── Private lifecycle methods ─────────────────────────────────────────

    private Browser getOrCreateBrowser() {
        Browser existingBrowser = browser.get();
        if (existingBrowser != null && existingBrowser.isConnected()) {
            return existingBrowser;
        }

        Playwright currentPlaywright = playwright.get();
        if (currentPlaywright == null) {
            currentPlaywright = createPlaywright();
            playwright.set(currentPlaywright);
        }

        if (!BROWSER_TYPES.containsKey(browserType)) {
            throw new IllegalArgumentException(
                    "Unsupported Playwright browser type: '" + browserType
                            + "'. Supported types: " + BROWSER_TYPES.keySet());
        }

        BrowserType resolvedBrowserType = BROWSER_TYPES.get(browserType).apply(currentPlaywright);
        Browser newBrowser = launchOrConnect(resolvedBrowserType);
        browser.set(newBrowser);
        return newBrowser;
    }

    private Playwright createPlaywright() {
        if (options != null) {
            return PlaywrightOptionsResolver.createPlaywright(options);
        }
        return Playwright.create();
    }

    private Browser launchOrConnect(BrowserType bt) {
        BrowserType.LaunchOptions resolved = resolveLaunchOptions();
        if (options != null) {
            return PlaywrightOptionsResolver.launchOrConnect(options, bt, resolved);
        }
        return bt.launch(resolved);
    }

    /**
     * Builds the effective launch options. When an {@code Options} object is present,
     * delegates to {@link PlaywrightOptionsResolver} for shorthand resolution,
     * then applies Serenity's {@code playwright.headless} property as a final fallback.
     */
    BrowserType.LaunchOptions resolveLaunchOptions() {
        BrowserType.LaunchOptions effective;

        if (options != null) {
            effective = PlaywrightOptionsResolver.resolveLaunchOptions(options);
        } else {
            effective = (launchOptions != null) ? launchOptions : new BrowserType.LaunchOptions();
        }

        // Serenity property fallback
        if (effective.headless == null) {
            environmentVariables.optionalProperty("playwright.headless")
                    .map(Boolean::valueOf).ifPresent(effective::setHeadless);
        }
        return effective;
    }

    /**
     * Builds the effective context options. When an {@code Options} object is present,
     * delegates to {@link PlaywrightOptionsResolver} for shorthand resolution.
     */
    Browser.NewContextOptions resolveContextOptions() {
        if (options != null) {
            return PlaywrightOptionsResolver.resolveContextOptions(options);
        }
        return contextOptions;
    }
}
