package net.serenitybdd.screenplay.playwright.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.interactions.CaptureNetworkRequests;
import net.serenitybdd.screenplay.playwright.interactions.CaptureNetworkRequests.CapturedRequest;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Query captured network requests.
 *
 * <p>Before using these questions, you must start capturing network requests
 * using {@link CaptureNetworkRequests#duringTest()}.</p>
 *
 * <p>Sample usage:</p>
 * <pre>
 *     // Start capturing
 *     actor.attemptsTo(CaptureNetworkRequests.duringTest());
 *
 *     // ... perform actions ...
 *
 *     // Query requests
 *     List&lt;CapturedRequest&gt; all = actor.asksFor(NetworkRequests.all());
 *     List&lt;CapturedRequest&gt; failed = actor.asksFor(NetworkRequests.failed());
 *     List&lt;CapturedRequest&gt; apiCalls = actor.asksFor(NetworkRequests.toUrlContaining("/api/"));
 *
 *     // Get counts
 *     int failedCount = actor.asksFor(NetworkRequests.failedCount());
 * </pre>
 *
 * @see CaptureNetworkRequests
 */
public class NetworkRequests {

    private NetworkRequests() {
        // Factory class - prevent instantiation
    }

    /**
     * Get all captured network requests.
     */
    public static Question<List<CapturedRequest>> all() {
        return new AllRequestsQuestion();
    }

    /**
     * Get all failed requests (network errors or 4xx/5xx status codes).
     */
    public static Question<List<CapturedRequest>> failed() {
        return new FailedRequestsQuestion();
    }

    /**
     * Get all requests with client errors (4xx status codes).
     */
    public static Question<List<CapturedRequest>> clientErrors() {
        return new ClientErrorRequestsQuestion();
    }

    /**
     * Get all requests with server errors (5xx status codes).
     */
    public static Question<List<CapturedRequest>> serverErrors() {
        return new ServerErrorRequestsQuestion();
    }

    /**
     * Get requests to URLs containing the specified substring.
     *
     * @param urlSubstring The substring to match in URLs
     */
    public static Question<List<CapturedRequest>> toUrlContaining(String urlSubstring) {
        return new UrlContainingQuestion(urlSubstring);
    }

    /**
     * Get requests to URLs matching the specified glob pattern.
     * Supports * (any characters) and ** (any path segment).
     *
     * @param pattern The glob pattern (e.g., "**\/api/**")
     */
    public static Question<List<CapturedRequest>> matching(String pattern) {
        return new MatchingPatternQuestion(pattern);
    }

    /**
     * Get requests with the specified HTTP method.
     *
     * @param method The HTTP method (GET, POST, etc.)
     */
    public static Question<List<CapturedRequest>> withMethod(String method) {
        return new MethodQuestion(method.toUpperCase());
    }

    /**
     * Get the count of all captured requests.
     */
    public static Question<Integer> count() {
        return new CountQuestion(null);
    }

    /**
     * Get the count of failed requests.
     */
    public static Question<Integer> failedCount() {
        return new CountQuestion(true);
    }

    private static List<CapturedRequest> getRequests(Actor actor) {
        List<CapturedRequest> requests = actor.recall(CaptureNetworkRequests.getNetworkRequestsKey());
        return requests != null ? requests : Collections.emptyList();
    }

    static class AllRequestsQuestion implements Question<List<CapturedRequest>> {
        @Override
        public List<CapturedRequest> answeredBy(Actor actor) {
            return getRequests(actor);
        }

        @Override
        public String toString() {
            return "all captured network requests";
        }
    }

    static class FailedRequestsQuestion implements Question<List<CapturedRequest>> {
        @Override
        public List<CapturedRequest> answeredBy(Actor actor) {
            return getRequests(actor).stream()
                .filter(CapturedRequest::isFailed)
                .collect(Collectors.toList());
        }

        @Override
        public String toString() {
            return "failed network requests";
        }
    }

    static class ClientErrorRequestsQuestion implements Question<List<CapturedRequest>> {
        @Override
        public List<CapturedRequest> answeredBy(Actor actor) {
            return getRequests(actor).stream()
                .filter(CapturedRequest::isClientError)
                .collect(Collectors.toList());
        }

        @Override
        public String toString() {
            return "network requests with client errors (4xx)";
        }
    }

    static class ServerErrorRequestsQuestion implements Question<List<CapturedRequest>> {
        @Override
        public List<CapturedRequest> answeredBy(Actor actor) {
            return getRequests(actor).stream()
                .filter(CapturedRequest::isServerError)
                .collect(Collectors.toList());
        }

        @Override
        public String toString() {
            return "network requests with server errors (5xx)";
        }
    }

    static class UrlContainingQuestion implements Question<List<CapturedRequest>> {
        private final String urlSubstring;

        UrlContainingQuestion(String urlSubstring) {
            this.urlSubstring = urlSubstring;
        }

        @Override
        public List<CapturedRequest> answeredBy(Actor actor) {
            return getRequests(actor).stream()
                .filter(req -> req.getUrl().contains(urlSubstring))
                .collect(Collectors.toList());
        }

        @Override
        public String toString() {
            return "network requests to URLs containing '" + urlSubstring + "'";
        }
    }

    static class MatchingPatternQuestion implements Question<List<CapturedRequest>> {
        private final String pattern;
        private final Pattern regex;

        MatchingPatternQuestion(String pattern) {
            this.pattern = pattern;
            // Convert glob to regex
            String regexPattern = pattern
                .replace(".", "\\.")
                .replace("**", ".*")
                .replace("*", "[^/]*");
            this.regex = Pattern.compile(regexPattern);
        }

        @Override
        public List<CapturedRequest> answeredBy(Actor actor) {
            return getRequests(actor).stream()
                .filter(req -> regex.matcher(req.getUrl()).find())
                .collect(Collectors.toList());
        }

        @Override
        public String toString() {
            return "network requests matching '" + pattern + "'";
        }
    }

    static class MethodQuestion implements Question<List<CapturedRequest>> {
        private final String method;

        MethodQuestion(String method) {
            this.method = method;
        }

        @Override
        public List<CapturedRequest> answeredBy(Actor actor) {
            return getRequests(actor).stream()
                .filter(req -> method.equals(req.getMethod()))
                .collect(Collectors.toList());
        }

        @Override
        public String toString() {
            return method + " network requests";
        }
    }

    static class CountQuestion implements Question<Integer> {
        private final Boolean failedOnly;

        CountQuestion(Boolean failedOnly) {
            this.failedOnly = failedOnly;
        }

        @Override
        public Integer answeredBy(Actor actor) {
            List<CapturedRequest> requests = getRequests(actor);
            if (failedOnly != null && failedOnly) {
                return (int) requests.stream().filter(CapturedRequest::isFailed).count();
            }
            return requests.size();
        }

        @Override
        public String toString() {
            return failedOnly != null && failedOnly
                ? "count of failed network requests"
                : "count of network requests";
        }
    }
}
