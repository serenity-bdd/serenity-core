package net.serenitybdd.screenplay.playwright.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    /**
     * Handle requests with a custom route handler.
     * Provides full control over the request/response.
     *
     * <p>Example:</p>
     * <pre>
     *     InterceptNetwork.forUrl("**\/api/**").andHandle(route -> {
     *         if (route.request().method().equals("POST")) {
     *             route.fulfill(new Route.FulfillOptions().setBody("{}"));
     *         } else {
     *             route.resume();
     *         }
     *     });
     * </pre>
     */
    public Performable andHandle(Consumer<Route> handler) {
        return new InterceptAndHandle(urlPattern, handler);
    }

    /**
     * Respond with a JSON body (automatically serialized and content-type set).
     */
    public InterceptAndFulfillJson andRespondWithJson(Object jsonObject) {
        return new InterceptAndFulfillJson(urlPattern, jsonObject);
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

/**
 * Handle intercepted requests with a custom handler.
 */
class InterceptAndHandle implements Performable {
    private final String urlPattern;
    private final Consumer<Route> handler;

    InterceptAndHandle(String urlPattern, Consumer<Route> handler) {
        this.urlPattern = urlPattern;
        this.handler = handler;
    }

    @Override
    @Step("{0} intercepts requests matching #urlPattern with custom handler")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        page.route(urlPattern, handler);
    }
}

/**
 * Fulfill intercepted requests with a JSON response.
 */
class InterceptAndFulfillJson implements Performable {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final String urlPattern;
    private final Object jsonObject;
    private int status = 200;
    private final Map<String, String> headers = new HashMap<>();

    InterceptAndFulfillJson(String urlPattern, Object jsonObject) {
        this.urlPattern = urlPattern;
        this.jsonObject = jsonObject;
    }

    /**
     * Set the HTTP status code.
     */
    public InterceptAndFulfillJson withStatus(int status) {
        this.status = status;
        return this;
    }

    /**
     * Add a response header.
     */
    public InterceptAndFulfillJson withHeader(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    @Override
    @Step("{0} intercepts and mocks JSON response for #urlPattern")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();

        page.route(urlPattern, route -> {
            try {
                String jsonBody = objectMapper.writeValueAsString(jsonObject);
                Map<String, String> responseHeaders = new HashMap<>(headers);
                responseHeaders.put("Content-Type", "application/json");

                route.fulfill(new Route.FulfillOptions()
                    .setStatus(status)
                    .setBody(jsonBody)
                    .setHeaders(responseHeaders));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize JSON response", e);
            }
        });
    }
}
