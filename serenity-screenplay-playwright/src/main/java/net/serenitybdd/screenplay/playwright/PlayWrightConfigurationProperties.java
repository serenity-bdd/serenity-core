package net.serenitybdd.screenplay.playwright;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.microsoft.playwright.options.Proxy;
import net.thucydides.core.util.EnvironmentVariables;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public enum PlayWrightConfigurationProperties {

    BROWSER_TYPE("playwright.browsertype"),

    /**
     * Additional arguments to pass to the browser instance (see https://peter.sh/experiments/chromium-command-line-switches/).
     */
    ARGS("playwright.args"),

    /**
     * Browser distribution channel.
     */
    BROWSER_CHANNEL("playwright.channel"),

    /**
     * Enable Chromium sandboxing. Defaults to {@code false}.
     */
    CHROMIUM_SANDBOX("playwright.chromiumSandbox"),
    /**
     * **Chromium-only** Whether to auto-open a Developer Tools panel for each tab. If this option is {@code true}, the {@code headless}
     * option will be set {@code false}.
     */
    DEVTOOLS("playwright.devtools"),
    /**
     * If specified, accepted downloads are downloaded into this directory. Otherwise, temporary directory is created and is
     * deleted when browser is closed.
     */
    DOWNLOADS_PATH("playwright.downloadsPath"),
    /**
     * Specify environment variables that will be visible to the browser. Defaults to {@code process.env}.
     */
    ENV("playwright.env"),
    /**
     * Path to a browser executable to run instead of the bundled one. If {@code executablePath} is a relative path, then it is
     * resolved relative to the current working directory. Note that Playwright only works with the bundled Chromium, Firefox
     * or WebKit, use at your own risk.
     */
    EXECUTABLE_PATH("playwright.executablePath"),
    /**
     * Close the browser process on SIGHUP. Defaults to {@code true}.
     */
    HANDLE_SIGHUP("playwright.handleSIGHUP"),
    /**
     * Close the browser process on Ctrl-C. Defaults to {@code true}.
     */
    HANDLE_SIGINT("playwright.handleSIGINT"),
    /**
     * Close the browser process on SIGTERM. Defaults to {@code true}.
     */
    HANDLE_SIGTERM("playwright.handleSIGTERM"),
    /**
     * Whether to run browser in headless mode. More details for <a
     * href="https://developers.google.com/web/updates/2017/04/headless-chrome">Chromium</a> and <a
     * href="https://developer.mozilla.org/en-US/docs/Mozilla/Firefox/Headless_mode">Firefox</a>. Defaults to {@code true} unless the
     * {@code devtools} option is {@code true}.
     */
    HEADLESS("playwright.headless"),

    TRACING("playwright.tracing"),

    /**
     * If {@code true}, Playwright does not pass its own configurations args and only uses the ones from {@code args}. Dangerous option;
     * use with care. Defaults to {@code false}.
     */
    IGNORE_ALL_DEFAULT_APPS("playwright.ignoreAllDefaultArgs"),

    /**
     * If {@code true}, Playwright does not pass its own configurations args and only uses the ones from {@code args}. Dangerous option;
     * use with care.
     */
    IGNORE_DEFAULT_APPS("playwright.ignoreDefaultArgs"),

    /**
     * Slows down Playwright operations by the specified amount of milliseconds. Useful so that you can see what is going on.
     */
    SLOW_MO("playwright.slowMo"),

    /**
     * Maximum time in milliseconds to wait for the browser instance to start. Defaults to {@code 30000} (30 seconds). Pass {@code 0} to
     * disable timeout.
     */
    TIMEOUT("playwright.timeout"),

    /**
     * Network proxy settings.
     * You define the proxy settings using the following four properties (server, bypass, username, password)
     */
    PROXY("playwright.proxy"),

    PROXY_SERVER("playwright.proxy.server"),
    PROXY_BYPASS("playwright.proxy.bypass"),
    PROXY_USERNAME("playwright.proxy.username"),
    PROXY_PASSWORD("playwright.proxy.password");

    private final String property;

    PlayWrightConfigurationProperties(String property) {
        this.property = property;
    }

    public Optional<String> asStringFrom(EnvironmentVariables environmentVariables) {
        return environmentVariables.optionalProperty(property);
    }

    public Optional<Boolean> asBooleanFrom(EnvironmentVariables environmentVariables) {
        return environmentVariables.optionalProperty(property).map(Boolean::valueOf);
    }

    public Optional<Double> asDoubleFrom(EnvironmentVariables environmentVariables) {
        return environmentVariables.optionalProperty(property).map(Double::valueOf);
    }

    public Optional<Path> asPathFrom(EnvironmentVariables environmentVariables) {
        return environmentVariables.optionalProperty(property).map(Paths::get);
    }

    public Optional<Proxy> asProxyFrom(EnvironmentVariables environmentVariables) {
        if (environmentVariables.optionalProperty(PROXY_SERVER.property).isPresent()) {
            String server = environmentVariables.getProperty(PROXY_SERVER.property);

            final Proxy proxy = new Proxy(server);
            environmentVariables.optionalProperty(PROXY_USERNAME.property).ifPresent(
                proxy::setUsername
            );
            environmentVariables.optionalProperty(PROXY_PASSWORD.property).ifPresent(
                proxy::setPassword
            );
            environmentVariables.optionalProperty(PROXY_BYPASS.property).ifPresent(
                proxy::setBypass
            );
            return Optional.of(proxy);
        } else {
            return Optional.empty();
        }
    }

    public Optional<List<String>> asListOfStringsFrom(EnvironmentVariables environmentVariables) {
        if (environmentVariables.optionalProperty(property).isPresent()) {
            return Optional.of(stream(environmentVariables.getProperty(property)
                .split(","))
                .map(String::trim)
                .collect(Collectors.toList()));
        } else {
            return Optional.empty();
        }
    }

    public Optional<Map<String, String>> asJsonMapFrom(EnvironmentVariables environmentVariables) {
        if (environmentVariables.optionalProperty(property).isPresent()) {
            Gson gson = new Gson();
            Type mapOfStringsType = new TypeToken<Map<String, String>>() {
            }.getType();
            return Optional.of(gson.fromJson(environmentVariables.getProperty(property), mapOfStringsType));
        } else {
            return Optional.empty();
        }
    }

}
