package net.serenitybdd.screenplay.playwright.abilities;

import com.google.common.eventbus.Subscribe;
import com.microsoft.playwright.*;
import com.microsoft.playwright.junit.Options;
import com.microsoft.playwright.junit.OptionsFactory;
import net.serenitybdd.core.eventbus.Broadcaster;
import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.serenitybdd.playwright.PlaywrightSerenity;
import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.HasTeardown;
import net.serenitybdd.screenplay.RefersToActor;
import net.serenitybdd.screenplay.events.*;
import net.serenitybdd.screenplay.playwright.Photographer;
import net.thucydides.core.events.TestLifecycleEvents;
import net.thucydides.core.model.screenshots.ScreenshotPermission;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.webdriver.capabilities.RemoteTestName;
import net.thucydides.model.domain.TakeScreenshots;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.screenshots.ScreenshotAndHtmlSource;
import net.thucydides.model.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.serenitybdd.screenplay.playwright.evidence.PlaywrightFailureEvidence;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static net.serenitybdd.screenplay.playwright.PlayWrightConfigurationProperties.BROWSER_TYPE;
import static net.serenitybdd.screenplay.playwright.PlayWrightConfigurationProperties.HEADLESS;
import static net.serenitybdd.screenplay.playwright.PlayWrightConfigurationProperties.TRACING;

/**
 * A Screenplay ability that wraps the Playwright Browser object.
 * <p>
 * The browser type is read from {@code playwright.browsertype} and headless mode from
 * {@code playwright.headless}. For additional launch options, use {@link #withOptions(BrowserType.LaunchOptions)}.
 * </p>
 */
public class BrowseTheWebWithPlaywright implements Ability, RefersToActor, HasTeardown {
    private static final Logger LOGGER = LoggerFactory.getLogger(BrowseTheWebWithPlaywright.class);

    /**
     * Keep tabs on which actor is associated with this ability, so we can manage start and end performance events
     */
    private Actor actor;
    private Photographer photographer;

    Playwright playwright;

    BrowserType.LaunchOptions launchOptions;
    Browser.NewContextOptions contextOptions;
    private static final String TRACES_PATH = "target/playwright/traces";
    boolean tracingEnabled;
    String traceName;

    private final Optional<String> browserType;
    private Options playwrightOptions;

    private static final String DEFAULT_BROWSER_TYPE = "chromium";

    /**
     * Tracks whether the Page is owned externally (e.g. provided by @UsePlaywright).
     * When true, tearDown() will not close the Page, Context, Browser, or Playwright
     * since the external owner is responsible for their lifecycle.
     */
    private boolean externallyManaged = false;

    /**
     * A Browser refers to an instance of Chromium, Firefox or WebKit.
     */
    private Browser browser;

    /**
     * Browser contexts represent individual sessions within the browser.
     * This field keeps track of the current browser context being used by an actor
     */
    private BrowserContext currentContext;

    /**
     * Page provides methods to interact with a single tab in a Browser
     */
    private Page currentPage;

    private final EnvironmentVariables environmentVariables;

    protected BrowseTheWebWithPlaywright(EnvironmentVariables environmentVariables) {
        this(environmentVariables,
            new BrowserType.LaunchOptions(),
            BROWSER_TYPE.asStringFrom(environmentVariables).orElse(null));
    }

    protected BrowseTheWebWithPlaywright(EnvironmentVariables environmentVariables, BrowserType.LaunchOptions launchOptions) {
        this(environmentVariables, launchOptions, BROWSER_TYPE.asStringFrom(environmentVariables).orElse(null));
    }

    protected BrowseTheWebWithPlaywright(EnvironmentVariables environmentVariables, BrowserType.LaunchOptions launchOptions, String browserType) {
        this(environmentVariables, launchOptions, null, browserType);
    }

    protected BrowseTheWebWithPlaywright(EnvironmentVariables environmentVariables, BrowserType.LaunchOptions launchOptions, Browser.NewContextOptions contextOptions, String browserType) {
        this.environmentVariables = environmentVariables;
        this.launchOptions = launchOptions;
        this.contextOptions = contextOptions;
        this.browserType = Optional.ofNullable(browserType);
        registerForEventNotification();
    }

    private void registerForEventNotification() {
        Broadcaster.getEventBus().register(this);
        TestLifecycleEvents.register(this);
    }

    public static BrowseTheWebWithPlaywright as(Actor actor) {
        if (actor.abilityTo(BrowseTheWebWithPlaywright.class) == null) {
            throw new ActorCannotUsePlaywrightException(actor.getName());
        }
        return actor.abilityTo(BrowseTheWebWithPlaywright.class).asActor(actor);
    }

    public Browser getBrowser() {
        if (browser == null) {
            if (currentPage != null) {
                browser = currentPage.context().browser();
            } else {
                browser = initialiseBrowser();
            }
        }
        return browser;
    }

    /**
     * A BrowserContext is an isolated incognito-alike session within a browser instance.
     */
    private BrowserContext getCurrentContext() {
        if (currentContext == null) {
            if (currentPage != null) {
                currentContext = currentPage.context();
            } else {
                Browser.NewContextOptions ctxOptions = resolveContextOptions();
                currentContext = getBrowser().newContext(ctxOptions);
            }
        }
        return currentContext;
    }

    private Browser.NewContextOptions resolveContextOptions() {
        if (playwrightOptions != null) {
            return net.serenitybdd.playwright.PlaywrightOptionsResolver.resolveContextOptions(playwrightOptions);
        }
        return contextOptions;
    }

    public Page getCurrentPage() {
        if (externallyManaged && currentPage != null) {
            return currentPage;
        }
        if (currentPage == null) {
            // Add tracing details to debug tests with trace viewer: https://playwright.dev/java/docs/trace-viewer
            tracingEnabled = TRACING.asBooleanFrom(environmentVariables).orElse(false);
            if (tracingEnabled) {
                Tracing.StartOptions tracingOptions = new Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true);

                Optional<String> guessedTestName;
                Optional<TestOutcome> latestOutcome = StepEventBus.getParallelEventBus().getBaseStepListener().latestTestOutcome();

                guessedTestName = latestOutcome.map(
                    testOutcome -> Optional.of(testOutcome.getStoryTitle() + " - " + testOutcome.getTitle())
                ).orElseGet(RemoteTestName::fromCurrentTest);

                guessedTestName.ifPresent(name -> {
                    traceName = name;
                    tracingOptions.setName(name);
                    tracingOptions.setTitle(name);
                });

                getCurrentContext().tracing().start(tracingOptions);
            }

            currentPage = getCurrentContext().newPage();

            // Register the page with PlaywrightSerenity for screenshot capture integration
            PlaywrightSerenity.registerPage(currentPage);
        }
        return currentPage;
    }

    private final static Map<String, Function<Playwright, BrowserType>> OPEN_BROWSER = new HashMap<>();

    static {
        OPEN_BROWSER.put("chromium", (Playwright::chromium));
        OPEN_BROWSER.put("webkit", (Playwright::webkit));
        OPEN_BROWSER.put("firefox", (Playwright::firefox));
    }

    /**
     * Create a new Playwright browser instance based on configuration options defined in the environment variables.
     * You can define the browser in the environment variables using playwright.browsertype
     */
    private Browser initialiseBrowser() {
        if (playwright == null) {
            playwright = (playwrightOptions != null)
                    ? net.serenitybdd.playwright.PlaywrightOptionsResolver.createPlaywright(playwrightOptions)
                    : Playwright.create();
        }
        if (!OPEN_BROWSER.containsKey(configuredBrowser())) {
            throw new InvalidPlaywrightBrowserType(configuredBrowser());
        }
        BrowserType bt = OPEN_BROWSER.get(configuredBrowser()).apply(playwright);
        BrowserType.LaunchOptions resolved = resolveLaunchOptions();
        if (playwrightOptions != null) {
            return net.serenitybdd.playwright.PlaywrightOptionsResolver.launchOrConnect(playwrightOptions, bt, resolved);
        }
        return bt.launch(resolved);
    }

    private String configuredBrowser() {
        return browserType.orElse(DEFAULT_BROWSER_TYPE);
    }

    /**
     * Builds the effective launch options. When {@code playwrightOptions} is present,
     * delegates to {@link net.serenitybdd.playwright.PlaywrightOptionsResolver} for
     * shorthand resolution. Otherwise the instance's own {@code launchOptions} field
     * is used. Serenity properties are applied as fallbacks.
     */
    private BrowserType.LaunchOptions resolveLaunchOptions() {
        BrowserType.LaunchOptions effective;
        if (playwrightOptions != null) {
            effective = net.serenitybdd.playwright.PlaywrightOptionsResolver.resolveLaunchOptions(playwrightOptions);
        } else {
            effective = launchOptions;
        }
        // Serenity property fallbacks
        if (effective.headless == null) {
            HEADLESS.asBooleanFrom(environmentVariables).ifPresent(effective::setHeadless);
        }
        if (effective.tracesDir == null && TRACING.asBooleanFrom(environmentVariables).orElse(false)) {
            effective.setTracesDir(Paths.get(TRACES_PATH));
        }
        return effective;
    }

    public Photographer getPhotographer() {
        if (photographer == null) {
            photographer = new Photographer();
        }
        return photographer;
    }

    public ScreenshotAndHtmlSource takeScreenShot() {
        BaseStepListener baseStepListener = StepEventBus.getParallelEventBus().getBaseStepListener();
        Page currentPage = getCurrentPage();

        try {
            Path outputDirectory = baseStepListener.getOutputDirectory().toPath();
            Path pageSourceFile = Files.createTempFile(outputDirectory, "pagesource", ".txt");
            Files.write(pageSourceFile, currentPage.content().getBytes(StandardCharsets.UTF_8));
            File screenshot = getPhotographer().takesAScreenshot(currentPage);

            return new ScreenshotAndHtmlSource(screenshot, pageSourceFile.toFile());
        } catch (IOException e) {
            throw new AssertionError("Failed to take Playwright screenshot", e);
        }
    }

    public void notifyScreenChange() {
        BaseStepListener baseStepListener = StepEventBus.getParallelEventBus().getBaseStepListener();
        ScreenshotPermission screenshots = new ScreenshotPermission(ConfiguredEnvironment.getConfiguration());
        // Take screenshot for after each UI action when SERENITY_TAKE_SCREENSHOTS is FOR_EACH_ACTION
        if (screenshots.areAllowed(TakeScreenshots.FOR_EACH_ACTION)) {
            ScreenshotAndHtmlSource screenshotAndHtmlSource = takeScreenShot();

            baseStepListener.getCurrentTestOutcome().currentStep().ifPresent(
                step -> step.addScreenshot(screenshotAndHtmlSource)
            );
        }
    }

    public <T extends Ability> T asActor(Actor actor) {
        this.actor = actor;
        return (T) this;
    }

    @Subscribe
    public void beginPerformance(ActorBeginsPerformanceEvent performanceEvent) {
        if (messageIsForThisActor(performanceEvent)) {
            LOGGER.debug("BEGIN " + performanceEvent.getClass());
        }
    }

    @Subscribe
    public void endPerformance(ActorEndsPerformanceEvent performanceEvent) {
        if (messageIsForThisActor(performanceEvent)) {
            LOGGER.debug("END " + performanceEvent.getClass());
        }
    }


    @Subscribe
    public void perform(ActorPerforms performAction) {
        if (messageIsForThisActor(performAction)) {
            LOGGER.debug("Perform " + performAction.getPerformable());
        }
    }

    @Subscribe
    public void prepareQuestion(ActorAsksQuestion questionEvent) {
        if (messageIsForThisActor(questionEvent)) {
            LOGGER.debug("Question " + questionEvent.getQuestion());
        }
    }

    /**
     * Shut down the Playwright instance and browser cleanly at the end of a Screenplay test.
     */
    @Subscribe
    public void testFinishes(TestLifecycleEvents.TestFinished testFinished) {
        // Take screenshot and capture evidence for failed test
        boolean hasPage = currentPage != null && !currentPage.isClosed();
        boolean hasPlaywrightOrExternal = playwright != null || externallyManaged;
        if (hasPlaywrightOrExternal && hasPage) {
            BaseStepListener baseStepListener = StepEventBus.getParallelEventBus().getBaseStepListener();

            if (baseStepListener.currentTestFailed()) {
                // Capture screenshot if allowed
                ScreenshotPermission screenshots = new ScreenshotPermission(ConfiguredEnvironment.getConfiguration());
                if (screenshots.areAllowed(TakeScreenshots.FOR_FAILURES)) {
                    ScreenshotAndHtmlSource screenshotAndHtmlSource = takeScreenShot();
                    baseStepListener.firstFailingStep().ifPresent(
                        step -> step.addScreenshot(screenshotAndHtmlSource)
                    );
                }

                // Capture additional failure evidence (console errors, failed network requests)
                if (actor != null) {
                    try {
                        PlaywrightFailureEvidence.captureAndAttach(actor, currentPage);
                    } catch (Exception e) {
                        LOGGER.debug("Failed to capture failure evidence: {}", e.getMessage());
                    }
                }
            }
        }

        // Perform cleanup
        tearDown();
    }

    /**
     * Clean up Playwright resources. This method is called by the Screenplay framework
     * when actor.wrapUp() is invoked, and also automatically at test completion.
     */
    @Override
    public void tearDown() {
        if (externallyManaged) {
            // Only unregister pages from PlaywrightSerenity — do NOT close
            // Page/Context/Browser/Playwright since the external owner manages their lifecycle.
            if (currentPage != null) {
                PlaywrightSerenity.unregisterPage(currentPage);
            }
            currentPage = null;
            currentContext = null;
            browser = null;
            return;
        }

        // Stop tracing before browser is closed
        if (currentContext != null && tracingEnabled) {
            try {
                currentContext.tracing().stop(
                    new Tracing.StopOptions().setPath(Paths.get(String.format("%s/%s.zip", TRACES_PATH, traceName)))
                );
            } catch (Exception e) {
                LOGGER.debug("Failed to stop tracing: {}", e.getMessage());
            }
        }

        // Unregister and close all pages in the context
        if (currentContext != null) {
            for (Page page : currentContext.pages()) {
                PlaywrightSerenity.unregisterPage(page);
                try {
                    if (!page.isClosed()) {
                        page.close();
                    }
                } catch (Exception e) {
                    LOGGER.debug("Failed to close page: {}", e.getMessage());
                }
            }
        }
        currentPage = null;

        // Close the browser context
        if (currentContext != null) {
            try {
                currentContext.close();
            } catch (Exception e) {
                LOGGER.debug("Failed to close browser context: {}", e.getMessage());
            }
            currentContext = null;
        }

        // Close the browser
        if (browser != null) {
            try {
                browser.close();
            } catch (Exception e) {
                LOGGER.debug("Failed to close browser: {}", e.getMessage());
            }
            browser = null;
        }

        // Close Playwright
        if (playwright != null) {
            try {
                playwright.close();
            } catch (Exception e) {
                LOGGER.debug("Failed to close Playwright: {}", e.getMessage());
            }
            playwright = null;
        }
    }

    private boolean messageIsForThisActor(ActorPerformanceEvent event) {
        return (actor != null) && event.getName().equals(actor.getName());
    }

    /**
     * Create a new Playwright ability using default configuration values.
     */
    public static BrowseTheWebWithPlaywright usingTheDefaultConfiguration() {
        return new BrowseTheWebWithPlaywright(SystemEnvironmentVariables.currentEnvironmentVariables());
    }

    public static BrowseTheWebWithPlaywright withOptions(BrowserType.LaunchOptions options) {
        return new BrowseTheWebWithPlaywright(SystemEnvironmentVariables.currentEnvironmentVariables(), options);
    }

    /**
     * Create a new Playwright ability using configuration from a Playwright {@link OptionsFactory}.
     * <p>
     * This allows reusing the same {@code OptionsFactory} that you use with {@code @UsePlaywright}
     * in Screenplay tests.
     * </p>
     */
    public static BrowseTheWebWithPlaywright withOptions(OptionsFactory optionsFactory) {
        Options options = optionsFactory.getOptions();
        EnvironmentVariables env = SystemEnvironmentVariables.currentEnvironmentVariables();

        String browserType = (options.browserName != null)
                ? options.browserName
                : BROWSER_TYPE.asStringFrom(env).orElse(null);

        BrowseTheWebWithPlaywright ability = new BrowseTheWebWithPlaywright(
                env, new BrowserType.LaunchOptions(), null, browserType);
        ability.playwrightOptions = options;
        return ability;
    }

    public BrowseTheWebWithPlaywright withContextOptions(Browser.NewContextOptions contextOptions) {
        return new BrowseTheWebWithPlaywright(SystemEnvironmentVariables.currentEnvironmentVariables(), launchOptions,
            contextOptions, browserType.orElse(null));
    }

    public BrowseTheWebWithPlaywright withBrowserType(String browserType) {
        return new BrowseTheWebWithPlaywright(environmentVariables, launchOptions, browserType);
    }

    public BrowseTheWebWithPlaywright withHeadlessMode(Boolean headless) {
        return new BrowseTheWebWithPlaywright(environmentVariables, launchOptions.setHeadless(headless), browserType.orElse(null));
    }

    /**
     * Create a lightweight BrowseTheWebWithPlaywright ability that wraps an externally-provided Page.
     * <p>
     * Use this when Playwright manages its own lifecycle externally (e.g. via {@code @UsePlaywright})
     * and you want Screenplay actors to reuse the existing browser session instead of creating a new one.
     * </p>
     * <p>
     * The returned ability will NOT close the Page, BrowserContext, Browser, or Playwright instance
     * on teardown, since those are owned by the external provider.
     * </p>
     *
     * @param page the externally-managed Playwright Page to wrap
     * @return a BrowseTheWebWithPlaywright ability backed by the given page
     */
    public static BrowseTheWebWithPlaywright withPage(Page page) {
        BrowseTheWebWithPlaywright ability = new BrowseTheWebWithPlaywright(
            SystemEnvironmentVariables.currentEnvironmentVariables()
        );
        ability.externallyManaged = true;
        ability.currentPage = page;
        ability.currentContext = page.context();
        ability.browser = page.context().browser();
        PlaywrightSerenity.registerPage(page);
        return ability;
    }

    /**
     * Get all pages (tabs) in the current browser context.
     */
    public java.util.List<Page> getAllPages() {
        return getCurrentContext().pages();
    }

    /**
     * Switch to a different page by index.
     * Note: All pages in the context remain registered with PlaywrightSerenity
     * since they are all valid pages that can take screenshots.
     */
    public void switchToPage(int index) {
        java.util.List<Page> pages = getAllPages();
        if (index < 0 || index >= pages.size()) {
            throw new IllegalArgumentException("Page index " + index + " is out of bounds. Available pages: " + pages.size());
        }
        currentPage = pages.get(index);
    }

    /**
     * Switch to the page containing the given title.
     */
    public void switchToPageWithTitle(String title) {
        for (Page page : getAllPages()) {
            if (page.title().contains(title)) {
                currentPage = page;
                return;
            }
        }
        throw new IllegalArgumentException("No page found with title containing: " + title);
    }

    /**
     * Switch to the page with the given URL substring.
     */
    public void switchToPageWithUrl(String urlSubstring) {
        for (Page page : getAllPages()) {
            if (page.url().contains(urlSubstring)) {
                currentPage = page;
                return;
            }
        }
        throw new IllegalArgumentException("No page found with URL containing: " + urlSubstring);
    }

    /**
     * Open a new page (tab) in the current context.
     */
    public Page openNewPage() {
        Page newPage = getCurrentContext().newPage();
        PlaywrightSerenity.registerPage(newPage);
        currentPage = newPage;
        return newPage;
    }

    /**
     * Close the current page and switch to the previous one if available.
     */
    public void closeCurrentPage() {
        if (currentPage != null && !currentPage.isClosed()) {
            PlaywrightSerenity.unregisterPage(currentPage);
            currentPage.close();
        }
        java.util.List<Page> remainingPages = getAllPages();
        if (!remainingPages.isEmpty()) {
            currentPage = remainingPages.get(remainingPages.size() - 1);
        } else {
            currentPage = null;
        }
    }

    /**
     * Get all cookies from the current context.
     */
    public java.util.List<com.microsoft.playwright.options.Cookie> getCookies() {
        return getCurrentContext().cookies();
    }

    /**
     * Add a cookie to the current context.
     */
    public void addCookie(com.microsoft.playwright.options.Cookie cookie) {
        getCurrentContext().addCookies(java.util.Collections.singletonList(cookie));
    }

    /**
     * Add multiple cookies to the current context.
     */
    public void addCookies(java.util.List<com.microsoft.playwright.options.Cookie> cookies) {
        getCurrentContext().addCookies(cookies);
    }

    /**
     * Clear all cookies from the current context.
     */
    public void clearCookies() {
        getCurrentContext().clearCookies();
    }

    /**
     * Set a restored browser context and page.
     * Used internally by RestoreSessionState to apply saved session state.
     *
     * @param context The new browser context with restored state
     * @param page The new page in the restored context
     */
    public void setRestoredContext(BrowserContext context, Page page) {
        this.currentContext = context;
        this.currentPage = page;
    }

    /**
     * Get the APIRequestContext for making API calls within the browser session.
     * This allows API testing with the same cookies and authentication as the browser.
     *
     * @return The APIRequestContext for the current browser context
     */
    public com.microsoft.playwright.APIRequestContext getAPIRequestContext() {
        return getCurrentContext().request();
    }
}
