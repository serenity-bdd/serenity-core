package net.serenitybdd.screenplay.playwright.abilities;

import com.google.common.eventbus.Subscribe;
import com.microsoft.playwright.*;
import net.serenitybdd.core.eventbus.Broadcaster;
import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.RefersToActor;
import net.serenitybdd.screenplay.events.*;
import net.thucydides.core.events.TestLifecycleEvents;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.PageObjectDependencyInjector;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SerenityWebdriverManager;
import org.openqa.selenium.WebDriver;

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

    /**
     * Keep tabs on which actor is associated with this ability, so we can manage start and end performance events
     */
    private Actor actor;

    Playwright playwright;

    BrowserType.LaunchOptions options;

    private Optional<String> browserType;

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

    protected BrowseTheWebWithPlaywright(EnvironmentVariables environmentVariables, BrowserType.LaunchOptions options) {
        this(environmentVariables, options, null);
    }

    protected BrowseTheWebWithPlaywright(EnvironmentVariables environmentVariables, BrowserType.LaunchOptions options, String browserType) {
        this.environmentVariables = environmentVariables;
        this.options = options;
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
        // TODO: Add the ability to allow options for a context
        if (currentContext == null) {
            currentContext = getBrowser().newContext();
        }
        return currentContext;
    }

    public Page getCurrentPage() {
        if (currentPage == null) {
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
        ;
        return OPEN_BROWSER.get(configuredBrowser()).apply(playwright).launch(options);
    }

    private String configuredBrowser() {
        return browserType.orElse(DEFAULT_BROWSER_TYPE);
    }

    private BrowserType.LaunchOptions launchOptionsDefinedIn(EnvironmentVariables environmentVariables) {
        if (options.args == null) {
            ARGS.asListOfStringsFrom(environmentVariables).ifPresent(options::setArgs);
        }
        if (options.channel == null) {
            BROWSER_CHANNEL.asBrowserChannelFrom(environmentVariables).ifPresent(options::setChannel);
        }
        if (options.chromiumSandbox == null) {
            CHROMIUM_SANDBOX.asBooleanFrom(environmentVariables).ifPresent(options::setChromiumSandbox);
        }
        if (options.devtools == null) {
            DEVTOOLS.asBooleanFrom(environmentVariables).ifPresent(options::setChromiumSandbox);
        }
        if (options.downloadsPath == null) {
            DOWNLOADS_PATH.asPathFrom(environmentVariables).ifPresent(options::setDownloadsPath);
        }
        if (options.env == null) {
            ENV.asJsonMapFrom(environmentVariables).ifPresent(options::setEnv);
        }
        if (options.executablePath == null) {
            EXECUTABLE_PATH.asPathFrom(environmentVariables).ifPresent(options::setExecutablePath);
        }
        if (options.handleSIGHUP == null) {
            HANDLE_SIGHUP.asBooleanFrom(environmentVariables).ifPresent(options::setHandleSIGHUP);
        }
        if (options.handleSIGINT == null) {
            HANDLE_SIGINT.asBooleanFrom(environmentVariables).ifPresent(options::setHandleSIGINT);
        }
        if (options.handleSIGTERM == null) {
            HANDLE_SIGTERM.asBooleanFrom(environmentVariables).ifPresent(options::setHandleSIGTERM);
        }
        if (options.headless == null || !options.headless) {
            HEADLESS.asBooleanFrom(environmentVariables).ifPresent(options::setHeadless);
        }
        if (options.ignoreAllDefaultArgs == null) {
            IGNORE_ALL_DEFAULT_APPS.asBooleanFrom(environmentVariables).ifPresent(options::setIgnoreAllDefaultArgs);
        }
        if (options.ignoreDefaultArgs == null) {
            IGNORE_DEFAULT_APPS.asListOfStringsFrom(environmentVariables).ifPresent(options::setIgnoreDefaultArgs);
        }
        if (options.proxy == null) {
            PROXY.asProxyFrom(environmentVariables).ifPresent(options::setProxy);
        }
        if (options.slowMo == null) {
            SLOW_MO.asDoubleFrom(environmentVariables).ifPresent(options::setSlowMo);
        }
        if (options.timeout == null) {
            TIMEOUT.asDoubleFrom(environmentVariables).ifPresent(options::setTimeout);
        }
        return options;
    }

    public <T extends Ability> T asActor(Actor actor) {
        this.actor = actor;
        return (T) this;
    }

    @Subscribe public void beginPerformance(ActorBeginsPerformanceEvent performanceEvent) {
        if (messageIsForThisActor(performanceEvent)) {
            System.out.println("BEGIN " + performanceEvent.getClass());
        }
    }

    @Subscribe
    public void endPerformance(ActorEndsPerformanceEvent performanceEvent) {
        if (messageIsForThisActor(performanceEvent)) {
            System.out.println("END " + performanceEvent.getClass());
        }
    }


    @Subscribe public void perform(ActorPerforms performAction) {
        if (messageIsForThisActor(performAction)) {
            System.out.println("Perform " + performAction.getPerformable());
        }
    }

    @Subscribe public void prepareQuestion(ActorAsksQuestion questionEvent) {
        if (messageIsForThisActor(questionEvent)) {
            System.out.println("Question " + questionEvent.getQuestion());
        }
    }

    /**
     * Shut down the Playwright instance and browser cleanly at the end of a Screenplay test.
     */
    @Subscribe
    public void testFinishes(TestLifecycleEvents.TestFinished testFinished) {
        if (playwright != null) {
            playwright.close();
            playwright = null;
        }
    }


    private boolean messageIsForThisActor(ActorPerformanceEvent event) {
        return event.getName().equals(actor.getName());
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

    public BrowseTheWebWithPlaywright withBrowserType(String browserType) {
        return new BrowseTheWebWithPlaywright(environmentVariables, options, browserType);
    }

    public BrowseTheWebWithPlaywright withHeadlessMode(Boolean headless) {
        return new BrowseTheWebWithPlaywright(environmentVariables, options.setHeadless(headless), browserType.orElse(null));
    }
}
