package net.serenitybdd.screenplay.playwright.network;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Intercept network requests and modify their behavior.
 *
 * <p>Playwright's route API allows you to intercept, modify, or mock network requests.
 * This is useful for testing error scenarios, mocking APIs, or blocking resources.</p>
 */
public class InterceptNetwork {

    private final String urlPattern;

    private InterceptNetwork(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    /**
     * Start intercepting requests matching the URL pattern.
     * Uses Playwright's glob pattern matching.
     */
    public static InterceptNetwork forUrl(String urlPattern) {
        return new InterceptNetwork(urlPattern);
    }

    /**
     * Abort matching requests (useful for blocking resources).
     */
    public Performable andAbort() {
        return new InterceptAndAbort(urlPattern);
    }

    /**
     * Respond with a custom body.
     */
    public InterceptAndFulfill andRespondWith(String body) {
        return new InterceptAndFulfill(urlPattern, body);
    }

    /**
     * Continue the request with an additional header.
     */
    public Performable andContinueWithHeader(String name, String value) {
        return new InterceptAndContinue(urlPattern, name, value);
    }
}

/**
 * Abort intercepted requests.
 */
class InterceptAndAbort implements Performable {
    private final String urlPattern;

    InterceptAndAbort(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    @Override
    @Step("{0} intercepts and aborts requests matching " + "#urlPattern")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        page.route(urlPattern, Route::abort);
    }
}

/**
 * Fulfill intercepted requests with a custom response.
 */
class InterceptAndFulfill implements Performable {
    private final String urlPattern;
    private final String body;
    private String contentType = "text/plain";
    private int status = 200;
    private final Map<String, String> headers = new HashMap<>();

    InterceptAndFulfill(String urlPattern, String body) {
        this.urlPattern = urlPattern;
        this.body = body;
    }

    /**
     * Set the content type of the response.
     */
    public InterceptAndFulfill withContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    /**
     * Set the HTTP status code.
     */
    public InterceptAndFulfill withStatus(int status) {
        this.status = status;
        return this;
    }

    /**
     * Add a response header.
     */
    public InterceptAndFulfill withHeader(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    @Override
    @Step("{0} intercepts and mocks response for " + "#urlPattern")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();

        page.route(urlPattern, route -> {
            Map<String, String> responseHeaders = new HashMap<>(headers);
            responseHeaders.put("Content-Type", contentType);

            route.fulfill(new Route.FulfillOptions()
                .setStatus(status)
                .setBody(body)
                .setHeaders(responseHeaders));
        });
    }
}

/**
 * Continue intercepted requests with modifications.
 */
class InterceptAndContinue implements Performable {
    private final String urlPattern;
    private final String headerName;
    private final String headerValue;

    InterceptAndContinue(String urlPattern, String headerName, String headerValue) {
        this.urlPattern = urlPattern;
        this.headerName = headerName;
        this.headerValue = headerValue;
    }

    @Override
    @Step("{0} intercepts and adds header to requests matching " + "#urlPattern")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();

        page.route(urlPattern, route -> {
            Map<String, String> headers = new HashMap<>(route.request().headers());
            headers.put(headerName, headerValue);
            route.resume(new Route.ResumeOptions().setHeaders(headers));
        });
    }
}
