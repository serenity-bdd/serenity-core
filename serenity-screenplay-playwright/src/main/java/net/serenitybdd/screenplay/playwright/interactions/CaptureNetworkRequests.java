package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Request;
import com.microsoft.playwright.Response;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Start capturing network requests and responses for later analysis.
 *
 * <p>This interaction captures all network traffic including:</p>
 * <ul>
 *   <li>Request URL, method, and headers</li>
 *   <li>Response status code and body</li>
 *   <li>Failed requests (network errors, timeouts)</li>
 * </ul>
 *
 * <p>Sample usage:</p>
 * <pre>{@code
 *     // Start capturing network requests
 *     actor.attemptsTo(CaptureNetworkRequests.duringTest());
 *
 *     // ... perform actions ...
 *
 *     // Query captured requests
 *     List<CapturedRequest> requests = actor.asksFor(NetworkRequests.all());
 *     List<CapturedRequest> failed = actor.asksFor(NetworkRequests.failed());
 *
 *     // Clear captured requests
 *     actor.attemptsTo(CaptureNetworkRequests.clear());
 * }</pre>
 *
 * @see net.serenitybdd.screenplay.playwright.questions.NetworkRequests
 */
public class CaptureNetworkRequests implements Performable {

    static final String NETWORK_REQUESTS_KEY = "playwright.networkRequests";
    static final String REQUEST_LISTENER_KEY = "playwright.requestListener";
    static final String RESPONSE_LISTENER_KEY = "playwright.responseListener";
    static final String FAILED_LISTENER_KEY = "playwright.requestFailedListener";

    private final boolean clear;

    private CaptureNetworkRequests(boolean clear) {
        this.clear = clear;
    }

    /**
     * Start capturing network requests during the test.
     */
    public static CaptureNetworkRequests duringTest() {
        return new CaptureNetworkRequests(false);
    }

    /**
     * Clear all previously captured network requests.
     */
    public static CaptureNetworkRequests clear() {
        return new CaptureNetworkRequests(true);
    }

    @Override
    @Step("{0} starts capturing network requests")
    public <T extends Actor> void performAs(T actor) {
        if (clear) {
            List<CapturedRequest> requests = actor.recall(NETWORK_REQUESTS_KEY);
            if (requests != null) {
                requests.clear();
            }
            return;
        }

        // Check if listener is already registered
        Consumer<Request> existingListener = actor.recall(REQUEST_LISTENER_KEY);
        if (existingListener != null) {
            return;
        }

        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();

        List<CapturedRequest> requests = new CopyOnWriteArrayList<>();
        actor.remember(NETWORK_REQUESTS_KEY, requests);

        // Capture requests
        Consumer<Request> requestListener = request -> {
            CapturedRequest captured = new CapturedRequest(
                request.url(),
                request.method(),
                request.resourceType(),
                request.headers()
            );
            requests.add(captured);
        };
        actor.remember(REQUEST_LISTENER_KEY, requestListener);
        page.onRequest(requestListener);

        // Capture responses
        Consumer<Response> responseListener = response -> {
            String url = response.url();
            for (CapturedRequest req : requests) {
                if (req.getUrl().equals(url) && req.getStatus() == null) {
                    req.setResponse(response.status(), response.statusText());
                    break;
                }
            }
        };
        actor.remember(RESPONSE_LISTENER_KEY, responseListener);
        page.onResponse(responseListener);

        // Capture failed requests
        Consumer<Request> failedListener = request -> {
            String url = request.url();
            for (CapturedRequest req : requests) {
                if (req.getUrl().equals(url) && req.getStatus() == null) {
                    req.setFailed(request.failure());
                    break;
                }
            }
        };
        actor.remember(FAILED_LISTENER_KEY, failedListener);
        page.onRequestFailed(failedListener);
    }

    /**
     * Get the key used to store network requests in actor's memory.
     */
    public static String getNetworkRequestsKey() {
        return NETWORK_REQUESTS_KEY;
    }

    /**
     * Represents a captured network request with its response.
     */
    public static class CapturedRequest {
        private final String url;
        private final String method;
        private final String resourceType;
        private final java.util.Map<String, String> requestHeaders;
        private Integer status;
        private String statusText;
        private String failureText;
        private boolean failed;

        public CapturedRequest(String url, String method, String resourceType,
                               java.util.Map<String, String> requestHeaders) {
            this.url = url;
            this.method = method;
            this.resourceType = resourceType;
            this.requestHeaders = requestHeaders;
        }

        void setResponse(int status, String statusText) {
            this.status = status;
            this.statusText = statusText;
            this.failed = status >= 400;
        }

        void setFailed(String failureText) {
            this.failureText = failureText;
            this.failed = true;
        }

        public String getUrl() {
            return url;
        }

        public String getMethod() {
            return method;
        }

        public String getResourceType() {
            return resourceType;
        }

        public java.util.Map<String, String> getRequestHeaders() {
            return requestHeaders;
        }

        public Integer getStatus() {
            return status;
        }

        public String getStatusText() {
            return statusText;
        }

        public String getFailureText() {
            return failureText;
        }

        /**
         * Check if this request failed (network error or 4xx/5xx status).
         */
        public boolean isFailed() {
            return failed;
        }

        /**
         * Check if this is a client error (4xx status).
         */
        public boolean isClientError() {
            return status != null && status >= 400 && status < 500;
        }

        /**
         * Check if this is a server error (5xx status).
         */
        public boolean isServerError() {
            return status != null && status >= 500;
        }

        @Override
        public String toString() {
            if (failureText != null) {
                return method + " " + url + " - FAILED: " + failureText;
            }
            if (status != null) {
                return method + " " + url + " - " + status + " " + statusText;
            }
            return method + " " + url + " - (pending)";
        }
    }
}
