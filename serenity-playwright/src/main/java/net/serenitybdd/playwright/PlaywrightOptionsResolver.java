package net.serenitybdd.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.junit.Options;

/**
 * Stateless utility that translates a Playwright {@link Options} object into the
 * concrete Playwright/Browser/Context instances.
 * <p>
 * Both {@link PlaywrightBrowserManager} and the Screenplay
 * {@code BrowseTheWebWithPlaywright} ability delegate here so the resolution
 * logic lives in one place.
 * </p>
 * <p>
 * <b>Mutation note:</b> {@link #resolveLaunchOptions} and
 * {@link #resolveContextOptions} apply shorthand fields ({@code headless},
 * {@code channel}, {@code baseUrl}, {@code ignoreHTTPSErrors}) directly onto
 * the nested objects inside {@code Options}. This matches how Playwright's own
 * JUnit extension behaves. Callers that need isolation should obtain a fresh
 * {@code Options} instance per invocation (e.g. via {@code OptionsFactory.getOptions()}).
 * </p>
 */
public class PlaywrightOptionsResolver {

    private PlaywrightOptionsResolver() {
        // utility class
    }

    /**
     * Resolve the browser type name from Options, falling back to the given default.
     *
     * @param options  the Playwright Options (may be null)
     * @param fallback the default browser type when Options doesn't specify one
     * @return a non-null browser type name
     */
    public static String resolveBrowserType(Options options, String fallback) {
        if (options != null && options.browserName != null) {
            return options.browserName;
        }
        return fallback;
    }

    /**
     * Build the effective {@link BrowserType.LaunchOptions} from the given Options.
     * <p>
     * When {@code options.launchOptions} is present it is used as the base;
     * otherwise a fresh instance is created. Shorthand fields {@code headless}
     * and {@code channel} are then applied on top.
     * </p>
     *
     * @param options the Playwright Options (must not be null)
     * @return resolved launch options (never null)
     */
    public static BrowserType.LaunchOptions resolveLaunchOptions(Options options) {
        BrowserType.LaunchOptions effective = (options.launchOptions != null)
                ? options.launchOptions
                : new BrowserType.LaunchOptions();
        if (options.headless != null) {
            effective.setHeadless(options.headless);
        }
        if (options.channel != null) {
            effective.setChannel(options.channel);
        }
        return effective;
    }

    /**
     * Build the effective {@link Browser.NewContextOptions} from the given Options.
     * <p>
     * When {@code options.contextOptions} is present it is used as the base.
     * Shorthand fields {@code baseUrl} and {@code ignoreHTTPSErrors} are applied
     * on top when set. Returns {@code null} when neither the base nor any
     * shorthand is specified.
     * </p>
     *
     * @param options the Playwright Options (must not be null)
     * @return resolved context options, or null if nothing is configured
     */
    public static Browser.NewContextOptions resolveContextOptions(Options options) {
        Browser.NewContextOptions effective = options.contextOptions;

        boolean hasOverrides = options.baseUrl != null || options.ignoreHTTPSErrors != null;
        if (hasOverrides) {
            if (effective == null) {
                effective = new Browser.NewContextOptions();
            }
            if (options.baseUrl != null) {
                effective.setBaseURL(options.baseUrl);
            }
            if (options.ignoreHTTPSErrors != null) {
                effective.setIgnoreHTTPSErrors(options.ignoreHTTPSErrors);
            }
        }
        return effective;
    }

    /**
     * Create a {@link Playwright} instance, applying
     * {@code playwrightCreateOptions} and {@code testIdAttribute} from Options.
     *
     * @param options the Playwright Options (must not be null)
     * @return a new Playwright instance
     */
    public static Playwright createPlaywright(Options options) {
        Playwright pw = (options.playwrightCreateOptions != null)
                ? Playwright.create(options.playwrightCreateOptions)
                : Playwright.create();

        if (options.testIdAttribute != null) {
            pw.selectors().setTestIdAttribute(options.testIdAttribute);
        }
        return pw;
    }

    /**
     * Launch a browser or connect to a remote endpoint based on the Options.
     * <p>
     * When {@code options.wsEndpoint} is set and non-empty, a WebSocket
     * connection is established using {@code options.connectOptions}.
     * Otherwise the browser is launched locally using the given launch options.
     * </p>
     *
     * @param options       the Playwright Options (must not be null)
     * @param browserType   the resolved BrowserType to launch/connect
     * @param launchOptions the resolved launch options (used when not connecting)
     * @return a connected or launched Browser
     */
    public static Browser launchOrConnect(Options options, BrowserType browserType,
                                   BrowserType.LaunchOptions launchOptions) {
        if (options.wsEndpoint != null && !options.wsEndpoint.isEmpty()) {
            return browserType.connect(options.wsEndpoint, options.connectOptions);
        }
        return browserType.launch(launchOptions);
    }
}
