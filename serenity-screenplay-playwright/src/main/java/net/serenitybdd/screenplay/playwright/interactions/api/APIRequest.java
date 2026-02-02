package net.serenitybdd.screenplay.playwright.interactions.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.model.rest.RestMethod;
import net.serenitybdd.model.rest.RestQuery;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.thucydides.core.steps.session.TestSession;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static net.thucydides.core.steps.StepEventBus.getEventBus;

/**
 * Make API requests within the browser session context.
 * This allows API testing with the same cookies and authentication as the browser,
 * enabling hybrid UI+API test scenarios.
 *
 * <p>The API requests share the browser's session, so if you've logged in via the UI,
 * API calls will automatically include authentication cookies.</p>
 *
 * <p>Sample usage:</p>
 * <pre>
 *     // Simple GET request
 *     actor.attemptsTo(APIRequest.get("/api/users"));
 *
 *     // POST with JSON body
 *     actor.attemptsTo(
 *         APIRequest.post("/api/users")
 *             .withJsonBody(Map.of("name", "John", "email", "john@example.com"))
 *     );
 *
 *     // Request with headers
 *     actor.attemptsTo(
 *         APIRequest.get("/api/protected")
 *             .withHeader("X-Custom-Header", "value")
 *     );
 *
 *     // Query the response
 *     int status = actor.asksFor(LastAPIResponse.statusCode());
 *     String body = actor.asksFor(LastAPIResponse.body());
 *     Map&lt;String, Object&gt; json = actor.asksFor(LastAPIResponse.jsonBody());
 * </pre>
 *
 * @see net.serenitybdd.screenplay.playwright.questions.api.LastAPIResponse
 */
public class APIRequest implements Performable {

    static final String LAST_API_RESPONSE_KEY = "playwright.lastAPIResponse";

    private final String method;
    private final String url;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> queryParams = new HashMap<>();
    private Object body;
    private String contentType;
    private Integer timeout;
    private Boolean failOnStatusCode;

    private APIRequest(String method, String url) {
        this.method = method;
        this.url = url;
    }

    // Factory methods for common HTTP methods

    /**
     * Create a GET request.
     *
     * @param url The URL or path to request
     */
    public static APIRequest get(String url) {
        return new APIRequest("GET", url);
    }

    /**
     * Create a POST request.
     *
     * @param url The URL or path to request
     */
    public static APIRequest post(String url) {
        return new APIRequest("POST", url);
    }

    /**
     * Create a PUT request.
     *
     * @param url The URL or path to request
     */
    public static APIRequest put(String url) {
        return new APIRequest("PUT", url);
    }

    /**
     * Create a PATCH request.
     *
     * @param url The URL or path to request
     */
    public static APIRequest patch(String url) {
        return new APIRequest("PATCH", url);
    }

    /**
     * Create a DELETE request.
     *
     * @param url The URL or path to request
     */
    public static APIRequest delete(String url) {
        return new APIRequest("DELETE", url);
    }

    /**
     * Create a HEAD request.
     *
     * @param url The URL or path to request
     */
    public static APIRequest head(String url) {
        return new APIRequest("HEAD", url);
    }

    /**
     * Create a request with a custom HTTP method.
     *
     * @param method The HTTP method
     * @param url The URL or path to request
     */
    public static APIRequest to(String method, String url) {
        return new APIRequest(method.toUpperCase(), url);
    }

    // Builder methods

    /**
     * Add a header to the request.
     *
     * @param name Header name
     * @param value Header value
     */
    public APIRequest withHeader(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    /**
     * Add multiple headers to the request.
     *
     * @param headers Map of header names to values
     */
    public APIRequest withHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    /**
     * Add a query parameter to the request URL.
     *
     * @param name Parameter name
     * @param value Parameter value
     */
    public APIRequest withQueryParam(String name, String value) {
        this.queryParams.put(name, value);
        return this;
    }

    /**
     * Add multiple query parameters to the request URL.
     *
     * @param params Map of parameter names to values
     */
    public APIRequest withQueryParams(Map<String, String> params) {
        this.queryParams.putAll(params);
        return this;
    }

    /**
     * Set the request body as a JSON object.
     * Automatically sets Content-Type to application/json.
     *
     * @param jsonBody Object to serialize as JSON (Map, List, or POJO)
     */
    public APIRequest withJsonBody(Object jsonBody) {
        this.body = jsonBody;
        this.contentType = "application/json";
        return this;
    }

    /**
     * Set the request body as a string.
     *
     * @param body The request body
     */
    public APIRequest withBody(String body) {
        this.body = body;
        return this;
    }

    /**
     * Set the request body as bytes.
     *
     * @param body The request body
     */
    public APIRequest withBody(byte[] body) {
        this.body = body;
        return this;
    }

    /**
     * Set the Content-Type header.
     *
     * @param contentType The content type (e.g., "application/xml")
     */
    public APIRequest withContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    /**
     * Set the request timeout in milliseconds.
     *
     * @param timeoutMs Timeout in milliseconds
     */
    public APIRequest withTimeout(int timeoutMs) {
        this.timeout = timeoutMs;
        return this;
    }

    /**
     * Configure whether to throw an exception on non-2xx status codes.
     * Default is false (does not throw).
     *
     * @param fail If true, throws on non-2xx responses
     */
    public APIRequest failOnStatusCode(boolean fail) {
        this.failOnStatusCode = fail;
        return this;
    }

    @Override
    @Step("{0} sends #method request to #url")
    public <T extends Actor> void performAs(T actor) {
        APIRequestContext apiContext = BrowseTheWebWithPlaywright.as(actor).getAPIRequestContext();

        RequestOptions options = RequestOptions.create();

        // Add headers
        for (Map.Entry<String, String> header : headers.entrySet()) {
            options.setHeader(header.getKey(), header.getValue());
        }

        // Add query params
        for (Map.Entry<String, String> param : queryParams.entrySet()) {
            options.setQueryParam(param.getKey(), param.getValue());
        }

        // Set body
        if (body != null) {
            if (body instanceof String) {
                options.setData((String) body);
            } else if (body instanceof byte[]) {
                options.setData((byte[]) body);
            } else {
                // Assume it's a JSON-serializable object
                options.setData(body);
            }
        }

        // Set timeout
        if (timeout != null) {
            options.setTimeout(timeout);
        }

        // Set fail on status code
        if (failOnStatusCode != null) {
            options.setFailOnStatusCode(failOnStatusCode);
        }

        // Execute the request
        APIResponse response = switch (method) {
            case "GET" -> apiContext.get(url, options);
            case "POST" -> apiContext.post(url, options);
            case "PUT" -> apiContext.put(url, options);
            case "PATCH" -> apiContext.patch(url, options);
            case "DELETE" -> apiContext.delete(url, options);
            case "HEAD" -> apiContext.head(url, options);
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        };

        // Store the response for querying
        actor.remember(LAST_API_RESPONSE_KEY, response);

        // Record the REST query for Serenity reports
        recordRestQuery(response);
    }

    private void recordRestQuery(APIResponse response) {
        RestMethod restMethod = RestMethod.restMethodCalled(method).orElse(RestMethod.GET);

        // Build request headers string
        String requestHeadersStr = headers.entrySet().stream()
            .map(e -> e.getKey() + ": " + e.getValue())
            .collect(Collectors.joining("\n"));

        // Add content type to headers if present
        if (contentType != null && !headers.containsKey("Content-Type")) {
            requestHeadersStr = (requestHeadersStr.isEmpty() ? "" : requestHeadersStr + "\n") +
                "Content-Type: " + contentType;
        }

        // Build response headers string
        String responseHeadersStr = response.headers().entrySet().stream()
            .map(e -> e.getKey() + ": " + e.getValue())
            .collect(Collectors.joining("\n"));

        // Get request body content
        String requestContent = formatRequestBody();

        // Get response body (limit size for large responses)
        String responseBody = getResponseBody(response);

        // Build the REST query
        RestQuery restQuery = RestQuery.withMethod(restMethod)
            .andPath(response.url())
            .withContent(requestContent)
            .withContentType(contentType != null ? contentType : "")
            .withRequestHeaders(requestHeadersStr)
            .withRequestCookies("")  // Cookies are handled automatically by browser context
            .withResponse(responseBody)
            .withStatusCode(response.status())
            .withResponseHeaders(responseHeadersStr)
            .withResponseCookies("");

        // Record to Serenity reports
        if (TestSession.isSessionStarted()) {
            TestSession.addEvent(new RecordPlaywrightRestQueryEvent(restQuery));
        } else {
            getEventBus().getBaseStepListener().recordRestQuery(restQuery);
        }
    }

    private String formatRequestBody() {
        if (body == null) {
            return "";
        }
        if (body instanceof String) {
            return (String) body;
        }
        if (body instanceof byte[]) {
            return "[Binary data: " + ((byte[]) body).length + " bytes]";
        }
        // JSON serialize other objects
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(body);
        } catch (Exception e) {
            return body.toString();
        }
    }

    private String getResponseBody(APIResponse response) {
        final int MAX_BODY_SIZE = 16 * 1024;
        try {
            String contentType = response.headers().get("content-type");
            if (contentType != null && (contentType.contains("application/json") ||
                contentType.contains("text/") || contentType.contains("application/xml"))) {
                String body = response.text();
                if (body.length() > MAX_BODY_SIZE) {
                    return body.substring(0, MAX_BODY_SIZE) +
                        "\n\n[Response truncated - exceeded " + MAX_BODY_SIZE + " bytes]";
                }
                return body;
            } else if (contentType != null) {
                return "[Binary content - Content-Type: " + contentType + "]";
            }
            return "";
        } catch (Exception e) {
            return "[Error reading response: " + e.getMessage() + "]";
        }
    }

    /**
     * Get the key used to store the last API response in actor's memory.
     */
    public static String getLastApiResponseKey() {
        return LAST_API_RESPONSE_KEY;
    }
}
