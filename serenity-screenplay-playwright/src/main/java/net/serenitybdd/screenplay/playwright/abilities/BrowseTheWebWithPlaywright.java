package net.serenitybdd.screenplay.playwright.abilities;

import com.google.common.eventbus.Subscribe;
import com.microsoft.playwright.*;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.serenitybdd.core.eventbus.Broadcaster;
import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.RefersToActor;
import net.serenitybdd.screenplay.events.*;
import net.serenitybdd.screenplay.playwright.Photographer;
import net.thucydides.core.events.TestLifecycleEvents;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TakeScreenshots;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.screenshots.ScreenshotPermission;
import net.thucydides.core.screenshots.ScreenshotAndHtmlSource;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.capabilities.RemoteTestName;
import org.assertj.core.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import static net.serenitybdd.screenplay.playwright.PlayWrightConfigurationProperties.*;

/**
 * This ability wraps the Playwright Browser object.
 * The following options can be used to configure the Chrome instance:
 * - webdriver.chrome.binary: path to the chrome binary
 * -
 */
public class BrowseTheWebWithPlaywright implements Ability, RefersToActor {
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

    private static final String DEFAULT_BROWSER_TYPE = "chromium";

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

    private EnvironmentVariables environmentVariables;

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
            browser = initialiseBrowser();
        }
        return browser;
    }

    /**
     * A BrowserContext is an isolated incognito-alike session within a browser instance.
     */
    private BrowserContext getCurrentContext() {
        if (currentContext == null) {
            currentContext = getBrowser().newContext(contextOptions);
        }
        return currentContext;
    }

    public Page getCurrentPage() {
        if (currentPage == null) {
            // Add tracing details to debug tests with trace viewer: https://playwright.dev/java/docs/trace-viewer
            tracingEnabled = TRACING.asBooleanFrom(environmentVariables).orElse(false);
            if (tracingEnabled) {
                Tracing.StartOptions tracingOptions = new Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true);

                Optional<String> guessedTestName;
                Optional<TestOutcome> latestOutcome = StepEventBus.getEventBus().getBaseStepListener().latestTestOutcome();

                guessedTestName = latestOutcome.map(
                    testOutcome -> Optional.of(testOutcome.getStoryTitle() + ": " + testOutcome.getTitle())
                ).orElseGet(RemoteTestName::fromCurrentTest);

                guessedTestName.ifPresent(name -> {
                    traceName = name;
                    tracingOptions.setName(name);
                    tracingOptions.setTitle(name);
                });

                getCurrentContext().tracing().start(tracingOptions);
            }

            currentPage = getCurrentContext().newPage();
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
            playwright = Playwright.create();
        }
        final BrowserType.LaunchOptions options = launchOptionsDefinedIn(environmentVariables);
        if (!OPEN_BROWSER.containsKey(configuredBrowser())) {
            throw new InvalidPlaywrightBrowserType(configuredBrowser());
        }
        return OPEN_BROWSER.get(configuredBrowser()).apply(playwright).launch(options);
    }

    private String configuredBrowser() {
        return browserType.orElse(DEFAULT_BROWSER_TYPE);
    }

    private BrowserType.LaunchOptions launchOptionsDefinedIn(EnvironmentVariables environmentVariables) {
        if (launchOptions.args == null) {
            ARGS.asListOfStringsFrom(environmentVariables).ifPresent(launchOptions::setArgs);
        }
        if (launchOptions.channel == null) {
            BROWSER_CHANNEL.asStringFrom(environmentVariables).ifPresent(launchOptions::setChannel);
        }
        if (launchOptions.chromiumSandbox == null) {
            CHROMIUM_SANDBOX.asBooleanFrom(environmentVariables).ifPresent(launchOptions::setChromiumSandbox);
        }
        if (launchOptions.devtools == null) {
            DEVTOOLS.asBooleanFrom(environmentVariables).ifPresent(launchOptions::setChromiumSandbox);
        }
        if (launchOptions.downloadsPath == null) {
            DOWNLOADS_PATH.asPathFrom(environmentVariables).ifPresent(launchOptions::setDownloadsPath);
        }
        if (launchOptions.env == null) {
            ENV.asJsonMapFrom(environmentVariables).ifPresent(launchOptions::setEnv);
        }
        if (launchOptions.executablePath == null) {
            EXECUTABLE_PATH.asPathFrom(environmentVariables).ifPresent(launchOptions::setExecutablePath);
        }
        if (launchOptions.handleSIGHUP == null) {
            HANDLE_SIGHUP.asBooleanFrom(environmentVariables).ifPresent(launchOptions::setHandleSIGHUP);
        }
        if (launchOptions.handleSIGINT == null) {
            HANDLE_SIGINT.asBooleanFrom(environmentVariables).ifPresent(launchOptions::setHandleSIGINT);
        }
        if (launchOptions.handleSIGTERM == null) {
            HANDLE_SIGTERM.asBooleanFrom(environmentVariables).ifPresent(launchOptions::setHandleSIGTERM);
        }
        if (launchOptions.headless == null || !launchOptions.headless) {
            HEADLESS.asBooleanFrom(environmentVariables).ifPresent(launchOptions::setHeadless);
        }
        if (launchOptions.ignoreAllDefaultArgs == null) {
            IGNORE_ALL_DEFAULT_APPS.asBooleanFrom(environmentVariables).ifPresent(launchOptions::setIgnoreAllDefaultArgs);
        }
        if (launchOptions.ignoreDefaultArgs == null) {
            IGNORE_DEFAULT_APPS.asListOfStringsFrom(environmentVariables).ifPresent(launchOptions::setIgnoreDefaultArgs);
        }
        if (launchOptions.proxy == null) {
            PROXY.asProxyFrom(environmentVariables).ifPresent(launchOptions::setProxy);
        }
        if (launchOptions.slowMo == null) {
            SLOW_MO.asDoubleFrom(environmentVariables).ifPresent(launchOptions::setSlowMo);
        }
        if (launchOptions.timeout == null) {
            TIMEOUT.asDoubleFrom(environmentVariables).ifPresent(launchOptions::setTimeout);
        }
        if (launchOptions.tracesDir == null && TRACING.asBooleanFrom(environmentVariables).isPresent()) {
            launchOptions.setTracesDir(Paths.get(TRACES_PATH));
        }
        return launchOptions;
    }

    public Photographer getPhotographer() {
        if (photographer == null) {
            photographer = new Photographer();
        }
        return photographer;
    }

    public ScreenshotAndHtmlSource takeScreenShot() {
        BaseStepListener baseStepListener = StepEventBus.getEventBus().getBaseStepListener();
        Page currentPage = getCurrentPage();

        try {
            Path outputDirectory = baseStepListener.getOutputDirectory().toPath();
            Path pageSourceFile = Files.createTempFile(outputDirectory, "pagesource", ".txt");
            Files.write(pageSourceFile, currentPage.content().getBytes(StandardCharsets.UTF_8));
            File screenshot = getPhotographer().takesAScreenshot(currentPage);

            return new ScreenshotAndHtmlSource(screenshot, pageSourceFile.toFile());
        } catch (IOException e) {
            Assertions.fail("Failed to take Playwright screenshot", e);
            return null;
        }
    }

    public void notifyScreenChange() {
        BaseStepListener baseStepListener = StepEventBus.getEventBus().getBaseStepListener();
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
        // Stop tracing before browser is closed
        if (currentContext != null && tracingEnabled) {
            currentContext.tracing().stop(
                new Tracing.StopOptions().setPath(Paths.get(String.format("%s/%s.zip", TRACES_PATH, traceName)))
            );
        }
        if (playwright != null) {
            BaseStepListener baseStepListener = StepEventBus.getEventBus().getBaseStepListener();
            ScreenshotPermission screenshots = new ScreenshotPermission(ConfiguredEnvironment.getConfiguration());
            // Take screenshot for failed test when SERENITY_TAKE_SCREENSHOTS is FOR_FAILURES
            if (baseStepListener.currentTestFailed() && screenshots.areAllowed(TakeScreenshots.FOR_FAILURES)) {
                ScreenshotAndHtmlSource screenshotAndHtmlSource = takeScreenShot();

                baseStepListener.firstFailingStep().ifPresent(
                    step -> step.addScreenshot(screenshotAndHtmlSource)
                );
            }

            playwright.close();
            currentPage = null;
            currentContext = null;
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
        return new BrowseTheWebWithPlaywright(Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }

    public static BrowseTheWebWithPlaywright withOptions(BrowserType.LaunchOptions options) {
        return new BrowseTheWebWithPlaywright(Injectors.getInjector().getInstance(EnvironmentVariables.class), options);
    }

    public BrowseTheWebWithPlaywright withContextOptions(Browser.NewContextOptions contextOptions) {
        return new BrowseTheWebWithPlaywright(Injectors.getInjector().getInstance(EnvironmentVariables.class), launchOptions,
            contextOptions, browserType.orElse(null));
    }

    public BrowseTheWebWithPlaywright withBrowserType(String browserType) {
        return new BrowseTheWebWithPlaywright(environmentVariables, launchOptions, browserType);
    }

    public BrowseTheWebWithPlaywright withHeadlessMode(Boolean headless) {
        return new BrowseTheWebWithPlaywright(environmentVariables, launchOptions.setHeadless(headless), browserType.orElse(null));
    }
}
